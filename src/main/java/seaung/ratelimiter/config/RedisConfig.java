package seaung.ratelimiter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@DependsOn({"embeddedRedisServerConfig"}) // 빈 초기화 순서 지정
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private int redisPort;

    /**
     * Multi-Thread 에서 Thread-Safe한 Redis 클라이언트로 netty에 의해 관리된다.
     * Sentinel, Cluster, Redis data model 같은 고급 기능들을 지원하며
     * 비동기 방식으로 요청하기에 TPS/CPU/Connection 개수와 응답속도 등 전 분야에서 Jedis 보다 뛰어나다.
     * 스프링 부트의 기본 의존성은 현재 Lettuce로 되어있다.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("RedisConnectionFactory 초기화");
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    /**
     * Redis data access code를 간소화 하기 위해 제공되는 클래스이다.
     * 주어진 객체들을 자동으로 직렬화/역직렬화 하며 binary 데이터를 Redis에 저장한다.
     * 기본설정은 JdkSerializationRedisSerializer 이다.
     */
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        log.info("RedisTemplate 초기화");
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }
}
