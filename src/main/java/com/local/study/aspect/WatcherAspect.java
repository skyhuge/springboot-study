package com.local.study.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WatcherAspect {

    @Pointcut("@annotation(com.local.study.annotation.Watcher)")
    public void cut(){}

    @Around("cut()")
    public Object action(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("aspect process.");
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        System.out.println("cost: "+(System.currentTimeMillis()-start));
        return proceed;
    }
}
