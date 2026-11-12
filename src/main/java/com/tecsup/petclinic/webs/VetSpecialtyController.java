package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.entities.VetSpecialty;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;
import com.tecsup.petclinic.mapper.SpecialtyMapper;
import com.tecsup.petclinic.mapper.VetMapper;
import com.tecsup.petclinic.mapper.VetSpecialtyMapper;
import com.tecsup.petclinic.services.VetSpecialtyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class VetSpecialtyController {

    private final VetSpecialtyService vetSpecialtyService;
    private final VetSpecialtyMapper mapper;
    private final VetMapper vetMapper;
    private final SpecialtyMapper specialtyMapper;

    public VetSpecialtyController(VetSpecialtyService vetSpecialtyService,
                                  VetSpecialtyMapper mapper,
                                  VetMapper vetMapper,
                                  SpecialtyMapper specialtyMapper) {
        this.vetSpecialtyService = vetSpecialtyService;
        this.mapper = mapper;
        this.vetMapper = vetMapper;
        this.specialtyMapper = specialtyMapper;
    }

    @PostMapping("/vet-specialties")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<VetSpecialtyDTO> assignSpecialtyToVet(@RequestBody VetSpecialtyDTO dto) {
        try {
            VetSpecialty vs = mapper.toEntity(dto);
            VetSpecialty created = vetSpecialtyService.assignSpecialtyToVet(vs);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/vet-specialties/{vetId}/{specId}")
    public ResponseEntity<Void> removeSpecialtyFromVet(@PathVariable Integer vetId, @PathVariable Integer specId) {
        try {
            vetSpecialtyService.removeSpecialtyFromVet(vetId, specId);
            return ResponseEntity.ok().build();
        } catch (VetSpecialtyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/vets/{vetId}/specialties")
    public ResponseEntity<List<SpecialtyDTO>> findSpecialtiesByVet(@PathVariable Integer vetId) {
        List<Specialty> specialties = vetSpecialtyService.findSpecialtiesByVet(vetId);
        return ResponseEntity.ok(specialtyMapper.toDTOList(specialties));
    }

    @GetMapping("/specialties/{specId}/vets")
    public ResponseEntity<List<VetDTO>> findVetsBySpecialty(
            @PathVariable Integer specId,
            @RequestParam(value = "minExperience", required = false) Integer minExperience) {
        List<Vet> vets;
        if (minExperience != null) {
            vets = vetSpecialtyService.findVetsByMinExperience(specId, minExperience);
        } else {
            vets = vetSpecialtyService.findVetsBySpecialty(specId);
        }
        return ResponseEntity.ok(vetMapper.toDTOList(vets));
    }

    @PutMapping("/vet-specialties/{vetId}/{specId}/primary")
    public ResponseEntity<VetSpecialtyDTO> setPrimarySpecialty(@PathVariable Integer vetId, @PathVariable Integer specId) {
        try {
            VetSpecialty updated = vetSpecialtyService.setPrimarySpecialty(vetId, specId);
            return ResponseEntity.ok(mapper.toDTO(updated));
        } catch (VetSpecialtyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/vet-specialties/{vetId}/{specId}/experience")
    public ResponseEntity<VetSpecialtyDTO> updateYearsExperience(
            @PathVariable Integer vetId,
            @PathVariable Integer specId,
            @RequestBody Map<String, Integer> payload) {
        try {
            Integer years = payload.get("yearsExperience");
            VetSpecialty updated = vetSpecialtyService.updateYearsExperience(vetId, specId, years);
            return ResponseEntity.ok(mapper.toDTO(updated));
        } catch (VetSpecialtyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/vet-specialties/{vetId}/{specId}/certification")
    public ResponseEntity<VetSpecialtyDTO> updateCertificationDate(
            @PathVariable Integer vetId,
            @PathVariable Integer specId,
            @RequestBody Map<String, String> payload) {
        try {
            LocalDate date = LocalDate.parse(payload.get("certificationDate"));
            VetSpecialty updated = vetSpecialtyService.updateCertificationDate(vetId, specId, date);
            return ResponseEntity.ok(mapper.toDTO(updated));
        } catch (VetSpecialtyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

