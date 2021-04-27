package com.crow.iot.esp32.crowOS.backend.security.role.permission;

/**
 * @author : error23
 * Created : 13/04/2020
 */
public enum Privilege {

	CREATE,
	READ,
	UPDATE,
	DELETE,

	READ_OWN,
	UPDATE_OWN,
	DELETE_OWN;

	/**
	 * Converts one privilege to own privilege
	 *
	 * @param privilege to convert
	 * @return converted to own privilege
	 */
	public static Privilege toOwn(Privilege privilege) {

		if (CREATE.equals(privilege)) return privilege;
		return Privilege.valueOf(privilege.name() + "_OWN");

	}

}
