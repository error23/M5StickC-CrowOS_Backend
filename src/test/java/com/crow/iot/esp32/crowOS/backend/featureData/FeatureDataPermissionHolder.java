package com.crow.iot.esp32.crowOS.backend.featureData;

import com.crow.iot.esp32.crowOS.backend.AbstractPermissionHolder;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Permission;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : error23
 * Created : 07/05/2021
 */
@Getter
public class FeatureDataPermissionHolder extends AbstractPermissionHolder {

	private Permission createFeatureData;

	private Permission readFeatureData;

	private Permission updateFeatureData;

	private Permission deleteFeatureData;

	public FeatureDataPermissionHolder() {

		this.createFeatureData = new Permission();
		this.createFeatureData.setSecuredResource(SecuredResource.FEATURE_DATA);
		this.createFeatureData.setPrivileges(new ArrayList<>(List.of(Privilege.CREATE)));

		this.readFeatureData = new Permission();
		this.readFeatureData.setSecuredResource(SecuredResource.FEATURE_DATA);
		this.readFeatureData.setPrivileges(new ArrayList<>(List.of(Privilege.READ)));

		this.updateFeatureData = new Permission();
		this.updateFeatureData.setSecuredResource(SecuredResource.FEATURE_DATA);
		this.updateFeatureData.setPrivileges(new ArrayList<>(List.of(Privilege.UPDATE)));

		this.deleteFeatureData = new Permission();
		this.deleteFeatureData.setSecuredResource(SecuredResource.FEATURE_DATA);
		this.deleteFeatureData.setPrivileges(new ArrayList<>(List.of(Privilege.DELETE)));
	}
}
