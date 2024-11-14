package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.dto.enumerate.EventState;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    boolean existsByIdAndInitiatorId(Long eventId, Long userId);

    Optional<Event> findByInitiatorIdAndId(Long userId, Long eventId);

    // Метод для поиска события по идентификатору инициатора и идентификатору события
    @Query("SELECT e FROM Event e WHERE e.id = :eventId AND e.initiator.id = :userId")
    Optional<Event> findByIdAndInitiatorId(@Param("eventId") Long eventId, @Param("userId") Long userId);

    Optional<Event> findByIdAndState(Long id, EventState state);

    boolean existsByCategory(Category category);
}