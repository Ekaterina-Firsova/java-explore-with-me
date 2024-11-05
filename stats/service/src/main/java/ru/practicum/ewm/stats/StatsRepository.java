package ru.practicum.ewm.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.ewm.ViewStatsDto(s.app, s.uri, COUNT(s) AS hits) " +
            "FROM EndpointHit s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR s.uri IN :uris) " +
            "GROUP BY s.app, s.uri " +
            "HAVING (:unique = false OR COUNT(DISTINCT s.ip) = COUNT(s.ip))")
    List<ViewStatsDto> findStatisticsByParams(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris,
            @Param("unique") boolean unique);

}
