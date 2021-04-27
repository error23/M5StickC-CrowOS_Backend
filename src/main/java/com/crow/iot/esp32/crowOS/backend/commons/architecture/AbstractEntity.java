package com.crow.iot.esp32.crowOS.backend.commons.architecture;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.commons.CommonTools;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.Operator;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.SearchFilter;
import com.crow.iot.esp32.crowOS.backend.commons.json.JsonHelper;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeStringType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.hibernate.HibernateException;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author : error23
 * Created : 28/03/2020
 */
@MappedSuperclass
@DynamicUpdate
@TypeDefs ({
	@TypeDef (name = "string-array", typeClass = StringArrayType.class),
	@TypeDef (name = "int-array", typeClass = IntArrayType.class),
	@TypeDef (name = "json", typeClass = JsonStringType.class),
	@TypeDef (name = "jsonb", typeClass = JsonBinaryType.class),
	@TypeDef (name = "jsonb-node", typeClass = JsonNodeBinaryType.class),
	@TypeDef (name = "json-node", typeClass = JsonNodeStringType.class),
})
@Getter
@Slf4j
public abstract class AbstractEntity implements Serializable {

	private static final long serialVersionUID = - 9191853664639713048L;

	@Column (name = "created")
	@Temporal (TemporalType.TIMESTAMP)
	private Date created;

	@Column (name = "updated")
	@Temporal (TemporalType.TIMESTAMP)
	private Date updated;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn (name = "owner")
	@Setter
	@JsonIgnore
	private Account owner;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn (name = "updated_by")
	@JsonIgnore
	private Account updatedBy;

	@Column (name = "version")
	@Version
	private Integer version;

	/**
	 * Called when creating new entity in order to set {@link #created}, {@link #updated} dates and {@link #owner}
	 */
	@PrePersist
	protected void prePersist() {

		this.created = new Date();
		this.updated = new Date();
		this.owner = SecurityTools.getConnectedAccount();
	}

	/**
	 * Called on update entity to set {@link #updated} and {@link #updatedBy}
	 */
	@PreUpdate
	protected void preUpdate() {

		this.updated = new Date();
		this.updatedBy = SecurityTools.getConnectedAccount();
	}

	/**
	 * Override equals in order to equals on id
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) return true;
		if (obj == null) return false;

		if (this.getClass().isAssignableFrom(obj.getClass())) {
			return Objects.equals(this.getId(), ((AbstractEntity) obj).getId());
		}
		else {
			return false;
		}
	}

	/**
	 * Overrides hashcode in order to use id hashcode
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		return Objects.hashCode(this.getId());
	}

	/**
	 * Adds one {@link SearchFilter} filter to {@link AbstractQuery} criteria using {@link AbstractDao} hibernate dao for this specific object
	 * this method is called by reflexivity from {@link AbstractDao#addFilters} class
	 * override this method in order to add custom filters if needed
	 *
	 * @param from        clause
	 * @param abstractDao to use
	 * @param criteria    to add to
	 * @param filter      to add
	 */
	public static void addFilter(From<?, ?> from, @NotNull AbstractDao<?> abstractDao, AbstractQuery<?> criteria, SearchFilter filter) {

		CriteriaBuilder builder = abstractDao.getCriteriaBuilder();

		try {

			// Retrieve the current entityClass
			Class<?> entityClass = from.getJavaType();

			// Find the class of the first property
			String property = filter.getProperty();
			int dotIndex = property.indexOf('.');
			String firstProperty = dotIndex > - 1 ? property.substring(0, dotIndex) : property;

			Class<?> propertyClass = CommonTools.findPropertyClass(entityClass, firstProperty);
			if (propertyClass == null) throw new HibernateException("No property " + firstProperty + " on " + entityClass.getSimpleName());

			// If first property is  hibernate abstract Entity, and we are looking for a sub-property of that Entity, call addFilter on that Entity
			if (dotIndex > - 1 && AbstractEntity.class.isAssignableFrom(propertyClass)) {

				// Create next filter by removing the first property
				SearchFilter nextFilter = new SearchFilter(property.substring(dotIndex + 1), filter.getOperator(), filter.getValues());

				// Get the join to that next Entity
				Join<?, ?> nextJoin = abstractDao.findJoinByAttributeName(from, firstProperty);

				// Call addFilter on the next Path
				CommonTools.invokeStaticMethod(propertyClass, "addFilter", new Class<?>[] { From.class, AbstractDao.class, AbstractQuery.class, SearchFilter.class }, new Object[] { nextJoin, abstractDao, criteria, nextFilter });
				return;

			}

			// Else, build the new Predicate
			Predicate whereClause;

			// Build a Path through the first property
			Path<?> nextPath = from.get(firstProperty);

			// If first property is a JSON field
			Type typeAnnotation = CommonTools.findPropertyAnnotation(entityClass, firstProperty, org.hibernate.annotations.Type.class);
			if (typeAnnotation != null && typeAnnotation.type().equals("jsonb")) {

				// If there is no path inside the JSON field, build a "classic" predicate
				if (dotIndex == - 1) {
					whereClause = buildPredicate(builder, nextPath, filter.getOperator(), filter.getValues());
				}
				// Else, build a "jsonb" predicate
				else {
					String pathInsideJsonField = property.substring(dotIndex + 1);
					whereClause = buildPredicateJsonb(builder, nextPath, pathInsideJsonField, filter.getOperator(), filter.getValues());
				}
			}

			// If it's a non-JSON, non-other-entity, field
			else {

				// Cast all values
				List<Object> castedValues = new ArrayList<>();
				if (! CollectionUtils.isEmpty(filter.getValues())) {
					for (Object value : filter.getValues()) {
						castedValues.add(CommonTools.cast(value, propertyClass));
					}
				}
				// Build where clause with casted values
				whereClause = buildPredicate(builder, nextPath, filter.getOperator(), castedValues);
			}

			// Add the new where clause
			abstractDao.and(criteria, whereClause);
		}
		catch (Exception e) {
			log.info("Unable to apply a filter: {}", JsonHelper.fromObjectToString(filter), e);
			throw new HibernateException("Unable to apply a filter: " + JsonHelper.fromObjectToString(filter));
		}
	}

