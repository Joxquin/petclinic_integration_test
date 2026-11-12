package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VetSpecialtyControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    static {
        om.findAndRegisterModules();
    }

    @Autowired
    private MockMvc mockMvc;

    // Integrante A — Asignación
    @Test
    public void testAssignSpecialtyToVet() throws Exception {
        VetSpecialtyDTO dto = VetSpecialtyDTO.builder()
                .vetId(1)
                .specialtyId(3)
                .certificationDate(LocalDate.of(2021, 5, 20))
                .yearsExperience(2)
                .isPrimary(false)
                .notes("Dentistry training")
                .build();

        mockMvc.perform(post("/vet-specialties")
                        .content(om.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vetId", is(1)))
                .andExpect(jsonPath("$.specialtyId", is(3)));
    }

    @Test
    public void testRemoveSpecialtyFromVet() throws Exception {
        mockMvc.perform(delete("/vet-specialties/4/2"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAssignDuplicate_ShouldFail() throws Exception {
        VetSpecialtyDTO dto = VetSpecialtyDTO.builder()
                .vetId(2)
                .specialtyId(1)
                .certificationDate(LocalDate.of(2015, 6, 15))
                .yearsExperience(8)
                .isPrimary(true)
                .notes("Duplicate")
                .build();

        mockMvc.perform(post("/vet-specialties")
                        .content(om.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    // Integrante B — Consultas
    @Test
    public void testFindSpecialtiesByVet() throws Exception {
        mockMvc.perform(get("/vets/3/specialties"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    public void testFindVetsBySpecialty() throws Exception {
        mockMvc.perform(get("/specialties/1/vets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    public void testFindSpecialtiesByVet_Empty() throws Exception {
        mockMvc.perform(get("/vets/6/specialties"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(0)));
    }

    // Integrante C — Especialidad principal
    @Test
    public void testSetPrimarySpecialty() throws Exception {
        mockMvc.perform(put("/vet-specialties/3/3/primary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPrimary", is(true)));
    }

    @Test
    public void testSetPrimarySpecialty_OnlyOnePrimary() throws Exception {
        mockMvc.perform(put("/vet-specialties/3/3/primary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPrimary", is(true)));
    }

    @Test
    public void testSetPrimarySpecialty_NotAssigned() throws Exception {
        mockMvc.perform(put("/vet-specialties/1/2/primary"))
                .andExpect(status().isNotFound());
    }

    // Integrante D — Experiencia y certificaciones
    @Test
    public void testFindVetsByMinExperience() throws Exception {
        mockMvc.perform(get("/specialties/1/vets?minExperience=7"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    public void testUpdateYearsExperience() throws Exception {
        mockMvc.perform(put("/vet-specialties/2/1/experience")
                        .content(om.writeValueAsString(Map.of("yearsExperience", 10)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yearsExperience", is(10)));
    }

    @Test
    public void testUpdateCertificationDate() throws Exception {
        mockMvc.perform(put("/vet-specialties/2/1/certification")
                        .content(om.writeValueAsString(Map.of("certificationDate", "2019-01-01")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificationDate", is("2019-01-01")));
    }
}

