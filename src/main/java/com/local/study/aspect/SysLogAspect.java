package com.local.study.aspect;


import com.local.study.annotation.AnnotationScanner;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

@Component
@Aspect
public class SysLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(SysLogAspect.class);

    private static Map<String,List<AnnotationScanner.ClassMetaData>> metaDataMap;

    //scan all @SysLog method
    static {
        Map<String, List<AnnotationScanner.ClassMetaData>> map = AnnotationScanner.scan();
        if (CollectionUtils.isEmpty(map)){
            logger.info("no @SysLog annotation found");
        }else {
            metaDataMap = map;
        }
    }

    @Pointcut("@annotation(com.local.study.annotation.SysLog)")
    public void cut(){}


    @Around("cut()")
    public Object saveLogs(ProceedingJoinPoint point){

        try {
            Class<?> clazz = point.getTarget().getClass();
            String name = clazz.getSimpleName();
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            List<AnnotationScanner.ClassMetaData> data = metaDataMap.get(name);
            Object[] args = point.getArgs();
            for (AnnotationScanner.ClassMetaData metaData: data){
                if (metaData.getMethodName().equals(method.getName())){
                    Parameter[] parameters = metaData.getParameterNames();
                    for (int i = 0; i<parameters.length; i++){
                        logger.info(name + ":"+parameters[i].getName()+args[i]);
                    }
                }
            }
            System.out.println("start");

            Object proceed = point.proceed();

            System.out.println("end");
            return proceed;
        } catch (Throwable throwable) {
            logger.error("save log error",throwable);
            return null;
        }
    }
}
