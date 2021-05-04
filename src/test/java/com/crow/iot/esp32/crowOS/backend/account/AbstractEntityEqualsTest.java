package com.crow.iot.esp32.crowOS.backend.account;

import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author : error23
 * Created : 11/06/2020
 */
@SuppressWarnings ("all")
class AbstractEntityEqualsTest {

	@Test
	void whenSameObject_thanReturnTrue() {

		Account account = new Account();
		assertThat(account.equals(account)).isTrue();
	}

	@Test
	void whenNull_thanReturnFalse() {

		Account account = new Account();
		assertThat(account.equals(null)).isFalse();
	}

	@Test
	void whenSameId_thanReturnTrue() {

		Account account = new Account();
		account.setId(1L);
		account.setFirstName("igor");

		Account account1 = new Account();
		account1.setId(1L);
		account1.setFirstName("rajic");

		assertThat(account.equals(account1)).isTrue();
	}

	@Test
	void whenDifferentId_thanReturnFalse() {

		Account account = new Account();
		account.setId(1L);
		account.setFirstName("igor");

		Account account1 = new Account();
		account1.setId(1L);
		account1.setFirstName("igor");

		assertThat(account).isEqualToComparingFieldByField(account1);

		account1.setId(2L);
		assertThat(account.equals(account1)).isFalse();
	}

	@Test
	void whenComparingToSomethingElse_thanReturnFalse() {

		Account account = new Account();
		account.setId(1L);

		Role role = new Role();
		role.setId(1L);
		assertThat(account.equals(role)).isFalse();
	}

}
