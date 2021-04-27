package com.crow.iot.esp32.crowOS.backend.account;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractDao;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

/**
 * @author : error23
 * Created : 08/04/2020
 */
@Repository
public class AccountDao extends AbstractDao<Account> {

	public AccountDao() {

		super();
	}

	/**
	 * Search for one account by it email
	 *
	 * @param email to search for
	 * @return found account with fetched roles
	 */
	@Nullable
	public Account get(String email) {

		if (email == null) return null;

		CriteriaBuilder builder = this.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> accountRoot = criteria.from(Account.class);

		accountRoot.fetch(Account_.roles, JoinType.LEFT);
		this.and(criteria, builder.equal(accountRoot.get(Account_.email), email.toLowerCase()));
		criteria.select(accountRoot);

		try {

			return this.getEntityManager().createQuery(criteria).getSingleResult();

		}
		catch (NoResultException exception) {

			return null;
		}

	}
}
