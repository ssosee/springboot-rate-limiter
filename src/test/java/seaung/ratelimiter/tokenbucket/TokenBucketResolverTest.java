package seaung.ratelimiter.tokenbucket;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import seaung.ratelimiter.exception.RateLimiterException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class TokenBucketResolverTest {

    @Autowired
    TokenBucketResolver tokenBucketResolver;

    @Autowired
    RedisTemplate<?, ?> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory()
                .getConnection()
                .flushAll();
    }

    @Test
    @DisplayName("처리율 제한 장치의 정책을 준수하면 true를 반환한다.")
    void checkBucketCounter() {
        // given
        String key = "1";

        // when
        boolean result = tokenBucketResolver.checkBucketCounter(key);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("남은 토큰의 갯수를 반환한다.")
    void getAvailableTokens() {
        // given
        String key = "1";

        // when
        long availableTokens = tokenBucketResolver.getAvailableTokens(key);

        // then
        assertThat(availableTokens).isEqualTo(100);
    }

    @Test
    @DisplayName("처리율 제한 장치의 정책을 위반하면 예외가 발생한다.")
    void checkBucketCounterException() {
        // given
        String key = "1";

        // when
        for(int i = 0; i < 100; i++) {
            tokenBucketResolver.checkBucketCounter(key);
        }

        // then
        assertThatThrownBy(() -> tokenBucketResolver.checkBucketCounter(key))
                .isInstanceOf(RateLimiterException.class)
                .hasMessage(RateLimiterException.TOO_MANY_REQUEST);
    }

    @Test
    @DisplayName("key 값에 따라서 처리율 제한 장치의 정책이 적용된다.")
    void checkBucketCounterAppliedAccordingToKey() {
        // given
        String key1 = "1";
        String key2 = "2";

        // when
        for(int i = 0; i < 100; i++) {
            tokenBucketResolver.checkBucketCounter(key1);
        }
        boolean key2Result = tokenBucketResolver.checkBucketCounter(key2);

        // then
        assertAll(
                () -> assertThatThrownBy(() -> tokenBucketResolver.checkBucketCounter(key1))
                        .isInstanceOf(RateLimiterException.class)
                        .hasMessage(RateLimiterException.TOO_MANY_REQUEST),
                () -> assertTrue(key2Result)
        );
    }
}