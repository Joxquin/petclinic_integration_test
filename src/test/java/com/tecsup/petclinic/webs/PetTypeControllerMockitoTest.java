package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.PetTypeDTO;
import com.tecsup.petclinic.entities.PetType;
import com.tecsup.petclinic.exceptions.PetTypeNotFoundException;
import com.tecsup.petclinic.services.PetTypeService;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class PetTypeControllerMockitoTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PetTypeService petTypeService;

    // Integrante A — Listado y consulta
    @Test
    public void testFindAllTypes() throws Exception {
        PetType petType = TObjectCreator.getPetType();
        Mockito.when(petTypeService.findAll()).thenReturn(List.of(petType));

        mockMvc.perform(get("/types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("cat")));
    }

    @Test
    public void testFindTypeOK() throws Exception {
        PetType petType = TObjectCreator.getPetType();
        Mockito.when(petTypeService.findById(1)).thenReturn(petType);

        mockMvc.perform(get("/types/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("cat")));
    }

    @Test
    public void testFindTypeKO() throws Exception {
        Mockito.when(petTypeService.findById(666)).thenThrow(new PetTypeNotFoundException("Type not found"));

        mockMvc.perform(get("/types/666"))
                .andExpect(status().isNotFound());
    }

    // Integrante B — Creación y reporte
    @Test
    public void testCreateType() throws Exception {
        PetTypeDTO newTypeDTO = TObjectCreator.newPetTypeTO();
        PetType saved = PetType.builder()
                .id(9)
                .name(newTypeDTO.getName())
                .description(newTypeDTO.getDescription())
                .active(newTypeDTO.getActive())
                .sizeCategory(newTypeDTO.getSizeCategory())
                .averageLifespan(newTypeDTO.getAverageLifespan())
                .careLevel(newTypeDTO.getCareLevel())
                .build();

        Mockito.when(petTypeService.create(any(PetType.class))).thenReturn(saved);

        mockMvc.perform(post("/types")
                        .content(om.writeValueAsString(newTypeDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(9)))
                .andExpect(jsonPath("$.name", is("ferret")));
    }

    @Test
    public void testGetPetCountByType() throws Exception {
        Map<String, Long> report = new LinkedHashMap<>();
        report.put("cat", 4L);
        report.put("dog", 4L);

        Mockito.when(petTypeService.getPetCountByType(false)).thenReturn(report);

        mockMvc.perform(get("/types/report/pet-count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cat", is(4)));
    }

    @Test
    public void testDeleteType() throws Exception {
        Mockito.doNothing().when(petTypeService).delete(1);

        mockMvc.perform(delete("/types/1"))
                .andExpect(status().isOk());
    }

    // Integrante C — Filtros y errores
    @Test
    public void testFindActiveTypes() throws Exception {
        List<PetType> activeList = List.of(
                PetType.builder().id(1).name("cat").active(true).build(),
                PetType.builder().id(2).name("dog").active(true).build()
        );
        Mockito.when(petTypeService.findByActive(true)).thenReturn(activeList);

        mockMvc.perform(get("/types?active=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    public void testGetPetCountByType_ExcludeInactive() throws Exception {
        Map<String, Long> report = new LinkedHashMap<>();
        report.put("cat", 4L);

        Mockito.when(petTypeService.getPetCountByType(true)).thenReturn(report);

        mockMvc.perform(get("/types/report/pet-count?excludeInactive=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cat", is(4)))
                .andExpect(jsonPath("$.snake").doesNotExist());
    }

    @Test
    public void testDeleteTypeKO() throws Exception {
        Mockito.doThrow(new PetTypeNotFoundException("Type not found")).when(petTypeService).delete(1000);

        mockMvc.perform(delete("/types/1000"))
                .andExpect(status().isNotFound());
    }
}

