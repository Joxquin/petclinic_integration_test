package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.PetTypeDTO;
import com.tecsup.petclinic.entities.PetType;
import com.tecsup.petclinic.exceptions.PetTypeNotFoundException;
import com.tecsup.petclinic.mapper.PetTypeMapper;
import com.tecsup.petclinic.services.PetTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class PetTypeController {

    private final PetTypeService petTypeService;
    private final PetTypeMapper mapper;

    public PetTypeController(PetTypeService petTypeService, PetTypeMapper mapper) {
        this.petTypeService = petTypeService;
        this.mapper = mapper;
    }

    @GetMapping("/types")
    public ResponseEntity<List<PetTypeDTO>> findAllTypes(@RequestParam(value = "active", required = false) Boolean active) {
        List<PetType> types;
        if (active != null) {
            types = petTypeService.findByActive(active);
        } else {
            types = petTypeService.findAll();
        }
        return ResponseEntity.ok(mapper.toDTOList(types));
    }

    @GetMapping("/types/{id}")
    public ResponseEntity<PetTypeDTO> findById(@PathVariable Integer id) {
        try {
            PetType petType = petTypeService.findById(id);
            return ResponseEntity.ok(mapper.toDTO(petType));
        } catch (PetTypeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/types")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PetTypeDTO> create(@RequestBody PetTypeDTO dto) {
        PetType petType = mapper.toEntity(dto);
        PetType created = petTypeService.create(petType);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    @GetMapping("/types/report/pet-count")
    public ResponseEntity<Map<String, Long>> getPetCountByType(
            @RequestParam(value = "excludeInactive", required = false, defaultValue = "false") boolean excludeInactive) {
        Map<String, Long> report = petTypeService.getPetCountByType(excludeInactive);
        return ResponseEntity.ok(report);
    }

    @DeleteMapping("/types/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            petTypeService.delete(id);
            return ResponseEntity.ok().build();
        } catch (PetTypeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

