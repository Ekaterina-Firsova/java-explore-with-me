package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.entity.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

//    @Query("SELECT e FROM Event e " +
//            "WHERE (:user IS NULL OR e.initiator.id IN :user) " +
//            "AND (:states IS NULL OR e.state IN :states) " +
//            "AND (:categories IS NULL OR e.category.id IN :categories) " +
//            "AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart) " +
//            "AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)")
//    List<Event> findByFilters(List<Long> user, List<String> states, List<Long> categories,
//                              LocalDateTime rangeStart, LocalDateTime rangeEnd);
}