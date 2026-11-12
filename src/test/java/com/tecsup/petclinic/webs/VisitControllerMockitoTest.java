package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.entities.Visit;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.services.VisitService;
import com.tecsup.petclinic.util.TObjectCreator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VisitControllerMockitoTest {

    private static final ObjectMapper om = new ObjectMapper();

    static {
        om.findAndRegisterModules();
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VisitService visitService;

    // Integrante A — Listado y consulta
    @Test
    public void testFindAllVisits() throws Exception {
        Visit visit = TObjectCreator.getVisit();
        Mockito.when(visitService.findAll()).thenReturn(List.of(visit));

        mockMvc.perform(get("/visits"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("rabies shot")));
    }

    @Test
    public void testFindVisitOK() throws Exception {
        Visit visit = TObjectCreator.getVisit();
        Mockito.when(visitService.findById(1)).thenReturn(visit);

        mockMvc.perform(get("/visits/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("rabies shot")));
    }

    @Test
    public void testFindVisitKO() throws Exception {
        Mockito.when(visitService.findById(666)).thenThrow(new VisitNotFoundException("Visit not found"));

        mockMvc.perform(get("/visits/666"))
                .andExpect(status().isNotFound());
    }

    // Integrante B — Creación y borrado
    @Test
    public void testCreateVisit() throws Exception {
        VisitDTO visitDTO = TObjectCreator.newVisitTO();
        Visit saved = Visit.builder()
                .id(10)
                .petId(visitDTO.getPetId())
                .vetId(visitDTO.getVetId())
                .visitDate(visitDTO.getVisitDate())
                .description(visitDTO.getDescription())
                .cost(visitDTO.getCost())
                .build();

        Mockito.when(visitService.create(any(Visit.class))).thenReturn(saved);

        mockMvc.perform(post("/visits")
                        .content(om.writeValueAsString(visitDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.description", is("checkup")));
    }

    @Test
    public void testUpdateVisit() throws Exception {
        Visit existing = TObjectCreator.getVisit();
        Visit updated = TObjectCreator.getVisit();
        updated.setDescription("rabies shot updated");

        Mockito.when(visitService.findById(1)).thenReturn(existing);
        Mockito.when(visitService.update(any(Visit.class))).thenReturn(updated);

        VisitDTO updateDTO = TObjectCreator.getVisitTO();
        updateDTO.setDescription("rabies shot updated");

        mockMvc.perform(put("/visits/1")
                        .content(om.writeValueAsString(updateDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("rabies shot updated")));
    }

    @Test
    public void testDeleteVisit() throws Exception {
        Mockito.doNothing().when(visitService).delete(1);

        mockMvc.perform(delete("/visits/1"))
                .andExpect(status().isOk());
    }

    // Integrante C — Consultas por relación
    @Test
    public void testFindByPetId() throws Exception {
        Visit v1 = TObjectCreator.getVisit();
        Mockito.when(visitService.findByPetId(7)).thenReturn(List.of(v1));

        mockMvc.perform(get("/visits?petId=7"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    public void testFindByVetId() throws Exception {
        Visit v1 = TObjectCreator.getVisit();
        Mockito.when(visitService.findByVetId(2)).thenReturn(List.of(v1));

        mockMvc.perform(get("/visits?vetId=2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    public void testCalculateTotalCostByPet() throws Exception {
        Mockito.when(visitService.calculateTotalCostByPet(7)).thenReturn(new BigDecimal("320.00"));

        mockMvc.perform(get("/visits/pet/7/total-cost"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(320.00)));
    }

    // Integrante D — Consultas por fecha
    @Test
    public void testFindByDateBetween() throws Exception {
        Visit v5 = Visit.builder().id(5).visitDate(LocalDate.of(2024, 1, 15)).build();
        Visit v6 = Visit.builder().id(6).visitDate(LocalDate.of(2024, 2, 20)).build();
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);

        Mockito.when(visitService.findByDateBetween(from, to)).thenReturn(List.of(v5, v6));

        mockMvc.perform(get("/visits?from=2024-01-01&to=2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    public void testFindByVetIdAndDateBetween() throws Exception {
        Visit v5 = Visit.builder().id(5).vetId(1).visitDate(LocalDate.of(2024, 1, 15)).build();
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);

        Mockito.when(visitService.findByVetIdAndDateBetween(1, from, to)).thenReturn(List.of(v5));

        mockMvc.perform(get("/visits?vetId=1&from=2024-01-01&to=2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    public void testCountVisitsByPet() throws Exception {
        Mockito.when(visitService.countVisitsByPet(7)).thenReturn(2L);

        mockMvc.perform(get("/visits/pet/7/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(2)));
    }
}

