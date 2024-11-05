package ru.practicum.ewm;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsDto {

    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotBlank
    private Long hits;
}
