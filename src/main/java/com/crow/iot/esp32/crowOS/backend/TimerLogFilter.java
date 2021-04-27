package com.crow.iot.esp32.crowOS.backend;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : error23
 * Created : 23/05/2020
 */
@Component
@Order (- 60000)
@Slf4j
public class TimerLogFilter implements Filter {

	private static final int LONG_QUERY = 1500;

	private static final String[] IP_HEADER_CANDIDATES = {
		"X-Forwarded-For",
		"Proxy-Client-IP",
		"WL-Proxy-Client-IP",
		"HTTP_X_FORWARDED_FOR",
		"HTTP_X_FORWARDED",
		"HTTP_X_CLUSTER_CLIENT_IP",
		"HTTP_CLIENT_IP",
		"HTTP_FORWARDED_FOR",
		"HTTP_FORWARDED",
		"HTTP_VIA",
		"REMOTE_ADDR"
	};

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, @NotNull FilterChain chain) throws IOException, ServletException {

		long startTime = System.currentTimeMillis();
		chain.doFilter(request, response);
		long elapsedTime = System.currentTimeMillis() - startTime;

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		String method = httpServletRequest.getMethod();
		String uri = httpServletRequest.getRequestURI();
		int status = httpServletResponse.getStatus();
		String remoteAddress = getRemoteIpFromRequest(httpServletRequest);

		if (elapsedTime >= LONG_QUERY) {
			log.warn("{} on {} gives {} for {} in {}ms", method, uri, status, remoteAddress, elapsedTime);
		}
		else {
			log.info("{} on {} gives {} for {} in {}ms", method, uri, status, remoteAddress, elapsedTime);
		}
	}

	/**
	 * Gets remote ip from {@link HttpServletRequest}
	 *
	 * @param request to get from
	 * @return remote user ip
	 */
	public static String getRemoteIpFromRequest(HttpServletRequest request) {

		for (String headerName : IP_HEADER_CANDIDATES) {

			String header = request.getHeader(headerName);
			if (StringUtils.isNotBlank(header) && ! "unknown".equalsIgnoreCase(header)) {
				return header.split(",")[0];
			}
		}

		return request.getRemoteAddr();
	}
}
