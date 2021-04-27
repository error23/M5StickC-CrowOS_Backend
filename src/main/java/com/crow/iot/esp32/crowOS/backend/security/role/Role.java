package com.crow.iot.esp32.crowOS.backend.security.role;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractEntity;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Permission;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.springframework.util.CollectionUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;

/**
 * @author : error23
 * Created : 13/04/2020
 */
@Entity
@DynamicUpdate
@Table (name = "role")
@Getter
@Setter
@BatchSize (size = 200)
public class Role extends AbstractEntity {

	private static final long serialVersionUID = 5212023204628428841L;

	@Id
	@Column (name = "id", nullable = false)
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "role_gen")
	@SequenceGenerator (name = "role_gen", sequenceName = "role_seq")
	private Long id;

	@Column (name = "priority")
	private Integer priority;

	@Column (name = "name")
	private String name;

	@Column (name = "root", nullable = false)
	private boolean root = false;

	@Type (type = "jsonb")
	@Column (name = "permissions", columnDefinition = "jsonb")
	private List<Permission> permissions;

	/**
	 * Indicates if this {@link Role} has @{@link Privilege} on @{@link SecuredResource}
	 *
	 * @param privilege       to check for
	 * @param securedResource to check for
	 * @return true if can
	 */
	public boolean can(Privilege privilege, SecuredResource securedResource) {

		if (this.root) return true;
		if (CollectionUtils.isEmpty(this.permissions)) return false;

		for (Permission permission : this.permissions) {
			if (permission.getSecuredResource().equals(securedResource)) {
				return permission.getPrivileges().contains(privilege);
			}
		}

		return false;
	}

}
