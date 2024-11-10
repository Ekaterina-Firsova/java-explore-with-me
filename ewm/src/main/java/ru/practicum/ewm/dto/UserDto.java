package ru.practicum.ewm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id; // Идентификатор пользователя

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    @Size(max = 255, message = "Email не может превышать 255 символов")
    private String email; // Почтовый адрес пользователя

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 1, max = 100, message = "Имя должно содержать от 1 до 100 символов")
    private String name; // Имя пользователя
}
