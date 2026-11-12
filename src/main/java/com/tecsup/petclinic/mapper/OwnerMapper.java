package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.entities.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface OwnerMapper {

    Owner toEntity(OwnerDTO dto);

    OwnerDTO toDTO(Owner entity);

    List<OwnerDTO> toDTOList(List<Owner> entities);

    List<Owner> toEntityList(List<OwnerDTO> dtos);
}

