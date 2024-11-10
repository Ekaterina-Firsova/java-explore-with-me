package ru.practicum.ewm.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewEventDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
//    List<EventFullDto> getEvents(List<Long> user, List<String> states, List<Long> categories,
//                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<EventFullDto> getEvents(
            List<Long> users, List<String> states, List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto addEvent(Long userId, @Valid NewEventDto newEventDto);

    List<EventShortDto> getEvents(@NotNull Long userId, Integer from, Integer size);
}
