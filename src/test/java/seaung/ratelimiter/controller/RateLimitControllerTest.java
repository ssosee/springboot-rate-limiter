package seaung.ratelimiter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@AutoConfigureMockMvc
class RateLimitControllerTest {

    @Test
    @DisplayName("정상응답 확인")
    void rateLimitController() throws Exception {
        // given
        RestTemplate restTemplate = new RestTemplate();

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(new URI("http://localhost:8080/rateLimiter/1"), String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("rateLimiter 예 확인")
    void rateLimitControllerException() throws Exception {
        // given
        RestTemplate restTemplate = new RestTemplate();

        // when
        for(int i = 0; i < 5; i++) {
            restTemplate.getForEntity(new URI("http://localhost:8080/rateLimiter/1"), String.class);
        }

        // then
        assertThatThrownBy(() -> restTemplate.getForEntity(new URI("http://localhost:8080/rateLimiter/1"), String.class))
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessage("too many requests");
    }
}