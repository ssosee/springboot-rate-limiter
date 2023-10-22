package seaung.ratelimiter.tokenbucket;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import java.time.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import seaung.ratelimiter.exception.RateLimiterException;

@Getter
@RequiredArgsConstructor
public enum RatePlan {

    TEST("test") {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        }
    },
    LOCAL("local") {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        }
    };

    public abstract Bandwidth getLimit();

    private final String planName;

    public static Bandwidth resolvePlan(String targetPlan) {
        if(targetPlan.equals(TEST.getPlanName())) return TEST.getLimit();
        else if(targetPlan.equals(LOCAL.getPlanName())) return LOCAL.getLimit();

        throw new RateLimiterException(RateLimiterException.NOT_FOUND);
    }
}
