package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Visit;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.repositories.VisitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;

    public VisitServiceImpl(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Override
    public List<Visit> findAll() {
        return visitRepository.findAll();
    }

    @Override
    public Visit findById(Integer id) throws VisitNotFoundException {
        return visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found with id: " + id));
    }

    @Override
    public Visit create(Visit visit) {
        return visitRepository.save(visit);
    }

    @Override
    public Visit update(Visit visit) {
        return visitRepository.save(visit);
    }

    @Override
    public void delete(Integer id) throws VisitNotFoundException {
        Visit visit = findById(id);
        visitRepository.delete(visit);
    }

    @Override
    public List<Visit> findByPetId(Integer petId) {
        return visitRepository.findByPetId(petId);
    }

    @Override
    public List<Visit> findByVetId(Integer vetId) {
        return visitRepository.findByVetId(vetId);
    }

    @Override
    public BigDecimal calculateTotalCostByPet(Integer petId) {
        return visitRepository.findByPetId(petId).stream()
                .map(Visit::getCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<Visit> findByDateBetween(LocalDate from, LocalDate to) {
        return visitRepository.findByVisitDateBetween(from, to);
    }

    @Override
    public List<Visit> findByVetIdAndDateBetween(Integer vetId, LocalDate from, LocalDate to) {
        return visitRepository.findByVetIdAndVisitDateBetween(vetId, from, to);
    }

    @Override
    public long countVisitsByPet(Integer petId) {
        return visitRepository.findByPetId(petId).size();
    }
}

