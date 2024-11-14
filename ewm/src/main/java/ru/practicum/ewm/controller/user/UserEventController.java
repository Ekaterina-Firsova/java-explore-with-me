package ru.practicum.ewm.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewEventDto;

import ru.practicum.ewm.dto.UpdateEventUserRequest;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserEventController {

    private final EventService eventService;

    //добавление нового события
    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> addEvent(
            @Valid @RequestBody NewEventDto newEventDto,
            @PathVariable Long userId) {

        log.info("Request POST /users/{}/events with body : {}", userId, newEventDto);
        // Проверка на время начала события - не менее чем через два часа от текущего времени
        LocalDateTime minEventDate = LocalDateTime.now().plusHours(2);
        if (newEventDto.getEventDate().isBefore(minEventDate)) {
            throw new BadRequestException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: "
                    + newEventDto.getEventDate());
        }
        EventFullDto eventFullDto = eventService.addEvent(userId, newEventDto);

        return new ResponseEntity<>(eventFullDto, HttpStatus.CREATED);
    }

    //получение события, добавленного пользователем
    @GetMapping("/{userId}/events")
    public ResponseEntity<?> getEvents(
            @PathVariable @NotNull Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {

        log.info("Request GET /users/{}/events with params: from={}, size={}", userId, from, size);

        if (from < 0 || size <= 0) {
            throw new ConflictException("The 'from' parameter must be >= 0 and 'size' must be > 0");
        }

        List<EventShortDto> events = eventService.getEvents(userId, from, size);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // получение полной информации о событии добавленной текущем пользователем
    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId) {

        log.info("Request GET /users/{}/events/{}", userId, eventId);

        EventFullDto event = eventService.getEvent(userId, eventId);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    // изменение события
    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateRequest) {

        log.info("Request PATCH /users/{}/events/{} with update data : {}", userId, eventId, updateRequest);

        EventFullDto updatedEvent = eventService.updateEvent(userId, eventId, updateRequest);
        return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
    }
}
