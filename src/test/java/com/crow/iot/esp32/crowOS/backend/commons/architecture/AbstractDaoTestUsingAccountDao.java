package com.crow.iot.esp32.crowOS.backend.commons.architecture;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.account.AccountDao;
import com.crow.iot.esp32.crowOS.backend.account.Account_;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.OrderDto;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.paginated.PaginatedDto;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.Operator;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.SearchDto;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.SearchFilter;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import com.crow.iot.esp32.crowOS.backend.security.role.RoleDao;
import com.crow.iot.esp32.crowOS.backend.security.role.Role_;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Permission;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import org.hibernate.LazyInitializationException;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author : error23
 * Created : 08/06/2020
 */
@SpringBootTest
@Transactional
@SuppressWarnings ("unchecked")
class AbstractDaoTestUsingAccountDao {

	@Autowired
	AccountDao accountDao;

	@Autowired
	RoleDao roleDao;

	Account account;
	Account account1;

	Role role;
	Role role1;

	@BeforeEach
	void setUp() {

		this.account = new Account();
		this.account.setEnabled(true);
		this.account.setFirstName("igor");
		this.account.setLastName("Rajic");
		this.account.setEmail("error23.d@gmail.com");
		this.account.setPassword("test");

		this.account1 = new Account();
		this.account1.setEnabled(true);
		this.account1.setFirstName("NENAD");
		this.account1.setLastName("MIJATOVIC");
		this.account1.setEmail("wamp@gmail.com");
		this.account1.setPassword("test2");

		this.role = new Role();
		this.role.setName("testA");
		this.role.setRoot(false);
		this.role.setPriority(1);

		ArrayList<Permission> permissions = new ArrayList<>();

		Permission p = new Permission();
		p.setPrivileges(List.of(Privilege.READ, Privilege.CREATE, Privilege.DELETE));
		p.setSecuredResource(SecuredResource.ACCOUNT);
		permissions.add(p);

		this.role.setPermissions(permissions);

		this.role1 = new Role();
		this.role1.setName("testB");
		this.role1.setRoot(false);
		this.role1.setPriority(1);

		ArrayList<Permission> permissions1 = new ArrayList<>();

		Permission p1 = new Permission();
		p1.setPrivileges(List.of(Privilege.READ, Privilege.CREATE));
		p1.setSecuredResource(SecuredResource.ACCOUNT);
		permissions1.add(p1);

		this.role1.setPermissions(permissions1);

	}

	@Test
	void whenAutowiring_thanAssertNotingIsNullAndEntityClassIsSetUp() {

		assertThat(this.accountDao).isNotNull();
		assertThat(this.accountDao.getEntityManager()).isNotNull();
		assertThat(this.accountDao.getCriteriaBuilder()).isNotNull();
		assertThat(this.accountDao.getEntityClass()).isNotNull();
		assertThat(this.accountDao.getEntityClass().isAssignableFrom(Account.class)).isTrue();

	}

	@Test
	void whenSavingAccount_thanSuccess() {

		Long accountId = this.accountDao.save(this.account);
		Account account = this.accountDao.get(accountId);

		assertThat(account).isNotNull();
		assertThat(accountId).isNotNull();
		assertThat(account.getId()).isEqualTo(accountId);
	}

	@Test
	void whenBatchSaving_thanSuccess() {

		List<Account> accounts = new ArrayList<>();

		for (int i = 0; i < 400; i++) {
			Account account = new Account();
			account.setEnabled(true);
			account.setFirstName("igor");
			account.setLastName("rajic");
			account.setEmail("error23.d@gmail.com");
			account.setPassword("test");
			accounts.add(account);
		}

		this.accountDao.batchSave(accounts);

		List<Account> expected = this.accountDao.list();
		assertThat(expected).isNotEmpty();
		for (Account expectedAccount : expected) {
			assertThat(expectedAccount).isNotNull();
			assertThat(expectedAccount.getId()).isNotNull();
		}
	}

