package com.crow.iot.esp32.crowOS.backend.featureData;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractEndpoint;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : error23
 * Created : 01/05/2021
 */
@RestController
@RequestMapping ("/featureData")
@RequiredArgsConstructor
public class FeatureDataEndpoint extends AbstractEndpoint {

	private final FeatureDataService featureDataService;

	private final FeatureDataMapper mapper;

	@Operation (summary = "Lists all feature datas for connected user")
	@GetMapping ()
	@ResponseBody
	@ResponseStatus (HttpStatus.OK)
	public List<FeatureDataDto> list() {

		List<FeatureData> featureDatas = this.featureDataService.list();

		ArrayList<FeatureDataDto> featureDataDtos = new ArrayList<>();
		for (FeatureData featureData : featureDatas) {
			featureDataDtos.add(this.mapper.toDto(featureData));
		}

		return featureDataDtos;
	}

	@Operation (summary = "Updates or creates feature data")
	@PutMapping
	@ResponseBody
	@ResponseStatus (HttpStatus.ACCEPTED)
	public List<FeatureDataDto> createOrUpdate(@RequestBody List<FeatureDataDto> dtos) throws MethodArgumentNotValidException {

		List<FeatureData> featureDatas = this.featureDataService.updateOrCreate(dtos);

		ArrayList<FeatureDataDto> featureDataDtos = new ArrayList<>();
		for (FeatureData featureData : featureDatas) {
			featureDataDtos.add(this.mapper.toDto(featureData));
		}

		return featureDataDtos;
	}

}
