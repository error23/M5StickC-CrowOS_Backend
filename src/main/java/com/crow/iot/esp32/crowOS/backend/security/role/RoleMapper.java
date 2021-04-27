package com.crow.iot.esp32.crowOS.backend.security.role;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.AbstractMapper;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * @author : error23
 * Created : 05/06/2020
 */
@Mapper (config = AbstractMapper.class)
public interface RoleMapper extends AbstractMapper<RoleDto, Role> {

	@Mapping (target = "root", ignore = true)
	@Override
	Role toEntity(RoleDto dto);

	@Mapping (target = "root", ignore = true)
	@Override
	Role merge(RoleDto dto, @MappingTarget Role entity);

	@BeforeMapping
	default void permissionCheck(@NotNull RoleDto roleDto, @MappingTarget Role role) {

		if (roleDto.isChanged("root")) {
			SecurityTools.assertIsConnectedAccountRoot();
			role.setRoot(roleDto.getRoot());
		}
	}
}

