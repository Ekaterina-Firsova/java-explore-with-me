package ru.practicum.ewm.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.ewm.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.ewm.ViewStatsDto;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest
@ContextConfiguration(classes = StatsClientImpl.class)
@TestPropertySource(properties = "stats.server.uri=http://localhost:9090")
class StatsClientImplTest {

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private StatsClientImpl client;

    @Value("${stats.server.uri}") // Получаем базовый URL из свойств
    private String baseUrl;

    @BeforeEach
    void setUp() {
        mockServer.reset();
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    void testSaveHit() throws Exception {
        final String requestBody = "{\n" +
                "  \"app\": \"app\",\n" +
                "  \"uri\": \"/events/1\",\n" +
                "  \"ip\": \"192.163.0.1\",\n" +
                "  \"timestamp\": \"2023-11-04 11:01:00\"\n" +
                "}";

        LocalDateTime fixedDateTime = LocalDateTime.parse("2023-11-04 11:01:00", StatsClientImpl.getFormatter());
        final EndpointHitDto endpointHitDto =
                new EndpointHitDto("app", fixedDateTime, "/events/1", "192.163.0.1");
        final String url = baseUrl + "/hit";

        mockServer.expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody, true))
                .andRespond(withStatus(HttpStatus.CREATED));

        client.saveHit(endpointHitDto);
        mockServer.verify();
    }

    @Test
    void testSaveHitError() throws Exception {
        final String requestBody = "{\n" +
                "  \"app\": \"app\",\n" +
                "  \"uri\": \"/events/1\",\n" +
                "  \"ip\": \"192.163.0.1\",\n" +
                "  \"timestamp\": \"2023-11-04 11:01:00\"\n" +
                "}";

        LocalDateTime fixedDateTime = LocalDateTime.parse("2023-11-04 11:01:00", StatsClientImpl.getFormatter());
        final EndpointHitDto endpointHitDto =
                new EndpointHitDto("app", fixedDateTime, "/events/1", "192.163.0.1");
        final String url = baseUrl + "/hit";

        // Настраиваем ожидания для mockServer, чтобы он ответил с ошибкой
        mockServer.expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody, true))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)); // 500 ошибка

        client.saveHit(endpointHitDto);
        mockServer.verify();
    }

    @Test
    void testGetStatsSuccess() {
        LocalDateTime start = LocalDateTime.parse("2023-11-01 00:00:00", StatsClientImpl.getFormatter());
        LocalDateTime end = LocalDateTime.parse("2023-11-02 00:00:00", StatsClientImpl.getFormatter());
        List<String> uris = List.of("/events/1");

        // Ожидаемый ответ от сервера
        final String expectedJsonResponse = "[\n" +
                "  {\n" +
                "    \"app\": \"app\",\n" +
                "    \"uri\": \"/events/1\",\n" +
                "    \"hits\": 5\n" +
                "  }\n" +
                "]";

        mockServer.expect(requestTo("http://localhost:9090/stats?start=2023-11-01%2000:00:00&end=2023-11-02%2000:00:00&uris=/events/1&unique=true"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(expectedJsonResponse, MediaType.APPLICATION_JSON));

        List<ViewStatsDto> actualResponse = client.getStats(start, end, uris, true);

        assertThat(actualResponse).hasSize(1);
        assertThat(actualResponse.getFirst().getUri()).isEqualTo("/events/1");
        assertThat(actualResponse.getFirst().getHits()).isEqualTo(5);
    }

    @Test
    void testGetStatsError() {
        LocalDateTime start = LocalDateTime.parse("2023-11-01 00:00:00", StatsClientImpl.getFormatter());
        LocalDateTime end = LocalDateTime.parse("2023-11-02 00:00:00", StatsClientImpl.getFormatter());
        List<String> uris = List.of("/events/1");

        mockServer.expect(requestTo("http://localhost:9090/stats?start=2023-11-01%2000:00:00&end=2023-11-02%2000:00:00&uris=/events/1&unique=true"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        List<ViewStatsDto> actualResponse = client.getStats(start, end, uris, true);

        assertThat(actualResponse).isEmpty();
    }

}