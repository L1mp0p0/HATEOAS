package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserRequest;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureMockMvc
@Testcontainers
class UserApiIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    private long createUser(String name, String email, int age) throws Exception {
        String body = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest(name, email, age))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    @Test
    void fullCrudFlow() throws Exception {
        long id = createUser("Alex", "alex@mail.ru", 20);

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.createdAt").exists());

        mockMvc.perform(put("/api/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Alexander", "alex@mail.ru", 21))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alexander"))
                .andExpect(jsonPath("$.age").value(21));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.users.length()").value(1));

        mockMvc.perform(delete("/api/users/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409OnDuplicateEmail() throws Exception {
        createUser("Alex", "alex@mail.ru", 20);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Other", "alex@mail.ru", 30))))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn409WhenUpdatingToExistingEmail() throws Exception {
        createUser("Alex", "alex@mail.ru", 20);
        long secondId = createUser("Ivan", "ivan@mail.ru", 25);

        mockMvc.perform(put("/api/users/" + secondId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Ivan", "alex@mail.ru", 25))))
                .andExpect(status().isConflict());
    }

    /** Клиент проходит по ссылкам, зная только точку входа /api. */
    @Test
    void shouldNavigateApiByLinks() throws Exception {
        long id = createUser("Alex", "alex@mail.ru", 20);

        String root = mockMvc.perform(get("/api")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String usersPath = path(objectMapper.readTree(root).at("/_links/users/href").asText());

        String list = mockMvc.perform(get(usersPath)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String selfPath = path(objectMapper.readTree(list).at("/_embedded/users/0/_links/self/href").asText());
        assertEquals("/api/users/" + id, selfPath);

        String one = mockMvc.perform(get(selfPath)).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alex@mail.ru"))
                .andReturn().getResponse().getContentAsString();
        String deletePath = path(objectMapper.readTree(one).at("/_links/delete/href").asText());

        mockMvc.perform(delete(deletePath)).andExpect(status().isNoContent());
        mockMvc.perform(get(selfPath)).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnLocationHeaderOnCreate() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Alex", "alex@mail.ru", 20))))
                .andExpect(status().isCreated())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertTrue(location != null && location.endsWith("/api/users/"
                + objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong()));
    }

    @Test
    void shouldExposeOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("User Service API"))
                .andExpect(jsonPath("$.paths['/api/users']").exists())
                .andExpect(jsonPath("$.paths['/api/users/{id}']").exists())
                .andExpect(jsonPath("$.paths['/api']").exists());
    }

    @Test
    void shouldServeSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    private static String path(String href) {
        return URI.create(href).getPath();
    }
}
