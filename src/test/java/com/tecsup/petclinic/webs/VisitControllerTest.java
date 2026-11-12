package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.VisitDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VisitControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    static {
        om.findAndRegisterModules();
    }

    @Autowired
    private MockMvc mockMvc;

    // Integrante A — Listado y consulta
    @Test
    public void testFindAllVisits() throws Exception {
        mockMvc.perform(get("/visits"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("rabies shot")));
    }

    @Test
    public void testFindVisitOK() throws Exception {
        mockMvc.perform(get("/visits/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("rabies shot")));
    }

    @Test
    public void testFindVisitKO() throws Exception {
        mockMvc.perform(get("/visits/666"))
                .andExpect(status().isNotFound());
    }

    // Integrante B — Creación y borrado
    @Test
    public void testCreateVisit() throws Exception {
        VisitDTO newVisit = VisitDTO.builder()
                .petId(1)
                .vetId(1)
                .visitDate(LocalDate.of(2023, 5, 10))
                .description("routine checkup")
                .cost(new BigDecimal("50.00"))
                .build();

        mockMvc.perform(post("/visits")
                        .content(om.writeValueAsString(newVisit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("routine checkup")));
    }

    @Test
    public void testUpdateVisit() throws Exception {
        VisitDTO newVisit = VisitDTO.builder()
                .petId(1)
                .vetId(1)
                .visitDate(LocalDate.of(2024, 6, 1))
                .description("vaccine visit")
                .cost(new BigDecimal("40.00"))
                .build();

        ResultActions postResult = mockMvc.perform(post("/visits")
                        .content(om.writeValueAsString(newVisit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String response = postResult.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(response, "$.id");

        VisitDTO updateDTO = VisitDTO.builder()
                .id(id)
                .petId(1)
                .vetId(1)
                .visitDate(LocalDate.of(2023, 6, 1))
                .description("vaccine visit confirmed")
                .cost(new BigDecimal("40.00"))
                .build();

        mockMvc.perform(put("/visits/" + id)
                        .content(om.writeValueAsString(updateDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("vaccine visit confirmed")));
    }

    @Test
    public void testDeleteVisit() throws Exception {
        VisitDTO newVisit = VisitDTO.builder()
                .petId(2)
                .vetId(1)
                .visitDate(LocalDate.of(2024, 7, 1))
                .description("temporary visit")
                .cost(new BigDecimal("30.00"))
                .build();

        ResultActions postResult = mockMvc.perform(post("/visits")
                        .content(om.writeValueAsString(newVisit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String response = postResult.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/visits/" + id))
                .andExpect(status().isOk());
    }

    // Integrante C — Consultas por relación
    @Test
    public void testFindByPetId() throws Exception {
        mockMvc.perform(get("/visits?petId=7"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    public void testFindByVetId() throws Exception {
        mockMvc.perform(get("/visits?vetId=2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testCalculateTotalCostByPet() throws Exception {
        mockMvc.perform(get("/visits/pet/7/total-cost"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(320.0)));
    }

    // Integrante D — Consultas por fecha
    @Test
    public void testFindByDateBetween() throws Exception {
        mockMvc.perform(get("/visits?from=2024-01-01&to=2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    public void testFindByVetIdAndDateBetween() throws Exception {
        mockMvc.perform(get("/visits?vetId=1&from=2024-01-01&to=2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testCountVisitsByPet() throws Exception {
        mockMvc.perform(get("/visits/pet/7/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(2)));
    }
}
