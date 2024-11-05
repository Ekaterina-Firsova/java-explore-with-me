package ru.practicum.ewm.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StatsClientTest {

//    @Mock
//    private RestTemplate restTemplate;
//
//    @InjectMocks
//    private StatsClient statsClient;
//
//    private final String baseUrl = "http://localhost:9090";
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        statsClient = new StatsClient(restTemplate, baseUrl);
//    }
//
//    @Test
//    void testSendHit_Success() {
//        EndpointHitDto hitDto = new EndpointHitDto("app", LocalDateTime.now(), "/endpoint", "127.0.0.1" );
//        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/hit").toUriString();
//
//        when(restTemplate.postForEntity(eq(url), eq(hitDto), eq(Void.class)))
//                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));
//
//        statsClient.sendHit(hitDto);
//
//        verify(restTemplate, times(1)).postForEntity(eq(url), eq(hitDto), eq(Void.class));
//    }
//
//    @Test
//    void testSendHit_Failure() {
//        EndpointHitDto hitDto = new EndpointHitDto("app", LocalDateTime.now(), "/endpoint", "127.0.0.1");
//        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/hit").toUriString();
//
//        when(restTemplate.postForEntity(eq(url), eq(hitDto), eq(Void.class)))
//                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
//
//        statsClient.sendHit(hitDto);
//
//        verify(restTemplate, times(1)).postForEntity(eq(url), eq(hitDto), eq(Void.class));
//    }
//
//    @Test
//    void testGetStats_Success() {
//        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
//        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 0, 0);
//        List<String> uris = List.of("/test1", "/test2");
//        boolean unique = true;
//
//        String startEncoded = statsClient.getDateTimeFormatter().format(start).replace(" ", "%20");
//        String endEncoded = statsClient.getDateTimeFormatter().format(end).replace(" ", "%20");
//        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/stats")
//                .queryParam("start", startEncoded)
//                .queryParam("end", endEncoded)
//                .queryParam("unique", unique)
//                .queryParam("uris", "/test1")
//                .queryParam("uris", "/test2")
//                .toUriString();
//
//        List<EndpointHitDto> expectedResponse = List.of(new EndpointHitDto("app", LocalDateTime.now(), "/endpoint", "127.0.0.1"));
//        ResponseEntity<List<EndpointHitDto>> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
//
//        when(restTemplate.getForEntity(eq(url), any(Class.class))).thenReturn(responseEntity);
//
//        List<ViewStatsDto> actualResponse = statsClient.getStats(start, end, uris, unique);
//
//        assertEquals(expectedResponse, actualResponse);
//        verify(restTemplate, times(1)).getForEntity(eq(url), any(Class.class));
//    }

//    @Test
//    void testGetStats_Failure() {
//        // Arrange
//        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
//        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 0, 0);
//        List<String> uris = List.of("/test1", "/test2");
//        boolean unique = true;
//
//            String startEncoded = statsClient.getDateTimeFormatter().format(start).replace(" ", "%20");
//            String endEncoded = statsClient.getDateTimeFormatter().format(end).replace(" ", "%20");
//        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/stats")
//                .queryParam("start", startEncoded)
//                .queryParam("end", endEncoded)
//                .queryParam("unique", unique)
//                .queryParam("uris", "/test1")
//                .queryParam("uris", "/test2")
//                .toUriString();
//
//        when(restTemplate.getForEntity(eq(url), any(Class.class)))
//                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
//
//        // Act
//        List<EndpointHitDto> actualResponse = statsClient.getStats(start, end, uris, unique);
//
//        // Assert
//        assertTrue(actualResponse.isEmpty());
//        verify(restTemplate, times(1)).getForEntity(eq(url), any(Class.class));
//    }
}