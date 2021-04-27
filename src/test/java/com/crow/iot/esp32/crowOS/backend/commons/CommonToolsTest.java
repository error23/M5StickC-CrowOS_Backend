package com.crow.iot.esp32.crowOS.backend.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author : error23
 * Created : 06/06/2020
 */
class CommonToolsTest {

	private static final String FULL_DATE = "2020-06-06T17:39:05Z";

	private static final String FR_FULL_HUMAN_DATE = "06/06/2020 17:39";
	private static final String EN_FULL_HUMAN_DATE = "6/6/20, 4:39 PM";
	private static final String CN_FULL_HUMAN_DATE = "2020/6/6 上午10:39";

	private static final String ONLY_DAY = "2020-06-06";

	private static final String FR_ONLY_DAY_HUMAN_DATE = "06/06/2020";
	private static final String EN_ONLY_DAY_HUMAN_DATE = "6/6/20";
	private static final String CN_ONLY_DAY_HUMAN_DATE = "2020/6/6";

	private static final String ONLY_HOUR = "17:39";

	private static final String FR_ONLY_HOUR_HUMAN_DATE = "17:39";
	private static final String EN_ONLY_HOUR_HUMAN_DATE = "4:39 PM";
	private static final String CN_ONLY_HOUR_HUMAN_DATE = "上午10:39";

	private static final long TIMESTAMP = 1591457945000L;
	private static final Date DATE = new Date(TIMESTAMP);

	private static class TestClass {

		@Setter
		private Integer toDelete;

		@Setter
		@JsonProperty ("someValue")
		private List<Integer> integers;

		public static Integer add(Integer a, Integer b) {

			if (a == null || b == null) return null;
			return a + b;
		}

		public Integer del(Integer a, Integer b) {

			if (a == null || b == null) return null;
			return a - b - this.toDelete;
		}
	}

	private static class TestChild extends TestClass {

	}

	@Test
	void whenFormattingFullDate_thanSuccess() {

		assertThat(CommonTools.formatFullDate(DATE)).isEqualTo(FULL_DATE);
		assertThat(CommonTools.formatFullDate(null)).isNull();
	}

	@Test
	void whenFormattingHumanFullDate_thanSuccess() {

		assertThat(CommonTools.formatHumanFullDate(DATE, Locale.FRANCE, TimeZone.getTimeZone("Europe/Paris"))).isEqualTo(FR_FULL_HUMAN_DATE);
		assertThat(CommonTools.formatHumanFullDate(DATE, Locale.ENGLISH, TimeZone.getTimeZone("Europe/London"))).isEqualTo(EN_FULL_HUMAN_DATE);
		assertThat(CommonTools.formatHumanFullDate(DATE, Locale.CHINESE, TimeZone.getTimeZone("CST"))).isEqualTo(CN_FULL_HUMAN_DATE);
		assertThat(CommonTools.formatHumanFullDate(null, null, null)).isNull();
		assertThat(CommonTools.formatHumanFullDate(DATE, null, null)).isEqualTo(FR_FULL_HUMAN_DATE);

	}

	@Test
	void whenFormattingOnlyDay_thanSuccess() {

		assertThat(CommonTools.formatOnlyDay(DATE)).isEqualTo(ONLY_DAY);
		assertThat(CommonTools.formatOnlyDay(null)).isNull();
	}

	@Test
	void whenFormattingHumanOnlyDat_thanSuccess() {

		assertThat(CommonTools.formatHumanOnlyDay(DATE, Locale.FRANCE, TimeZone.getTimeZone("Europe/Paris"))).isEqualTo(FR_ONLY_DAY_HUMAN_DATE);
		assertThat(CommonTools.formatHumanOnlyDay(DATE, Locale.ENGLISH, TimeZone.getTimeZone("Europe/London"))).isEqualTo(EN_ONLY_DAY_HUMAN_DATE);
		assertThat(CommonTools.formatHumanOnlyDay(DATE, Locale.CHINESE, TimeZone.getTimeZone("CST"))).isEqualTo(CN_ONLY_DAY_HUMAN_DATE);
		assertThat(CommonTools.formatHumanOnlyDay(null, null, null)).isNull();
		assertThat(CommonTools.formatHumanOnlyDay(DATE, null, null)).isEqualTo(FR_ONLY_DAY_HUMAN_DATE);
	}

	@Test
	void whenFormattingOnlyHour_thanSuccess() {

		assertThat(CommonTools.formatOnlyHour(DATE)).isEqualTo(ONLY_HOUR);
		assertThat(CommonTools.formatOnlyHour(null)).isNull();

	}

	@Test
	void whenFormattingHumanOnlyHour_thanSuccess() {

		assertThat(CommonTools.formatHumanOnlyHour(DATE, Locale.FRANCE, TimeZone.getTimeZone("Europe/Paris"))).isEqualTo(FR_ONLY_HOUR_HUMAN_DATE);
		assertThat(CommonTools.formatHumanOnlyHour(DATE, Locale.ENGLISH, TimeZone.getTimeZone("Europe/London"))).isEqualTo(EN_ONLY_HOUR_HUMAN_DATE);
		assertThat(CommonTools.formatHumanOnlyHour(DATE, Locale.CHINESE, TimeZone.getTimeZone("CST"))).isEqualTo(CN_ONLY_HOUR_HUMAN_DATE);
		assertThat(CommonTools.formatHumanOnlyHour(null, null, null)).isNull();
		assertThat(CommonTools.formatHumanOnlyHour(DATE, null, null)).isEqualTo(FR_ONLY_HOUR_HUMAN_DATE);
	}

