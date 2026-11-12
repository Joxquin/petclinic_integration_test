package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.mapper.OwnerMapper;
import com.tecsup.petclinic.services.OwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class OwnerController {

    private final OwnerService ownerService;
    private final OwnerMapper mapper;

    public OwnerController(OwnerService ownerService, OwnerMapper mapper) {
        this.ownerService = ownerService;
        this.mapper = mapper;
    }

    @GetMapping("/owners")
    public ResponseEntity<List<OwnerDTO>> findAllOwners(@RequestParam(value = "lastName", required = false) String lastName) {
        List<Owner> owners;
        if (lastName != null && !lastName.isEmpty()) {
            owners = ownerService.findByLastName(lastName);
        } else {
            owners = ownerService.findAll();
        }
        return ResponseEntity.ok(mapper.toDTOList(owners));
    }

    @GetMapping("/owners/{id}")
    public ResponseEntity<OwnerDTO> findById(@PathVariable Integer id) {
        try {
            Owner owner = ownerService.findById(id);
            return ResponseEntity.ok(mapper.toDTO(owner));
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/owners")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OwnerDTO> create(@RequestBody OwnerDTO ownerDTO) {
        if (ownerDTO.getLastName() == null || ownerDTO.getLastName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Owner owner = mapper.toEntity(ownerDTO);
        Owner created = ownerService.create(owner);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    @PutMapping("/owners/{id}")
    public ResponseEntity<OwnerDTO> update(@PathVariable Integer id, @RequestBody OwnerDTO ownerDTO) {
        try {
            ownerService.findById(id);
            Owner owner = mapper.toEntity(ownerDTO);
            owner.setId(id);
            Owner updated = ownerService.update(owner);
            return ResponseEntity.ok(mapper.toDTO(updated));
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/owners/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            ownerService.delete(id);
            return ResponseEntity.ok().build();
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

