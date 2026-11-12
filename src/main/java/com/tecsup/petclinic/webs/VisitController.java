package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.entities.Visit;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.mapper.VisitMapper;
import com.tecsup.petclinic.services.VisitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
public class VisitController {

    private final VisitService visitService;
    private final VisitMapper mapper;

    public VisitController(VisitService visitService, VisitMapper mapper) {
        this.visitService = visitService;
        this.mapper = mapper;
    }

    @GetMapping("/visits")
    public ResponseEntity<List<VisitDTO>> findVisits(
            @RequestParam(value = "petId", required = false) Integer petId,
            @RequestParam(value = "vetId", required = false) Integer vetId,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<Visit> visits;
        if (vetId != null && from != null && to != null) {
            visits = visitService.findByVetIdAndDateBetween(vetId, from, to);
        } else if (from != null && to != null) {
            visits = visitService.findByDateBetween(from, to);
        } else if (petId != null) {
            visits = visitService.findByPetId(petId);
        } else if (vetId != null) {
            visits = visitService.findByVetId(vetId);
        } else {
            visits = visitService.findAll();
        }
        return ResponseEntity.ok(mapper.toDTOList(visits));
    }

    @GetMapping("/visits/{id}")
    public ResponseEntity<VisitDTO> findById(@PathVariable Integer id) {
        try {
            Visit visit = visitService.findById(id);
            return ResponseEntity.ok(mapper.toDTO(visit));
        } catch (VisitNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/visits")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<VisitDTO> create(@RequestBody VisitDTO visitDTO) {
        Visit visit = mapper.toEntity(visitDTO);
        Visit created = visitService.create(visit);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    @PutMapping("/visits/{id}")
    public ResponseEntity<VisitDTO> update(@PathVariable Integer id, @RequestBody VisitDTO visitDTO) {
        try {
            visitService.findById(id);
            Visit visit = mapper.toEntity(visitDTO);
            visit.setId(id);
            Visit updated = visitService.update(visit);
            return ResponseEntity.ok(mapper.toDTO(updated));
        } catch (VisitNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/visits/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            visitService.delete(id);
            return ResponseEntity.ok().build();
        } catch (VisitNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/visits/pet/{id}/total-cost")
    public ResponseEntity<BigDecimal> getTotalCostByPet(@PathVariable("id") Integer petId) {
        BigDecimal total = visitService.calculateTotalCostByPet(petId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/visits/pet/{id}/count")
    public ResponseEntity<Long> getCountByPet(@PathVariable("id") Integer petId) {
        long count = visitService.countVisitsByPet(petId);
        return ResponseEntity.ok(count);
    }
}

