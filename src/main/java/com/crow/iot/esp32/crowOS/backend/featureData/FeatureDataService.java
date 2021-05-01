package com.crow.iot.esp32.crowOS.backend.featureData;

import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.MethodArgumentNotValidExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * @author : error23
 * Created : 01/05/2021
 */
@Service
@Transactional
@RequiredArgsConstructor
public class FeatureDataService {

	private final FeatureDataDao featureDataDao;

	private final FeatureDataMapper mapper;

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

		FeatureData created = this.mapper.toEntity(featureDataDto);
		this.featureDataDao.save(created);

		return created;

	}

	/**
	 * Updates one {@link FeatureData}
	 *
	 * @param featureData    to update
	 * @param featureDataDto to update from
	 * @return updated {@link FeatureData}
	 */
	public FeatureData update(@NotNull FeatureData featureData, @NotNull FeatureDataDto featureDataDto) {

		this.mapper.merge(featureDataDto, featureData);
		return featureData;
	}

}
