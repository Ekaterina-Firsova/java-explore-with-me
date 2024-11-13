package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entity.EventView;

public interface EventViewRepository extends JpaRepository<EventView, Long> {
    boolean existsByEventIdAndIpAddress(Long eventId, String ipAddress);
}
