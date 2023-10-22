package seaung.ratelimiter.config;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import seaung.ratelimiter.tokenbucket.RatePlan;

@Slf4j
@Configuration
@DependsOn({"embeddedRedisServerConfig"}) // 빈 초기화 순서 지정
public class RateLimiterConfig {

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private int redisPort;
    @Value("${bucket.plan}")
    private String bucketPlan;

    @Bean
    public RedisClient redisClient() {
        return RedisClient.create(RedisURI.builder()
                .withHost(redisHost)
                .withPort(redisPort)
                .build());
    }

    @Bean
    public LettuceBasedProxyManager lettuceBasedProxyManager() {
        return LettuceBasedProxyManager
                .builderFor(redisClient())
                .withExpirationStrategy(ExpirationAfterWriteStrategy
                        .basedOnTimeForRefillingBucketUpToMax(Duration.ofSeconds(10))
                )
                .build();
    }

    @Bean
    public BucketConfiguration bucketConfiguration() {
        log.info("처리율 제한 장치 플랜={}", bucketPlan);
        return BucketConfiguration.builder()
                .addLimit(RatePlan.resolvePlan(bucketPlan))
                .build();
    }
}
