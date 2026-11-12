package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.entities.Pet;
import com.tecsup.petclinic.entities.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface VisitMapper {

    @Mapping(target = "pet", source = "petId", qualifiedByName = "petFromId")
    Visit toEntity(VisitDTO dto);

    @Mapping(target = "petId", source = "pet", qualifiedByName = "idFromPet")
    VisitDTO toDTO(Visit entity);

    List<VisitDTO> toDTOList(List<Visit> entities);

    List<Visit> toEntityList(List<VisitDTO> dtos);

    @Named("petFromId")
    default Pet petFromId(Integer petId) {
        if (petId == null) {
            return null;
        }
        return Pet.builder().id(petId).build();
    }

    @Named("idFromPet")
    default Integer idFromPet(Pet pet) {
        if (pet == null) {
            return null;
        }
        return pet.getId();
    }
}
