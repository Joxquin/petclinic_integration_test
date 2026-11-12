package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Pet;
import com.tecsup.petclinic.entities.PetType;
import com.tecsup.petclinic.exceptions.PetTypeNotFoundException;
import com.tecsup.petclinic.repositories.PetRepository;
import com.tecsup.petclinic.repositories.PetTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PetTypeServiceImpl implements PetTypeService {

    private final PetTypeRepository petTypeRepository;
    private final PetRepository petRepository;

    public PetTypeServiceImpl(PetTypeRepository petTypeRepository, PetRepository petRepository) {
        this.petTypeRepository = petTypeRepository;
        this.petRepository = petRepository;
    }

    @Override
    public List<PetType> findAll() {
        return petTypeRepository.findAll();
    }

    @Override
    public PetType findById(Integer id) throws PetTypeNotFoundException {
        return petTypeRepository.findById(id)
                .orElseThrow(() -> new PetTypeNotFoundException("PetType not found with id: " + id));
    }

    @Override
    public PetType create(PetType type) {
        return petTypeRepository.save(type);
    }

    @Override
    public PetType update(PetType type) {
        return petTypeRepository.save(type);
    }

    @Override
    public void delete(Integer id) throws PetTypeNotFoundException {
        PetType petType = findById(id);
        petTypeRepository.delete(petType);
    }

    @Override
    public List<PetType> findByActive(Boolean active) {
        return petTypeRepository.findByActive(active);
    }

    @Override
    public Map<String, Long> getPetCountByType(boolean excludeInactive) {
        List<PetType> types = excludeInactive ? petTypeRepository.findByActive(true) : petTypeRepository.findAll();
        List<Pet> pets = petRepository.findAll();

        Map<String, Long> countMap = new LinkedHashMap<>();
        for (PetType type : types) {
            long count = pets.stream().filter(p -> type.getId().equals(p.getTypeId())).count();
            countMap.put(type.getName(), count);
        }
        return countMap;
    }
}
