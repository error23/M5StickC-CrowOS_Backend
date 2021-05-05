package com.crow.iot.esp32.crowOS.backend.featureData;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author : error23
 * Created : 01/05/2021
 */
@Entity
@DynamicUpdate
@Table (name = "feature_data")
@Getter
@Setter
public class FeatureData extends AbstractEntity {

	private static final long serialVersionUID = - 5056597107226048243L;

	@Id
	@Column (name = "id", nullable = false)
	@Access (AccessType.PROPERTY)
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "feature_data_gen")
	@SequenceGenerator (name = "feature_data_gen", sequenceName = "feature_data_seq")
	@JsonIdentityInfo (
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
	private Long id;

	@Column (name = "feature_factory_name")
	private String featureFactoryName;

	@Type (type = "jsonb")
	@Column (name = "feature_saved_data", columnDefinition = "jsonb")
	private String savedData;

}
