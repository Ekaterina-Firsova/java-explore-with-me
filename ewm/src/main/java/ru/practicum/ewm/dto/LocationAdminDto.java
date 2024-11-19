package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationAdminDto {
    private Long id;

    private String name;

    @NotNull
    private Double lat;

    @NotNull
    private Double lon;

    @NotNull
    @Positive
    private Double radius;
}
