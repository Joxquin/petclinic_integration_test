package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import com.tecsup.petclinic.entities.VetSpecialty;
import com.tecsup.petclinic.entities.VetSpecialtyId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface VetSpecialtyMapper {

    @Mapping(target = "id", expression = "java(new com.tecsup.petclinic.entities.VetSpecialtyId(dto.getVetId(), dto.getSpecialtyId()))")
    VetSpecialty toEntity(VetSpecialtyDTO dto);

    @Mapping(target = "vetId", expression = "java(entity.getId() != null ? entity.getId().getVetId() : null)")
    @Mapping(target = "specialtyId", expression = "java(entity.getId() != null ? entity.getId().getSpecialtyId() : null)")
    VetSpecialtyDTO toDTO(VetSpecialty entity);

    List<VetSpecialtyDTO> toDTOList(List<VetSpecialty> entities);

    List<VetSpecialty> toEntityList(List<VetSpecialtyDTO> dtos);
}

