package com.crow.iot.esp32.crowOS.backend.commons.architecture;

import com.crow.iot.esp32.crowOS.backend.commons.CommonTools;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.OrderDto;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.paginated.PaginatedDto;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.SearchDto;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.SearchFilter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Filter;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : error23
 * Created : 28/03/2020
 */
public abstract class AbstractDao<T extends AbstractEntity> {

	/** {@link AbstractEntity} class */
	private Class<T> entityClass;

	/** JPA entity manager */
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Creates new {@link AbstractDao}
	 */
	public AbstractDao() {

		this.entityClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * Gets the CriteriaBuilder from {@link #entityManager}
	 *
	 * @return criteria builder
	 */
	protected CriteriaBuilder getCriteriaBuilder() {

		return this.getEntityManager().getCriteriaBuilder();
	}

	protected EntityManager getEntityManager() {

		return this.entityManager;
	}

	protected Class<?> getEntityClass() {

		return this.entityClass;
	}

	/**
	 * Unwraps current hibernate session from {@link #entityManager}
	 *
	 * @return current Hibernate session
	 */
	private Session getCurrentSession() {

		return this.getEntityManager().unwrap(Session.class);
	}

	/**
	 * Enables one {@link Filter}
	 *
	 * @param filterName to enable
	 * @return enabled filter
	 */
	protected Filter enableFilter(String filterName) {

		return this.getCurrentSession().enableFilter(filterName);
	}

	/**
	 * Executes all SQL statements.
	 */
	protected void flush() {

		this.getCurrentSession().flush();
	}

	/**
	 * Flush and clear current session. </br>
	 * It will execute all SQL and make all lazy entities unusable
	 */
	protected void flushAndClear() {

		this.getCurrentSession().flush();
		this.getCurrentSession().clear();
	}

	/**
	 * Persists one entity into database
	 *
	 * @param entity to persist
	 * @return entity id
	 */
	public Long save(T entity) {

		Assert.notNull(entity, "entity cannot be null");
		return (Long) this.getCurrentSession().save(entity);
	}

	/**
	 * Persists a large amount of entity</br>
	 * calls {@link #flushAndClear()} every 200 statements
	 *
	 * @param entities to save
	 */
	public void batchSave(@NotNull Collection<T> entities) {

		Session session = this.getCurrentSession();

		int i = 0;
		for (T entity : entities) {
			session.save(entity);
			i++;
			if (i % 200 == 0) this.flushAndClear();
		}

	}

	/**
	 * Deletes one entity.
	 *
	 * @param entity to delete
	 */
	public void delete(T entity) {

		this.getCurrentSession().delete(entity);
	}

	/**
	 * Deletes a large amount of entity </br>
	 * calls {@link #flushAndClear()} every 200 statements
	 *
	 * @param entities to delete
	 */
	public void batchDelete(@NotNull Collection<T> entities) {

		Session session = this.getCurrentSession();
		int i = 0;

		for (T entity : entities) {
			session.delete(entity);
			i++;
			if (i % 200 == 0) this.flush();
		}
	}

	/**
	 * Refreshes one entity from the db
	 *
	 * @param entity to refresh
	 */
	public void refresh(T entity) {

		this.getEntityManager().refresh(entity);
	}

	/**
	 * Reattach one entity to current transaction
	 *
	 * @param entity to reattach
	 */
	public void reattach(T entity) {

		this.getCurrentSession().lock(entity, LockMode.NONE);
	}

	/**
	 * Adds a join to from if not exists
	 *
	 * @param from          to add the join to
	 * @param attributeName join attribute name to add
	 * @param <X>           from parameter
	 * @param <Y>           from parameter
	 * @return found or added join
	 */
	protected <X, Y> Join<Y, ?> findJoinByAttributeName(@NotNull From<X, Y> from, String attributeName) {

		Set<Join<Y, ?>> joins = from.getJoins();

		for (Join<Y, ?> join : joins) {
			if (attributeName.equals(join.getAttribute().getName())) return join;
		}
		// If join is null, create it
		return from.join(attributeName);
	}

	/**
	 * Adds one and clause to the criteria if where clause doesn't exist creates it
	 *
	 * @param criteria  to add to
	 * @param predicate to add
	 */
	protected void and(@NotNull AbstractQuery<?> criteria, Predicate predicate) {

		if (criteria.getRestriction() != null) {
			criteria.where(this.getCriteriaBuilder().and(criteria.getRestriction(), predicate));
		}
		else {
			criteria.where(predicate);
		}
	}

	/**
	 * Adds one or clause to the criteria if where clause doesn't exist creates it
	 *
	 * @param criteria  to add to
	 * @param predicate to add
	 */
	protected void or(@NotNull AbstractQuery<?> criteria, Predicate predicate) {

		if (criteria.getRestriction() != null) {
			criteria.where(this.getCriteriaBuilder().or(criteria.getRestriction(), predicate));
		}
		else {
			criteria.where(predicate);
		}
	}

	/**
	 * Adds pagination to one {@link TypedQuery} from {@link PaginatedDto}
	 *
	 * @param query      to add pagination to
	 * @param pagination to add
	 */
	protected void addPagination(TypedQuery<?> query, @NotNull PaginatedDto pagination) {

		if (pagination.getPage() == null || pagination.getNumberPerPage() == null) return;
		query.setFirstResult(pagination.getPage() * pagination.getNumberPerPage());
		query.setMaxResults(pagination.getNumberPerPage());

	}

	/**
	 * Adds order to one criteria
	 *
	 * @param criteria to add order from
	 * @param root     to add order from
	 * @param orderDto to add order from
	 */
	protected void addOrder(CriteriaQuery<?> criteria, Root<T> root, @NotNull OrderDto orderDto) {

		if (orderDto.getOrder() == null) return;
		CriteriaBuilder builder = this.getCriteriaBuilder();
		List<Order> orders = new ArrayList<>();

		for (Map.Entry<String, OrderDto.OrderDirection> order : orderDto.getOrder().entrySet()) {

			String[] splitProperty = order.getKey().split("\\.");
			From<?, ?> from = root;

			for (int i = 0; i < splitProperty.length - 1; i++) {
				from = this.findJoinByAttributeName(from, splitProperty[i]);
			}

			if (OrderDto.OrderDirection.DESC.equals(order.getValue())) {
				orders.add(builder.desc(from.get(splitProperty[splitProperty.length - 1])));
			}
			else if (OrderDto.OrderDirection.ASC.equals(order.getValue())) {
				orders.add(builder.asc(from.get(splitProperty[splitProperty.length - 1])));
			}

		}

		criteria.orderBy(orders);
	}

	/**
	 * Adds {@link SearchFilter} from {@link SearchDto} to one {@link CriteriaQuery}
	 *
	 * @param criteria to add to
	 * @param dto      to add from
	 */
	protected void addFilters(CriteriaQuery<?> criteria, Root<T> root, @NotNull SearchDto dto) {

		if (StringUtils.isNotBlank(dto.getSearch())) {
			this.and(criteria, this.createSearchClause(criteria, this.getCriteriaBuilder(), root, dto.getSearch()));
		}

		if (CollectionUtils.isEmpty(dto.getSearchFilters())) return;

		for (SearchFilter filter : dto.getSearchFilters()) {
			CommonTools.invokeStaticMethod(this.entityClass, "addFilter", new Class<?>[] { From.class, AbstractDao.class, AbstractQuery.class, SearchFilter.class }, new Object[] { root, this, criteria, filter });

		}
	}

	/**
	 * Create a search clause for criteria</br>
	 * Default implementation allows just search by the id<br/>
	 * override this method in order to add more specific implementation
	 *
	 * @param criteria to add search close to
	 * @param builder  to use
	 * @param root     criteria root
	 * @param search   clause
	 * @return criteria with search clause
	 */
	protected Predicate createSearchClause(CriteriaQuery<?> criteria, @NotNull CriteriaBuilder builder, @NotNull Path<T> root, String search) {

		try {
			Long id = Long.parseLong(search);
			return builder.equal(root.get("id"), id);

		}
		catch (NumberFormatException e) {
			return builder.disjunction();
		}

	}

	/**
	 * Sets positional parameters to one query
	 *
	 * @param query                to set for
	 * @param positionalParameters to be set
	 * @return query with parameters set
	 */
	@Contract ("_, _ -> param1")
	private <E> NativeQuery<E> setPositionalParameters(NativeQuery<E> query, @NotNull HashMap<String, Object> positionalParameters) {

		for (String key : positionalParameters.keySet()) {
			Object value = positionalParameters.get(key);
			if (value instanceof Collection<?>) {
				query.setParameterList(key, (Collection<?>) value);
			}
			else {
				query.setParameter(key, value);
			}
		}
		return query;
	}

	/**
	 * Creates one {@link NativeQuery}
	 *
	 * @param queryString query string
	 * @return created query
	 */
	protected NativeQuery<?> createNativeQuery(String queryString) {

		return this.getCurrentSession().createNativeQuery(queryString);
	}

	/**
	 * Creates one {@link NativeQuery} and sets the result class
	 *
	 * @param queryString to execute
	 * @param resultClass to get
	 * @return created query
	 */
	protected <E> NativeQuery<E> createNativeQuery(String queryString, Class<E> resultClass) {

		return this.getCurrentSession().createNativeQuery(queryString, resultClass);
	}

	/**
	 * Creates one {@link NativeQuery} and set its positional parameters
	 *
	 * @param queryString          to execute
	 * @param positionalParameters to be set
	 * @return created query
	 */
	protected NativeQuery<?> createNativeQuery(String queryString, HashMap<String, Object> positionalParameters) {

		NativeQuery<?> query = this.createNativeQuery(queryString);
		return this.setPositionalParameters(query, positionalParameters);
	}

	/**
	 * Creates one {@link NativeQuery} and set its positional parameters
	 *
	 * @param queryString          to execute
	 * @param positionalParameters to be set
	 * @param resultClass          to get
	 * @return created query
	 */
	protected <E> NativeQuery<E> createNativeQuery(String queryString, Class<E> resultClass, HashMap<String, Object> positionalParameters) {

		NativeQuery<E> query = this.createNativeQuery(queryString, resultClass);
		return this.setPositionalParameters(query, positionalParameters);
	}

	/**
	 * Gets one entity by its id
	 *
	 * @param id to get for
	 * @return entity to get
	 */
	public T get(@NotNull Long id) {

		return this.getEntityManager().find(this.entityClass, id);
	}

	/**
	 * Gets a lazy proxy of the entity, without calling the Database<br/>
	 *
	 * @param id to get for
	 * @return a lazy entity
	 */
	public T getLazyEntity(@NotNull Long id) {

		Assert.notNull(id, "id cannot be null");
		return this.getEntityManager().getReference(this.entityClass, id);
	}

	/**
	 * Lists all entities from database
	 *
	 * @return a list of entities
	 */
	public List<T> list() {

		CriteriaBuilder builder = this.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(this.entityClass);
		Root<T> root = criteria.from(this.entityClass);
		criteria.select(root);

		return this.getEntityManager().createQuery(criteria).getResultList();
	}

	/**
	 * Searches for a paginated ordered and filtered list of entities
	 *
	 * @param dto to search with
	 * @return result list
	 */
	public List<T> search(@NotNull SearchDto dto) {

		CriteriaBuilder builder = this.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(this.entityClass);
		Root<T> root = criteria.from(this.entityClass);

		this.addFilters(criteria, root, dto);
		this.addOrder(criteria, root, dto);

		TypedQuery<T> query = this.getEntityManager().createQuery(criteria);
		this.addPagination(query, dto);

		return query.getResultList();

	}

	/**
	 * Count number total of tuples filtered by {@link SearchDto}
	 *
	 * @param searchDto to filter with
	 * @return number of tuples
	 */
	public Long count(SearchDto searchDto) {

		CriteriaBuilder builder = this.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<T> root = criteria.from(this.entityClass);
		criteria.select(builder.countDistinct(root));
		if (searchDto != null) this.addFilters(criteria, root, searchDto);

		return this.getEntityManager().createQuery(criteria).getSingleResult();

	}

}
