package com.crow.iot.esp32.crowOS.backend.featureData;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.account.AccountService;
import com.crow.iot.esp32.crowOS.backend.commons.json.JsonHelper;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : error23
 * Created : 08/05/2021
 */
@SpringBootTest
@AutoConfigureMockMvc
class FeatureDataEndpointTest {

	@MockBean
	AccountService accountService;

	@MockBean
	FeatureDataService featureDataService;

	@Autowired
	MockMvc mvc;

	Account connectedAccount;

	List<FeatureDataDto> dtos;

	List<FeatureData> featureDatas;

	String dtosString;

	@BeforeEach
	void setUp() throws MethodArgumentNotValidException {

		Role role = new Role();
		role.setId(1L);
		role.setName("testA");
		role.setRoot(true);
		role.setPriority(1);

		this.connectedAccount = new Account();
		this.connectedAccount.setId(1L);
		this.connectedAccount.setEnabled(true);
		this.connectedAccount.setFirstName("error23");
		this.connectedAccount.setLastName("rolly");
		this.connectedAccount.setEmail("error23.d@gmail.com");
		this.connectedAccount.setPassword("test");
		this.connectedAccount.setLocale(Locale.FRENCH);
		this.connectedAccount.setRoles(new ArrayList<>(List.of(role)));
		this.connectedAccount.setOwner(this.connectedAccount);
		Mockito.when(this.accountService.get(1L)).thenReturn(this.connectedAccount);
		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.dtos = new ArrayList<>();
		this.featureDatas = new ArrayList<>();

		for (int i = 0; i < 20; i++) {

			FeatureDataDto dto = new FeatureDataDto();
			dto.setId((long) i);
			dto.setSavedData(JsonHelper.fromObjectToNode("{\"test\": " + i + " }"));
			dto.setFeatureFactoryName("success" + i);
			this.dtos.add(dto);

			FeatureData featureData = new FeatureData();
			featureData.setId((long) i);
			featureData.setOwner(this.connectedAccount);
			featureData.setSavedData(dto.getSavedData());
			featureData.setFeatureFactoryName(dto.getFeatureFactoryName());
			this.featureDatas.add(featureData);
		}

		this.dtosString = "[" + StringUtils.join(this.dtos, ",") + "]";

		Mockito.when(this.featureDataService.list()).thenReturn(this.featureDatas);
		Mockito.when(this.featureDataService.updateOrCreate(any())).thenReturn(this.featureDatas);

	}

	@Test
	void whenList_thanSuccess() throws Exception {

		this.mvc.perform(get("/featureData"))
		        .andDo(log())
		        .andExpect(status().isOk())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(content().json(this.dtosString, true));

		verify(this.featureDataService, times(1)).list();

	}

	@Test
	void whenCreateOrUpdate_thanSuccess() throws Exception {

		this.mvc.perform(put("/featureData")
			                 .contentType(MediaType.APPLICATION_JSON_VALUE)
			                 .content(this.dtosString)
		                )
		        .andDo(log())
		        .andExpect(status().isAccepted())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(content().json(this.dtosString, true));

		verify(this.featureDataService, times(1)).updateOrCreate(any());

	}
}
