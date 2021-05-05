package com.crow.iot.esp32.crowOS.backend.featureData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author : error23
 * Created : 05/05/2021
 */
@SpringBootTest
class FeatureDataMapperTest {

	@Autowired
	FeatureDataMapper mapper;

	FeatureData featureData;

	FeatureDataDto featureDataDto;

	@BeforeEach
	void setUp() {

		this.featureData = new FeatureData();
		this.featureData.setId(1L);
		this.featureData.setFeatureFactoryName("featureFactoryName");
		this.featureData.setSavedData("{\"testData\":true}");

		this.featureDataDto = new FeatureDataDto();
		this.featureDataDto.setId(1L);
		this.featureDataDto.setFeatureFactoryName("featureFactoryName");
		this.featureDataDto.setSavedData("{\"testData\":true}");
	}

	@Test
	void whenConvertingToDto_thanSuccess() {

		FeatureDataDto dto = this.mapper.toDto(this.featureData);
		assertThat(dto.toString()).isEqualTo(this.featureDataDto.toString());
	}

	@Test
	void whenConvertingToEntity_thanSuccess() {

		FeatureData entity = this.mapper.toEntity(this.featureDataDto);
		assertThat(entity).isEqualToComparingFieldByField(this.featureData);
	}

	@Test
	void whenMerging_thanSuccess() {

		FeatureData featureData = this.mapper.merge(this.featureDataDto, new FeatureData());
		assertThat(featureData).isEqualToComparingFieldByField(featureData);
	}

}
