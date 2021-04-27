package com.crow.iot.esp32.crowOS.backend.commons.json;

import com.crow.iot.esp32.crowOS.backend.commons.CommonTools;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.AbstractDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author : error23
 * Created : 06/06/2020
 */
@SpringBootTest
class JsonHelperTest {

	SimpleDto simpleDto;
	String jsonStringOriginal = "{\"myString\":\"string\",\"myInt\":1,\"myInteger\":2,\"myBool\":true,\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"}";
	String jsonStringNullValues = "{\"myString\":null,\"myInt\":1,\"myInteger\":2,\"myBool\":true,\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"}";
	String jsonStringMissingValues = "{\"myInt\":1,\"myInteger\":2,\"myBool\":true,\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"}";
	String jsonStringUnknownValues = "{\"unknownValue\":1,\"myString\":\"string\",\"myInt\":1,\"myInteger\":2,\"myBool\":true,\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"}";
	String jsonStringNoQuotes = "{myString:\"string\",myInt:1,myInteger:2,\"myBool\":true,\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"}";
	String jsonStringListSimple = "[1,2,3,4]";
	String jsonStringListObject = "[{\"myString\":\"string\",\"myInt\":1,\"myInteger\":2,\"myBool\":true,\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"},{\"myString\":\"string\",\"myInt\":1,\"myInteger\":2,\"myBool\":true,\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"},{\"myString\":\"string\",\"myInt\":1,\"myInteger\":2,\"myBool\":true,\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"},{\"myString\":\"string\",\"myInt\":1,\"myInteger\":2,\"myBool\":true," +
		"\"myBoolean\":false," +
		"\"date\":\"2020-06-06T17:39:05Z\"},{\"myString\":\"string\",\"myInt\":1,\"myInteger\":2,\"myBool\":true,\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"},{\"myString\":\"string\",\"myInt\":1,\"myInteger\":2,\"myBool\":true,\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"},{\"myString\":\"string\",\"myInt\":1,\"myInteger\":2,\"myBool\":true,\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"},{\"myString\":\"string\",\"myInt\":1,\"myInteger\":2,\"myBool\":true," +
		"\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"},{\"myString\":\"string\",\"myInt\":1,\"myInteger\":2,\"myBool\":true,\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"},{\"myString\":\"string\",\"myInt\":1,\"myInteger\":2,\"myBool\":true,\"myBoolean\":false,\"date\":\"2020-06-06T17:39:05Z\"}]";

	@Getter
	@Setter
	private static class SimpleDto extends AbstractDto {

		private String myString;
		private int myInt;
		private Integer myInteger;
		private boolean myBool;
		private Boolean myBoolean;
		private Date date;

	}

	@BeforeEach
	void setUp() throws ParseException {

		this.simpleDto = new SimpleDto();

		this.simpleDto.setMyString("string");
		this.simpleDto.setMyInt(1);
		this.simpleDto.setMyInteger(2);
		this.simpleDto.setMyBool(true);
		this.simpleDto.setMyBoolean(false);
		this.simpleDto.setDate(CommonTools.getFullDateFormat().parse("2020-06-06T17:39:05Z"));
	}

	@Test
	void whenConvertingObjectToString_thanConvertObjectToString() {

		String jsonString = JsonHelper.fromObjectToString(this.simpleDto);
		assertThat(jsonString).isEqualTo(this.jsonStringOriginal);
	}

	@Test
	void whenConvertingObjectToStringWithNullValues_thanIgnoreNullValuesInJson() {

		this.simpleDto.setMyString(null);
		String jsonString = JsonHelper.fromObjectToString(this.simpleDto);
		assertThat(jsonString).isEqualTo(this.jsonStringMissingValues);
	}

	@Test
	void whenConvertingFromStringToObject_thanConvertFromStringToObject() {

		SimpleDto dto = JsonHelper.fromStringToObject(this.jsonStringOriginal, SimpleDto.class);
		assertThat(dto).isEqualToComparingFieldByField(this.simpleDto);
	}

	@Test
	void whenConvertingFromStringWithNullValuesToObject_thanSetNullValues() throws ParseException {

		this.simpleDto = new SimpleDto();

		this.simpleDto.setMyString(null);
		this.simpleDto.setMyInt(1);
		this.simpleDto.setMyInteger(2);
		this.simpleDto.setMyBool(true);
		this.simpleDto.setMyBoolean(false);
		this.simpleDto.setDate(CommonTools.getFullDateFormat().parse("2020-06-06T17:39:05Z"));

		SimpleDto dto = JsonHelper.fromStringToObject(this.jsonStringNullValues, SimpleDto.class);

		assertThat(dto).isEqualToComparingFieldByField(this.simpleDto);
		assertThat(dto.getMyString()).isNull();
		assertThat(dto.isChanged("myString")).isTrue();
		assertThat(dto.isChanged("myInt")).isTrue();
		assertThat(dto.isChanged("myInteger")).isTrue();
		assertThat(dto.isChanged("myBool")).isTrue();
		assertThat(dto.isChanged("myBoolean")).isTrue();
		assertThat(dto.isChanged("date")).isTrue();

	}

	@Test
	void whenConvertingFromStringWithMissingValuesToObject_thanIgnoreMissingValues() throws ParseException {

		this.simpleDto = new SimpleDto();

		this.simpleDto.setMyInt(1);
		this.simpleDto.setMyInteger(2);
		this.simpleDto.setMyBool(true);
		this.simpleDto.setMyBoolean(false);
		this.simpleDto.setDate(CommonTools.getFullDateFormat().parse("2020-06-06T17:39:05Z"));

		SimpleDto dto = JsonHelper.fromStringToObject(this.jsonStringMissingValues, SimpleDto.class);

		assertThat(dto).isEqualToComparingFieldByField(this.simpleDto);
		assertThat(dto.getMyString()).isNull();
		assertThat(dto.isChanged("myString")).isFalse();
		assertThat(dto.isChanged("myInt")).isTrue();
		assertThat(dto.isChanged("myInteger")).isTrue();
		assertThat(dto.isChanged("myBool")).isTrue();
		assertThat(dto.isChanged("myBoolean")).isTrue();
		assertThat(dto.isChanged("date")).isTrue();
	}

	@Test
	void whenConvertingFromStringWithUnknownValues_thanIgnoreUnknownValues() {

		SimpleDto dto = JsonHelper.fromStringToObject(this.jsonStringUnknownValues, SimpleDto.class);
		assertThat(dto).isEqualToComparingFieldByField(this.simpleDto);

	}

	@Test
	void whenConvertingFromStringWithoutQuotes_thanDontFail() {

		SimpleDto dto = JsonHelper.fromStringToObject(this.jsonStringNoQuotes, SimpleDto.class);
		assertThat(dto).isEqualToComparingFieldByField(this.simpleDto);

	}

	@Test
	void whenConvertingFromObjectToNode_thanConvertFromObjectToNode() {

		JsonNode node = JsonHelper.fromObjectToNode(this.simpleDto);

		assertThat(node.get("myString").asText()).isEqualTo(this.simpleDto.getMyString());
		assertThat(node.get("myInt").asInt()).isEqualTo(this.simpleDto.getMyInt());
		assertThat(node.get("myInteger").asInt()).isEqualTo(this.simpleDto.getMyInteger());
		assertThat(node.get("myBool").asBoolean()).isEqualTo(this.simpleDto.isMyBool());
		assertThat(node.get("myBoolean").asBoolean()).isEqualTo(this.simpleDto.getMyBoolean());
		assertThat(node.get("date").asText()).isEqualTo("2020-06-06T17:39:05Z");

	}

	@Test
	void whenConvertingFromNodeToObject_thanConvertFromNodeToObject() {

		JsonNode node = JsonHelper.fromObjectToNode(this.simpleDto);
		SimpleDto dto = JsonHelper.fromNodeToObject(node, SimpleDto.class);
		assertThat(this.simpleDto).isEqualToComparingFieldByField(dto);
	}

	@Test
	void whenConvertingSimpleListToJsonString_thanConvertSimpleListToJsonString() {

		List<Integer> list = List.of(1, 2, 3, 4);

		String jsonString = JsonHelper.fromObjectToString(list);
		assertThat(this.jsonStringListSimple).isEqualTo(jsonString);
	}

	@Test
	void whenConvertingSimpleJsonStringListToObject_thanConvertToObject() {

		List<Integer> list = JsonHelper.fromStringToObject(this.jsonStringListSimple, TypeFactory.defaultInstance().constructCollectionType(List.class, Integer.class));
		assertThat(list).isEqualTo(List.of(1, 2, 3, 4));

	}

	@Test
	void whenConvertingObjectListToJsonString_thanConvertObjectsToJsonStringList() throws ParseException {

		List<SimpleDto> dtos = new ArrayList<>();
		for (int i = 0; i < 10; i++) {

			SimpleDto dto = new SimpleDto();

			dto.setMyString("string");
			dto.setMyInt(1);
			dto.setMyInteger(2);
			dto.setMyBool(true);
			dto.setMyBoolean(false);
			dto.setDate(CommonTools.getFullDateFormat().parse("2020-06-06T17:39:05Z"));

			dtos.add(dto);
		}

		String json = JsonHelper.fromObjectToString(dtos);
		assertThat(this.jsonStringListObject).isEqualTo(json);

	}

	@Test
	void whenConvertingJsonStringListObject_thanConvertToObjectList() throws ParseException {

		List<SimpleDto> expectedDtos = new ArrayList<>();
		for (int i = 0; i < 10; i++) {

			SimpleDto dto = new SimpleDto();

			dto.setMyString("string");
			dto.setMyInt(1);
			dto.setMyInteger(2);
			dto.setMyBool(true);
			dto.setMyBoolean(false);
			dto.setDate(CommonTools.getFullDateFormat().parse("2020-06-06T17:39:05Z"));

			expectedDtos.add(dto);
		}

		List<SimpleDto> dtos = JsonHelper.fromStringToObject(this.jsonStringListObject, TypeFactory.defaultInstance().constructCollectionType(List.class, SimpleDto.class));

		for (int i = 0; i < 10; i++) {
			assertThat(expectedDtos.get(i)).isEqualToComparingFieldByField(dtos.get(i));
		}

	}

	@Test
	void whenConvertingListToJsonNode_thanConvertListToJsonNode() {

		JsonNode node = JsonHelper.fromObjectToNode(List.of(1, 2, 3, 4));

		for (int i = 0; i < 4; i++) {
			assertThat(i + 1).isEqualTo(node.get(i).asInt());
		}

		List<Integer> list = JsonHelper.fromNodeToObjectList(node, Integer.class);

		assertThat(list).isEqualTo(List.of(1, 2, 3, 4));

	}
}
