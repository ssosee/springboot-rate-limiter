package seaung.ratelimiter.aop;

import org.aspectj.lang.annotation.Pointcut;

public class AppPointCuts {

    @Pointcut("execution(* *..*Controller.*(..))")
    public void allController() {}
}
