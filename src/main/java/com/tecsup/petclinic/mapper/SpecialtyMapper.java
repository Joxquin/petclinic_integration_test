package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface SpecialtyMapper {

    @Mapping(target = "vets", ignore = true)
    @Mapping(source = "HOpen", target = "hOpen")
    @Mapping(source = "HClose", target = "hClose")
    Specialty toEntity(SpecialtyDTO dto);

    @Mapping(source = "HOpen", target = "hOpen")
    @Mapping(source = "HClose", target = "hClose")
    SpecialtyDTO toDTO(Specialty entity);

    List<SpecialtyDTO> toDTOList(List<Specialty> entities);

    List<Specialty> toEntityList(List<SpecialtyDTO> dtos);
}
