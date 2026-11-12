package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.entities.VetSpecialty;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface VetSpecialtyService {
    VetSpecialty assignSpecialtyToVet(VetSpecialty vetSpecialty) throws Exception;
    void removeSpecialtyFromVet(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException;
    List<Specialty> findSpecialtiesByVet(Integer vetId);
    List<Vet> findVetsBySpecialty(Integer specialtyId);
    VetSpecialty setPrimarySpecialty(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException;
    List<Vet> findVetsByMinExperience(Integer specialtyId, Integer minExperience);
    VetSpecialty updateYearsExperience(Integer vetId, Integer specialtyId, Integer years) throws VetSpecialtyNotFoundException;
    VetSpecialty updateCertificationDate(Integer vetId, Integer specialtyId, LocalDate date) throws VetSpecialtyNotFoundException;
}

