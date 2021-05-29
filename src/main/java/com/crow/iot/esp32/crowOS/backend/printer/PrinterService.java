package com.crow.iot.esp32.crowOS.backend.printer;

import com.crow.iot.esp32.crowOS.backend.commons.DuplicatedResourceException;
import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.MethodArgumentNotValidExceptionFactory;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.Operator;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.SearchDto;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.SearchFilter;
import com.crow.iot.esp32.crowOS.backend.printer.flashForge.dreamer.DreamerClient;
import com.crow.iot.esp32.crowOS.backend.printer.flashForge.dreamer.DreamerPrinter_;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
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

	private final DreamerClient client;

	/**
	 * List all {@link Printer} for connected account
	 *
	 * @return list of accounts {@link Printer}
	 */
	@PostAuthorize ("hasPermission(returnObject, 'READ')")
	public List<Printer> list() {

		SearchDto dto = new SearchDto();

		if (! SecurityTools.canConnectedAccount(Privilege.READ, SecuredResource.DREAMER_PRINTER, null)) {
			dto.addFilter(new SearchFilter(
				DreamerPrinter_.OWNER,
				Operator.EQUALS,
				SecurityTools.getConnectedAccount()));
		}

		return this.printerDao.search(dto);

	}

	/**
	 * Retrieves one {@link Printer} from database
	 *
	 * @param id of dreamer printer to retrieve
	 * @return retrieved {@link Printer}
	 */
	@PostAuthorize ("hasPermission(returnObject, 'READ')")
	public Printer get(Long id) throws MethodArgumentNotValidException {

		if (id == null) throw MethodArgumentNotValidExceptionFactory.NOT_NULL(this, "id");

		Printer printer = this.printerDao.get(id);
		if (printer == null) throw new ResourceNotFoundException("Printer", id);

		return printer;
	}

	/**
	 * Creates new {@link Printer}
	 *
	 * @param printerDto to create
	 * @return created {@link Printer}
	 */
	@PreAuthorize ("hasPermission('DREAMER_PRINTER', 'CREATE')")
	public Printer create(@NotNull PrinterDto printerDto) {

		List<Printer> duplicates = this.printerDao.search(new SearchDto(
			new SearchFilter(DreamerPrinter_.OWNER, Operator.EQUALS, SecurityTools.getConnectedAccount()),
			new SearchFilter(DreamerPrinter_.MACHINE_NAME, Operator.EQUALS, printerDto.getMachineName())

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
	 * Updates dreamerPrinter color only
	 *
	 * @param printer to update
	 * @param color   to update with
	 */
	@PreAuthorize ("hasPermission('DREAMER_PRINTER_COLOR','UPDATE')")
	public void updateLedColor(Printer printer, ColorRGB color) {

		printer.setLedColor(color);
	}

	@Scheduled (fixedDelayString = "${flashforge.dreamer.schedule.delay}")
	private void synchronizePrinter() {

	}
}
