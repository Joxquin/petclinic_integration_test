package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.PetTypeDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class PetTypeControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    // Integrante A — Listado y consulta
    @Test
    public void testFindAllTypes() throws Exception {
        mockMvc.perform(get("/types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("cat")));
    }

    @Test
    public void testFindTypeOK() throws Exception {
        mockMvc.perform(get("/types/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("cat")));
    }

    @Test
    public void testFindTypeKO() throws Exception {
        mockMvc.perform(get("/types/666"))
                .andExpect(status().isNotFound());
    }

    // Integrante B — Creación y reporte
    @Test
    public void testCreateType() throws Exception {
        PetTypeDTO newType = PetTypeDTO.builder()
                .name("ferret")
                .description("Small mammal")
                .active(true)
                .sizeCategory("small")
                .averageLifespan(7)
                .careLevel("medium")
                .build();

        mockMvc.perform(post("/types")
                        .content(om.writeValueAsString(newType))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("ferret")));
    }

    @Test
    public void testGetPetCountByType() throws Exception {
        mockMvc.perform(get("/types/report/pet-count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cat", is(4)));
    }

    @Test
    public void testDeleteType() throws Exception {
        PetTypeDTO toCreate = PetTypeDTO.builder()
                .name("hamster-temp")
                .active(true)
                .build();

        ResultActions postResult = mockMvc.perform(post("/types")
                        .content(om.writeValueAsString(toCreate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String response = postResult.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/types/" + id))
                .andExpect(status().isOk());
    }

    // Integrante C — Filtros y errores
    @Test
    public void testFindActiveTypes() throws Exception {
        mockMvc.perform(get("/types?active=true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(7)));
    }

    @Test
    public void testGetPetCountByType_ExcludeInactive() throws Exception {
        mockMvc.perform(get("/types/report/pet-count?excludeInactive=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cat", is(4)))
                .andExpect(jsonPath("$.snake").doesNotExist());
    }

    @Test
    public void testDeleteTypeKO() throws Exception {
        mockMvc.perform(delete("/types/1000"))
                .andExpect(status().isNotFound());
    }
}

