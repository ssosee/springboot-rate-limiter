package seaung.ratelimiter.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import seaung.ratelimiter.tokenbucket.TokenBucketResolver;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ControllerAspect {
    private final TokenBucketResolver tokenBucketResolver;

    @Around("seaung.ratelimiter.aop.AppPointCuts.allController() && args(id)")
    public Object doController(ProceedingJoinPoint joinPoint, String id) throws Throwable {
        try {
            log.info("[doController] 컨트롤러 호출 전 {}", joinPoint.getClass());
            // 처리율 제한 장치 실행
            tokenBucketResolver.checkBucketCounter(id);
        } catch (Exception e) {
            log.error("[doController] 예외 발생", e);
            throw e;
        }

        return joinPoint.proceed();
    }
}
