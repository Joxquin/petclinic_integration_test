package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.entities.VetSpecialty;
import com.tecsup.petclinic.entities.VetSpecialtyId;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;
import com.tecsup.petclinic.services.VetSpecialtyService;
import com.tecsup.petclinic.util.TObjectCreator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VetSpecialtyControllerMockitoTest {

    private static final ObjectMapper om = new ObjectMapper();

    static {
        om.findAndRegisterModules();
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VetSpecialtyService vetSpecialtyService;

    // Integrante A — Asignación
    @Test
    public void testAssignSpecialtyToVet() throws Exception {
        VetSpecialtyDTO dto = TObjectCreator.newVetSpecialtyTO();
        VetSpecialty saved = VetSpecialty.builder()
                .id(new VetSpecialtyId(dto.getVetId(), dto.getSpecialtyId()))
                .certificationDate(dto.getCertificationDate())
                .yearsExperience(dto.getYearsExperience())
                .isPrimary(dto.getIsPrimary())
                .notes(dto.getNotes())
                .build();

        Mockito.when(vetSpecialtyService.assignSpecialtyToVet(any(VetSpecialty.class))).thenReturn(saved);

        mockMvc.perform(post("/vet-specialties")
                        .content(om.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vetId", is(1)))
                .andExpect(jsonPath("$.specialtyId", is(2)));
    }

    @Test
    public void testRemoveSpecialtyFromVet() throws Exception {
        Mockito.doNothing().when(vetSpecialtyService).removeSpecialtyFromVet(4, 2);

        mockMvc.perform(delete("/vet-specialties/4/2"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAssignDuplicate_ShouldFail() throws Exception {
        Mockito.when(vetSpecialtyService.assignSpecialtyToVet(any(VetSpecialty.class)))
                .thenThrow(new DuplicateKeyException("Duplicate assignment"));

        VetSpecialtyDTO dto = TObjectCreator.getVetSpecialtyTO();

        mockMvc.perform(post("/vet-specialties")
                        .content(om.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    // Integrante B — Consultas
    @Test
    public void testFindSpecialtiesByVet() throws Exception {
        Specialty s1 = Specialty.builder().id(2).name("surgery").build();
        Specialty s2 = Specialty.builder().id(3).name("dentistry").build();

        Mockito.when(vetSpecialtyService.findSpecialtiesByVet(3)).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/vets/3/specialties"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    public void testFindVetsBySpecialty() throws Exception {
        Vet v1 = Vet.builder().id(2).firstName("Helen").lastName("Leary").build();
        Vet v2 = Vet.builder().id(5).firstName("Henry").lastName("Stevens").build();

        Mockito.when(vetSpecialtyService.findVetsBySpecialty(1)).thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/specialties/1/vets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    public void testFindSpecialtiesByVet_Empty() throws Exception {
        Mockito.when(vetSpecialtyService.findSpecialtiesByVet(6)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/vets/6/specialties"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(0)));
    }

    // Integrante C — Especialidad principal
    @Test
    public void testSetPrimarySpecialty() throws Exception {
        VetSpecialty vs = TObjectCreator.getVetSpecialty();
        vs.setIsPrimary(true);

        Mockito.when(vetSpecialtyService.setPrimarySpecialty(2, 1)).thenReturn(vs);

        mockMvc.perform(put("/vet-specialties/2/1/primary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPrimary", is(true)));
    }

    @Test
    public void testSetPrimarySpecialty_NotAssigned() throws Exception {
        Mockito.when(vetSpecialtyService.setPrimarySpecialty(1, 2))
                .thenThrow(new VetSpecialtyNotFoundException("Not found"));

        mockMvc.perform(put("/vet-specialties/1/2/primary"))
                .andExpect(status().isNotFound());
    }

    // Integrante D — Experiencia y certificaciones
    @Test
    public void testFindVetsByMinExperience() throws Exception {
        Vet v1 = Vet.builder().id(2).build();
        Vet v2 = Vet.builder().id(5).build();

        Mockito.when(vetSpecialtyService.findVetsByMinExperience(1, 7)).thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/specialties/1/vets?minExperience=7"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    public void testUpdateYearsExperience() throws Exception {
        VetSpecialty vs = TObjectCreator.getVetSpecialty();
        vs.setYearsExperience(10);

        Mockito.when(vetSpecialtyService.updateYearsExperience(2, 1, 10)).thenReturn(vs);

        mockMvc.perform(put("/vet-specialties/2/1/experience")
                        .content(om.writeValueAsString(Map.of("yearsExperience", 10)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yearsExperience", is(10)));
    }

    @Test
    public void testUpdateCertificationDate() throws Exception {
        VetSpecialty vs = TObjectCreator.getVetSpecialty();
        LocalDate newDate = LocalDate.of(2019, 1, 1);
        vs.setCertificationDate(newDate);

        Mockito.when(vetSpecialtyService.updateCertificationDate(eq(2), eq(1), any(LocalDate.class))).thenReturn(vs);

        mockMvc.perform(put("/vet-specialties/2/1/certification")
                        .content(om.writeValueAsString(Map.of("certificationDate", "2019-01-01")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificationDate", is("2019-01-01")));
    }
}

