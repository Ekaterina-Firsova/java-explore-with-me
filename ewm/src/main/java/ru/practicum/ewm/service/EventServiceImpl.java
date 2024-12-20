package ru.practicum.ewm.service;

import com.querydsl.core.BooleanBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.UpdateEventUserRequest;
import ru.practicum.ewm.dto.enumerate.AdminStateAction;
import ru.practicum.ewm.dto.enumerate.EventState;
import ru.practicum.ewm.dto.enumerate.UserStateAction;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.EventView;
import ru.practicum.ewm.entity.Location;
import ru.practicum.ewm.entity.QEvent;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.EventViewRepository;
import ru.practicum.ewm.repository.LocationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final QEvent qEvent = QEvent.event;
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventViewRepository eventViewRepository;
    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest) {

        if (updateRequest.getEventDate() != null && updateRequest.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата начала события должна быть позже текущей");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (updateRequest.getStateAction() != null && updateRequest.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
            if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException("Дата начала события должна быть не ранее чем за час от даты публикации");
            }
        }

        if (updateRequest.getStateAction() != null) {
            AdminStateAction adminAction = updateRequest.getStateAction();

            if (adminAction.equals(AdminStateAction.PUBLISH_EVENT)) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new ConflictException("Cannot publish the event because it's already published");
                }
                if (!event.getState().equals(EventState.PENDING)) {
                    throw new ConflictException("Event must be in PENDING state to be published");
                }
                event.setState(EventState.PUBLISHED);
            } else if (adminAction.equals(AdminStateAction.REJECT_EVENT)) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new ConflictException("Cannot reject the event because it's already published");
                }
                event.setState(EventState.CANCELED);
            }
        }

        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryService.findById(updateRequest.getCategory());
            event.setCategory(category);
        }
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getLocation() != null) {
            event.setLocationDto(updateRequest.getLocation());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> getEvents(
            List<Long> users, List<String> states, List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        BooleanBuilder builder = new BooleanBuilder();

        if (users != null && !users.isEmpty()) {
            builder.and(qEvent.initiator.id.in(users));
        }

        if (states != null && !states.isEmpty()) {
            List<EventState> eventStates = states.stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
            builder.and(qEvent.state.in(eventStates));
        }

        if (categories != null && !categories.isEmpty()) {
            builder.and(qEvent.category.id.in(categories));
        }

        if (rangeStart != null) {
            builder.and(qEvent.eventDate.goe(rangeStart));
        }

        if (rangeEnd != null) {
            builder.and(qEvent.eventDate.loe(rangeEnd));
        }

        Pageable pageable = PageRequest.of(from / size, size);

        return eventRepository.findAll(builder, pageable)
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, @Valid NewEventDto newEventDto) {
        User user = userService.findById(userId);

        Category category = categoryService.findById(Long.valueOf(newEventDto.getCategory()));

        Event event = Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .locationDto(newEventDto.getLocation())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .initiator(user)
                .createdOn(LocalDateTime.now()) // Устанавливаем текущую дату создания
                .state(EventState.PENDING) // Статус нового события — ожидающее модерации
                .confirmedRequests(0)
                .build();

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        BooleanBuilder builder = new BooleanBuilder();
        if (userId != null && userId > 0) {
            builder.and(qEvent.initiator.id.eq(userId));
        }

        return eventRepository.findAll(builder, pageable)
                .stream()
                .peek(event -> event.setLocationName(findLocationName(event.getLocationDto())))
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        Optional<Event> event = eventRepository.findByInitiatorIdAndId(userId, eventId);
        return EventMapper.toEventFullDto(event
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " for user with id=" + userId + " was not found.")));
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {

        LocalDateTime minEventDate = LocalDateTime.now().plusHours(2);
        if (updateRequest.getEventDate() != null && updateRequest.getEventDate().isBefore(minEventDate)) {
            throw new BadRequestException("Event date must be at least two hours from now.");
        }

        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found."));

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.getCategory() != null) {
            Category category = categoryService.findById(updateRequest.getCategory());
            event.setCategory(category);
        }

        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }

        if (updateRequest.getLocation() != null) {
            event.setLocationDto(updateRequest.getLocation());
        }

        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case UserStateAction.SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case UserStateAction.CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getFilteredEvents(String text, List<Long> categories, Boolean paid,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                 Boolean onlyAvailable, String sort, Integer from, Integer size,
                                                 Long locationId) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        Pageable pageable = PageRequest.of(from / size, size);

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qEvent.state.eq(EventState.PUBLISHED));

        if (text != null && !text.isEmpty()) {
            String lowerCaseText = text.toLowerCase();
            builder.and(qEvent.annotation.toLowerCase().contains(lowerCaseText)
                    .or(qEvent.description.toLowerCase().contains(lowerCaseText)));
        }

        if (categories != null && !categories.isEmpty()) {
            builder.and(qEvent.category.id.in(categories));
        }

        if (paid != null) {
            builder.and(qEvent.paid.eq(paid));
        }

        builder.and(qEvent.eventDate.goe(rangeStart));

        if (rangeEnd != null) {
            builder.and(qEvent.eventDate.loe(rangeEnd));
        }

        if (onlyAvailable != null && onlyAvailable) {
            builder.and(qEvent.confirmedRequests.lt(qEvent.participantLimit));
        }

        if (sort == null || sort.equalsIgnoreCase("EVENT_DATE")) {
            pageable = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        } else if (sort.equalsIgnoreCase("VIEWS")) {
            pageable = PageRequest.of(from / size, size, Sort.by("views").descending());
        }

        if (locationId != null && locationId > 0) {
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new NotFoundException("Location with id=" + locationId + " was not found"));

            return eventRepository.findAll(builder, pageable)
                    .stream()
                    .filter(event -> {
                        double distance = calculateDistance(
                                location.getLat(),
                                location.getLon(),
                                event.getLocationDto().getLat(),
                                event.getLocationDto().getLon());
                        return distance <= location.getRadius();
                    })
                    .peek(event -> event.setLocationName(findLocationName(event.getLocationDto())))
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
        }

        return eventRepository.findAll(builder, pageable)
                .stream()
                .peek(event -> event.setLocationName(findLocationName(event.getLocationDto())))
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto getEventByIdAndState(Long id, EventState state, String ipAddress) {

        Event event = eventRepository.findByIdAndState(id, state)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found."));

        if (!eventViewRepository.existsByEventIdAndIpAddress(id, ipAddress)) {
            EventView view = new EventView();
            view.setEventId(id);
            view.setIpAddress(ipAddress);
            eventViewRepository.save(view);

            event.setViews(event.getViews() == null ? 1 : event.getViews() + 1);
            eventRepository.save(event);
        }

        eventRepository.save(event);

        return EventMapper.toEventFullDto(event);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        //расчет координат попадания в локацию
        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    @Override
    public String findLocationName(LocationDto locationDto) {
        if (locationDto == null) return null;

        Pageable pageable = PageRequest.of(0, 1);
        List<Location> locations = locationRepository.findNearestByCoordinates(locationDto.getLat(), locationDto.getLon(), pageable);
        return locations.isEmpty() ? null : locations.getFirst().getName();
    }
}
