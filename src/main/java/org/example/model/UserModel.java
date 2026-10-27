package org.example.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.dto.UserResponse;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Relation(itemRelation = "user", collectionRelation = "users")
@Schema(description = "Пользователь со ссылками на связанные ресурсы")
public class UserModel extends RepresentationModel<UserModel> {

    @Schema(description = "Идентификатор", example = "1")
    private final Long id;

    @Schema(description = "Имя", example = "Alex")
    private final String name;

    @Schema(description = "Email (уникальный)", example = "alex@mail.ru")
    private final String email;

    @Schema(description = "Возраст", example = "20")
    private final Integer age;

    @Schema(description = "Дата и время создания", example = "2026-01-01T12:00:00")
    private final LocalDateTime createdAt;

    public UserModel(UserResponse user) {
        this.id = user.id();
        this.name = user.name();
        this.email = user.email();
        this.age = user.age();
        this.createdAt = user.createdAt();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Integer getAge() { return age; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
