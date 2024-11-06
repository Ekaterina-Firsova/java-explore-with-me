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

//    @Query("SELECT new ru.practicum.ewm.ViewStatsDto(s.app, s.uri, COUNT(s) AS hits) " +
//            "FROM EndpointHit s " +
//            "WHERE s.timestamp BETWEEN :start AND :end " +
//            "AND (:uris IS NULL OR s.uri IN :uris) " +
//            "GROUP BY s.app, s.uri " +
//            "HAVING (:unique = false OR COUNT(DISTINCT s.ip) = COUNT(s.ip)) " +
//            "ORDER BY hits DESC")
//    List<ViewStatsDto> findStatisticsByParams(
//            @Param("start") LocalDateTime start,
//            @Param("end") LocalDateTime end,
//            @Param("uris") List<String> uris,
//            @Param("unique") boolean unique);

    @Query("SELECT new ru.practicum.ewm.ViewStatsDto(e.app, e.uri, COUNT(e) AS hits) " +
            "FROM EndpointHit e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR e.uri IN :uris) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY hits DESC")
    List<ViewStatsDto> findUniqueStatisticsByParams(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.ewm.ViewStatsDto(e.app, e.uri, (1L) AS hits ) " +
            "FROM EndpointHit e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR e.uri IN :uris) " +
            "ORDER BY e.app, e.uri")
    List<ViewStatsDto> findStatisticsByParams(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);
}