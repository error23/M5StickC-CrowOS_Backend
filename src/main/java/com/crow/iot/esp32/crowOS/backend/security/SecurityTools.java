package com.crow.iot.esp32.crowOS.backend.security;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.commons.I18nHelper;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractEntity;
import com.crow.iot.esp32.crowOS.backend.security.config.SecurityDetails;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

/**
 * @author : error23
 * Created : 08/04/2020
 */
@Slf4j
public class SecurityTools {

	/**
	 * Forbid creation of common tools since everything in this class is static tool
	 */
	private SecurityTools() {

		throw new IllegalStateException("Utility class cant be instantiated");

	}

	/**
	 * @return security context authentication
	 */
	private static Authentication getAuthentication() {

		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * @return currently connected {@link Account}
	 */
	@Nullable
	public static Account getConnectedAccount() {

		Authentication authentication = getAuthentication();
		if (authentication == null) return null;
		if (! authentication.isAuthenticated()) return null;
		if (! (authentication.getPrincipal() instanceof SecurityDetails)) return null;

		return ((SecurityDetails) authentication.getPrincipal()).getAccount();
	}

	/**
	 * This will load and set connected account into {@link SecurityContextHolder}
	 *
	 * @param account to be set
	 */
	public static void setConnectedAccount(Account account) {

		if (account == null) return;

		UserDetails accountUserDetails = new SecurityDetails(account);
		Authentication authentication = new UsernamePasswordAuthenticationToken(accountUserDetails, accountUserDetails.getPassword(), accountUserDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		if (account.getLocale() != null) LocaleContextHolder.setLocale(account.getLocale());

	}

	/**
	 * Checks if {@link Account} has at least one {@link Role} with root privilege
	 *
	 * @param account to check for
	 * @return true if {@link Account} is root
	 */
	public static boolean isAccountRoot(Account account) {

		if (account == null || CollectionUtils.isEmpty(account.getRoles())) return false;

		for (Role role : account.getRoles()) {
			if (role.isRoot()) return true;
		}

		return false;
	}

	/**
	 * Asserts that {@link Account} has at least one {@link Role} with root privilege
	 *
	 * @param account to assert for
	 */
	public static void assertIsAccountRoot(Account account) {

		if (! isAccountRoot(account)) {
			String id = I18nHelper.getI18n().tr("unknown");
			if (account != null) id = String.valueOf(account.getId());

			MissingPermissionException e = new MissingPermissionException("Sorry, Account {0} doesn''t have root privilege!", id);
			log.trace("Missing permission: ", e);
			throw e;
		}

	}

	/**
	 * Checks if {@link Account} has {@link Privilege} on {@link SecuredResource}.
	 *
	 * @param account                 to check for
	 * @param privilege               to check
	 * @param securedResource         to check
	 * @param securedResourceInstance to check for
	 * @param <E>                     securedResourceInstance type
	 * @return true if {@link Account} has {@link Privilege} on {@link SecuredResource}
	 */
	public static <E extends AbstractEntity> boolean canAccount(Account account, Privilege privilege, SecuredResource securedResource, E securedResourceInstance) {

		if (account == null || CollectionUtils.isEmpty(account.getRoles())) return false;

		for (Role role : account.getRoles()) {

			// If has exact privilege return true
			if (role.can(privilege, securedResource)) {
				return true;
			}

			// If doesn't have exact privilege but is an owner of securedResourceInstance and has permission to modify own resource return true
			if (securedResourceInstance != null && account.equals(securedResourceInstance.getOwner())) {
				if (role.can(Privilege.toOwn(privilege), securedResource)) return true;
			}
		}

		return false;
	}

	/**
	 * Asserts that {@link Account} has {@link Privilege} on {@link SecuredResource}.
	 *
	 * @param account                 to assert for
	 * @param privilege               to assert
	 * @param securedResource         to assert for
	 * @param securedResourceInstance to assert for
	 * @param <E>                     securedResourceInstance type
	 * @throws MissingPermissionException if {@link Account} doesn't have {@link Privilege} on {@link SecuredResource}
	 */
	public static <E extends AbstractEntity> void assertCanAccount(Account account, Privilege privilege, SecuredResource securedResource, E securedResourceInstance) {

		if (! canAccount(account, privilege, securedResource, securedResourceInstance)) {
			MissingPermissionException e = new MissingPermissionException(privilege, securedResource);
			log.trace("Missing permission: ", e);
			throw e;
		}
	}

	/**
	 * Check if currently connected {@link Account} is root
	 *
	 * @return true if currently connected {@link Account} has at leas one {@link Role} with root privilege
	 */
	public static boolean isConnectedAccountRoot() {

		return isAccountRoot(getConnectedAccount());
	}

	/**
	 * Asserts that currently connected {@link Account} is root
	 */
	public static void assertIsConnectedAccountRoot() {

		assertIsAccountRoot(getConnectedAccount());
	}

	/**
	 * Checks if currently connected {@link Account} has {@link Privilege} on {@link SecuredResource}.
	 *
	 * @param privilege               to check
	 * @param securedResource         to check for
	 * @param securedResourceInstance to check for
	 * @param <E>                     securedResourceInstance type
	 * @return true if {@link Account} has {@link Privilege} on {@link SecuredResource}
	 */
	public static <E extends AbstractEntity> boolean canConnectedAccount(Privilege privilege, SecuredResource securedResource, E securedResourceInstance) {

		return canAccount(getConnectedAccount(), privilege, securedResource, securedResourceInstance);
	}

	/**
	 * Asserts that currently connected {@link Account} has {@link Privilege} on {@link SecuredResource}.
	 *
	 * @param privilege               to assert
	 * @param securedResource         to assert for
	 * @param securedResourceInstance to assert for
	 * @param <E>                     securedResourceInstance type
	 * @throws MissingPermissionException if currently connected {@link Account} doesn't have {@link Privilege} on {@link SecuredResource}
	 */
	public static <E extends AbstractEntity> void assertCanConnectedAccount(Privilege privilege, SecuredResource securedResource, E securedResourceInstance) {

		assertCanAccount(getConnectedAccount(), privilege, securedResource, securedResourceInstance);
	}

}
