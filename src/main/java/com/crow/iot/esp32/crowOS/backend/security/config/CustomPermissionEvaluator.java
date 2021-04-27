package com.crow.iot.esp32.crowOS.backend.security.config;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractEntity;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author : error23
 * Created : 27/04/2020
 */
@Component
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

	@Override
	public boolean hasPermission(Authentication authentication, Object securedResourceInstance, Object privilege) {

		Account account = this.retrieveAccount(authentication);
		if (securedResourceInstance == null || ! (privilege instanceof String)) {
			log.error("Something went wrong here : securedResourceInstance = {}, privilege = {}", securedResourceInstance, privilege);
			return false;
		}

		// If securedResourceInstance is string call hasPermission with string parameter
		if (securedResourceInstance instanceof String) {
			return this.hasPermission(authentication, null, securedResourceInstance.toString(), privilege);
		}

		// If securedResourceInstance is collection than cast it to collection and check instance privilege for each element
		if (securedResourceInstance instanceof Collection) {
			for (Object securedResourceInstanceElement : (Collection<?>) securedResourceInstance) {
				this.checkInstancePrivilege(securedResourceInstanceElement, privilege, account);
			}

			return true;
		}

		// Else if securedResourceInstance is an object than just check instance privilege
		return this.checkInstancePrivilege(securedResourceInstance, privilege, account);
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String securedResourceString, Object privilege) {

		Account account = this.retrieveAccount(authentication);
		if (securedResourceString == null || ! (privilege instanceof String)) {
			log.error("Something went wrong here : securedResourceString = {}, privilege = {}", securedResourceString, privilege);
			return false;
		}

		SecurityTools.assertCanAccount(account, Privilege.valueOf((String) privilege), SecuredResource.valueOf(securedResourceString.toUpperCase()), null);
		return true;
	}

	/**
	 * Checks if account has privilege on securedResourceInstance
	 *
	 * @param securedResourceInstance to check on
	 * @param privilege               to check
	 * @param account                 to check for
	 * @return true if account has privilege on securedResourceInstance
	 */
	private boolean checkInstancePrivilege(Object securedResourceInstance, Object privilege, Account account) {

		if (! (securedResourceInstance instanceof AbstractEntity)) {
			log.error("Something went wrong here : securedResourceInstance is not instance of AbstractEntity.class but : {}", securedResourceInstance);
			return false;
		}

		AbstractEntity securedObjectInstanceAbstractEntity = (AbstractEntity) securedResourceInstance;

		String securedResourceString = securedObjectInstanceAbstractEntity.getClass().getSimpleName();
		SecuredResource securedResource = SecuredResource.valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, securedResourceString));

		SecurityTools.assertCanAccount(account, Privilege.valueOf((String) privilege), securedResource, securedObjectInstanceAbstractEntity);
		return true;
	}

	/**
	 * Retrieve account from {@link Authentication}
	 *
	 * @param authentication to retrieve from
	 * @return account retrieved account or null if not authenticated
	 */
	private Account retrieveAccount(Authentication authentication) {

		if (authentication == null || ! authentication.isAuthenticated()) return null;

		if (authentication.getPrincipal() instanceof SecurityDetails) {
			SecurityDetails securityDetails = (SecurityDetails) authentication.getPrincipal();
			return securityDetails.getAccount();
		}

		log.error("Something went wrong here : authentication.getPrincipal() is not instance of SecurityDetails but : {}", authentication.getPrincipal());

		return null;
	}

}
