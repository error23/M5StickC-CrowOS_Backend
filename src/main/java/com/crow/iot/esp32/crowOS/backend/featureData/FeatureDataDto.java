package com.crow.iot.esp32.crowOS.backend.featureData;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.IdDto;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * @author : error23
 * Created : 01/05/2021
 */
@Getter
@Setter
public class FeatureDataDto extends IdDto {

	@NotNull
	private String featureName;
	private String savedData;

}
