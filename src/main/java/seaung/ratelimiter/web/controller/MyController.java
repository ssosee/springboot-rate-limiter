package seaung.ratelimiter.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import seaung.ratelimiter.tokenbucket.TokenBucketResolver;

@RestController
@RequiredArgsConstructor
public class MyController {

    private final TokenBucketResolver tokenBucketResolver;

    @GetMapping("/hello/{id}")
    public String hello(@PathVariable String id) {
        long availableTokens = tokenBucketResolver.getAvailableTokens(id);
        return "hello world!, counter="+availableTokens;
    }
}
