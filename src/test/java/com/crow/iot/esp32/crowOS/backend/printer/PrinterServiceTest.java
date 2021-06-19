package com.crow.iot.esp32.crowOS.backend.printer;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.account.AccountDao;
import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.printer.flashForge.dreamer.FlashForgeDreamerClient;
import com.crow.iot.esp32.crowOS.backend.security.MissingPermissionException;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import com.crow.iot.esp32.crowOS.backend.security.role.RoleDao;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import com.rits.cloning.Cloner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author : error23
 * Created : 17/06/2021
 */
@SpringBootTest
@Transactional
@ExtendWith (MockitoExtension.class)
class PrinterServiceTest {

	@Autowired
	RoleDao roleDao;

	@Autowired
	AccountDao accountDao;

	@Autowired
	PasswordEncoder passwordEncoder;

	@MockBean
	FlashForgeDreamerClient flashForgeDreamerClient;

	@Autowired
	PrinterDao printerDao;

	@InjectMocks
	@Autowired
	PrinterService printerService;

	Account connectedAccount;
	Account notConnectedAccount;
	PrinterPermissionHolder permissionHolder;

	Printer myPrinter;
	Printer otherPrinter;

	@BeforeEach
	void setUp() {

		this.permissionHolder = new PrinterPermissionHolder();

		Role role = new Role();
		role.setPriority(1);
		role.setName("accountRole");
		role.setRoot(false);
		role.setPermissions(new ArrayList<>());
		this.roleDao.save(role);

		this.connectedAccount = new Account();
		this.connectedAccount.setEnabled(true);
		this.connectedAccount.setFirstName("error23");
		this.connectedAccount.setLastName("rolly");
		this.connectedAccount.setEmail("error23.d@gmail.com");
		this.connectedAccount.setPassword(this.passwordEncoder.encode("test"));
		this.connectedAccount.setLocale(Locale.FRENCH);
		this.connectedAccount.setRoles(new ArrayList<>(List.of(role)));
		this.connectedAccount.setOwner(this.connectedAccount);
		this.accountDao.save(this.connectedAccount);
		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.notConnectedAccount = Cloner.standard().deepClone(this.connectedAccount);
		this.notConnectedAccount.setId(null);
		this.accountDao.save(this.notConnectedAccount);

		for (int i = 0; i < 40; i++) {
			Printer printer = new Printer();
			printer.setMachineIp("192.168.0." + i);
			printer.setMachinePort(8899);
			this.printerDao.save(printer);

			if (i < 20) {
				printer.setOwner(this.connectedAccount);
			}
			else {
				printer.setOwner(this.notConnectedAccount);
			}

			this.printerDao.save(printer);

			if (i == 0) {
				this.myPrinter = printer;
			}
			else if (i == 21) {
				this.otherPrinter = printer;
			}
		}

	}

	@Test
	void whenList_thanFail() {

		MissingPermissionException exception = assertThrows(MissingPermissionException.class, () -> this.printerService.list());
		assertThat(exception.getPrivilege()).isEqualTo(Privilege.READ);

	}

	@Test
	void whenList_thanSuccess() {

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.toOwn(this.permissionHolder.getReadPrinter()));
		List<Printer> printers = this.printerService.list();
		assertThat(printers).hasSize(20);

