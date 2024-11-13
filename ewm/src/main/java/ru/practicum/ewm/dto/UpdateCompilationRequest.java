package ru.practicum.ewm.dto;

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
public class UpdateCompilationRequest {

    private List<Long> events; // Список id событий для полной замены текущего списка

    private Boolean pinned; // Закреплена ли подборка на главной странице сайта

    @Size(min = 1, max = 50, message = "Title length must be between 1 and 50 characters")
    private String title; // Заголовок подборки
}