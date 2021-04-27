package com.crow.iot.esp32.crowOS.backend.commons;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;

/**
 * @author : error23
 * Created : 23/05/2020
 */
@Slf4j
public class SqlLogger implements MessageFormattingStrategy {

	private static final int longQuery = 500;
	private Formatter formatter = new BasicFormatterImpl();

	@Override
	public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {

		String formattedQuery = "\n----------------------- " + category + " -----------------------"
			+ "\n" + this.formatter.format(sql)
			+ "\n" + "------------------ Finished in " + elapsed + "ms ---------------------";

		if (elapsed >= longQuery) {
			log.warn(formattedQuery);
		}
		else {
			log.debug(formattedQuery);
		}

		return "";
	}
}


