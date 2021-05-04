package com.crow.iot.esp32.crowOS.backend.commons.architecture.dto;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author : error23
 * Created : 05/06/2020
 */
@SpringBootTest
class AbstractDtoTest {

	private static final String[] FIELDS = new String[] { "myString", "myInt", "myInteger", "myBool", "myBoolean" };

	@Getter
	@Setter
	private static class TestClass extends AbstractDto {

		private String myString;
		private int myInt;
		private Integer myInteger;
		private boolean myBool;
		private Boolean myBoolean;

	}

	@Test
	void whenIsChangedCalledWithUnknownName_thanThrowRuntimeException() {

		assertThrows(RuntimeException.class, () -> new TestClass().isChanged("unknown"));
	}

	@Test
	@SuppressWarnings ("unchecked")
	void whenClassIsCreated_thanFieldsAreInitialized() {

		TestClass testClass = new TestClass();

		List<String> fieldsName = (List<String>) ReflectionTestUtils.getField(testClass, "fieldsName");
		List<String> changeLog = (List<String>) ReflectionTestUtils.getField(testClass, "changeLog");

		assertThat(fieldsName).containsAll(List.of(FIELDS));
		assertThat(changeLog).isNotNull();
	}

	@Test
	void whenValuesIsSet_thanIsChangedIsTrue() {

		TestClass testClass = new TestClass();
		testClass.setMyString("string");
		testClass.setMyInt(1);
		testClass.setMyInteger(2);
		testClass.setMyBool(true);
		testClass.setMyBoolean(false);

		assertThat("string").isEqualTo(testClass.getMyString());
		assertThat(1).isEqualTo(testClass.getMyInt());
		assertThat(2).isEqualTo(testClass.getMyInteger());
		assertThat(testClass.isMyBool()).isTrue();
		assertThat(testClass.getMyBoolean()).isFalse();

		// Assert that everything changed
		assertThat(testClass.isAnyFieldChanged()).isTrue();
		for (String fieldName : FIELDS) {
			assertThat(testClass.isChanged(fieldName)).isTrue();
		}
	}

	@Test
	void whenNullValuesAreSet_thanIsChangedIsTrue_ElseIfNoValueIsSet_thanIsChangedIsFalse() {

		TestClass testClass = new TestClass();
		testClass.setMyString(null);
		testClass.setMyInteger(null);
		testClass.setMyBoolean(null);

		assertThat(testClass.getMyString()).isNull();
		assertThat(testClass.getMyInteger()).isNull();
		assertThat(testClass.getMyBoolean()).isNull();

		assertThat(testClass.isAnyFieldChanged()).isTrue();
		assertThat(testClass.isChanged("myString")).isTrue();
		assertThat(testClass.isChanged("myInt")).isFalse();
		assertThat(testClass.isChanged("myInteger")).isTrue();
		assertThat(testClass.isChanged("myBool")).isFalse();
		assertThat(testClass.isChanged("myBoolean")).isTrue();
	}

	@Test
	void whenValuesNotSet_thanIsChangedIsFalse() {

		TestClass testClass = new TestClass();

		// Check nothing changed
		for (String fieldName : FIELDS) {
			assertThat(testClass.isChanged(fieldName)).isEqualTo(false);
		}
		assertThat(testClass.isAnyFieldChanged()).isFalse();
	}

}
