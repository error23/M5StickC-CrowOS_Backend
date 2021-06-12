package com.crow.iot.esp32.crowOS.backend.commons;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jetbrains.annotations.NotNull;

/**
 * @author : error23
 * Created : 12/06/2021
 */
@Aspect
@Slf4j
public class LogAspect {

	@Before ("execution(* com.crow..*(..))")
	public void logMethod(@NotNull JoinPoint joinPoint) {

		log.trace("Method {} executed with {} params", joinPoint.getSignature().toShortString(), joinPoint.getArgs());

	}

}
