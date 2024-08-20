package com.timur.spotify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификацию")
public class SignInRequest {

    @Schema(description = "Имя пользователя", example = "Jon")
    @Size(min = 3, max = 50, message = "Username must have from 3 to 50 symbols")
    @NotBlank(message = "Username can't be null")
    private String username;

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(min = 3, max = 255, message = "Username must have from 8 to 255 symbols")
    @NotBlank(message = "Password can't be null")
    private String password;
}