package com.crow.iot.esp32.crowOS.backend.featureData;

import com.crow.iot.esp32.crowOS.backend.commons.DuplicatedResourceException;
import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.MethodArgumentNotValidExceptionFactory;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.Operator;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.SearchDto;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search.SearchFilter;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : error23
 * Created : 01/05/2021
 */
@Service
@Transactional
@RequiredArgsConstructor
public class FeatureDataService {

	private final ApplicationContext applicationContext;

	private final FeatureDataDao featureDataDao;

	private final FeatureDataMapper mapper;

	/**
	 * List all {@link FeatureData} for connected account
	 *
	 * @return list of accounts {@link FeatureData}
	 */
	@PostAuthorize ("hasPermission(returnObject, 'READ')")
	public List<FeatureData> list() {

		SearchDto dto = new SearchDto();

		if (! SecurityTools.canConnectedAccount(Privilege.READ, SecuredResource.FEATURE_DATA, null)) {
			dto.addFilter(new SearchFilter(
				FeatureData_.OWNER,
				Operator.EQUALS,
				SecurityTools.getConnectedAccount()));
		}

		return this.featureDataDao.search(dto);

	}

	/**
	 * Retrieves one {@link FeatureData} from database
	 *
	 * @param id of feature data to retrieve
	 * @return retrieved feature data
	 */
	@PostAuthorize ("hasPermission(returnObject, 'READ')")
	public FeatureData get(Long id) throws MethodArgumentNotValidException {

		if (id == null) throw MethodArgumentNotValidExceptionFactory.NOT_NULL(this, "id");

		FeatureData featureData = this.featureDataDao.get(id);
		if (featureData == null) throw new ResourceNotFoundException("FeatureData", id);

		return featureData;

	}

	/**
	 * Creates new {@link FeatureData}
	 *
	 * @param featureDataDto to create
	 * @return created {@link FeatureData}
	 */
	@PreAuthorize ("hasPermission('FEATURE_DATA', 'CREATE')")
	public FeatureData create(@NotNull FeatureDataDto featureDataDto) {

		List<FeatureData> duplicates = this.featureDataDao.search(new SearchDto(
			new SearchFilter(FeatureData_.OWNER, Operator.EQUALS, SecurityTools.getConnectedAccount()),
			new SearchFilter(FeatureData_.FEATURE_FACTORY_NAME, Operator.EQUALS, featureDataDto.getFeatureFactoryName())

		));

		if (! CollectionUtils.isEmpty(duplicates)) throw new DuplicatedResourceException("FeatureData", "featureFactoryName", featureDataDto.getFeatureFactoryName());

		FeatureData created = this.mapper.toEntity(featureDataDto);
		this.featureDataDao.save(created);

		return created;

	}

	/**
	 * Updates or creates one {@link FeatureData}
	 * For every dto having id = null create new FeatureData else update existing one
	 *
	 * @param dtos to create or update
	 * @return created or updated {@link FeatureData}
	 */
	public List<FeatureData> updateOrCreate(@NotNull List<FeatureDataDto> dtos) throws MethodArgumentNotValidException {

		FeatureDataService self = this.applicationContext.getBean(FeatureDataService.class);

		List<FeatureData> featureDatas = new ArrayList<>();
		for (FeatureDataDto dto : dtos) {
			if (dto.getId() == null) {

				featureDatas.add(self.create(dto));
			}
			else {
				FeatureData featureData = this.get(dto.getId());
				featureDatas.add(self.update(featureData, dto));
			}
		}

		return featureDatas;
	}

	/**
	 * Updates one {@link FeatureData}
	 *
	 * @param featureData    to update
	 * @param featureDataDto to update from
	 * @return updated {@link FeatureData}
	 */
	@PreAuthorize ("hasPermission(#featureData,'UPDATE')")
	public FeatureData update(@NotNull FeatureData featureData, @NotNull FeatureDataDto featureDataDto) {

		this.mapper.merge(featureDataDto, featureData);
		return featureData;
	}

}
