package com.crow.iot.esp32.crowOS.backend.printer.flashForge.dreamer;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.AbstractMapper;
import org.mapstruct.Mapper;

/**
 * @author : error23
 * Created : 29/05/2021
 */
@Mapper (config = AbstractMapper.class)
public abstract class DreamerPrinterMapper implements AbstractMapper<DreamerPrinterDto, DreamerPrinter> {

}