	@Test
	void whenDeleting_thanSuccess() {

		Long id = this.accountDao.save(this.account);
		this.accountDao.delete(this.accountDao.get(id));
		assertThat(this.accountDao.get(id)).isNull();

	}

	@Test
	void whenBatchDeleting_thanSuccess() {

		this.whenBatchSaving_thanSuccess();
		assertThat(this.accountDao.list()).isNotEmpty();
		List<Account> toRemove = this.accountDao.list();
		assertThat(toRemove);
		this.accountDao.batchDelete(toRemove);
		assertThat(this.accountDao.list()).isEmpty();
	}

	@Test
	void whenReattachAndRefresh_thanSuccess() {

		this.accountDao.save(this.account);
		this.accountDao.flushAndClear();
		this.account.setFirstName("test");
		assertThat(this.account.getFirstName()).isEqualTo("test");
		this.accountDao.reattach(this.account);
		this.accountDao.refresh(this.account);
		assertThat(this.account.getFirstName()).isEqualTo("igor");

	}

	@Test
	void whenFindJoinByAttributeNameJoinIsNull_thanCreateNewJoin() {

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();

		CriteriaQuery<Account> criteriaExpected = builder.createQuery(Account.class);
		Root<Account> rootExpected = criteriaExpected.from(Account.class);
		Join<Account, Role> joinExpected = rootExpected.join(Account_.ROLES);

		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);
		Join<Account, Role> join = (Join<Account, Role>) this.accountDao.findJoinByAttributeName(root, Account_.ROLES);

