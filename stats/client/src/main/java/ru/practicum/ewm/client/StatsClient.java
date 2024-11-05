package ru.practicum.ewm.client;

import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void saveHit(EndpointHitDto hitDto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}