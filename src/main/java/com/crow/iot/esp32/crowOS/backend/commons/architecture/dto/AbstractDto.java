package com.crow.iot.esp32.crowOS.backend.commons.architecture.dto;

import com.crow.iot.esp32.crowOS.backend.commons.json.JsonHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : error23
 * Created : 30/03/2020
 */
public abstract class AbstractDto {

	/** Indicates changed fields in this dto filled up by DtoChangeLogAspect */
	private List<String> changeLog;

	/** List of field names of this dto */
	private List<String> fieldsName;

	/**
	 * Creates empty abstract dto and initialize filedNames and change log
	 */
	public AbstractDto() {

		this.changeLog = new ArrayList<>();
		this.fieldsName = new ArrayList<>();

		for (Field field : this.getClass().getDeclaredFields()) {
			this.fieldsName.add(field.getName());
		}
	}

	/**
	 * Indicates if one attribute has changed since the creation of dto
	 *
	 * @param attribute to check
	 * @return true if attribute setter has called
	 */
	public boolean isChanged(String attribute) {

		if (this.changeLog.contains(attribute)) return true;
		if (! this.fieldsName.contains(attribute)) throw new RuntimeException("field " + attribute + " unknown in " + this.getClass().getName());
		return false;
	}

	/**
	 * Indicates if any field was changed
	 *
	 * @return true if any of dto fields was changed
	 */
	@JsonIgnore
	public boolean isAnyFieldChanged() {

		for (String field : this.fieldsName) {
			if (this.isChanged(field)) return true;
		}
		return false;
	}

	/**
	 * Adds one parameter to change log
	 *
	 * @param attribute to add
	 */
	public void addToChangeLog(String attribute) {

		this.changeLog.add(attribute);
	}

	@Override
	public String toString() {

		return JsonHelper.fromObjectToString(this);
	}
}
