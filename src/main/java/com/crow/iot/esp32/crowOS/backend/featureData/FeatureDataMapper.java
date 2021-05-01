package com.crow.iot.esp32.crowOS.backend.featureData;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.AbstractMapper;
import org.mapstruct.Mapper;

/**
 * @author : error23
 * Created : 01/05/2021
 */
@Mapper (config = AbstractMapper.class)
public abstract class FeatureDataMapper implements AbstractMapper<FeatureDataDto, FeatureData> {

}

