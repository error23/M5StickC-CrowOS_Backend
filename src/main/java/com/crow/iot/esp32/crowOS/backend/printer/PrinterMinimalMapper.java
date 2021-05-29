package com.crow.iot.esp32.crowOS.backend.printer;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.AbstractMapper;
import org.mapstruct.Mapper;

/**
 * @author : error23
 * Created : 29/05/2021
 */
@Mapper (config = AbstractMapper.class)
public abstract class PrinterMinimalMapper implements AbstractMapper<PrinterMinimalDto, Printer> {

}
