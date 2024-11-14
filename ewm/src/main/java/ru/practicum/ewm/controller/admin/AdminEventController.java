package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventController {

    private final EventService eventService;

    //просмотр события
    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(
            @RequestParam(required = false) List<Long> users, //список id пользователей, чьи события нужно найти
            @RequestParam(required = false) List<String> states, //список состояний в которых находятся искомые события
            @RequestParam(required = false) List<Long> categories, //список id категорий в которых будет вестись поиск
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss") LocalDateTime rangeStart, //дата и время не раньше которых должно произойти событие
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss") LocalDateTime rangeEnd, //дата и время не позже которых должно произойти событие
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from, //количество событий, которые нужно пропустить для формирования текущего набора
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size //количество событий в наборе
    ) {
        log.info("Request GET /admin/events with parameters user: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from: {}, size: {}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        List<EventFullDto> events = eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);

        return ResponseEntity.ok(events);
    }

    // редактирование события
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(
            @Valid @RequestBody UpdateEventAdminRequest updateRequest,
            @PathVariable Long eventId) {

        log.info("Request PATCH /admin/events/{} with data: {}", eventId, updateRequest);

        EventFullDto updatedEvent = eventService.updateEvent(eventId, updateRequest);

        return ResponseEntity.ok(updatedEvent);
    }
}