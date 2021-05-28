package com.crow.iot.esp32.crowOS.backend.account;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractEntity;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;
import java.util.Locale;

/**
 * @author : error23
 * Created : 28/03/2020
 */
@Entity
@DynamicUpdate
@Table (name = "account")
@Getter
@Setter
public class Account extends AbstractEntity {

	private static final long serialVersionUID = - 5056597107226048243L;

	@Id
	@Column (name = "id", nullable = false)
	@Access (AccessType.PROPERTY)
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "account_gen")
	@SequenceGenerator (name = "account_gen", sequenceName = "account_seq")
	@JsonIdentityInfo (generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	private Long id;

	@Column (name = "first_name")
	private String firstName;

	@Column (name = "last_name")
	private String lastName;

	@Column (name = "email", nullable = false)
	private String email;

	@Column (name = "password", nullable = false)
	@JsonIgnore
	private String password;

	@Column (name = "enabled", nullable = false)
	private boolean enabled = true;

	@Type (type = "locale")
	@Column (name = "locale")
	private Locale locale;

	@ManyToMany (fetch = FetchType.LAZY)
	@BatchSize (size = 200)
	@JoinTable (
		name = "role_l_account",
		joinColumns = @JoinColumn (name = "account_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn (name = "role_id", referencedColumnName = "id")
	)
	@OrderBy ("priority desc")
	private List<Role> roles;

}
