package com.crow.iot.esp32.crowOS.backend;

import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

	@Override
	public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {

		if (! SecurityTools.canConnectedAccount(Privilege.READ, SecuredResource.STACK_TRACE, null)) {
			options.excluding(ErrorAttributeOptions.Include.STACK_TRACE);
		}

		Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
		errorAttributes.remove("timestamp");
		errorAttributes.remove("status");
		errorAttributes.remove("path");

		String error = (String) errorAttributes.get("error");
		if (StringUtils.isNotBlank(error) && error.equals("Unauthorized")) {
			errorAttributes.put("error", "BAD_CREDENTIALS_EXCEPTION");
		}

		errorAttributes.put("detailsHumanReadable", errorAttributes.get("message"));
		errorAttributes.remove("message");

		return errorAttributes;
	}
}
