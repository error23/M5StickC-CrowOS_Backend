package com.crow.iot.esp32.crowOS.backend.account;

import com.crow.iot.esp32.crowOS.backend.commons.DuplicatedResourceException;
import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.MethodArgumentNotValidExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * @author : error23
 * Created : 18/05/2020
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

	private final AccountDao accountDao;

	private final AccountMapper mapper;

	/**
	 * Retrieves one {@link Account} from database
	 *
	 * @param id of account to retrieve
	 * @return retrieved {@link Account}
	 */
	@PostAuthorize ("hasPermission(returnObject, 'READ')")
	public Account get(Long id) throws MethodArgumentNotValidException {

		if (id == null) throw MethodArgumentNotValidExceptionFactory.NOT_NULL(this, "id");

		Account account = this.accountDao.get(id);
		if (account == null) throw new ResourceNotFoundException("Account", id);

		return account;
	}

	/**
	 * Search for one account by it email
	 *
	 * @param email to search for
	 * @return found account with fetched roles
	 */
	@Nullable
	public Account get(String email) {

		return this.accountDao.get(email);
	}

	/**
	 * Creates one new {@link Account}
	 *
	 * @param accountDto to create
	 * @return created {@link Account}
	 */
	@PreAuthorize ("hasPermission('ACCOUNT', 'CREATE')")
	public Account create(@NotNull AccountDto accountDto) {

		if (this.accountDao.get(accountDto.getEmail()) != null) throw new DuplicatedResourceException("Account", "email", accountDto.getEmail());
		Account created = this.mapper.toEntity(accountDto);
		this.accountDao.save(created);
		created.setOwner(created);

		return created;
	}

	/**
	 * Updates one {@link Account}
	 *
	 * @param account    to update
	 * @param accountDto to update from
	 * @return updated {@link Account}
	 */
	@PreAuthorize ("hasPermission(#account,'UPDATE')")
	public Account update(@NotNull Account account, @NotNull AccountDto accountDto) {

		this.mapper.merge(accountDto, account);
		return account;
	}

	/**
	 * Delete one {@link Account}
	 *
	 * @param account to delete
	 */
	@PreAuthorize ("hasPermission(#account,'DELETE')")
	public void delete(@NotNull Account account) {

		this.accountDao.delete(account);
	}
}
