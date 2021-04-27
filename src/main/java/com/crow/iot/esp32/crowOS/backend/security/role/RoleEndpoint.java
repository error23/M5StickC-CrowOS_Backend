package com.crow.iot.esp32.crowOS.backend.security.role;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : error23
 * Created : 05/06/2020
 */
@Controller
@RequestMapping ("/role")
@RequiredArgsConstructor
public class RoleEndpoint extends AbstractEndpoint {

	private final RoleService roleService;

	private final RoleMapper mapper;

	@GetMapping ("/{roleId:[0-9]+}")
	@ResponseBody
	@ResponseStatus (HttpStatus.OK)
	public RoleDto get(@PathVariable ("roleId") Long id) throws MethodArgumentNotValidException {

		Role role = this.roleService.get(id);
		return this.mapper.toDto(role);
	}

	@PostMapping
	@ResponseBody
	@ResponseStatus (HttpStatus.CREATED)
	public RoleDto create(@RequestBody RoleDto dto) {

		Role role = this.roleService.create(dto);
		return this.mapper.toDto(role);
	}

	@PatchMapping
	@ResponseBody
	@ResponseStatus (HttpStatus.ACCEPTED)
	public RoleDto update(@RequestBody RoleDto dto) throws MethodArgumentNotValidException {

		Role role = this.roleService.get(dto.getId());
		return this.mapper.toDto(this.roleService.update(role, dto));
	}

	@DeleteMapping ("/{roleId:[0-9]+}")
	@ResponseBody
	@ResponseStatus (HttpStatus.ACCEPTED)
	public RoleDto delete(@PathVariable ("roleId") Long id) throws MethodArgumentNotValidException {

		Role role = this.roleService.get(id);
		this.roleService.delete(role);
		return this.mapper.toDto(role);

	}
}
