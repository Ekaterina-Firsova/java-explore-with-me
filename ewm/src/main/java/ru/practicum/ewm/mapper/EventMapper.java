package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.entity.Event;

import java.util.List;

@UtilityClass
public class EventMapper {

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .eventDate(event.getEventDate())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .paid(event.getPaid())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .build();
    }

    public Event toEvent(EventFullDto eventFullDto) {
        return Event.builder()
                .id(eventFullDto.getId())
                .eventDate(eventFullDto.getEventDate())
                .annotation(eventFullDto.getAnnotation())
                .category(CategoryMapper.toCategory(eventFullDto.getCategory()))
                .paid(eventFullDto.getPaid())
                .confirmedRequests(eventFullDto.getConfirmedRequests())
                .createdOn(eventFullDto.getCreatedOn())
                .description(eventFullDto.getDescription())
                .state(eventFullDto.getState())
                .title(eventFullDto.getTitle())
                .views(eventFullDto.getViews())
                .initiator(UserMapper.toUser(eventFullDto.getInitiator()))
                .location(eventFullDto.getLocation())
                .participantLimit(eventFullDto.getParticipantLimit())
                .publishedOn(eventFullDto.getPublishedOn())
                .requestModeration(eventFullDto.getRequestModeration())
                .build();
    }

    public List<EventFullDto> toListEventFullDto(List<Event> events) {
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .toList();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .eventDate(event.getEventDate())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .paid(event.getPaid())
                .confirmedRequests(event.getConfirmedRequests())
                .title(event.getTitle())
                .views(event.getViews())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .build();
    }

}
