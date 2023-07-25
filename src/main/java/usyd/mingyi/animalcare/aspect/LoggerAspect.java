package usyd.mingyi.animalcare.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.utils.BaseContext;

@Component
@Aspect
@Slf4j
public class LoggerAspect {

    @Pointcut("execution( * usyd.mingyi.animalcare.controller.*.*(..))")
    public void pointCut(){

    }


    @Around("pointCut()")
    public Object myLogger(ProceedingJoinPoint pjp) throws Throwable {
        String className = pjp.getTarget().getClass().getName();
        String methodName = pjp.getSignature().getName();
        Object[] args = pjp.getArgs();
        log.info("开始日志");
        long start = System.currentTimeMillis();
        Object object = pjp.proceed();
        long end = System.currentTimeMillis();
        long time = end-start;
        log.info("这个方法{}执行了{}毫秒",methodName,time);
        log.info("结束日志");
        return object;
    }

}
