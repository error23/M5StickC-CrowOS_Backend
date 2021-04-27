package com.crow.iot.esp32.crowOS.backend.commons;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.i18n.LocaleContextHolder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author : error23
 * Created : 02/04/2020
 */
@Slf4j
public class CommonTools {

	/** Full date format */
	@Getter
	private static final SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	/** Year month day date format. */
	private static final SimpleDateFormat onlyDayFormat = new SimpleDateFormat("yyyy-MM-dd");
	/** Hour date format. */
	private static final SimpleDateFormat onlyHourFormat = new SimpleDateFormat("HH:mm");

	/**
	 * Forbid creation of common tools since everything in this class is static tool
	 */
	private CommonTools() {

		throw new IllegalStateException("Utility class cant be instantiated");

	}

	/**
	 * Formats a date in full date format.
	 *
	 * @param d date to be formatted
	 * @return formatted date string
	 */
	public static String formatFullDate(Date d) {

		if (d == null) return null;
		return fullDateFormat.format(d);
	}

	/**
	 * Formats a date to full local human readable date
	 *
	 * @param date     to format
	 * @param locale   to use if null it will use the one from {@link LocaleContextHolder}
	 * @param timeZone to be used
	 * @return full human readable local date
	 */
	public static String formatHumanFullDate(Date date, Locale locale, TimeZone timeZone) {

		if (date == null) return null;
		if (locale == null) locale = LocaleContextHolder.getLocale();

		FastDateFormat dateFormat = FastDateFormat.getDateTimeInstance(FastDateFormat.SHORT, FastDateFormat.SHORT, timeZone, locale);
		return dateFormat.format(date);
	}

	/**
	 * Formats a date, keeping year month and day.
	 *
	 * @param d date to be formatted
	 * @return formatted date string
	 */
	public static String formatOnlyDay(Date d) {

		if (d == null) return null;
		return onlyDayFormat.format(d);
	}

	/**
	 * Formats a date to  year month and day local human readable date
	 *
	 * @param date     to format
	 * @param locale   to use if null it will use the one from {@link LocaleContextHolder}
	 * @param timeZone to be used
	 * @return year month and day human readable local date
	 */
	public static String formatHumanOnlyDay(Date date, Locale locale, TimeZone timeZone) {

		if (date == null) return null;
		if (locale == null) locale = LocaleContextHolder.getLocale();

		FastDateFormat dateFormat = FastDateFormat.getDateInstance(FastDateFormat.SHORT, timeZone, locale);
		return dateFormat.format(date);
	}

	/**
	 * Formats a date, keeping only hour minute and seconds.
	 *
	 * @param d date to be formatted
	 * @return formatted date string
	 */
	public static String formatOnlyHour(Date d) {

		if (d == null) return null;
		return onlyHourFormat.format(d);
	}

	/**
	 * Formats a date to hour minute and seconds local human readable date
	 *
	 * @param date     to format
	 * @param locale   to use if null it will use the one from {@link LocaleContextHolder}
	 * @param timeZone to be used
	 * @return hour minute and seconds human readable local date
	 */
	public static String formatHumanOnlyHour(Date date, Locale locale, TimeZone timeZone) {

		if (date == null) return null;
		if (locale == null) locale = LocaleContextHolder.getLocale();

		FastDateFormat dateFormat = FastDateFormat.getTimeInstance(FastDateFormat.SHORT, timeZone, locale);
		return dateFormat.format(date);
	}

	/**
	 * Adds {@link Protocol to one url}
	 *
	 * @param url      without protocol
	 * @param protocol to add
	 * @return url with protocol
	 */
	public static String addProtocolToUrl(String url, Protocol protocol) {

		if (StringUtils.isBlank(url) || protocol == null) return url;

		return protocol + "://" + url;
	}

	/**
	 * Removes a protocol from one url
	 *
	 * @param url with protocol
	 * @return urlWithoutProtocol
	 */
	public static String removeProtocolFromUrl(String url) {

		if (StringUtils.isBlank(url)) return url;

		int indexOfProtocol = url.indexOf("://");
		if (indexOfProtocol == - 1) return url;
		return url.substring(indexOfProtocol + 3);
	}

	/**
	 * Transforms extension into contentType.
	 *
	 * @param extension to transform
	 * @return contentType
	 */
	public static String fromExtensionToContentType(String extension) {

		if (StringUtils.isBlank(extension)) return extension;
		switch (extension) {
			case ".pdf":
				return "application/pdf";
			case ".exe":
				return "application/octet-stream";
			case ".zip":
				return "application/zip";
			case ".doc":
				return "application/msword";
			case ".docx":
				return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			case ".xls":
				return "application/vnd.ms-excel";
			case ".xlsx":
				return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			case ".ppt":
				return "application/vnd.ms-powerpoint";
			case ".pptx":
				return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
			case ".gif":
				return "image/gif";
			case ".png":
				return "image/png";
			case ".jpeg":
			case ".jpg":
				return "image/jpeg";
			case ".xml":
				return "application/xml";
			case ".csv":
				return "application/csv";
			default:
				return "application/force-download";
		}
	}

