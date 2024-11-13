package ru.practicum.ewm.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.enumerate.UserStateRequest;
import ru.practicum.ewm.entity.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    // Находит заявки со статусом "PENDING" для указанного события
    //List<ParticipationRequest> findPendingRequestsByEvent(Long eventId);

//    // для поиска заявок по событию и статусу
//    List<ParticipationRequest> findByEventIdAndStatus(Long eventId, String status);
//
//    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);
//
//    Integer countByEventAndStatus(Long eventId, UserStateRequest userStateRequest);

    List<ParticipationRequest> findByRequester_IdAndEvent(Long requesterId, Long eventId);

    boolean existsByRequester_IdAndEvent(Long requesterId, Long eventId);

    int countByEventAndStatus(Long eventId, UserStateRequest status);

    //List<ParticipationRequest> findByEventOrderByCreatedAsc(Long eventId);

    // Метод для получения заявок по eventId и списку requestIds, отсортированных по дате создания
    @Query("SELECT pr FROM ParticipationRequest pr WHERE pr.event = :eventId AND pr.id IN :requestIds ORDER BY pr.created ASC")
    List<ParticipationRequest> findByEventIdAndRequestIdsOrderByCreated(
            @Param("eventId") Long eventId,
            @Param("requestIds") @NotNull List<Long> requestIds
    );

    List<ParticipationRequest> findByEvent(Long eventId);

    List<ParticipationRequest> findByRequester_Id(Long userId);

    Optional<ParticipationRequest> findByIdAndRequester_Id(Long requestId, Long userId);
}
