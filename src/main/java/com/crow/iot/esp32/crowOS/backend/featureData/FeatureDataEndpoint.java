package com.crow.iot.esp32.crowOS.backend.featureData;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;

/**
 * @author : error23
 * Created : 01/05/2021
 */
@Controller
@RequestMapping ("/featureData")
@RequiredArgsConstructor
public class FeatureDataEndpoint {

	private final FeatureDataService featureDataService;

	private final FeatureDataMapper mapper;

	@GetMapping ("/{featureDataId:[0-9]+}")
	@ResponseBody
	@ResponseStatus (HttpStatus.OK)
	public FeatureDataDto get(@PathVariable ("featureDataId") Long id) throws MethodArgumentNotValidException {

		FeatureData featureData = this.featureDataService.get(id);
		return this.mapper.toDto(featureData);

	}

	@PostMapping
	@ResponseBody
	@ResponseStatus (HttpStatus.CREATED)
	public FeatureDataDto create(@RequestBody @Valid FeatureDataDto dto) {

		FeatureData featureData = this.featureDataService.create(dto);
		return this.mapper.toDto(featureData);
	}

	@PatchMapping
	@ResponseBody
	@ResponseStatus (HttpStatus.ACCEPTED)
	public FeatureDataDto update(@RequestBody FeatureDataDto dto) throws MethodArgumentNotValidException {

		FeatureData featureData = this.featureDataService.get(dto.getId());
		return this.mapper.toDto(this.featureDataService.update(featureData, dto));
	}

}
