package ru.practicum.ewm.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

import ru.practicum.ewm.exception.ApiError;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserEventController {

    private final EventService eventService;

    @PostMapping("/{userId}/events")
    public ResponseEntity<?> addEvent(
                        @RequestBody NewEventDto newEventDto,
                        @PathVariable Long userId) {

        log.info("Request POST /users/{}/events with body : {}", userId, newEventDto);
        // Проверка на время начала события - не менее чем через два часа от текущего времени
        LocalDateTime minEventDate = LocalDateTime.now().plusHours(2);
        if (newEventDto.getEventDate().isBefore(minEventDate)) {
            ApiError error = new ApiError(
                    HttpStatus.CONFLICT,
                    "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: "
                            + newEventDto.getEventDate(),
                    "For the requested operation the conditions are not met."
            );
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }

        EventFullDto eventFullDto = eventService.addEvent(userId, newEventDto);

        return new ResponseEntity<>(eventFullDto, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<?> getEvents(
            @PathVariable @NotNull Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Request GET /users/{}/events with params: from={}, size={}", userId, from, size);

        // Валидация параметров
        if (from < 0 || size <= 0) {
            ApiError error = new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Invalid parameters",
                    "The 'from' parameter must be >= 0 and 'size' must be > 0."
            );
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        try {
            List<EventShortDto> events = eventService.getEvents(userId, from, size);

            // Если событий нет, возвращаем пустой список
            if (events.isEmpty()) {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            }

            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (Exception e) {
            // Если произошла ошибка на уровне сервиса
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error fetching events",
                    e.getMessage()
            );
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
