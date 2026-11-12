package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.mapper.VetMapper;
import com.tecsup.petclinic.services.VetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class VetController {

    private final VetService vetService;
    private final VetMapper mapper;

    public VetController(VetService vetService, VetMapper mapper) {
        this.vetService = vetService;
        this.mapper = mapper;
    }

    @GetMapping("/vets")
    public ResponseEntity<List<VetDTO>> findAllVets(@RequestParam(value = "active", required = false) Boolean active) {
        List<Vet> vets;
        if (active != null) {
            vets = vetService.findByActive(active);
        } else {
            vets = vetService.findAll();
        }
        return ResponseEntity.ok(mapper.toDTOList(vets));
    }

    @GetMapping("/vets/{id}")
    public ResponseEntity<VetDTO> findById(@PathVariable Integer id) {
        try {
            Vet vet = vetService.findById(id);
            return ResponseEntity.ok(mapper.toDTO(vet));
        } catch (VetNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/vets")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<VetDTO> create(@RequestBody VetDTO vetDTO) {
        Vet vet = mapper.toEntity(vetDTO);
        Vet created = vetService.create(vet);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    @PutMapping("/vets/{id}/deactivate")
    public ResponseEntity<VetDTO> deactivate(@PathVariable Integer id) {
        try {
            Vet updated = vetService.deactivate(id);
            return ResponseEntity.ok(mapper.toDTO(updated));
        } catch (VetNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/vets/{id}/reactivate")
    public ResponseEntity<VetDTO> reactivate(@PathVariable Integer id) {
        try {
            Vet updated = vetService.reactivate(id);
            return ResponseEntity.ok(mapper.toDTO(updated));
        } catch (VetNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/vets/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            vetService.delete(id);
            return ResponseEntity.ok().build();
        } catch (VetNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

