package com.crow.iot.esp32.crowOS.backend.featureData;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.account.AccountDao;
import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.commons.json.JsonHelper;
import com.crow.iot.esp32.crowOS.backend.security.MissingPermissionException;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import com.crow.iot.esp32.crowOS.backend.security.role.RoleDao;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.rits.cloning.Cloner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
 * Created : 07/05/2021
 */
@SpringBootTest
@Transactional
class FeatureDataServiceTest {

	@Autowired
	RoleDao roleDao;

	@Autowired
	AccountDao accountDao;

	@Autowired
	FeatureDataDao featureDataDao;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	FeatureDataService featureDataService;

	Account connectedAccount;
	Account notConnectedAccount;
	FeatureDataPermissionHolder permissionHolder;

	FeatureData myFeatureData;
	FeatureData otherAccountFeatureData;

	@BeforeEach
	void setUp() {

		this.permissionHolder = new FeatureDataPermissionHolder();

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
			FeatureData featureData = new FeatureData();
			featureData.setSavedData(JsonHelper.fromObjectToNode("{\"test\": " + i + " }"));
			featureData.setFeatureFactoryName("featureFactoryName" + i);

			if (i < 20) {
				featureData.setOwner(this.connectedAccount);
			}
			else {
				featureData.setOwner(this.notConnectedAccount);
			}

			this.featureDataDao.save(featureData);

			if (i == 0) {
				this.myFeatureData = featureData;
			}
			else if (i == 21) {
				this.otherAccountFeatureData = featureData;
			}
		}

	}

	@Test
	void whenList_thanFail() {

		MissingPermissionException exception = assertThrows(MissingPermissionException.class, () -> this.featureDataService.list());
		assertThat(exception.getPrivilege()).isEqualTo(Privilege.READ);

	}

	@Test
	void whenList_thanSuccess() {

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.toOwn(this.permissionHolder.getReadFeatureData()));
		List<FeatureData> featureDatas = this.featureDataService.list();
		assertThat(featureDatas).hasSize(20);

		this.connectedAccount.getRoles().get(0).setPermissions(List.of(this.permissionHolder.getReadFeatureData()));
		featureDatas = this.featureDataService.list();
		assertThat(featureDatas).hasSize(40);

	}

	@Test
	void whenGet_thanFail() {

		MissingPermissionException missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.featureDataService.get(this.myFeatureData.getId()));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.READ);

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.toOwn(this.permissionHolder.getReadFeatureData()));
		missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.featureDataService.get(this.otherAccountFeatureData.getId()));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.READ);

		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> this.featureDataService.get(1000L));
		assertThat(resourceNotFoundException.getResource()).isEqualTo("FeatureData");
		assertThat(resourceNotFoundException.getIds()[0]).isEqualTo(1000L);

	}

	@Test
	void whenGet_thanSuccess() throws MethodArgumentNotValidException {

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.toOwn(this.permissionHolder.getReadFeatureData()));
		assertThat(this.featureDataService.get(this.myFeatureData.getId())).isEqualToComparingFieldByField(this.myFeatureData);

		this.connectedAccount.getRoles().get(0).setPermissions(List.of(this.permissionHolder.getReadFeatureData()));
		assertThat(this.featureDataService.get(this.otherAccountFeatureData.getId())).isEqualToComparingFieldByField(this.otherAccountFeatureData);

	}

	@Test
	void whenCreate_thenFail() {

		FeatureDataDto dto = new FeatureDataDto();
		dto.setSavedData(JsonHelper.fromObjectToNode("{\"test\": 3 }"));
		dto.setFeatureFactoryName("fail");

		MissingPermissionException missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.featureDataService.create(dto));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.CREATE);

	}

	@Test
	void whenCreate_thenSuccess() {

		FeatureDataDto dto = new FeatureDataDto();
		dto.setSavedData(JsonHelper.fromObjectToNode("{\"test\": 3 }"));
		dto.setFeatureFactoryName("success");

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.getCreateFeatureData());
		FeatureData featureData = this.featureDataService.create(dto);

		assertThat(featureData).isEqualToIgnoringGivenFields(dto, "id", "created", "updated", "version", "updatedBy", "owner", "changelog");
		assertThat(featureData.getId()).isNotNull();
		assertThat(featureData.getCreated()).isNotNull();
		assertThat(featureData.getUpdated()).isNotNull();
		assertThat(featureData.getVersion()).isNotNull();

	}

	@Test
	void whenUpdateOrCreate_thenFail() {

		List<FeatureDataDto> dtos = new ArrayList<>();

		for (int i = 0; i < 20; i++) {
			FeatureDataDto dto = new FeatureDataDto();
			dto.setSavedData(JsonHelper.fromObjectToNode("{\"test\": " + i + " }"));
			dto.setFeatureFactoryName("fail" + i);
			dtos.add(dto);
		}

		MissingPermissionException missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.featureDataService.updateOrCreate(dtos));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.CREATE);

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.getCreateFeatureData());

		FeatureDataDto dto = new FeatureDataDto();
		dto.setId(this.otherAccountFeatureData.getId());
		dto.setSavedData(JsonHelper.fromObjectToNode("{\"test\": 3 }"));
		dto.setFeatureFactoryName("fail again");

		dtos.clear();
		dtos.add(dto);

		missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.featureDataService.updateOrCreate(dtos));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.UPDATE);

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.UPDATE_OWN);

		missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.featureDataService.updateOrCreate(dtos));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.UPDATE);

	}

	@Test
	void whenUpdateOrCreate_thenSuccess() throws MethodArgumentNotValidException {

		List<FeatureDataDto> dtos = new ArrayList<>();

		for (int i = 0; i < 20; i++) {
			FeatureDataDto dto = new FeatureDataDto();
			dto.setSavedData(JsonHelper.fromObjectToNode("{\"test\": " + i + " }"));
			dto.setFeatureFactoryName("success" + i);
			dtos.add(dto);
		}

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.getCreateFeatureData());
		assertThat(this.featureDataService.updateOrCreate(dtos)).hasSize(20);

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.UPDATE_OWN);

		dtos.clear();
		for (int i = 20; i < 40; i++) {
			FeatureDataDto dto = new FeatureDataDto();
			dto.setSavedData(JsonHelper.fromObjectToNode("{\"test\": " + i + " }"));
			dto.setFeatureFactoryName("success" + i);
			dtos.add(dto);

		}

		FeatureDataDto updateMyFeatureDataDto = new FeatureDataDto();
		updateMyFeatureDataDto.setId(this.myFeatureData.getId());
		updateMyFeatureDataDto.setSavedData(JsonHelper.fromObjectToNode("{\"test\": 3 }"));
		updateMyFeatureDataDto.setFeatureFactoryName("my");
		dtos.add(updateMyFeatureDataDto);

		assertThat(this.featureDataService.updateOrCreate(dtos)).hasSize(21);

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.UPDATE);

		dtos.clear();
		for (int i = 40; i < 60; i++) {
			FeatureDataDto dto = new FeatureDataDto();
			dto.setSavedData(JsonHelper.fromObjectToNode("{\"test\": " + i + " }"));
			dto.setFeatureFactoryName("success" + i);
			dtos.add(dto);

		}

		FeatureDataDto updateOtherAccountFeatureDataDto = new FeatureDataDto();
		updateOtherAccountFeatureDataDto.setId(this.otherAccountFeatureData.getId());
		updateOtherAccountFeatureDataDto.setSavedData(JsonHelper.fromObjectToNode("{\"test\": 3 }"));
		updateOtherAccountFeatureDataDto.setFeatureFactoryName("other");

		dtos.add(updateMyFeatureDataDto);
		dtos.add(updateOtherAccountFeatureDataDto);

		List<FeatureData> featureDatas = this.featureDataService.updateOrCreate(dtos);
		assertThat(featureDatas).hasSize(22);

		boolean myFound = false;
		boolean otherFound = false;
		for (FeatureData featureData : featureDatas) {

			if (featureData.getId().equals(this.myFeatureData.getId())) {
				assertThat(featureData.getFeatureFactoryName()).isEqualTo("my");
				myFound = true;
			}
			else if (featureData.getId().equals(this.otherAccountFeatureData.getId())) {
				assertThat(featureData.getFeatureFactoryName()).isEqualTo("other");
				otherFound = true;
			}
		}

		assertThat(myFound).isTrue();
		assertThat(otherFound).isTrue();

	}

	@Test
	void whenUpdate_thenFail() {

		FeatureDataDto dto = new FeatureDataDto();
		dto.setSavedData(JsonHelper.fromObjectToNode("{\"test\": 3 }"));
		dto.setFeatureFactoryName("fail");

		MissingPermissionException missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.featureDataService.update(this.myFeatureData, dto));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.UPDATE);

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.toOwn(this.permissionHolder.getUpdateFeatureData()));

		missingPermissionException = assertThrows(MissingPermissionException.class, () -> this.featureDataService.update(this.otherAccountFeatureData, dto));
		assertThat(missingPermissionException.getPrivilege()).isEqualTo(Privilege.UPDATE);

	}

	@Test
	void whenUpdate_thenSuccess() {

		FeatureDataDto dto = new FeatureDataDto();
		dto.setSavedData(JsonHelper.fromObjectToNode("{\"test\": 3 }"));
		dto.setFeatureFactoryName("success");

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.toOwn(this.permissionHolder.getUpdateFeatureData()));
		FeatureData featureData = this.featureDataService.update(this.myFeatureData, dto);

		assertThat(featureData.getId()).isEqualTo(this.myFeatureData.getId());
		assertThat(featureData).isEqualToIgnoringGivenFields(dto, "id", "created", "updated", "version", "updatedBy", "owner", "changelog");

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.UPDATE);

		featureData = this.featureDataService.update(this.otherAccountFeatureData, dto);

		assertThat(featureData.getId()).isEqualTo(this.otherAccountFeatureData.getId());
		assertThat(featureData).isEqualToIgnoringGivenFields(dto, "id", "created", "updated", "version", "updatedBy", "owner", "changelog");
	}

}