	/**
	 * Gets file name extension
	 *
	 * @param path to get extension for
	 * @return extension of file
	 */
	@Nullable
	public static String getFileExtension(String path) {

		if (StringUtils.isBlank(path)) return path;
		if (! path.contains(".")) return null;

		String extension = path.substring(path.lastIndexOf("."));

		if (extension.length() > 5) log.warn("Extension of file {} looks to be longer than 5 characters", path);

		return extension;
	}

	/**
	 * Converts one date to GMT
	 *
	 * @param date     to convert
	 * @param timeZone actual date {@link TimeZone}
	 * @return date in GMT
	 */
	public static Date dateToGmt(Date date, TimeZone timeZone) {

		if (date == null) return null;
		if (timeZone == null) return date;
		return new Date(date.getTime() - getTimeZoneOffset(date, timeZone));
	}

	/**
	 * Converts one date from GMT to local date
	 *
	 * @param date     to convert
	 * @param timeZone local timezone
	 * @return date in local timezone
	 */
	public static Date dateToTimeZone(Date date, TimeZone timeZone) {

		if (date == null) return null;
		if (timeZone == null) return date;
		return new Date(date.getTime() + getTimeZoneOffset(date, timeZone));
	}

	/**
	 * Gets time zone offset of one date
	 *
	 * @param date     to get for
	 * @param timeZone of date
	 * @return offset
	 */
	private static int getTimeZoneOffset(Date date, @NotNull TimeZone timeZone) {

		int timeZoneOffset = timeZone.getRawOffset();

		if (timeZone.inDaylightTime(date)) {

			Date dstDate = new Date(date.getTime() - timeZone.getDSTSavings());
			if (timeZone.inDaylightTime(dstDate)) timeZoneOffset -= timeZone.getDSTSavings();
		}

		return timeZoneOffset;
	}

	/**
	 * Invokes static method of one class
	 *
	 * @param clazz      to invoke method for
	 * @param methodName name of method to invoke
	 * @param argTypes   method argument types
	 * @param args       method arguments
	 * @return method answer
	 */
	public static Object invokeStaticMethod(Class<?> clazz, String methodName, Class<?>[] argTypes, Object[] args) {

		return invokeMethod(clazz, null, methodName, argTypes, args);

	}

	/**
	 * Invokes one method of one class
	 *
	 * @param clazz      to invoke method for
	 * @param instance   instance of class to invoke method on
	 * @param methodName name of method to invoke
	 * @param argTypes   method argument types
	 * @param args       method arguments
	 * @return method answer
	 */
	public static Object invokeMethod(@NotNull Class<?> clazz, Object instance, String methodName, Class<?>[] argTypes, Object[] args) {

		try {
			// Find method and invoke it
			Method method = clazz.getDeclaredMethod(methodName, argTypes);
			method.setAccessible(true);
			return method.invoke(instance, args);
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
			// If not found than try to find it in superclass
			return invokeMethod(clazz.getSuperclass(), instance, methodName, argTypes, args);
		}
		catch (InvocationTargetException e) {
			throw (RuntimeException) e.getTargetException();
		}
	}

	/**
	 * Search for one property of class clazz
	 *
	 * @param clazz    to search for property
	 * @param property to search in clazz
	 * @return property class
	 */
	public static Class<?> findPropertyClass(Class<?> clazz, String property) throws NoSuchFieldException, SecurityException {

		if (clazz == null) return null;

		try {
			Field field = clazz.getDeclaredField(property);

			if (Collection.class.isAssignableFrom(field.getType())) {
				return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
			}

			return field.getType();
		}
		catch (NoSuchFieldException e) {
			if (clazz.getSuperclass() == null) throw e;
			return findPropertyClass(clazz.getSuperclass(), property);
		}

	}

	/**
	 * Finds one {@link Annotation} in one class on one property
	 *
	 * @param clazz           to search into
	 * @param property        of class to search into
	 * @param annotationClass to search for
	 * @return fount annotation
	 */
	public static <E extends Annotation> E findPropertyAnnotation(@NotNull Class<?> clazz, String property, Class<E> annotationClass) throws NoSuchFieldException, SecurityException {

		try {
			Field field = clazz.getDeclaredField(property);
			return field.getAnnotation(annotationClass);
		}
		catch (NoSuchFieldException e) {
			if (clazz.getSuperclass() == null) throw e;
			return findPropertyAnnotation(clazz.getSuperclass(), property, annotationClass);
		}
	}

	/**
	 * Casts object into one type
	 *
	 * @param toCast to cast
	 * @param type   type to cast to
	 * @return casted object
	 */
	public static Object cast(Object toCast, @NotNull Class<?> type) {

		try {

			// If object is already of good type, do nothing
			if (type.isInstance(toCast)) return toCast;

			// If is primitive value just cast it using (T)
			if (type.isPrimitive()) {
				return type.cast(toCast);
			}
			// If is Date instance parse iso formatted date
			else if (type.isInstance(new Date())) {

				try {
					return fullDateFormat.parse(toCast.toString());
				}
				catch (ParseException e) {
					return onlyDayFormat.parse(toCast.toString());
				}
			}
			// Else if other object use valueOf(String) method to cast it
			else {
				return type.getMethod("valueOf", String.class).invoke(null, toCast.toString());
			}
		}
		catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | ParseException e) {
			throw new RuntimeException("Error while casting values : " + e.getCause().getMessage());
		}
	}
}
