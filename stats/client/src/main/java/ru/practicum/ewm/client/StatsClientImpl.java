package ru.practicum.ewm.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class StatsClientImpl implements StatsClient {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestClient restClient;

    public static DateTimeFormatter getFormatter() {
        return FORMATTER;
    }

    public StatsClientImpl(RestClient.Builder builder, @Value("${stats.server.uri}") String uri) {
        this.restClient = builder
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void saveHit(EndpointHitDto endpointHitDto) {
        try {
            restClient.post()
                    .uri("/hit")
                    .body(endpointHitDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error POST /hit: {}", e.getMessage(), e);
        }
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("GET /stats with start: {}, end: {}, uris: {}, unique: {}", start, end, uris, unique);
        List<ViewStatsDto> response;
        try {
            response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/stats")
                            .queryParam("start", start.format(FORMATTER))
                            .queryParam("end", end.format(FORMATTER))
                            .queryParam("uris", uris.toArray())
                            .queryParam("unique", unique)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error GET /stats: {}", e.getMessage(), e);
            response = Collections.emptyList();
        }
        return response;
    }
}