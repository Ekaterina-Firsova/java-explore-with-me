package ru.practicum.ewm.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.enumerate.EventState;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.service.EventService;


import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventController {

    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size,
            HttpServletRequest request
    ) {
        log.info("Request GET /events with parameters: text: {}, categories: {}, paid: {}, rangeStart: {}, rangeEnd: {}, onlyAvailable: {}, sort: {}, from: {}, size: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("rangeStart must be before rangeEnd.");
        }

        List<EventShortDto> events = eventService.getFilteredEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size
        );

        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .timestamp(LocalDateTime.now())
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .build();

        statsClient.saveHit(endpointHitDto);

        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable @NotNull Long id, HttpServletRequest request) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .timestamp(LocalDateTime.now())
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .build();

        statsClient.saveHit(endpointHitDto);
        return ResponseEntity.ok(eventService.getEventByIdAndState(id, EventState.PUBLISHED, request.getRemoteAddr()));
    }
}
