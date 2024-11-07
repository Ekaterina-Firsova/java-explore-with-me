package ru.practicum.ewm.stats;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import ru.practicum.ewm.EndpointHitDto;

@UtilityClass
public class EndpointHitMapper {
    public EndpointHitDto toEndpointHitDto(@NotNull EndpointHit endpointHit) {

        return EndpointHitDto.builder()
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .uri(endpointHit.getUri())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }

    public EndpointHit toEndpointHit(@NotNull EndpointHitDto endpointHitDto) {

        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .ip(endpointHitDto.getIp())
                .uri(endpointHitDto.getUri())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }
}
