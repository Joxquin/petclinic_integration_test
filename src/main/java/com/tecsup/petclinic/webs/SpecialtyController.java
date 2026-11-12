package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.mapper.SpecialtyMapper;
import com.tecsup.petclinic.services.SpecialtyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class SpecialtyController {

    private final SpecialtyService specialtyService;
    private final SpecialtyMapper mapper;

    public SpecialtyController(SpecialtyService specialtyService, SpecialtyMapper mapper) {
        this.specialtyService = specialtyService;
        this.mapper = mapper;
    }

    @GetMapping("/specialties")
    public ResponseEntity<List<SpecialtyDTO>> findAllSpecialties(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "office", required = false) String office) {
        List<Specialty> specialties;
        if (name != null && !name.isEmpty()) {
            specialties = specialtyService.findByName(name);
        } else if (office != null && !office.isEmpty()) {
            specialties = specialtyService.findByOffice(office);
        } else {
            specialties = specialtyService.findAll();
        }
        return ResponseEntity.ok(mapper.toDTOList(specialties));
    }

    @GetMapping("/specialties/{id}")
    public ResponseEntity<SpecialtyDTO> findById(@PathVariable Integer id) {
        try {
            Specialty specialty = specialtyService.findById(id);
            return ResponseEntity.ok(mapper.toDTO(specialty));
        } catch (SpecialtyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/specialties")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SpecialtyDTO> create(@RequestBody SpecialtyDTO specialtyDTO) {
        if (specialtyDTO.getHOpen() != null && specialtyDTO.getHClose() != null) {
            if (specialtyDTO.getHOpen() >= specialtyDTO.getHClose()) {
                return ResponseEntity.badRequest().build();
            }
            if (specialtyDTO.getHOpen() < 0 || specialtyDTO.getHOpen() > 23 ||
                specialtyDTO.getHClose() < 0 || specialtyDTO.getHClose() > 23) {
                return ResponseEntity.badRequest().build();
            }
        }
        Specialty specialty = mapper.toEntity(specialtyDTO);
        Specialty created = specialtyService.create(specialty);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    @DeleteMapping("/specialties/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            specialtyService.delete(id);
            return ResponseEntity.ok().build();
        } catch (SpecialtyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

