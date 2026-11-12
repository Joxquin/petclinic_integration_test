package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.PetTypeDTO;
import com.tecsup.petclinic.entities.PetType;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface PetTypeMapper {

    PetType toEntity(PetTypeDTO dto);

    PetTypeDTO toDTO(PetType entity);

    List<PetTypeDTO> toDTOList(List<PetType> entities);

    List<PetType> toEntityList(List<PetTypeDTO> dtos);
}

