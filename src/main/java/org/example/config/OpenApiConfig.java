package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("User Service API")
                .version("1.0")
                .description("""
                        REST API управления пользователями.

                        Ответы оформлены по принципам **HATEOAS** (формат HAL): вместе с данными сервер отдаёт \
                        ссылки на связанные ресурсы и доступные действия в поле `_links`. \
                        Начните с точки входа `GET /api` и переходите по ссылкам.""")
                .contact(new Contact().name("L1mp0p0")));
    }
}
