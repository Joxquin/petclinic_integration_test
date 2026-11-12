package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.repositories.VetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;

    public VetServiceImpl(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    @Override
    public List<Vet> findAll() {
        return vetRepository.findAll();
    }

    @Override
    public Vet findById(Integer id) throws VetNotFoundException {
        return vetRepository.findById(id)
                .orElseThrow(() -> new VetNotFoundException("Vet not found with id: " + id));
    }

    @Override
    public Vet create(Vet vet) {
        return vetRepository.save(vet);
    }

    @Override
    public Vet update(Vet vet) {
        return vetRepository.save(vet);
    }

    @Override
    public void delete(Integer id) throws VetNotFoundException {
        Vet vet = findById(id);
        vetRepository.delete(vet);
    }

    @Override
    public Vet deactivate(Integer id) throws VetNotFoundException {
        Vet vet = findById(id);
        vet.setActive(false);
        return vetRepository.save(vet);
    }

    @Override
    public Vet reactivate(Integer id) throws VetNotFoundException {
        Vet vet = findById(id);
        vet.setActive(true);
        return vetRepository.save(vet);
    }

    @Override
    public List<Vet> findByActive(Boolean active) {
        return vetRepository.findByActive(active);
    }
}

