package seaung.ratelimiter.tokenbucket;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import seaung.ratelimiter.exception.RateLimiterException;

@Component
@RequiredArgsConstructor
public class TokenBucketResolver {

    private final BucketConfiguration bucketConfiguration;
    private final LettuceBasedProxyManager lettuceBasedProxyManager;

    private Bucket bucket(String key) {
        return lettuceBasedProxyManager.builder()
                .build(key.getBytes(), bucketConfiguration);
    }

    public boolean checkBucketCounter(String key) {
        Bucket bucket = bucket(key);
        if(!bucket.tryConsume(1)) {
            throw new RateLimiterException(RateLimiterException.TOO_MANY_REQUEST);
        }

        return true;
    }

    public long getAvailableTokens(String key) {
        return bucket(key).getAvailableTokens();
    }
}
