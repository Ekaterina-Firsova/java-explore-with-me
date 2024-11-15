package ru.practicum.ewm.dto;

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
public class UserShortDto {
    @NotNull(message = "ID пользователя не может быть null")
    private Long id; // Идентификатор пользователя

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 1, max = 100, message = "Имя должно содержать от 1 до 100 символов")
    private String name; // Имя пользователя
}
