package com.crow.iot.esp32.crowOS.backend.security;

import com.crow.iot.esp32.crowOS.backend.commons.I18nHelper;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : error23
 * Created : 13/04/2020
 */
@Getter
@NoArgsConstructor
public class MissingPermissionException extends RuntimeException {

	private static final long serialVersionUID = 983223446293998304L;
	private Privilege privilege;
	private SecuredResource securedResource;

	/**
	 * Creates new missing permission with translated message
	 *
	 * @param message to create with
	 * @param params  parameters of translated message
	 */
	public MissingPermissionException(String message, Object... params) {

		super(I18nHelper.getI18n().tr(message, params));
	}

	/**
	 * Creates new missing permission with translated message
	 *
	 * @param privilege       to use
	 * @param securedResource to use
	 */
	public MissingPermissionException(Privilege privilege, SecuredResource securedResource) {

		super(I18nHelper.getI18n().tr("Missing {0} permission on {1}!", privilege, securedResource));
		this.privilege = privilege;
		this.securedResource = securedResource;
	}

}
