package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.services.VetService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VetControllerMockitoTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VetService vetService;

    // Integrante A
    @Test
    public void testFindAllVets() throws Exception {
        List<Vet> vets = TObjectCreator.getActiveVets();
        Mockito.when(vetService.findAll()).thenReturn(vets);

        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].lastName", is("Carter")));
    }

    @Test
    public void testFindVetOK() throws Exception {
        Vet vet = TObjectCreator.getVet();
        Mockito.when(vetService.findById(1)).thenReturn(vet);

        mockMvc.perform(get("/vets/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lastName", is("Carter")));
    }

    @Test
    public void testFindVetKO() throws Exception {
        Mockito.when(vetService.findById(666)).thenThrow(new VetNotFoundException("Vet not found"));

        mockMvc.perform(get("/vets/666"))
                .andExpect(status().isNotFound());
    }

    // Integrante B
    @Test
    public void testCreateVet() throws Exception {
        VetDTO newVetDTO = TObjectCreator.newVetTO();
        Vet savedVet = Vet.builder()
                .id(10)
                .firstName(newVetDTO.getFirstName())
                .lastName(newVetDTO.getLastName())
                .email(newVetDTO.getEmail())
                .phone(newVetDTO.getPhone())
                .active(newVetDTO.getActive())
                .build();

        Mockito.when(vetService.create(any(Vet.class))).thenReturn(savedVet);

        mockMvc.perform(post("/vets")
                        .content(om.writeValueAsString(newVetDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.lastName", is("Smith")));
    }

    @Test
    public void testDeactivateVet() throws Exception {
        Vet deactivatedVet = TObjectCreator.getVet();
        deactivatedVet.setActive(false);

        Mockito.when(vetService.deactivate(1)).thenReturn(deactivatedVet);

        mockMvc.perform(put("/vets/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.active", is(false)));
    }

    @Test
    public void testReactivateVet() throws Exception {
        Vet reactivatedVet = TObjectCreator.getVet();
        reactivatedVet.setId(6);
        reactivatedVet.setActive(true);

        Mockito.when(vetService.reactivate(6)).thenReturn(reactivatedVet);

        mockMvc.perform(put("/vets/6/reactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(6)))
                .andExpect(jsonPath("$.active", is(true)));
    }

    // Integrante C
    @Test
    public void testFindActiveVets() throws Exception {
        List<Vet> activeVets = TObjectCreator.getActiveVets();
        Mockito.when(vetService.findByActive(true)).thenReturn(activeVets);

        mockMvc.perform(get("/vets?active=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(activeVets.size())));
    }

    @Test
    public void testFindInactiveVets() throws Exception {
        List<Vet> inactiveVets = TObjectCreator.getInactiveVets();
        Mockito.when(vetService.findByActive(false)).thenReturn(inactiveVets);

        mockMvc.perform(get("/vets?active=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(inactiveVets.size())));
    }

    @Test
    public void testDeleteVetKO() throws Exception {
        Mockito.doThrow(new VetNotFoundException("Vet not found")).when(vetService).delete(1000);

        mockMvc.perform(delete("/vets/1000"))
                .andExpect(status().isNotFound());
    }
}

