package com.crow.iot.esp32.crowOS.backend.commons.architecture.dto;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author : error23
 * Created : 22/05/2020
 */
@SuppressWarnings ({ "EmptyMethod", "unused", "RedundantSuppression" })
@MapperConfig (
	componentModel = "spring",
	unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface AbstractMapper<D extends AbstractDto, E extends AbstractEntity> {

	/**
	 * Converts dto to entity
	 *
	 * @param dto to convert
	 * @return converted entity
	 */
	E toEntity(D dto);

	/**
	 * Converts entity to dto
	 *
	 * @param entity to convert
	 * @return converted dto
	 */
	D toDto(E entity);

	/**
	 * Merges dto to entity
	 *
	 * @param dto    to merge
	 * @param entity to merge to
	 * @return merged entity
	 */
	@Mapping (target = "id", ignore = true)
	E merge(D dto, @MappingTarget E entity);

}
