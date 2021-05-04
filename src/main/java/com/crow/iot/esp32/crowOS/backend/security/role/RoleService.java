package com.crow.iot.esp32.crowOS.backend.security.role;

import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.MethodArgumentNotValidExceptionFactory;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.Operator;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.SearchDto;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.SearchFilter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

/**
 * @author : error23
 * Created : 22/05/2020
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {

	private final RoleDao roleDao;

	private final RoleMapper mapper;

	/**
	 * Retrieves one {@link Role} from database
	 *
	 * @param id of role to retrieve
	 * @return retrieved {@link Role}
	 */
	@PostAuthorize ("hasPermission(returnObject, 'READ')")
	public Role get(Long id) throws MethodArgumentNotValidException {

		if (id == null) throw MethodArgumentNotValidExceptionFactory.NOT_NULL(this, "id");

		Role role = this.roleDao.get(id);
		if (role == null) throw new ResourceNotFoundException("Role", id);

		return role;
	}

	/**
	 * Search for a list of {@link Role} by their ids
	 *
	 * @param ids to search for
	 * @return list of roles
	 */
	@PostAuthorize ("hasPermission(returnObject, 'READ')")
	public List<Role> list(@NotNull List<Long> ids) {

		SearchDto searchDto = new SearchDto(
			new SearchFilter(Role_.ID, Operator.IN, ids.toArray())
		);

		return this.roleDao.search(searchDto);
	}

	/**
	 * Creates one new {@link Role}
	 *
	 * @param roleDto to create from
	 * @return created {@link Role}
	 */
	@PreAuthorize ("hasPermission('ROLE', 'CREATE')")
	public Role create(@NotNull RoleDto roleDto) {

		Role created = this.mapper.toEntity(roleDto);
		this.roleDao.save(created);

		return created;
	}

	/**
	 * Updates one {@link Role}
	 *
	 * @param role    to update
	 * @param roleDto to update from
	 * @return updated {@link Role}
	 */
	@PreAuthorize ("hasPermission(#role,'UPDATE')")
	public Role update(@NotNull Role role, @NotNull RoleDto roleDto) {

		this.mapper.merge(roleDto, role);
		return role;
	}

	/**
	 * Delete one {@link Role}
	 *
	 * @param role to delete
	 */
	@PreAuthorize ("hasPermission(#role,'DELETE')")
	public void delete(@NotNull Role role) {

		this.roleDao.delete(role);
	}

}
