package com.crow.iot.esp32.crowOS.backend.commons.json;

import com.crow.iot.esp32.crowOS.backend.commons.CommonTools;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : error23
 * Created : 03/04/2020
 */
@Slf4j
public class JsonHelper {

	/** Singleton parser instance */
	@Getter
	private static final ObjectMapper parser = JsonMapper
		.builder()
		.serializationInclusion(JsonInclude.Include.NON_NULL)
		.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
		.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
		.defaultDateFormat(CommonTools.getFullDateFormat()).build();

	/**
	 * Converts one java object into json String
	 *
	 * @param toConvert object to convert
	 * @return converted  object
	 */
	public static String fromObjectToString(Object toConvert) {

		if (toConvert == null) return null;
		String result;

		try {
			result = JsonHelper.parser.writeValueAsString(toConvert);
		}
		catch (JsonProcessingException e) {
			log.error("Cannot parse value : {} StackTrace : {}", toConvert, e);
			throw new JsonException("Cannot parse value : " + toConvert, e);
		}

		return result;
	}

	/**
	 * Converts one json String into java object
	 *
	 * @param toConvert   json string to convert
	 * @param convertType class type to be converted into
	 * @return converted java object
	 */
	public static <T> T fromStringToObject(String toConvert, Class<T> convertType) {

		if (toConvert == null) return null;
		T result;

		try {
			result = JsonHelper.parser.readValue(toConvert, convertType);
		}
		catch (IOException e) {
			log.error("Cannot parse value : {} {} StackTrace : {}", convertType, toConvert, e);
			throw new JsonException("Cannot parse value : " + convertType + " " + toConvert, e);
		}

		return result;
	}

	/**
	 * Converts one json String into collection of java object
	 *
	 * @param toConvert             json string to convert
	 * @param collectionConvertType to convert into example <code>TypeFactory.defaultInstance().constructCollectionType(List.class, YourPojo.class)</code>
	 * @return converted java collection
	 */
	public static <T> T fromStringToObject(String toConvert, CollectionType collectionConvertType) {

		if (toConvert == null) return null;
		T result;

		try {
			result = JsonHelper.parser.readValue(toConvert, collectionConvertType);
		}
		catch (IOException e) {
			log.error("Cannot parse values : {} {} StackTrace : {}", collectionConvertType, toConvert, e);
			throw new JsonException("Cannot parse values : " + collectionConvertType + " " + toConvert, e);
		}

		return result;
	}

	/**
	 * Converts one java object to {@link JsonNode}
	 *
	 * @param toConvert object to convert
	 * @return converted json object
	 */
	public static JsonNode fromObjectToNode(Object toConvert) {

		if (toConvert == null) return null;
		return JsonHelper.parser.valueToTree(toConvert);

	}

	/**
	 * Converts one {@link JsonNode} into java object
	 *
	 * @param toConvert   to be converted
	 * @param convertType type of class to convert to
	 * @return converted java object
	 */
	public static <T> T fromNodeToObject(JsonNode toConvert, Class<T> convertType) {

		if (toConvert == null) return null;
		T result;

		try {
			result = JsonHelper.parser.treeToValue(toConvert, convertType);
		}
		catch (JsonProcessingException e) {
			log.error("Cannot parse value : {} {} StackTrace : {}", convertType, toConvert, e);
			throw new JsonException("Cannot parse value : " + convertType + " " + toConvert, e);
		}

		return result;

	}

	/**
	 * Converts one json into list of objects
	 *
	 * @param toConvert   json to convert
	 * @param convertType list type
	 * @return converted list with objects
	 */
	public static <T> List<T> fromNodeToObjectList(JsonNode toConvert, Class<T> convertType) {

		if (toConvert == null) return null;
		List<T> result = new ArrayList<>();

		for (JsonNode node : toConvert) {
			result.add(fromNodeToObject(node, convertType));
		}

		return result;
	}
}
