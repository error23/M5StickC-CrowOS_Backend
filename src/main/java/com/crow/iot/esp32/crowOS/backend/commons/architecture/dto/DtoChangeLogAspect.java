package com.crow.iot.esp32.crowOS.backend.commons.architecture.dto;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.jetbrains.annotations.NotNull;

/**
 * @author : error23
 * Created : 21/05/2020
 */
@Aspect
public class DtoChangeLogAspect {

	@After ("execution(* com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.AbstractDto+.set*(*))")
	public void afterSetterCall(@NotNull JoinPoint joinPoint) {

		String methodName = joinPoint.getSignature().getName();
		Object object = joinPoint.getThis();

		String upperCaseAttribute = methodName.replaceAll("^set", "");
		String firstLetter = upperCaseAttribute.substring(0, 1).toLowerCase();
		String restLetters = upperCaseAttribute.substring(1);
		String attribute = firstLetter + restLetters;

		AbstractDto dto = (AbstractDto) object;
		dto.addToChangeLog(attribute);

	}

}
