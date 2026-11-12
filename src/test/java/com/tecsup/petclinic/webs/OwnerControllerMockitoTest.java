package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.services.OwnerService;
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
public class OwnerControllerMockitoTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerService ownerService;

    // Integrante A — Listado y consulta
    @Test
    public void testFindAllOwners() throws Exception {
        Owner owner = TObjectCreator.getOwner();
        Mockito.when(ownerService.findAll()).thenReturn(List.of(owner));

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].lastName", is("Franklin")));
    }

    @Test
    public void testFindOwnerOK() throws Exception {
        Owner owner = TObjectCreator.getOwner();
        Mockito.when(ownerService.findById(1)).thenReturn(owner);

        mockMvc.perform(get("/owners/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lastName", is("Franklin")));
    }

    @Test
    public void testFindOwnerKO() throws Exception {
        Mockito.when(ownerService.findById(666)).thenThrow(new OwnerNotFoundException("Owner not found"));

        mockMvc.perform(get("/owners/666"))
                .andExpect(status().isNotFound());
    }

    // Integrante B — Creación
    @Test
    public void testCreateOwner() throws Exception {
        OwnerDTO ownerDTO = TObjectCreator.newOwnerTO();
        Owner savedOwner = Owner.builder()
                .id(11)
                .firstName(ownerDTO.getFirstName())
                .lastName(ownerDTO.getLastName())
                .address(ownerDTO.getAddress())
                .city(ownerDTO.getCity())
                .telephone(ownerDTO.getTelephone())
                .build();

        Mockito.when(ownerService.create(any(Owner.class))).thenReturn(savedOwner);

        mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(ownerDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(11)))
                .andExpect(jsonPath("$.lastName", is("Wayne")));
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
        Owner o1 = Owner.builder().id(2).firstName("Betty").lastName("Davis").build();
        Owner o2 = Owner.builder().id(4).firstName("Harold").lastName("Davis").build();

        Mockito.when(ownerService.findByLastName("Davis")).thenReturn(List.of(o1, o2));

        mockMvc.perform(get("/owners?lastName=Davis"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)));
    }

    // Integrante C — Actualización y borrado
    @Test
    public void testUpdateOwner() throws Exception {
        Owner existing = TObjectCreator.getOwner();
        Owner updated = TObjectCreator.getOwner();
        updated.setLastName("Franklin-Updated");

        Mockito.when(ownerService.findById(1)).thenReturn(existing);
        Mockito.when(ownerService.update(any(Owner.class))).thenReturn(updated);

        OwnerDTO updateDTO = TObjectCreator.getOwnerTO();
        updateDTO.setLastName("Franklin-Updated");

        mockMvc.perform(put("/owners/1")
                        .content(om.writeValueAsString(updateDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Franklin-Updated")));
    }

    @Test
    public void testDeleteOwner() throws Exception {
        Mockito.doNothing().when(ownerService).delete(1);

        mockMvc.perform(delete("/owners/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteOwnerKO() throws Exception {
        Mockito.doThrow(new OwnerNotFoundException("Owner not found")).when(ownerService).delete(1000);

        mockMvc.perform(delete("/owners/1000"))
                .andExpect(status().isNotFound());
    }
}

