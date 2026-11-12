package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.SpecialtyDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class SpecialtyControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    // Integrante A — Listado y consulta
    @Test
    public void testFindAllSpecialties() throws Exception {
        mockMvc.perform(get("/specialties"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    public void testFindSpecialtyOK() throws Exception {
        mockMvc.perform(get("/specialties/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    public void testFindSpecialtyKO() throws Exception {
        mockMvc.perform(get("/specialties/666"))
                .andExpect(status().isNotFound());
    }

    // Integrante B — Creación y búsqueda
    @Test
    public void testCreateSpecialty() throws Exception {
        SpecialtyDTO newSpec = SpecialtyDTO.builder()
                .name("dermatology")
                .office("Springfield")
                .hOpen(9)
                .hClose(17)
                .build();

        mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(newSpec))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("dermatology")))
                .andExpect(jsonPath("$.office", is("Springfield")));
    }

    @Test
    public void testFindByName() throws Exception {
        mockMvc.perform(get("/specialties?name=surgery"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("surgery")));
    }

    @Test
    public void testFindByOffice() throws Exception {
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
        SpecialtyDTO toCreate = SpecialtyDTO.builder()
                .name("ophthalmology")
                .office("Clinic West")
                .hOpen(8)
                .hClose(14)
                .build();

        ResultActions postResult = mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(toCreate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String response = postResult.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/specialties/" + id))
                .andExpect(status().isOk());
    }
}

