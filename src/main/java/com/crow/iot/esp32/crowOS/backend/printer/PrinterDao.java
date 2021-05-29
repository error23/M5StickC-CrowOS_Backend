package com.crow.iot.esp32.crowOS.backend.printer;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractDao;
import org.springframework.stereotype.Repository;

/**
 * @author : error23
 * Created : 28/05/2021
 */
@Repository
public class PrinterDao extends AbstractDao<Printer> {

	/**
	 * Creates new printer dao
	 */
	public PrinterDao() {

		super();
	}

}
