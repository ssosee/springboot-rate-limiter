package seaung.ratelimiter.web.controller;

import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RateLimitController {

    private final RateLimitConfig rateLimitConfig;

    @GetMapping("/rateLimiter/{id}")
    public ResponseEntity<String> getInfo(@PathVariable String id) {
        Bucket bucket = rateLimitConfig.resolveBucket(id);

        if(!bucket.tryConsume(1)) {
            return new ResponseEntity<>("too many requests", HttpStatus.TOO_MANY_REQUESTS);
        }

        return new ResponseEntity<>("hello", HttpStatus.OK);
    }
}
