package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {

    private Long id; // Идентификатор события

    private String annotation; // Краткое описание

    private CategoryDto category; // Категория события

    private Integer confirmedRequests; // Количество одобренных заявок

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время события

    private UserShortDto initiator; // Инициатор события (краткая информация о пользователе)

    private Boolean paid; // Необходимость оплаты участия

    private String title; // Заголовок события

    private Integer views; // Количество просмотров события

    private String locationName;
}
