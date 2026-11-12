package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;

import java.util.List;

public interface VetService {
    List<Vet> findAll();
    Vet findById(Integer id) throws VetNotFoundException;
    Vet create(Vet vet);
    Vet update(Vet vet);
    void delete(Integer id) throws VetNotFoundException;
    Vet deactivate(Integer id) throws VetNotFoundException;
    Vet reactivate(Integer id) throws VetNotFoundException;
    List<Vet> findByActive(Boolean active);
}

