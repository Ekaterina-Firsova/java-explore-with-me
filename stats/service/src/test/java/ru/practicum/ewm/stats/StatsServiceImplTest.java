package ru.practicum.ewm.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class StatsServiceImplTest {

    @Mock
    private StatsRepository repository;

    @InjectMocks
    private StatsServiceImpl statsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetStats_sortedByHitsDescending() {
        // Подготовка данных для проверки
        List<ViewStatsDto> mockData = List.of(
                new ViewStatsDto("app1", "/uri1", 10L),
                new ViewStatsDto("app1", "/uri2", 8L),
                new ViewStatsDto("app2", "/uri3", 5L)
        );

        // Используем findStatisticsByParams для неуникальных запросов
        Mockito.when(repository.findStatisticsByParams(any(), any(), any()))
                .thenReturn(mockData);

        // Вызов метода
        List<ViewStatsDto> result = statsService.getStats(
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, false
        );

        // Проверка на сортировку по убыванию
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i - 1).getHits() >= result.get(i).getHits(),
                    "Список должен быть отсортирован по убыванию количества просмотров");
        }
    }

    @Test
    public void testGetStats_uniqueParameterTrue() {
        // Подготовка данных с уникальными IP
        List<ViewStatsDto> mockDataUnique = List.of(
                new ViewStatsDto("app1", "/uri1", 1L) // ожидаемый результат для unique=true
        );

        // Используем findUniqueStatisticsByParams для уникальных запросов
        Mockito.when(repository.findUniqueStatisticsByParams(any(), any(), any()))
                .thenReturn(mockDataUnique);

        // Вызов метода с уникальными IP
        List<ViewStatsDto> result = statsService.getStats(
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, true
        );

        // Проверка, что вернулся один уникальный результат
        assertEquals(1, result.size(), "Ожидался один уникальный результат");
        assertEquals(1, result.get(0).getHits(), "Ожидался один уникальный хит");
    }

    @Test
    public void testGetStats_withUrisFilter() {
        // Подготовка данных с фильтром по URI
        List<ViewStatsDto> mockData = List.of(
                new ViewStatsDto("app1", "/events", 5L),
                new ViewStatsDto("app1", "/events/1", 3L)
        );

        // Используем findStatisticsByParams, так как фильтрация идет по URI, а не уникальности
        Mockito.when(repository.findStatisticsByParams(any(), any(), eq(List.of("/events", "/events/1"))))
                .thenReturn(mockData);

        // Вызов метода с фильтром по URI
        List<ViewStatsDto> result = statsService.getStats(
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), List.of("/events", "/events/1"), false
        );

        // Проверка, что результат содержит только URI из фильтра
        assertEquals(2, result.size(), "Ожидались записи только для URI /events и /events/1");
        assertTrue(result.stream().allMatch(r -> List.of("/events", "/events/1").contains(r.getUri())),
                "Все URI должны быть из фильтра");
    }
}