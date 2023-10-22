package seaung.ratelimiter.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

@Slf4j
@Configuration
@Profile({"test", "local"})
public class EmbeddedRedisServerConfig {

    @Value("${spring.redis.port}")
    private int redisPort;
    private RedisServer redisServer;

    @PostConstruct
    public void runRedis() throws IOException {
        int port = isRedisServerRunning() ? findAvailablePort() : redisPort;
        redisServer = new RedisServer(port);
        redisServer.start();
        log.info("내장 레디스 서버 실행(port: {})", port);
    }

    @PreDestroy
    public void stopRedis() {
        if(!ObjectUtils.isEmpty(redisServer)) {
            redisServer.stop();
            log.info("내장 레디스 서버 종료");
        }
    }

    /**
     * 내장 레디스 서버가 현재 실행중인지 확인
     */
    private boolean isRedisServerRunning() throws IOException {
        return isRunning(executeGrepProcessCommand(redisPort));
    }

    /**
     * 내 PC에서 사용가능한 port 조회
     */
    public int findAvailablePort() throws IOException {
        for(int port = 10000; port <= 65535; port++) {
            Process process = executeGrepProcessCommand(port);
            if(!isRunning(process)) return port;
        }

        throw new IllegalArgumentException("10000 ~ 65535 에서 사용가능한 port를 찾을 수 없습니다.");
    }

    /**
     * 해당 port를 사용중인 process를 확인하는 sh 실행
     * 참고로 윈도우에서는 안됩니다. 맥/리눅스에서만 가능합니다.
     * 윈도우에서 동일하게 사용하시려면 exe 프로세스 찾는 코드를 작성해야합니다.
     */
    private Process executeGrepProcessCommand(int port) throws IOException {
        String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
        String[] shell = {"/bin/sh", "-c", command};
        return Runtime.getRuntime().exec(shell);
    }

    /**
     * 해당 process가 실행 중인지 확인
     */
    private boolean isRunning(Process process) {
        String line;
        StringBuilder pidInfo = new StringBuilder();

        try(BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }
        } catch (Exception e) {
            log.error("isRunning ", e);
        }

        return !StringUtils.isEmpty(pidInfo.toString());
    }
}
