package seaung.ratelimiter.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AdviceController {
    @ExceptionHandler(RateLimiterException.class)
    public ResponseEntity<String> rateLimiterException(RateLimiterException e) {
        log.error("예외 발생 ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }
}