		assertThat(join).isNotNull();
		assertThat(join).isNotEqualTo(joinExpected);
		assertThat(join.getAttribute()).isEqualToComparingFieldByField(joinExpected.getAttribute());

	}

	@Test
	void whenFindJoinByAttributeNameJoinIsNotNull_thanReturnJoin() {

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();

		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);
		Join<Account, Role> joinExpected = root.join(Account_.ROLES);

		Join<Account, Role> join = (Join<Account, Role>) this.accountDao.findJoinByAttributeName(root, Account_.ROLES);

		assertThat(join).isNotNull();
		assertThat(join).isEqualTo(joinExpected);
		assertThat(join.getAttribute()).isEqualToComparingFieldByField(joinExpected.getAttribute());

	}

	@Test
	void whenCallingAndWithoutWhere_thanCreateWhere() {

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();

		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		this.accountDao.and(criteria, root.get(Account_.ID).isNotNull());
		this.accountDao.and(criteria, builder.equal(root.get(Account_.firstName), "igor"));

		assertThat(criteria.getRestriction()).isNotNull();
		assertThat(criteria.getRestriction().getExpressions()).hasSize(2);
		assertThat(criteria.getRestriction().getOperator()).isEqualTo(Predicate.BooleanOperator.AND);

	}

	@Test
	void whenCallingAndWithWhere_thanCreateAnd() {

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();

		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		criteria.where(root.get(Account_.ID).isNotNull());

		assertThat(criteria.getRestriction()).isNotNull();
		this.accountDao.and(criteria, builder.equal(root.get(Account_.firstName), "igor"));

		assertThat(criteria.getRestriction().getExpressions()).hasSize(2);
		assertThat(criteria.getRestriction().getOperator()).isEqualTo(Predicate.BooleanOperator.AND);

	}

	@Test
	void whenCallingOrWithoutWhere_thanCreateWhere() {

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();

		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		this.accountDao.or(criteria, root.get(Account_.ID).isNotNull());
		this.accountDao.or(criteria, builder.equal(root.get(Account_.firstName), "igor"));

		assertThat(criteria.getRestriction()).isNotNull();
		assertThat(criteria.getRestriction().getExpressions()).hasSize(2);
		assertThat(criteria.getRestriction().getOperator()).isEqualTo(Predicate.BooleanOperator.OR);

	}

	@Test
	void whenCallingOrWithWhere_thanCreateOr() {

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();

		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		criteria.where(root.get(Account_.ID).isNotNull());

		assertThat(criteria.getRestriction().getExpressions()).hasSize(0);
		this.accountDao.or(criteria, builder.equal(root.get(Account_.firstName), "igor"));

		assertThat(criteria.getRestriction().getExpressions()).hasSize(2);
		assertThat(criteria.getRestriction().getOperator()).isEqualTo(Predicate.BooleanOperator.OR);

	}

	@Test
	void whenAddingPagination_thanSuccess() {

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		criteria.from(Account.class);

		TypedQuery<Account> query = this.accountDao.getEntityManager().createQuery(criteria);

		this.accountDao.addPagination(query, new PaginatedDto());

		assertThat(query.getFirstResult()).isEqualTo(0);
		assertThat(query.getMaxResults()).isEqualTo(2147483647);

		PaginatedDto dto = new PaginatedDto();
		dto.setPage(3);
		dto.setNumberPerPage(50);
		this.accountDao.addPagination(query, dto);

		assertThat(query.getFirstResult()).isEqualTo(150);
		assertThat(query.getMaxResults()).isEqualTo(50);

	}

	@Test
	void wenAddingOrderWithoutJoin_thanSuccess() {

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		this.accountDao.addOrder(criteria, root, new OrderDto());
		assertThat(criteria.getOrderList()).isEmpty();

		OrderDto orderDto = new OrderDto();

		Map<String, OrderDto.OrderDirection> order = new HashMap<>();
		order.put(Account_.LAST_NAME, OrderDto.OrderDirection.ASC);
		order.put(Account_.FIRST_NAME, OrderDto.OrderDirection.DESC);
		orderDto.setOrder(order);

		this.accountDao.addOrder(criteria, root, orderDto);

		assertThat(criteria.getOrderList()).hasSize(2);

		assertThat(criteria.getOrderList().get(0).isAscending()).isTrue();
		assertThat(((SingularAttributePath<Account>) criteria.getOrderList().get(0).getExpression()).getAttribute()).isEqualTo(Account_.lastName);
		assertThat(criteria.getOrderList().get(1).isAscending()).isFalse();
		assertThat(((SingularAttributePath<Account>) criteria.getOrderList().get(1).getExpression()).getAttribute()).isEqualTo(Account_.firstName);

	}

	@Test
	void wenAddingOrderWithJoin_thanSuccess() {

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		this.accountDao.addOrder(criteria, root, new OrderDto());
		assertThat(criteria.getOrderList()).isEmpty();

		OrderDto orderDto = new OrderDto();

		Map<String, OrderDto.OrderDirection> order = new HashMap<>();
		order.put(Account_.LAST_NAME, OrderDto.OrderDirection.ASC);
		order.put(Account_.FIRST_NAME, OrderDto.OrderDirection.DESC);
		order.put("roles.name", OrderDto.OrderDirection.DESC);
		orderDto.setOrder(order);

		this.accountDao.addOrder(criteria, root, orderDto);

		assertThat(criteria.getOrderList()).hasSize(3);

		assertThat(criteria.getOrderList().get(0).isAscending()).isTrue();
		assertThat(((SingularAttributePath<Account>) criteria.getOrderList().get(0).getExpression()).getAttribute()).isEqualTo(Account_.lastName);
		assertThat(criteria.getOrderList().get(1).isAscending()).isFalse();
		assertThat(((SingularAttributePath<Account>) criteria.getOrderList().get(1).getExpression()).getAttribute()).isEqualTo(Account_.firstName);
		assertThat(criteria.getOrderList().get(2).isAscending()).isFalse();
		assertThat(((SingularAttributePath<Account>) criteria.getOrderList().get(2).getExpression()).getAttribute()).isEqualTo(Role_.name);

	}

	@Test
	void whenAddingEmptyFilters_thanReturnAll() {

		this.accountDao.save(this.account);
		this.accountDao.save(this.account1);
		this.roleDao.save(this.role);
		this.roleDao.save(this.role1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		this.accountDao.addFilters(criteria, root, new SearchDto());

		OrderDto orderDto = new OrderDto();

		Map<String, OrderDto.OrderDirection> order = new HashMap<>();
		order.put(Account_.ID, OrderDto.OrderDirection.ASC);
		orderDto.setOrder(order);

		this.accountDao.addOrder(criteria, root, orderDto);

		List<Account> allAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(allAccounts).hasSize(2);
		assertThat(allAccounts.get(0)).isEqualTo(this.account);
		assertThat(allAccounts.get(1)).isEqualTo(this.account1);

	}

	@Test
	void whenAddingNullAndNotNullFilter_thanSuccess() {

		this.account.setFirstName(null);
		this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> nullCriteria = builder.createQuery(Account.class);
		Root<Account> nullRoot = nullCriteria.from(Account.class);

		SearchDto nullSearchDto = new SearchDto(new SearchFilter("firstName", Operator.NULL));

		this.accountDao.addFilters(nullCriteria, nullRoot, nullSearchDto);

		List<Account> retrievedNullAccounts = this.accountDao.getEntityManager().createQuery(nullCriteria).getResultList();
		assertThat(retrievedNullAccounts).hasSize(1);
		assertThat(retrievedNullAccounts.get(0)).isEqualTo(this.account);

		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(new SearchFilter("firstName", Operator.NOT_NULL));

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account1);

	}

	@Test
	void whenAddingEqualsNotEqualsFilter_thanSuccess() {

		Long id = this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(
			new SearchFilter("firstName", Operator.EQUALS, "igor"),
			new SearchFilter("id", Operator.EQUALS, id)
		);

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);

		CriteriaQuery<Account> criteriaNotEquals = builder.createQuery(Account.class);
		Root<Account> rootNotEquals = criteriaNotEquals.from(Account.class);

		SearchDto searchDtoNotEquals = new SearchDto(
			new SearchFilter("firstName", Operator.NOT_EQUALS, "igor"),
			new SearchFilter("id", Operator.NOT_EQUALS, id)
		);

		this.accountDao.addFilters(criteriaNotEquals, rootNotEquals, searchDtoNotEquals);

		retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteriaNotEquals).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account1);
	}

	@Test
	void whenAddingMatchesNotMatchesFilter_thanSuccess() {

		this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(new SearchFilter("firstName", Operator.MATCHES, "go"));

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);

		CriteriaQuery<Account> criteriaNotMatches = builder.createQuery(Account.class);
		Root<Account> rootNotMatches = criteriaNotMatches.from(Account.class);

		SearchDto searchDtoNotMatches = new SearchDto(new SearchFilter("firstName", Operator.NOT_MATCHES, "go"));

		this.accountDao.addFilters(criteriaNotMatches, rootNotMatches, searchDtoNotMatches);

		retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteriaNotMatches).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account1);
	}

	@Test
	void whenAddingFilterMatchesStartFilter_thanSuccess() {

		this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(new SearchFilter("firstName", Operator.MATCHES_START, "ig"));

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);
	}

	@Test
	void whenAddingFilterMatchesEndFilter_thanSuccess() {

		this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(new SearchFilter("firstName", Operator.MATCHES_END, "or"));

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);
	}

	@Test
	void whenAddingMatchesNotMatchesIgnoreCaseFilter_thanSuccess() {

		this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(new SearchFilter("firstName", Operator.IGNORE_CASE_MATCHES, "na"));

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account1);

		CriteriaQuery<Account> criteriaNotMatches = builder.createQuery(Account.class);
		Root<Account> rootNotMatches = criteriaNotMatches.from(Account.class);

		SearchDto searchDtoNotMatches = new SearchDto(new SearchFilter("firstName", Operator.IGNORE_CASE_NOT_MATCHES, "na"));

		this.accountDao.addFilters(criteriaNotMatches, rootNotMatches, searchDtoNotMatches);

		retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteriaNotMatches).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);
	}

	@Test
	void whenAddingFilterMatchesStartIgnoreCaseFilter_thanSuccess() {

		this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(new SearchFilter("firstName", Operator.IGNORE_CASE_MATCHES_START, "ne"));

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account1);
	}

	@Test
	void whenAddingFilterMatchesEndIgnoreCaseFilter_thanSuccess() {

		this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(new SearchFilter("firstName", Operator.IGNORE_CASE_MATCHES_END, "ad"));

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account1);
	}

	@Test
	void whenAddingGreaterThanFilter_thanSuccess() {

		Long id = this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		Date date = new Date(this.account.getCreated().getTime() - 100);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(
			new SearchFilter("id", Operator.GREATER_THAN, id),
			new SearchFilter("created", Operator.GREATER_THAN, date)
		);

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account1);
	}

	@Test
	void whenAddingGreaterEqualsThanFilter_thanSuccess() {

		Long id = this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		Date date = new Date(this.account.getCreated().getTime() - 100);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		OrderDto orderDto = new OrderDto();

		Map<String, OrderDto.OrderDirection> order = new HashMap<>();
		order.put(Account_.ID, OrderDto.OrderDirection.ASC);
		orderDto.setOrder(order);

		this.accountDao.addOrder(criteria, root, orderDto);

		SearchDto searchDto = new SearchDto(
			new SearchFilter("id", Operator.GREATER_EQUALS_THAN, id),
			new SearchFilter("created", Operator.GREATER_EQUALS_THAN, date),
			new SearchFilter("created", Operator.GREATER_EQUALS_THAN, this.account.getCreated())
		);

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(2);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);
		assertThat(retrievedAccounts.get(1)).isEqualTo(this.account1);
	}

	@Test
	void whenAddingLowerThanFilter_thanSuccess() {

		this.accountDao.save(this.account);
		Long id = this.accountDao.save(this.account1);

		Date date = new Date(this.account.getCreated().getTime() + 100);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(
			new SearchFilter("id", Operator.LOWER_THAN, id),
			new SearchFilter("created", Operator.LOWER_THAN, date)
		);

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);
	}

	@Test
	void whenAddingLowerEqualsThanFilter_thanSuccess() {

		this.accountDao.save(this.account);
		Long id = this.accountDao.save(this.account1);

		Date date = new Date(this.account.getCreated().getTime() + 100);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		OrderDto orderDto = new OrderDto();

		Map<String, OrderDto.OrderDirection> order = new HashMap<>();
		order.put(Account_.ID, OrderDto.OrderDirection.ASC);
		orderDto.setOrder(order);

		this.accountDao.addOrder(criteria, root, orderDto);

		SearchDto searchDto = new SearchDto(
			new SearchFilter("id", Operator.LOWER_EQUALS_THAN, id),
			new SearchFilter("created", Operator.LOWER_EQUALS_THAN, date),
			new SearchFilter("created", Operator.LOWER_EQUALS_THAN, this.account1.getCreated())
		);

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(2);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);
		assertThat(retrievedAccounts.get(1)).isEqualTo(this.account1);
	}

	@Test
	void whenAddingBetweenFilter_thanSuccess() {

		Long id = this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		Date date = new Date(this.account.getCreated().getTime() + 100);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(
			new SearchFilter("id", Operator.BETWEEN, id + 1, id + 2),
			new SearchFilter("created", Operator.BETWEEN, this.account1.getCreated(), date)
		);

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account1);
	}

	@Test
	void whenAddingNotBetweenFilter_thanSuccess() {

		this.accountDao.save(this.account);
		Long id = this.accountDao.save(this.account1);

		Date date = new Date(this.account1.getCreated().getTime() + 1000);
		Date date1 = new Date(this.account1.getCreated().getTime() + 2000);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(
			new SearchFilter("id", Operator.NOT_BETWEEN, id, id + 2),
			new SearchFilter("created", Operator.NOT_BETWEEN, date, date1)
		);

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);
	}

	@Test
	void whenAddingInFilter_thanSuccess() {

		Long id1 = this.accountDao.save(this.account);
		Long id2 = this.accountDao.save(this.account1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		OrderDto orderDto = new OrderDto();

		Map<String, OrderDto.OrderDirection> order = new HashMap<>();
		order.put(Account_.ID, OrderDto.OrderDirection.ASC);
		orderDto.setOrder(order);

		this.accountDao.addOrder(criteria, root, orderDto);

		SearchDto searchDto = new SearchDto(
			new SearchFilter("id", Operator.IN, id1, id2),
			new SearchFilter("created", Operator.IN, this.account.getCreated(), this.account1.getCreated())
		);

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(2);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);
		assertThat(retrievedAccounts.get(1)).isEqualTo(this.account1);
	}

	@Test
	void whenAddingNotInFilter_thanSuccess() {

		this.accountDao.save(this.account);
		Long id = this.accountDao.save(this.account1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(
			new SearchFilter("id", Operator.NOT_IN, id),
			new SearchFilter("created", Operator.NOT_IN, new Date(0))
		);

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);
	}

	@Test
	void whenAddingMultipleFilter_ensureThatThereIsAndOperatorBetween() {

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(
			new SearchFilter("id", Operator.NOT_IN, 1),
			new SearchFilter("created", Operator.NOT_IN, new Date(0))
		);

		this.accountDao.addFilters(criteria, root, searchDto);

		assertThat(criteria.getRestriction().getOperator()).isEqualTo(Predicate.BooleanOperator.AND);
		assertThat(criteria.getRestriction().getExpressions()).hasSize(2);
	}

	@Test
	void whenAddingFiltersWithAnotherClassThanRoot_thanSuccess() {

		Long id = this.roleDao.save(this.role);
		this.roleDao.save(this.role1);

		this.account.setRoles(List.of(this.role));
		this.account1.setRoles(List.of(this.role1));

		this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		SearchDto searchDto = new SearchDto(
			new SearchFilter("roles.id", Operator.EQUALS, id),
			new SearchFilter("roles.name", Operator.EQUALS, "testA")
		);

		this.accountDao.addFilters(criteria, root, searchDto);

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);
	}

	@Test
	void whenCreateSearchClause_thanSuccess() {

		Long id = this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		this.accountDao.and(criteria, this.accountDao.createSearchClause(criteria, builder, root, String.valueOf(id)));

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);
	}

	@Test
	void whenCreateSearchClause_thanReturnNothing() {

		this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		CriteriaBuilder builder = this.accountDao.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> root = criteria.from(Account.class);

		this.accountDao.and(criteria, this.accountDao.createSearchClause(criteria, builder, root, "search"));

		List<Account> retrievedAccounts = this.accountDao.getEntityManager().createQuery(criteria).getResultList();
		assertThat(retrievedAccounts).isEmpty();
	}

	@Test
	void whenCreateNativeQueryWithPositionalParameter_thanSuccess() {

		Long id = this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		HashMap<String, Object> params = new HashMap<>();
		params.put("accountId", id);

		NativeQuery<Account> query = this.accountDao.createNativeQuery("SELECT * FROM account WHERE id = :accountId", Account.class, params);

		List<Account> retrievedAccounts = query.list();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);

	}

	@Test
	void whenCreateNativeQueryWithPositionalParameterList_thanSuccess() {

		Long id = this.accountDao.save(this.account);
		Long id1 = this.accountDao.save(this.account1);

		HashMap<String, Object> params = new HashMap<>();
		params.put("accountId", List.of(id, id1));

		NativeQuery<Account> query = this.accountDao.createNativeQuery("SELECT * FROM account WHERE id IN :accountId ORDER BY id ASC", Account.class, params);

		List<Account> retrievedAccounts = query.list();
		assertThat(retrievedAccounts).hasSize(2);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);
		assertThat(retrievedAccounts.get(1)).isEqualTo(this.account1);

	}

	@Test
	void whenCreateNativeQueryNonTypedWithPositionalParameter_thanSuccess() {

		Long id = this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		HashMap<String, Object> params = new HashMap<>();
		params.put("accountId", id);

		NativeQuery<Account> query = (NativeQuery<Account>) this.accountDao.createNativeQuery("SELECT * FROM account WHERE id = :accountId", params)
		                                                                   .addEntity(Account.class);

		List<Account> retrievedAccounts = query.getResultList();
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);

	}

	@Test
	void whenCreateNativeQueryNonTypedWithPositionalParameterList_thanSuccess() {

		Long id = this.accountDao.save(this.account);
		Long id1 = this.accountDao.save(this.account1);

		HashMap<String, Object> params = new HashMap<>();
		params.put("accountId", List.of(id, id1));

		NativeQuery<Account> query = (NativeQuery<Account>) this.accountDao.createNativeQuery("SELECT * FROM account WHERE id IN :accountId ORDER BY id ASC", params)
		                                                                   .addEntity(Account.class);

		List<Account> retrievedAccounts = query.getResultList();
		assertThat(retrievedAccounts).hasSize(2);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);
		assertThat(retrievedAccounts.get(1)).isEqualTo(this.account1);

	}

	@Test
	void whenGettingAccount_thanSuccess() {

		this.roleDao.save(this.role);
		this.account.setRoles(List.of(this.role));
		Long id = this.accountDao.save(this.account);

		Account account = this.accountDao.get(id);

		this.accountDao.flushAndClear();
		assertThat(account).isEqualTo(this.account);
		assertThat(account.getRoles()).isNotEmpty();
		assertThat(account.getRoles().get(0)).isEqualTo(this.role);

	}

	@SuppressWarnings ("ResultOfMethodCallIgnored")
	@Test
	void whenGettingLazyAccount_thanSuccess() {

		this.roleDao.save(this.role);
		this.account.setRoles(List.of(this.role));
		Long id = this.accountDao.save(this.account);

		this.accountDao.flushAndClear();

		Account account = this.accountDao.getLazyEntity(id);
		this.accountDao.getEntityManager().detach(account);

		assertThat(account.getId()).isEqualTo(this.account.getId());
		assertThrows(LazyInitializationException.class, account::getRoles);

	}

	@Test
	void whenSearch_thanSuccess() {

		Long id = this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		SearchFilter nameFilter = new SearchFilter("firstName", Operator.EQUALS, "igor");
		SearchDto searchDto = new SearchDto(nameFilter);

		Map<String, OrderDto.OrderDirection> order = new HashMap<>();
		order.put(Account_.ID, OrderDto.OrderDirection.ASC);
		searchDto.setOrder(order);

		searchDto.setSearch(String.valueOf(id));

		List<Account> retrievedAccounts = this.accountDao.search(searchDto);
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);

		searchDto.setSearch(null);
		searchDto.removeFilter(nameFilter);
		searchDto.setPage(0);
		searchDto.setNumberPerPage(1);

		retrievedAccounts = this.accountDao.search(searchDto);
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account);

		searchDto.setPage(1);

		retrievedAccounts = this.accountDao.search(searchDto);
		assertThat(retrievedAccounts).hasSize(1);
		assertThat(retrievedAccounts.get(0)).isEqualTo(this.account1);
	}

	@Test
	void whenCount_thanSuccess() {

		Long id = this.accountDao.save(this.account);
		this.accountDao.save(this.account1);

		assertThat(this.accountDao.count(null)).isEqualTo(2);

		SearchFilter nameFilter = new SearchFilter("firstName", Operator.EQUALS, "igor");
		SearchDto searchDto = new SearchDto(nameFilter);

		Map<String, OrderDto.OrderDirection> order = new HashMap<>();
		order.put(Account_.ID, OrderDto.OrderDirection.ASC);
		searchDto.setOrder(order);

		searchDto.setSearch(String.valueOf(id));

		assertThat(this.accountDao.count(searchDto)).isEqualTo(1);

		searchDto.setSearch(null);
		searchDto.removeFilter(nameFilter);
		searchDto.setPage(0);
		searchDto.setNumberPerPage(1);

		assertThat(this.accountDao.count(searchDto)).isEqualTo(2);

		searchDto.setPage(1);

		assertThat(this.accountDao.count(searchDto)).isEqualTo(2);

	}

}
