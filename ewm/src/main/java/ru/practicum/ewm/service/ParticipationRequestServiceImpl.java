package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.dto.enumerate.EventState;
import ru.practicum.ewm.dto.enumerate.UserStateRequest;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.ParticipationRequest;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService{

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByEvent(Long userId, Long eventId) {
        //Получение информации о запросах на участие в событии текущего пользователя
        //GET users/{userId}/events/{eventId}/requests
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new ConflictException("Requester id=" + userId + " is not owner for this event " + eventId);
        }

        List<ParticipationRequest> requests = requestRepository
                .findByEvent(eventId);

        return requests.stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(
            Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        //Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
        //@PatchMapping("users/{userId}/events/{eventId}/requests")

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + "user's with id =" +  userId + " was not found"));

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new IllegalArgumentException("Request moderation not required for this event");
        }

        List<ParticipationRequest> allRequests =
                requestRepository.findByEventIdAndRequestIdsOrderByCreated(eventId, updateRequest.getRequestIds());

        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        for (ParticipationRequest request : allRequests) {
            // Если заявка не в состоянии ожидания, выдаем ошибку
            if (!request.getStatus().equals(UserStateRequest.PENDING)) {
                throw new ConflictException("Only requests in PENDING status can be updated.");
            }

            // Проверка лимита участников
            if (updateRequest.getStatus().equals(UserStateRequest.CONFIRMED)) {
                if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    // Достигнут лимит участников
                    request.setStatus(UserStateRequest.REJECTED);
                    rejectedRequests.add(request);
                } else {
                    // Подтверждаем заявку и увеличиваем счетчик подтвержденных заявок
                    request.setStatus(UserStateRequest.CONFIRMED);
                    confirmedRequests.add(request);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                }
            } else {
                // Если запрос не подтвержден, он отклоняется
                request.setStatus(UserStateRequest.REJECTED);
                rejectedRequests.add(request);
            }
        }

        requestRepository.saveAll(confirmedRequests);
        requestRepository.saveAll(rejectedRequests);
        eventRepository.save(event);

        return new EventRequestStatusUpdateResult(
                confirmedRequests.stream().map(ParticipationRequestMapper::toDto).collect(Collectors.toList()),
                rejectedRequests.stream().map(ParticipationRequestMapper::toDto).collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        // Проверка на существование пользователя и события
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        // пользователь не может добавить запрос на своё событие
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event initiator cannot request to join their own event.");
        }

        // повторный запрос на участие
        if (requestRepository.existsByRequester_IdAndEvent(userId, eventId)) {
            throw new ConflictException("Duplicate request is not allowed.");
        }

        // нельзя участвовать в неопубликованном событии
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Cannot request to join an unpublished event.");
        }

        // Проверка: достигнут ли лимит запросов на участие
        if (event.getParticipantLimit() > 0 &&
                requestRepository.countByEventAndStatus(eventId, UserStateRequest.CONFIRMED) >= event.getParticipantLimit()) {
            throw new ConflictException("The participant limit has been reached.");
        }

        // Определение статуса заявки
        UserStateRequest status = event.getRequestModeration() ? UserStateRequest.PENDING : UserStateRequest.CONFIRMED;

        // Создание новой заявки
        ParticipationRequest newRequest = new ParticipationRequest();
        newRequest.setEvent(event.getId());
        newRequest.setRequester(user);
        newRequest.setStatus(status);
        //newRequest.setCreated(LocalDateTime.now());

        return ParticipationRequestMapper.toDto(requestRepository.save(newRequest));
        //return null;
    }
}
