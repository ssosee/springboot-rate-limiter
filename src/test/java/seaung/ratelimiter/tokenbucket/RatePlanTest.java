package seaung.ratelimiter.tokenbucket;

import static org.junit.jupiter.api.Assertions.*;

import io.github.bucket4j.Bandwidth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seaung.ratelimiter.exception.RateLimiterException;

import static org.assertj.core.api.Assertions.*;

class RatePlanTest {

    @Test
    @DisplayName("설정한 정책에 맞는 처리율 제한 장치 정책을 반환한다.")
    void resolvePlan() {
        // given
        String testPlan = "test";
        String localPlan = "local";

        // when
        Bandwidth testBandwidth = RatePlan.resolvePlan(testPlan);
        Bandwidth localBandwidth = RatePlan.resolvePlan(localPlan);

        // then
        assertAll(
                () -> assertThat(testBandwidth).isEqualTo(RatePlan.TEST.getLimit()),
                () -> assertThat(localBandwidth).isEqualTo(RatePlan.LOCAL.getLimit())
        );
    }

    @Test
    @DisplayName("설정한 정책이 처리율 제한 장치에 없으면 예외가 발생한다.")
    void resolvePlanException() {
        // given
        String myPlan = "myPlan";

        // when // then
        assertThatThrownBy(() -> RatePlan.resolvePlan(myPlan))
                .isInstanceOf(RateLimiterException.class)
                .hasMessage(RateLimiterException.NOT_FOUND);
    }
}