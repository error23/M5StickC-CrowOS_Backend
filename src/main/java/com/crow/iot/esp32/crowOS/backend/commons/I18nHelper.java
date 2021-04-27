package com.crow.iot.esp32.crowOS.backend.commons;

import org.springframework.context.i18n.LocaleContextHolder;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author : error23
 * Created : 02/06/2020
 */
public class I18nHelper {

	private static Class<?> currentClass = new Object() {

	}.getClass().getEnclosingClass();

	private static HashMap<Locale, I18n> i18nByLocale = new HashMap<>();

	/**
	 * Gets {@link I18n} with local retrieved from {@link LocaleContextHolder}
	 *
	 * @return {@link I18n}
	 */
	public static I18n getI18n() {

		return getI18n(null);
	}

	/**
	 * Gets {@link I18n} for one locale.
	 *
	 * @param lc locale to get for
	 * @return {@link I18n}
	 */
	public static I18n getI18n(Locale lc) {

		if (lc == null) lc = LocaleContextHolder.getLocale();

		if (i18nByLocale.containsKey(lc)) {
			return i18nByLocale.get(lc);
		}
		else {
			I18n i18n = I18nFactory.getI18n(currentClass, lc);
			i18nByLocale.put(lc, i18n);
			return i18n;
		}
	}

}
