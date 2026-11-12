package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.services.SpecialtyService;
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

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class SpecialtyControllerMockitoTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpecialtyService specialtyService;

    // Integrante A — Listado y consulta
    @Test
    public void testFindAllSpecialties() throws Exception {
        Specialty specialty = TObjectCreator.getSpecialty();
        Mockito.when(specialtyService.findAll()).thenReturn(List.of(specialty));

        mockMvc.perform(get("/specialties"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    public void testFindSpecialtyOK() throws Exception {
        Specialty specialty = TObjectCreator.getSpecialty();
        Mockito.when(specialtyService.findById(1)).thenReturn(specialty);

        mockMvc.perform(get("/specialties/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    public void testFindSpecialtyKO() throws Exception {
        Mockito.when(specialtyService.findById(666)).thenThrow(new SpecialtyNotFoundException("Specialty not found"));

        mockMvc.perform(get("/specialties/666"))
                .andExpect(status().isNotFound());
    }

    // Integrante B — Creación y búsqueda
    @Test
    public void testCreateSpecialty() throws Exception {
        SpecialtyDTO newSpecDTO = TObjectCreator.newSpecialtyTO();
        Specialty saved = Specialty.builder()
                .id(4)
                .name(newSpecDTO.getName())
                .office(newSpecDTO.getOffice())
                .hOpen(newSpecDTO.getHOpen())
                .hClose(newSpecDTO.getHClose())
                .build();

        Mockito.when(specialtyService.create(any(Specialty.class))).thenReturn(saved);

        mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(newSpecDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.name", is("dermatology")));
    }

    @Test
    public void testFindByName() throws Exception {
        Specialty surgery = Specialty.builder().id(2).name("surgery").office("Maryland").hOpen(8).hClose(12).build();
        Mockito.when(specialtyService.findByName("surgery")).thenReturn(List.of(surgery));

        mockMvc.perform(get("/specialties?name=surgery"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("surgery")));
    }

    @Test
    public void testFindByOffice() throws Exception {
        Specialty rad = TObjectCreator.getSpecialty();
        Mockito.when(specialtyService.findByOffice("Farewell")).thenReturn(List.of(rad));

        mockMvc.perform(get("/specialties?office=Farewell"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    // Integrante C — Validaciones y borrado
    @Test
    public void testCreateSpecialty_InvalidSchedule() throws Exception {
        SpecialtyDTO invalid = SpecialtyDTO.builder()
                .name("invalid")
                .office("Office A")
                .hOpen(18)
                .hClose(8)
                .build();

        mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(invalid))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateSpecialty_InvalidHourRange() throws Exception {
        SpecialtyDTO invalid = SpecialtyDTO.builder()
                .name("invalid-range")
                .office("Office B")
                .hOpen(-1)
                .hClose(25)
                .build();

        mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(invalid))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteSpecialty() throws Exception {
        Mockito.doNothing().when(specialtyService).delete(1);

        mockMvc.perform(delete("/specialties/1"))
                .andExpect(status().isOk());
    }
}

