package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.OwnerDTO;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class OwnerControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    // Integrante A — Listado y consulta
    @Test
    public void testFindAllOwners() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    public void testFindOwnerOK() throws Exception {
        mockMvc.perform(get("/owners/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lastName", is("Franklin")));
    }

    @Test
    public void testFindOwnerKO() throws Exception {
        mockMvc.perform(get("/owners/666"))
                .andExpect(status().isNotFound());
    }

    // Integrante B — Creación
    @Test
    public void testCreateOwner() throws Exception {
        OwnerDTO newOwner = OwnerDTO.builder()
                .firstName("Bruce")
                .lastName("Wayne")
                .address("Wayne Manor")
                .city("Gotham")
                .telephone("6085550000")
                .build();

        mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(newOwner))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("Bruce")))
                .andExpect(jsonPath("$.lastName", is("Wayne")))
                .andExpect(jsonPath("$.city", is("Gotham")));
    }

    @Test
    public void testCreateOwner_BadRequest() throws Exception {
        OwnerDTO invalidOwner = OwnerDTO.builder()
                .firstName("Bruce")
                .address("Wayne Manor")
                .build();

        mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(invalidOwner))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindByLastName() throws Exception {
        mockMvc.perform(get("/owners?lastName=Davis"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)));
    }

    // Integrante C — Actualización y borrado
    @Test
    public void testUpdateOwner() throws Exception {
        OwnerDTO newOwner = OwnerDTO.builder()
                .firstName("Clark")
                .lastName("Kent")
                .address("Metropolis St.")
                .city("Metropolis")
                .telephone("6085551111")
                .build();

        ResultActions postResult = mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(newOwner))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String response = postResult.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(response, "$.id");

        OwnerDTO updateOwner = OwnerDTO.builder()
                .id(id)
                .firstName("Clark")
                .lastName("Kent-Superman")
                .address("Metropolis St.")
                .city("Metropolis")
                .telephone("6085551111")
                .build();

        mockMvc.perform(put("/owners/" + id)
                        .content(om.writeValueAsString(updateOwner))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Kent-Superman")));

        mockMvc.perform(get("/owners/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Kent-Superman")));
    }

    @Test
    public void testDeleteOwner() throws Exception {
        OwnerDTO newOwner = OwnerDTO.builder()
                .firstName("Diana")
                .lastName("Prince")
                .address("Themyscira")
                .city("Paradise")
                .telephone("6085552222")
                .build();

        ResultActions postResult = mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(newOwner))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String response = postResult.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/owners/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteOwnerKO() throws Exception {
        mockMvc.perform(delete("/owners/1000"))
                .andExpect(status().isNotFound());
    }
}

