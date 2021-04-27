package com.crow.iot.esp32.crowOS.backend.commons.architecture.dto;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author : error23
 * Created : 06/06/2020
 */
@SpringBootTest
class AbstractMapperTest {

	SimpleMapper mapper = Mappers.getMapper(SimpleMapper.class);

	SimpleDto dto;

	SimpleEntity entity;

	@Mapper (config = AbstractMapper.class)
	interface SimpleMapper extends AbstractMapper<SimpleDto, SimpleEntity> {

	}

	@Getter
	@Setter
	static class SimpleEntity extends AbstractEntity {

		private static final long serialVersionUID = 3831515313929201380L;
		private Long id;
		private String firstName;
		private String lastName;

	}

	@Getter
	@Setter
	static class SimpleDto extends IdDto {

		private String firstName;
		private String lastName;

	}

	@BeforeEach
	void setUp() {

		this.dto = new SimpleDto();
		this.dto.setId(1L);
		this.dto.setLastName("lastName");
		this.dto.setFirstName("firstName");

		this.entity = new SimpleEntity();
		this.entity.setId(1L);
		this.entity.setLastName("lastName");
		this.entity.setFirstName("firstName");
	}

	@Test
	void whenCallingToEntity_thanConvertDtoToEntity() {

		SimpleEntity simpleEntity = this.mapper.toEntity(this.dto);

		assertThat(simpleEntity.getId()).isEqualTo(this.dto.getId());
		assertThat(simpleEntity.getLastName()).isEqualTo(this.dto.getLastName());
		assertThat(simpleEntity.getFirstName()).isEqualTo(this.dto.getFirstName());
	}

	@Test
	void whenCallingToDto_thanConvertEntityToDto() {

		SimpleDto simpleDto = this.mapper.toDto(this.entity);

		assertThat(this.entity.getId()).isEqualTo(simpleDto.getId());
		assertThat(this.entity.getLastName()).isEqualTo(simpleDto.getLastName());
		assertThat(this.entity.getFirstName()).isEqualTo(simpleDto.getFirstName());
	}

	@Test
	void whenMergingToEmptyEntity_ThanMergeEverythingFromDtoToEntity() {

		SimpleEntity simpleEntity = new SimpleEntity();
		simpleEntity = this.mapper.merge(this.dto, simpleEntity);

		assertThat(simpleEntity.getId()).isNull();
		assertThat(simpleEntity.getLastName()).isEqualTo(this.dto.getLastName());
		assertThat(simpleEntity.getFirstName()).isEqualTo(this.dto.getFirstName());

	}

	@Test
	void whenMergingToNotEmptyEntity_thanOverrideEntityValues() {

		this.dto.setFirstName("error23");
		this.dto.setLastName("rolly");
		this.dto.setId(2L);

		this.entity = this.mapper.merge(this.dto, this.entity);

		assertThat(this.entity.getId()).isEqualTo(1L); // Ignore id
		assertThat(this.entity.getLastName()).isEqualTo(this.dto.getLastName());
		assertThat(this.entity.getFirstName()).isEqualTo(this.dto.getFirstName());
	}

	@Test
	void whenMergingToNotEmptyEntity_thanIgnoreNullValuesAndOverrideTheRest() {

		// value to ignore
		this.dto.setId(null);

		// value to override
		this.entity.firstName = null;
		this.entity.lastName = null;

		this.mapper.merge(this.dto, this.entity);

		assertThat(this.entity.getId()).isEqualTo(1L);
		assertThat(this.entity.getLastName()).isEqualTo(this.dto.getLastName());
		assertThat(this.entity.getFirstName()).isEqualTo(this.dto.getFirstName());
	}

}
