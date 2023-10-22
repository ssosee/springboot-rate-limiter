package seaung.ratelimiter.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import seaung.ratelimiter.exception.RateLimiterException;

@SpringBootTest
@AutoConfigureMockMvc
class MyControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    RedisTemplate<?, ?> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("hello world를 요청한다.")
    void hello() throws Exception {
        // given
        String key = "1";

        // when // then
        mockMvc.perform(get("/hello/{id}", key)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string("hello world!"));
    }

    @Test
    @DisplayName("처리율 제한 장치의 정책을 위반하면 예외 응답을 준다.")
    void helloRateLimiterException() throws Exception {
        // given
        String key = "1";

        // when
        for(int i = 0; i < 100; i++) {
            mockMvc.perform(get("/hello/{id}", key)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("hello world!"));
        }

        // then
        mockMvc.perform(get("/hello/{id}", key)
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isTooManyRequests())
                .andExpect(content().string(RateLimiterException.TOO_MANY_REQUEST));
    }

}