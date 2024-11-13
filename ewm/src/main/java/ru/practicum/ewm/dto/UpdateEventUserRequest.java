package ru.practicum.ewm.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.enumerate.UserStateAction;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters.")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters.")
    private String description;

    @NotNull
    private LocalDateTime eventDate;

    private LocationDto location; // DTO для местоположения

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private UserStateAction stateAction;

    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters.")
    private String title;
}
