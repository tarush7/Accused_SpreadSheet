package com.cctns.apprehend.configuration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Copyright: NCRB.
 * Project Name: CCTNS 2.0
 * Class Name: LoggingAspect.java
 * Description:  Logging AOP to log every method necessary details.
 *
 * @author Ashwani
 * @version: v1.0
 * @since 2025 -06-22
 */
@Aspect
@Slf4j
@Component
public class LoggingAspect {

    /**
     * Application component methods.
     */
    @Pointcut("within(com.cctns..*)")
    public void applicationComponentMethods() {}

    /**
     * Log method details object.
     *
     * @param joinPoint the join point
     * @return the object
     * @throws Throwable the throwable
     */
    @Around("applicationComponentMethods()")
    public Object logMethodDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();

        // Log method entry
        log.info("Entering: {}", methodName);

        Object result;
        try {
            result = joinPoint.proceed(); // Execute method
        } catch (Throwable ex) {
            log.error("Exception in {}: {}", methodName, ex.getMessage());
            throw ex;
        }

        // Log execution time
        long endTime = System.currentTimeMillis();

        // Log method exit
        log.info("Exiting: {}, Execution Time: {}ms", methodName, (endTime - startTime));

        return result;
    }

    /**
     * Log method exception.
     *
     * @param joinPoint the join point
     * @param ex        the ex
     */
    @AfterThrowing(pointcut = "applicationComponentMethods()", throwing = "ex")
    public void logMethodException(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().toShortString();
        log.error("Exception in {}: {}", methodName, ex.getMessage());
    }

}

