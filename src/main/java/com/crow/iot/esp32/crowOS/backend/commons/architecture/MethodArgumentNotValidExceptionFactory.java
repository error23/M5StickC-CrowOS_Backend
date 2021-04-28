package com.crow.iot.esp32.crowOS.backend.commons.architecture;

import com.crow.iot.esp32.crowOS.backend.commons.I18nHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * @author : error23
 * Created : 01/07/2020
 */
public class MethodArgumentNotValidExceptionFactory {

	/**
	 * Creates MethodArgumentNotValidException for not null assert
	 *
	 * @param o     to create for
	 * @param field to create for
	 * @return MethodArgumentNotValidException
	 */
	@NotNull
	@Contract ("_, _ -> new")
	public static MethodArgumentNotValidException NOT_NULL(Object o, String... field) {

		BeanPropertyBindingResult result = new BeanPropertyBindingResult(o, o.getClass().getSimpleName());

		for (String f : field) {
			result.addError(new FieldError(o.getClass().getSimpleName(), f, I18nHelper.getI18n().tr("must not be null")));
		}

		return new MethodArgumentNotValidException(
			new MethodParameter(o.getClass().getConstructors()[0], - 1),
			result
		);
	}
}