		this.connectedAccount.getRoles().get(0).setPermissions(List.of(this.permissionHolder.getReadPrinter()));
		printers = this.printerService.list();
		assertThat(printers).hasSize(40);

	}

	@Test
	void whenGet_thanFail() {

		MissingPermissionException missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.printerService.get(this.myPrinter.getId()));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.READ);

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.toOwn(this.permissionHolder.getReadPrinter()));
		missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.printerService.get(this.otherPrinter.getId()));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.READ);

		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> this.printerService.get(1000L));
		assertThat(resourceNotFoundException.getResource()).isEqualTo("Printer");
		assertThat(resourceNotFoundException.getIds()[0]).isEqualTo(1000L);

	}

	@Test
	void whenGet_thanSuccess() throws MethodArgumentNotValidException {

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.toOwn(this.permissionHolder.getReadPrinter()));
		assertThat(this.printerService.get(this.myPrinter.getId())).isEqualToComparingFieldByField(this.myPrinter);

		this.connectedAccount.getRoles().get(0).setPermissions(List.of(this.permissionHolder.getReadPrinter()));
		assertThat(this.printerService.get(this.otherPrinter.getId())).isEqualToComparingFieldByField(this.otherPrinter);

	}

	@Test
	void whenCreate_thenSuccess() {

		PrinterDto dto = new PrinterDto();
		dto.setMachineIp("192.168.0.3");
		dto.setMachineName("test");
		dto.setMachinePort(8080);

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.getCreatePrinter());
		Printer printer = this.printerService.create(dto);

		assertThat(printer).isEqualToIgnoringGivenFields(dto, "id", "created", "updated", "version", "updatedBy", "owner", "changelog");
		assertThat(printer.getId()).isNotNull();
		assertThat(printer.getCreated()).isNotNull();
		assertThat(printer.getUpdated()).isNotNull();
		assertThat(printer.getVersion()).isNotNull();

	}

	@Test
	void whenUpdate_thenFail() {

		PrinterDto dto = new PrinterDto();
		dto.setMachineIp("192.168.0.3");
		dto.setMachineName("test");
		dto.setMachinePort(8080);

		MissingPermissionException missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.printerService.update(this.myPrinter, dto));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.UPDATE);

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.toOwn(this.permissionHolder.getUpdatePrinter()));

		missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.printerService.update(this.otherPrinter, dto));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.UPDATE);

	}

	@Test
	void whenUpdate_thenSuccess() {

		PrinterDto dto = new PrinterDto();
		dto.setMachineIp("192.168.0.3");
		dto.setMachineName("test");
		dto.setMachinePort(8080);

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.toOwn(this.permissionHolder.getUpdatePrinter()));
		Printer printer = this.printerService.update(this.myPrinter, dto);

		assertThat(printer.getId()).isEqualTo(this.myPrinter.getId());
		assertThat(printer).isEqualToIgnoringGivenFields(dto, "id", "created", "updated", "version", "updatedBy", "owner", "changelog");

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.UPDATE);

		printer = this.printerService.update(this.otherPrinter, dto);

		assertThat(printer.getId()).isEqualTo(this.otherPrinter.getId());
		assertThat(printer).isEqualToIgnoringGivenFields(dto, "id", "created", "updated", "version", "updatedBy", "owner", "changelog");
	}

	@Test
	void whenUpdateLedColor_thanFail() {

		MissingPermissionException missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.printerService.updateLedColor(this.myPrinter, ColorRGB.RED));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.UPDATE);
		assertThat(missingPermissionException.getSecuredResource()).isEqualTo(SecuredResource.PRINTER_COLOR);

	}

	@Test
	void whenUpdateLedColor_thanSuccess() {

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.getSetPrinterColor());

		this.printerService.updateLedColor(this.myPrinter, ColorRGB.RED);
		assertThat(ColorRGB.RED).isEqualTo(this.myPrinter.getLedColor());

	}

	@Test
	void whenUpdateMachineAdresse_thanFail() {

		MissingPermissionException missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.printerService.updateMachineAdresse(this.myPrinter, "123", 123));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.UPDATE);
		assertThat(missingPermissionException.getSecuredResource()).isEqualTo(SecuredResource.PRINTER_MACHINE_ADRESSE);

	}

	@Test
	void whenUpdateMachineAdresse_thanSuccess() {

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.getSetPrinterIp());

		this.printerService.updateMachineAdresse(this.myPrinter, "123", 123);
		assertThat("123").isEqualTo(this.myPrinter.getMachineIp());
		assertThat(123).isEqualTo(this.myPrinter.getMachinePort());
	}

}
