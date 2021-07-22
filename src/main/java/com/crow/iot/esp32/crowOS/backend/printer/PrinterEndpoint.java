package com.crow.iot.esp32.crowOS.backend.printer;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractEndpoint;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : error23
 * Created : 29/05/2021
 */
@RestController
@RequestMapping ("/printer")
@RequiredArgsConstructor
public class PrinterEndpoint extends AbstractEndpoint {

	private final PrinterService printerService;

	private final PrinterMapper printerMapper;

	private final PrinterMinimalMapper printerMinimalMapper;

	@Operation (summary = "Lists all printers for connected user")
	@GetMapping ()
	@ResponseBody
	@ResponseStatus (HttpStatus.OK)
	public List<PrinterMinimalDto> list() {

		List<Printer> printers = this.printerService.list();
		List<PrinterMinimalDto> dreamerPrinterDtos = new ArrayList<>();

		for (Printer printer : printers) {
			dreamerPrinterDtos.add(this.printerMinimalMapper.toDto(printer));
		}

		return dreamerPrinterDtos;
	}

	@Operation (summary = "Get printer by its id")
	@GetMapping ("/{printerId:[0-9]+}")
	@ResponseBody
	@ResponseStatus (HttpStatus.OK)
	public PrinterDto get(@PathVariable ("printerId") Long id) throws MethodArgumentNotValidException {

		Printer printer = this.printerService.get(id);
		return this.printerMapper.toDto(printer);
	}

	@Operation (summary = "Updates printer color")
	@PatchMapping ("/{printerId:[0-9]+}/color")
	@ResponseBody
	@ResponseStatus (HttpStatus.ACCEPTED)
	public void updateLedColor(@PathVariable ("printerId") Long id, @RequestParam @Valid @NotNull ColorRGB color) throws MethodArgumentNotValidException {

		Printer printer = this.printerService.get(id);
		this.printerService.updateLedColor(printer, color);

	}

	@Operation (summary = "Updates printer adresse")
	@PatchMapping ("/{printerId:[0-9]+}/adresse")
	@ResponseBody
	@ResponseStatus (HttpStatus.ACCEPTED)
	public void updateMachineAdresse(@PathVariable ("printerId") Long id, @RequestParam @Valid @NotNull String machineIp, @RequestParam @Valid @NotNull Integer machinePort) throws MethodArgumentNotValidException {

		Printer printer = this.printerService.get(id);
		this.printerService.updateMachineAdresse(printer, machineIp, machinePort);

	}

}
