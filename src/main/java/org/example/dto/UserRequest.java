package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Данные для создания или обновления пользователя")
public record UserRequest(

        @Schema(description = "Имя", example = "Alex")
        @NotBlank(message = "не должно быть пустым")
        String name,

        @Schema(description = "Email, должен быть уникальным", example = "alex@mail.ru")
        @NotBlank(message = "не должно быть пустым")
        @Email(message = "некорректный email")
        String email,

        @Schema(description = "Возраст от 0 до 150", example = "20")
        @NotNull(message = "обязательное поле")
        @Min(value = 0, message = "не может быть отрицательным")
        @Max(value = 150, message = "слишком большое значение")
        Integer age
) {
}
