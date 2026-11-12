package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface VetMapper {

    @Mapping(target = "specialties", ignore = true)
    Vet toEntity(VetDTO dto);

    VetDTO toDTO(Vet entity);

    List<VetDTO> toDTOList(List<Vet> entities);

    List<Vet> toEntityList(List<VetDTO> dtos);
}

