package ru.practicum.ewm.dto;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class LocationDto {
    @NotNull
    private Double lat;

    @NotNull
    private Double lon;
}
