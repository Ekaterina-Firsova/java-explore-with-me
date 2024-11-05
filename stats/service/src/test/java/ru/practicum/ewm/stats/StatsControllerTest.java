package ru.practicum.ewm.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {

    private MockMvc mvc;

    @Mock
    private StatsService statsService;

    @InjectMocks
    private StatsController statsController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(statsController).build();
        objectMapper.registerModule(new JavaTimeModule()); // для поддержки LocalDateTime в JSON
    }

    @Test
    public void testAddHit() throws Exception {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("testApp");
        endpointHitDto.setUri("/test/uri");
        endpointHitDto.setIp("127.0.0.1");
        endpointHitDto.setTimestamp(LocalDateTime.now());

        String jsonContent = objectMapper.writeValueAsString(endpointHitDto);

        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated());

        verify(statsService).addHit(any(EndpointHitDto.class));
    }

    @Test
    public void testGetStats() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/test/uri");
        boolean unique = false;

        ViewStatsDto viewStatsDto = new ViewStatsDto();
        viewStatsDto.setUri("/test/uri");
        viewStatsDto.setHits(10L);

        List<ViewStatsDto> stats = List.of(viewStatsDto);
        when(statsService.getStats(start, end, uris, unique)).thenReturn(stats);

        mvc.perform(get("/stats")
                        .param("start", start.toString())
                        .param("end", end.toString())
                        .param("uris", uris.get(0))
                        .param("unique", String.valueOf(unique)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(stats)));

        verify(statsService).getStats(start, end, uris, unique);
    }
}