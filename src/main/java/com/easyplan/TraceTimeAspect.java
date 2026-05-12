package com.easyplan;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile("test")
public class TraceTimeAspect {
	private static final Logger log = LoggerFactory.getLogger(TraceTimeAspect.class);
	
	@Around("@annotation(com.easyplan._shared.annotation.TraceTime)")
	public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.nanoTime();
		
		try {
			return joinPoint.proceed();
		} finally {
			long elapsedMs = (System.nanoTime() - start) / 1_000_000;
			
			log.info(
					"{TIME_TRACE} {}.{} took {} ms",
					joinPoint.getSignature().getDeclaringType().getSimpleName(),
					joinPoint.getSignature().getName(),
					elapsedMs
			);
		}
	}
}
