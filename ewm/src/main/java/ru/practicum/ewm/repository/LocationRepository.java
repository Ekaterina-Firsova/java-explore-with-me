package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.entity.Location;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l WHERE " +
            "SQRT(POWER(l.lat - :lat, 2) + POWER(l.lon - :lon, 2)) <= l.radius " +
            "ORDER BY SQRT(POWER(l.lat - :lat, 2) + POWER(l.lon - :lon, 2)) ASC")
    List<Location> findNearestByCoordinates(@Param("lat") double lat, @Param("lon") double lon, Pageable pageable);
}
