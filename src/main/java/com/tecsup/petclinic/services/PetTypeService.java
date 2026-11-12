package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.PetType;
import com.tecsup.petclinic.exceptions.PetTypeNotFoundException;

import java.util.List;
import java.util.Map;

public interface PetTypeService {
    List<PetType> findAll();
    PetType findById(Integer id) throws PetTypeNotFoundException;
    PetType create(PetType type);
    PetType update(PetType type);
    void delete(Integer id) throws PetTypeNotFoundException;
    List<PetType> findByActive(Boolean active);
    Map<String, Long> getPetCountByType(boolean excludeInactive);
}

