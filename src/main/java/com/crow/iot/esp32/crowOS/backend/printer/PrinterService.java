package com.crow.iot.esp32.crowOS.backend.printer;

import com.crow.iot.esp32.crowOS.backend.commons.DuplicatedResourceException;
import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.MethodArgumentNotValidExceptionFactory;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.Operator;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.SearchDto;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.SearchFilter;
import com.crow.iot.esp32.crowOS.backend.printer.flashForge.dreamer.FlashForgeDreamerClient;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

/**
 * @author : error23
 * Created : 29/05/2021
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PrinterService {

	private final PrinterDao printerDao;

	private final PrinterMapper mapper;

	private final FlashForgeDreamerClient flashForgeDreamerClient;

	/**
	 * List all {@link Printer} for connected account
	 *
	 * @return list of accounts {@link Printer}
	 */
	@PostAuthorize ("hasPermission(returnObject, 'READ')")
	public List<Printer> list() {

		SearchDto dto = new SearchDto();

		if (! SecurityTools.canConnectedAccount(Privilege.READ, SecuredResource.PRINTER, null)) {
			dto.addFilter(new SearchFilter(
				Printer_.OWNER,
				Operator.EQUALS,
				SecurityTools.getConnectedAccount()));
		}

		List<Printer> printers = this.printerDao.search(dto);
		this.synchronizeWithFlashForgeDreamer(printers);
		return printers;

	}

	/**
	 * Retrieves one {@link Printer} from database
	 *
	 * @param id of printer to retrieve
	 * @return retrieved {@link Printer}
	 */
	@PostAuthorize ("hasPermission(returnObject, 'READ')")
	public Printer get(Long id) throws MethodArgumentNotValidException {

		if (id == null) throw MethodArgumentNotValidExceptionFactory.NOT_NULL(this, "id");

		Printer printer = this.printerDao.get(id);
		if (printer == null) throw new ResourceNotFoundException("Printer", id);

		this.synchronizeWithFlashForgeDreamer(List.of(printer));

		return printer;
	}

	/**
	 * Creates new {@link Printer}
	 *
	 * @param printerDto to create
	 * @return created {@link Printer}
	 */
	@PreAuthorize ("hasPermission('PRINTER', 'CREATE')")
	public Printer create(@NotNull PrinterDto printerDto) {

		List<Printer> duplicates = this.printerDao.search(new SearchDto(
			new SearchFilter(Printer_.OWNER, Operator.EQUALS, SecurityTools.getConnectedAccount()),
			new SearchFilter(Printer_.MACHINE_NAME, Operator.EQUALS, printerDto.getMachineName())
		));

		if (! CollectionUtils.isEmpty(duplicates)) throw new DuplicatedResourceException("Printer", "machineName", printerDto.getMachineName());

		Printer created = this.mapper.toEntity(printerDto);
		this.printerDao.save(created);

		return created;

	}

	/**
	 * Updates one {@link Printer}
	 *
	 * @param printer    to update
	 * @param printerDto to update from
	 * @return updated {@link Printer}
	 */
	@PreAuthorize ("hasPermission(#printer,'UPDATE')")
	public Printer update(Printer printer, PrinterDto printerDto) {

		this.mapper.merge(printerDto, printer);
		return printer;
	}

	/**
	 * Updates {@link Printer} color only
	 *
	 * @param printer to update
	 * @param color   to update with
	 */
	@PreAuthorize ("hasPermission('PRINTER_COLOR','UPDATE')")
	public void updateLedColor(Printer printer, ColorRGB color) {

		this.flashForgeDreamerClient.sendHello(printer);
		this.flashForgeDreamerClient.setColor(printer, color);
		this.flashForgeDreamerClient.sendBuy(printer);
		printer.setLedColor(color);
	}

	/**
	 * Updates {@link Printer} machine ip and port values
	 *
	 * @param printer     to update for
	 * @param machineIp   to update
	 * @param machinePort to update
	 */
	@PreAuthorize ("hasPermission('PRINTER_MACHINE_ADRESSE', 'UPDATE')")
	public void updateMachineAdresse(Printer printer, String machineIp, Integer machinePort) {

		printer.setMachineIp(machineIp);
		printer.setMachinePort(machinePort);
		this.synchronizeWithFlashForgeDreamer(List.of(printer));
	}

	/**
	 * Synchronize printers with the physic printers for {@link FlashForgeDreamerClient}.
	 *
	 * @param printers to synchronize
	 */
	public void synchronizeWithFlashForgeDreamer(List<Printer> printers) {

		for (Printer printer : printers) {
			this.flashForgeDreamerClient.sendHello(printer);
			this.flashForgeDreamerClient.updateGeneralInfo(printer);
			this.flashForgeDreamerClient.updateTemperature(printer);
			this.flashForgeDreamerClient.updatePositions(printer);
			this.flashForgeDreamerClient.sendBuy(printer);
		}

	}

}
