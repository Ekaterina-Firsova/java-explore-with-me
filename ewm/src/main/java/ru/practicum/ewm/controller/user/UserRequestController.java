package ru.practicum.ewm.controller.user;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import ru.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewEventDto;

import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.dto.UpdateEventUserRequest;
import ru.practicum.ewm.entity.ParticipationRequest;
import ru.practicum.ewm.exception.ApiError;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.ParticipationRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserRequestController {

    private final ParticipationRequestService participationRequestService;

    //Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(
            @PathVariable Long userId,
            @PathVariable Long eventId) {

        log.info("Request GET /users/{}/events/{}/requests", userId, eventId);

        // Получаем список заявок на участие
        List<ParticipationRequestDto> participationRequests = participationRequestService
                .getParticipationRequestsByEvent(userId, eventId);

        return ResponseEntity.ok(participationRequests);
    }

    //Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(
            @Valid  @RequestBody EventRequestStatusUpdateRequest updateRequest,
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        EventRequestStatusUpdateResult result = participationRequestService.updateRequestStatus(userId, eventId, updateRequest);
        return ResponseEntity.ok(result);
    }

    //Добавление запроса от текущего пользователя на участие в событии
    @PostMapping("/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> addParticipationRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId) {

        ParticipationRequestDto createdRequest = participationRequestService.addRequest(userId, eventId);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable @NotNull Long userId) {
        List<ParticipationRequestDto> requests = participationRequestService.getRequestsByUserId(userId);

        return ResponseEntity.ok(requests);
    }

    //Отмена своего запроса на участие в событии
    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long requestId) {

        ParticipationRequestDto cancelledRequest = participationRequestService.cancelRequest(userId, requestId);

        return ResponseEntity.ok(cancelledRequest);
    }
}
