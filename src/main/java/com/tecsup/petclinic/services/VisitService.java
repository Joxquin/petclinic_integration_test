package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Visit;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VisitService {
    List<Visit> findAll();
    Visit findById(Integer id) throws VisitNotFoundException;
    Visit create(Visit visit);
    Visit update(Visit visit);
    void delete(Integer id) throws VisitNotFoundException;
    List<Visit> findByPetId(Integer petId);
    List<Visit> findByVetId(Integer vetId);
    BigDecimal calculateTotalCostByPet(Integer petId);
    List<Visit> findByDateBetween(LocalDate from, LocalDate to);
    List<Visit> findByVetIdAndDateBetween(Integer vetId, LocalDate from, LocalDate to);
    long countVisitsByPet(Integer petId);
}

