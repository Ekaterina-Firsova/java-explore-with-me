package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation; // Краткое описание события

    @NotNull
    private Integer category; // ID категории к которой относится событие

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description; // Полное описание события

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время на которые намечено событие

    @NotNull
    private LocationDto location; // Локация события

    //@Builder.Default
    private Boolean paid = false; // Нужно ли оплачивать участие в событии

    @Min(0)
    //@Builder.Default
    private Integer participantLimit = 0; // Ограничение на количество участников. 0-ограничений нет

    //@Builder.Default
    private Boolean requestModeration = true; // Нужна ли пре-модерация заявок на участие

    @NotBlank
    @Size(min = 3, max = 120)
    private String title; // Заголовок события
}

//public class NewEventDto {
//
//    private Integer category; // ID категории к которой относится событие
//}