	@Test
	void whenAddingProtocolToUrlThanSuccess() {

		String url = "crow.com?param=1";
		String expectedUrl = "http://crow.com?param=1";
		String sExpectedUrl = "https://crow.com?param=1";

		assertThat(CommonTools.addProtocolToUrl(null, Protocol.HTTP)).isNull();
		assertThat(CommonTools.addProtocolToUrl(null, Protocol.HTTPS)).isNull();
		assertThat(CommonTools.addProtocolToUrl("", Protocol.HTTPS)).isEmpty();
		assertThat(CommonTools.addProtocolToUrl(url, null)).isEqualTo(url);
		assertThat(CommonTools.addProtocolToUrl(url, Protocol.HTTP)).isEqualTo(expectedUrl);
		assertThat(CommonTools.addProtocolToUrl(url, Protocol.HTTPS)).isEqualTo(sExpectedUrl);
	}

	@Test
	void whenRemovingProtocolFromUrl_thanSuccess() {

		String url = "http://crow.com?param=1";
		String sUrl = "https://crow.com?param=1";
		String expectedUrl = "crow.com?param=1";

		assertThat(CommonTools.removeProtocolFromUrl(null)).isNull();
		assertThat(CommonTools.removeProtocolFromUrl("")).isEmpty();
		assertThat(CommonTools.removeProtocolFromUrl(url)).isEqualTo(expectedUrl);
		assertThat(CommonTools.removeProtocolFromUrl(sUrl)).isEqualTo(expectedUrl);
		assertThat(CommonTools.removeProtocolFromUrl(expectedUrl)).isEqualTo(expectedUrl);

	}

	@Test
	void whenGettingApplicationTypeFromExtensionNull_thanSuccess() {

		assertThat(CommonTools.fromExtensionToContentType("")).isEmpty();
		assertThat(CommonTools.fromExtensionToContentType(null)).isNull();
	}

	@Test
	void whenGettingFileExtension_thanSuccess() {

		String[] files = { "file.txt", "file.txt.exe", ".file.txt.pdf.jpg" };
		String[] extensions = { ".txt", ".exe", ".jpg" };

		for (int i = 0; i < 3; i++) {
			assertThat(CommonTools.getFileExtension(files[i])).isEqualTo(extensions[i]);
		}

		assertThat(CommonTools.getFileExtension(null)).isNull();
		assertThat(CommonTools.getFileExtension("")).isEmpty();
		assertThat(CommonTools.getFileExtension("noExtensionHere")).isNull();
	}

	@Test
	void whenCallingDateToGMT_thanSuccess() {

		assertThat(CommonTools.dateToGmt(DATE, TimeZone.getTimeZone("CNT"))).isEqualTo(new Date(1591474145000L));
		assertThat(CommonTools.dateToGmt(DATE, null)).isEqualTo(DATE);
		assertThat(CommonTools.dateToGmt(null, TimeZone.getTimeZone("CNT"))).isNull();

	}

	@Test
	void whenCallingDateToTimeZone_thanSuccess() {

		assertThat(CommonTools.dateToTimeZone(new Date(1591474145000L), TimeZone.getTimeZone("CNT"))).isEqualTo(DATE);
		assertThat(CommonTools.dateToTimeZone(DATE, null)).isEqualTo(DATE);
		assertThat(CommonTools.dateToTimeZone(null, TimeZone.getTimeZone("CNT"))).isNull();

	}

	@Test
	void whenInvokingStaticMethod_thanSuccess() {

		assertThat(CommonTools.invokeStaticMethod(TestClass.class, "add", new Class[] { Integer.class, Integer.class }, new Integer[] { 2, 2 })).isEqualTo(4);
		assertThat(CommonTools.invokeStaticMethod(TestClass.class, "add", new Class[] { Integer.class, Integer.class }, new Integer[] { 2, null })).isNull();

		assertThat(CommonTools.invokeStaticMethod(TestChild.class, "add", new Class[] { Integer.class, Integer.class }, new Integer[] { 2, 2 })).isEqualTo(4);
		assertThat(CommonTools.invokeStaticMethod(TestChild.class, "add", new Class[] { Integer.class, Integer.class }, new Integer[] { 2, null })).isNull();
	}

	@Test
	void whenInvokingMethod_thanSuccess() {

		TestClass testClass = new TestClass();
		testClass.setToDelete(2);

		assertThat(CommonTools.invokeMethod(TestClass.class, testClass, "del", new Class[] { Integer.class, Integer.class }, new Integer[] { 6, 2 })).isEqualTo(2);

		TestChild testChild = new TestChild();
		testChild.setToDelete(2);

		assertThat(CommonTools.invokeMethod(TestChild.class, testChild, "del", new Class[] { Integer.class, Integer.class }, new Integer[] { 6, 2 })).isEqualTo(2);

	}

	@Test
	void whenFindingPropertyClass_thanSuccess() throws NoSuchFieldException {

		assertThat(CommonTools.findPropertyClass(TestClass.class, "toDelete")).isEqualTo(Integer.class);
		assertThat(CommonTools.findPropertyClass(TestClass.class, "integers")).isEqualTo(Integer.class);

		assertThat(CommonTools.findPropertyClass(TestChild.class, "toDelete")).isEqualTo(Integer.class);
		assertThat(CommonTools.findPropertyClass(TestChild.class, "integers")).isEqualTo(Integer.class);
	}

	@Test
	void whenFindingPropertyAnnotation_thanSuccess() throws NoSuchFieldException {

		assertThat(CommonTools.findPropertyAnnotation(TestClass.class, "integers", JsonProperty.class).value()).isEqualTo("someValue");
		assertThat(CommonTools.findPropertyAnnotation(TestChild.class, "integers", JsonProperty.class).value()).isEqualTo("someValue");
		assertThat(CommonTools.findPropertyAnnotation(TestChild.class, "integers", Getter.class)).isNull();
	}
}
