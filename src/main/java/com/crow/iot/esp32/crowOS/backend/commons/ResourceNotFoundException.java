package com.crow.iot.esp32.crowOS.backend.commons;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author : error23
 * Created : 22/05/2020
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 9061535900113995223L;

	private String localizedMessage;
	private String resource;
	private Long[] ids;

	/**
	 * Creates new {@link ResourceNotFoundException} with resource name and its id
	 *
	 * @param resource name
	 * @param id       resource id
	 */
	public ResourceNotFoundException(String resource, @NotNull Long... id) {

		this.localizedMessage = I18nHelper.getI18n().trn("Resource: {0} {1} not found!", "Resources: {0} {1} not found!", id.length, resource, Arrays.toString(id));
		this.resource = resource;
		this.ids = id;
	}

	/**
	 * Creates new {@link ResourceNotFoundException} with resource name
	 *
	 * @param resource name
	 */
	public ResourceNotFoundException(String resource) {

		this.localizedMessage = I18nHelper.getI18n().tr("Resource: {0} not found!", resource);
		this.resource = resource;
	}
}
