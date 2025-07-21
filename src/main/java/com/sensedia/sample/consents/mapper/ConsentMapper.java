package com.sensedia.sample.consents.mapper;

import com.sensedia.sample.consents.domain.Consent;
import com.sensedia.sample.consents.dto.ConsentRequestCreateDTO;
import com.sensedia.sample.consents.dto.ConsentRequestUpdateDTO;
import com.sensedia.sample.consents.dto.ConsentResponseDTO;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = CpfMapperUtil.class)
public interface ConsentMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "creationDateTime", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "cpf", source = "cpf", qualifiedByName = "onlyDigits")
  Consent toEntity(ConsentRequestCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "creationDateTime", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "cpf", source = "cpf", qualifiedByName = "onlyDigits")
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Consent merge(ConsentRequestUpdateDTO dto, @MappingTarget Consent consent);

  ConsentResponseDTO toResponseDTO(Consent consent);
}
