package ru.practicum.ewm.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.enumerate.EventState;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.QEvent;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final QEvent qEvent = QEvent.event;
    private final UserService userService;
    private final CategoryService categoryService;


    @Override
    public List<EventFullDto> getEvents(
            List<Long> users, List<String> states, List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        // Создаем фильтр с использованием QueryDSL
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

        // Настройка пагинации
        Pageable pageable = PageRequest.of(from / size, size);

        // Вызов метода репозитория с фильтром и пагинацией
        return eventRepository.findAll(builder, pageable)
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, @Valid NewEventDto newEventDto) {
        // Проверяем, существует ли пользователь
        User user = userService.findById(userId);

        // Проверяем, существует ли категория
        Category category = categoryService.findById(Long.valueOf(newEventDto.getCategory()));

        // Создаем новый объект Event и заполняем его данными из NewEventDto
        Event event = Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(LocationMapper.toLocation(newEventDto.getLocation()))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .initiator(user)
                .createdOn(LocalDateTime.now()) // Устанавливаем текущую дату создания
                .state(EventState.PENDING) // Статус нового события — ожидающее модерации
                .build();

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        // Настройка пагинации
        Pageable pageable = PageRequest.of(from / size, size);

        // Создаем фильтр с использованием QueryDSL
        BooleanBuilder builder = new BooleanBuilder();
        if (userId != null && userId > 0) {
            builder.and(qEvent.initiator.id.eq(userId));
        }

        // Вызов метода репозитория с фильтром и пагинацией
        return eventRepository.findAll(builder, pageable)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

}
