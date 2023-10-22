package seaung.ratelimiter.exception;

public class RateLimiterException extends IllegalArgumentException {

    public static final String TOO_MANY_REQUEST = "너무 많은 요청을 보냈습니다.";
    public static final String NOT_FOUND = "처리율 제한 장치 플랜을 찾을 수 없습니다.";

    public RateLimiterException() {
        super();
    }

    public RateLimiterException(String s) {
        super(s);
    }

    public RateLimiterException(String message, Throwable cause) {
        super(message, cause);
    }

    public RateLimiterException(Throwable cause) {
        super(cause);
    }
}
