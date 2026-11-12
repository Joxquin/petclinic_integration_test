package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.entities.VetSpecialty;
import com.tecsup.petclinic.entities.VetSpecialtyId;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;
import com.tecsup.petclinic.repositories.SpecialtyRepository;
import com.tecsup.petclinic.repositories.VetRepository;
import com.tecsup.petclinic.repositories.VetSpecialtyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VetSpecialtyServiceImpl implements VetSpecialtyService {

    private final VetSpecialtyRepository vetSpecialtyRepository;
    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;

    public VetSpecialtyServiceImpl(VetSpecialtyRepository vetSpecialtyRepository,
                                   VetRepository vetRepository,
                                   SpecialtyRepository specialtyRepository) {
        this.vetSpecialtyRepository = vetSpecialtyRepository;
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public VetSpecialty assignSpecialtyToVet(VetSpecialty vetSpecialty) throws Exception {
        if (vetSpecialtyRepository.existsById(vetSpecialty.getId())) {
            throw new DuplicateKeyException("Duplicate assignment for vet " + vetSpecialty.getId().getVetId() + " and specialty " + vetSpecialty.getId().getSpecialtyId());
        }
        return vetSpecialtyRepository.save(vetSpecialty);
    }

    @Override
    public void removeSpecialtyFromVet(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException {
        VetSpecialtyId id = new VetSpecialtyId(vetId, specialtyId);
        VetSpecialty vs = vetSpecialtyRepository.findById(id)
                .orElseThrow(() -> new VetSpecialtyNotFoundException("Assignment not found for vet " + vetId + " and specialty " + specialtyId));
        vetSpecialtyRepository.delete(vs);
    }

    @Override
    public List<Specialty> findSpecialtiesByVet(Integer vetId) {
        List<VetSpecialty> list = vetSpecialtyRepository.findByIdVetId(vetId);
        List<Integer> specIds = list.stream().map(vs -> vs.getId().getSpecialtyId()).collect(Collectors.toList());
        return specialtyRepository.findAllById(specIds);
    }

    @Override
    public List<Vet> findVetsBySpecialty(Integer specialtyId) {
        List<VetSpecialty> list = vetSpecialtyRepository.findByIdSpecialtyId(specialtyId);
        List<Integer> vetIds = list.stream().map(vs -> vs.getId().getVetId()).collect(Collectors.toList());
        return vetRepository.findAllById(vetIds);
    }

    @Override
    @Transactional
    public VetSpecialty setPrimarySpecialty(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException {
        VetSpecialtyId id = new VetSpecialtyId(vetId, specialtyId);
        VetSpecialty target = vetSpecialtyRepository.findById(id)
                .orElseThrow(() -> new VetSpecialtyNotFoundException("Assignment not found for vet " + vetId + " and specialty " + specialtyId));

        List<VetSpecialty> allForVet = vetSpecialtyRepository.findByIdVetId(vetId);
        for (VetSpecialty vs : allForVet) {
            vs.setIsPrimary(vs.getId().equals(id));
            vetSpecialtyRepository.save(vs);
        }
        target.setIsPrimary(true);
        return target;
    }

    @Override
    public List<Vet> findVetsByMinExperience(Integer specialtyId, Integer minExperience) {
        List<VetSpecialty> list = vetSpecialtyRepository.findByIdSpecialtyIdAndYearsExperienceGreaterThanEqual(specialtyId, minExperience);
        List<Integer> vetIds = list.stream().map(vs -> vs.getId().getVetId()).collect(Collectors.toList());
        return vetRepository.findAllById(vetIds);
    }

    @Override
    public VetSpecialty updateYearsExperience(Integer vetId, Integer specialtyId, Integer years) throws VetSpecialtyNotFoundException {
        VetSpecialtyId id = new VetSpecialtyId(vetId, specialtyId);
        VetSpecialty vs = vetSpecialtyRepository.findById(id)
                .orElseThrow(() -> new VetSpecialtyNotFoundException("Assignment not found"));
        vs.setYearsExperience(years);
        return vetSpecialtyRepository.save(vs);
    }

    @Override
    public VetSpecialty updateCertificationDate(Integer vetId, Integer specialtyId, LocalDate date) throws VetSpecialtyNotFoundException {
        VetSpecialtyId id = new VetSpecialtyId(vetId, specialtyId);
        VetSpecialty vs = vetSpecialtyRepository.findById(id)
                .orElseThrow(() -> new VetSpecialtyNotFoundException("Assignment not found"));
        vs.setCertificationDate(date);
        return vetSpecialtyRepository.save(vs);
    }
}

