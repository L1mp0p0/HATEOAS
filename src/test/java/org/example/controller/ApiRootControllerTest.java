package org.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiRootController.class)
class ApiRootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void root_shouldReturnNavigationLinks() throws Exception {
        mockMvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/hal+json"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api"))
                .andExpect(jsonPath("$._links.users.href").value("http://localhost/api/users"))
                .andExpect(jsonPath("$._links.docs.href").value("http://localhost/swagger-ui.html"));
    }
}
