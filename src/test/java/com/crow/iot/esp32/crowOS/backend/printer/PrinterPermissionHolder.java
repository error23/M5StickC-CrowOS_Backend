package com.crow.iot.esp32.crowOS.backend.printer;

import com.crow.iot.esp32.crowOS.backend.AbstractPermissionHolder;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Permission;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : error23
 * Created : 19/06/2021
 */
@Getter
public class PrinterPermissionHolder extends AbstractPermissionHolder {

	private Permission createPrinter;

	private Permission readPrinter;

	private Permission updatePrinter;

	private Permission deletePrinter;

	private Permission setPrinterColor;

	private Permission setPrinterIp;

	public PrinterPermissionHolder() {

		this.createPrinter = new Permission();
		this.createPrinter.setSecuredResource(SecuredResource.PRINTER);
		this.createPrinter.setPrivileges(new ArrayList<>(List.of(Privilege.CREATE)));

		this.readPrinter = new Permission();
		this.readPrinter.setSecuredResource(SecuredResource.PRINTER);
		this.readPrinter.setPrivileges(new ArrayList<>(List.of(Privilege.READ)));

		this.updatePrinter = new Permission();
		this.updatePrinter.setSecuredResource(SecuredResource.PRINTER);
		this.updatePrinter.setPrivileges(new ArrayList<>(List.of(Privilege.UPDATE)));

		this.deletePrinter = new Permission();
		this.deletePrinter.setSecuredResource(SecuredResource.PRINTER);
		this.deletePrinter.setPrivileges(new ArrayList<>(List.of(Privilege.DELETE)));

		this.setPrinterColor = new Permission();
		this.setPrinterColor.setSecuredResource(SecuredResource.PRINTER_COLOR);
		this.setPrinterColor.setPrivileges(new ArrayList<>(List.of(Privilege.UPDATE)));

		this.setPrinterIp = new Permission();
		this.setPrinterIp.setSecuredResource(SecuredResource.PRINTER_MACHINE_ADRESSE);
		this.setPrinterIp.setPrivileges(new ArrayList<>(List.of(Privilege.UPDATE)));
	}
}
