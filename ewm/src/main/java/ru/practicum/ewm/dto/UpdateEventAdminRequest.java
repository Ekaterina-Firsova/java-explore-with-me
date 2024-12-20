package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.enumerate.AdminStateAction;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {

    @Size(min = 20, max = 2000, message = "Аннотация должна быть между 20 и 2000 символами")
    private String annotation; // Новая аннотация

    private Long category; // Новая категория

    @Size(min = 20, max = 7000, message = "Описание должно быть между 20 и 7000 символами")
    private String description; // Новое описание

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Новая дата и время события

    private LocationDto location; // Новое местоположение (ссылка на LocationDto)

    private Boolean paid; // Платное ли событие

    @Min(value = 0, message = "Лимит участников должен быть положительным числом")
    private Integer participantLimit; // Новый лимит пользователей

    private Boolean requestModeration; // Нужно ли пре-модерировать заявки

    private AdminStateAction stateAction; // Действие, которое необходимо выполнить (публикация или отклонение)

    @Size(min = 3, max = 120, message = "Заголовок должен быть между 3 и 120 символами")
    private String title; // Новый заголовок
}