	/**
	 * Builds criteria predicate that will check (pathExpression operator value) is true
	 *
	 * @param builder        to build with
	 * @param pathExpression to check
	 * @param operator       to use
	 * @param values         to check with
	 * @return built predicate
	 */
	private static Predicate buildPredicate(CriteriaBuilder builder, Expression pathExpression, @NotNull Operator operator, List<Object> values) {

		switch (operator) {

			case NULL:
				return builder.isNull(pathExpression);
			case NOT_NULL:
				return builder.isNotNull(pathExpression);

			case EQUALS:
				return builder.equal(pathExpression, values.get(0));
			case NOT_EQUALS:
				return builder.not(builder.equal(pathExpression, values.get(0)));

			case MATCHES:
				return builder.like(pathExpression, "%" + values.get(0) + "%");
			case MATCHES_START:
				return builder.like(pathExpression, values.get(0) + "%");
			case MATCHES_END:
				return builder.like(pathExpression, "%" + values.get(0));

			case IGNORE_CASE_MATCHES:
				return builder.like(builder.upper(pathExpression), ("%" + values.get(0) + "%").toUpperCase());
			case IGNORE_CASE_MATCHES_START:
				return builder.like(builder.upper(pathExpression), (values.get(0) + "%").toUpperCase());
			case IGNORE_CASE_MATCHES_END:
				return builder.like(builder.upper(pathExpression), ("%" + values.get(0)).toUpperCase());

			case NOT_MATCHES:
				return builder.not(builder.like(pathExpression, "%" + values.get(0) + "%"));
			case IGNORE_CASE_NOT_MATCHES:
				return builder.not(builder.like(builder.upper(pathExpression), ("%" + values.get(0) + "%").toUpperCase()));

			case GREATER_THAN:
				return builder.greaterThan(pathExpression, (Comparable) values.get(0));
			case GREATER_EQUALS_THAN:
				return builder.greaterThanOrEqualTo(pathExpression, (Comparable) values.get(0));

			case LOWER_THAN:
				return builder.lessThan(pathExpression, (Comparable) values.get(0));
			case LOWER_EQUALS_THAN:
				return builder.lessThanOrEqualTo(pathExpression, (Comparable) values.get(0));

			case BETWEEN:
				return builder.between(pathExpression, (Comparable) values.get(0), (Comparable) values.get(1));
			case NOT_BETWEEN:
				return builder.not(builder.between(pathExpression, (Comparable) values.get(0), (Comparable) values.get(1)));

			case IN:
				return pathExpression.in(values);
			case NOT_IN:
				return builder.not(pathExpression.in(values));
			default:
				throw new NotImplementedException("This operator is not yet implemented");
		}
	}

