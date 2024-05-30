package students.javabot.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ControllerLogsAspect {

    @Pointcut("execution(public * students.javabot.service.*())")
    public void 

}
