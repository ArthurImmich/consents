package com.sensedia.sample.consents.mapper;

import com.sensedia.sample.consents.domain.Consent;
import com.sensedia.sample.consents.dto.ConsentRequestCreateDTO;
import com.sensedia.sample.consents.dto.ConsentRequestUpdateDTO;
import com.sensedia.sample.consents.dto.ConsentResponseDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsentMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "creationDateTime", ignore = true)
	@Mapping(target = "version", ignore = true)
	Consent toEntity(ConsentRequestCreateDTO dto);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "creationDateTime", ignore = true)
	@Mapping(target = "version", ignore = true)
	Consent toEntity(ConsentRequestUpdateDTO dto);

	ConsentResponseDTO toResponseDTO(Consent consent);

}