	/**
	 * Builds criteria predicate that will check (pathExpression->jsonbPath operator value) is true
	 *
	 * @param builder        to build with
	 * @param pathExpression to check
	 * @param jsonbPath      to check
	 * @param operator       to use
	 * @param values         to check with
	 * @return built predicate
	 */
	private static Predicate buildPredicateJsonb(CriteriaBuilder builder, Expression pathExpression, @NotNull String jsonbPath, Operator operator, List<Object> values) {

		String[] jsonbPathSplit = jsonbPath.split("\\.");
		Expression[] jsonbPathElements = new Expression[jsonbPathSplit.length + 1];

		jsonbPathElements[0] = pathExpression;
		for (int i = 0; i < jsonbPathSplit.length; i++) {
			jsonbPathElements[i + 1] = builder.literal(jsonbPathSplit[i]);
		}

		List<Expression> jsonbValues = new ArrayList<>();
		switch (operator) {
			case MATCHES:
			case MATCHES_START:
			case MATCHES_END:
			case IGNORE_CASE_MATCHES_START:
			case IGNORE_CASE_MATCHES_END:
			case NOT_MATCHES:
			case IGNORE_CASE_NOT_MATCHES:
				pathExpression = builder.function("jsonb_extract_path_text", Object.class, jsonbPathElements);
				break;
			default:

				pathExpression = builder.function("jsonb_extract_path", Object.class, jsonbPathElements);

				for (Object value : values) {
					jsonbValues.add(builder.function("to_jsonb", Object.class, builder.literal(value)));
				}
		}

		switch (operator) {

			case NULL:
				return builder.isNull(pathExpression);
			case NOT_NULL:
				return builder.isNotNull(pathExpression);
			case EQUALS:
				return builder.equal(pathExpression, jsonbValues.get(0));
			case NOT_EQUALS:
				return builder.not(builder.equal(pathExpression, jsonbValues.get(0)));

			case MATCHES:
				return builder.like(pathExpression, "%" + values.get(0) + "%");
			case MATCHES_START:
				return builder.like(pathExpression, values.get(0) + "%");
			case MATCHES_END:
				return builder.like(pathExpression, "%" + values.get(0));

			case IGNORE_CASE_MATCHES:
				return builder.like(builder.upper(pathExpression), ("%" + values.get(0) + "%").toUpperCase());
			case IGNORE_CASE_MATCHES_START:
				return builder.like(builder.upper(pathExpression), (values.get(0) + "%").toUpperCase());
			case IGNORE_CASE_MATCHES_END:
				return builder.like(builder.upper(pathExpression), ("%" + values.get(0)).toUpperCase());

			case NOT_MATCHES:
				return builder.not(builder.like(pathExpression, (String) values.get(0)));
			case IGNORE_CASE_NOT_MATCHES:
				return builder.not(builder.like(builder.upper(pathExpression), ((String) values.get(0)).toUpperCase()));

			case GREATER_THAN:
				return builder.greaterThan(pathExpression, (Expression<Comparable>) jsonbValues.get(0));
			case GREATER_EQUALS_THAN:
				return builder.greaterThanOrEqualTo(pathExpression, (Expression<Comparable>) jsonbValues.get(0));

			case LOWER_THAN:
				return builder.lessThan(pathExpression, (Expression<Comparable>) jsonbValues.get(0));
			case LOWER_EQUALS_THAN:
				return builder.lessThanOrEqualTo(pathExpression, (Expression<Comparable>) jsonbValues.get(0));

			case BETWEEN:
				return builder.between(pathExpression, (Expression<Comparable>) jsonbValues.get(0), (Expression<Comparable>) jsonbValues.get(1));
			case NOT_BETWEEN:
				return builder.not(builder.between(pathExpression, (Expression<Comparable>) jsonbValues.get(0), (Expression<Comparable>) jsonbValues.get(1)));
			case IN:
				return pathExpression.in(jsonbValues);
			case NOT_IN:
				return builder.not(pathExpression.in(jsonbValues));
			default:
				throw new NotImplementedException("This operator is not yet implemented");

		}

	}

	/**
	 * @return gets id from database
	 */
	public abstract Long getId();

	@Override
	public String toString() {

		return JsonHelper.fromObjectToString(this);
	}
}
