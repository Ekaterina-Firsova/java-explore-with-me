package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    private List<Long> events; // Список идентификаторов событий входящих в подборку

    private Boolean pinned = false; // Закреплена ли подборка на главной странице сайта

    @NotBlank(message = "Title must not be blank")
    @Size(min = 1, max = 50, message = "Title length must be between 1 and 50 characters")
    private String title; // Заголовок подборки
}
