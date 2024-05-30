package students.javabot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Aspect
@Component
public class ServiceLogsAspect {

    @Pointcut("execution(public * students.javabot.Service.*.*(..))")
    public void callService() {
    }

    @Before("callService()")
    public void beforeCallService(JoinPoint joinPoint) {
        List<String> args = Arrays.stream(joinPoint.getArgs())
                .map(Object::toString)
                .toList();

        log.info("Call {} with args {}", joinPoint.getSignature().getName(), args);
    }

    @AfterReturning(value = "callService()", returning = "object")
    public void afterCallService(JoinPoint joinPoint, ResponseEntity<?> object) {
        log.info("Call {} returned {}", joinPoint.getSignature().getName(), object.getBody());
    }
}
