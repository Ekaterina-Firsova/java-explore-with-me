package ru.practicum.ewm.stats;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void addHit(@RequestBody @Valid final EndpointHitDto endpointHitDto) {
        log.info("Request POST /hit with body : {}", endpointHitDto);
        statsService.addHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", required = false, defaultValue = "false") boolean unique) {

        log.info("Request GET /stats with start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        List<ViewStatsDto> stats = statsService.getStats(start, end, uris, unique);
        return ResponseEntity.ok(stats);
    }
}
