package com.fortalezasec.firewarning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortalezasec.firewarning.domain.Incidente;
import com.fortalezasec.firewarning.domain.NivelPerigo;
import com.fortalezasec.firewarning.domain.DTOs.EmpresaDTO;
import com.fortalezasec.firewarning.domain.DTOs.EmpresaFavoritaDTO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class EmpresaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldListAllEmpresasAndReturnStatus200Ok() throws Exception {
    MvcResult result = mockMvc
        .perform(get("/empresas").with(httpBasic("josecarlos@etc.br", "senha")).contentType("application/json"))
        .andExpect(status().isOk()).andReturn();

    String empresasJsonResponse = result.getResponse().getContentAsString();

    EmpresaDTO[] empresasConverted = objectMapper.readValue(empresasJsonResponse, EmpresaDTO[].class);

    assertEquals("05014725000152", empresasConverted[0].getCnpj());
    assertEquals("03021302000134", empresasConverted[1].getCnpj());
    assertEquals("69855137000124", empresasConverted[2].getCnpj());

  }

  @Test
  void shouldListAllIncidentesAndReturnStatus200Ok() throws Exception {
    MvcResult result = mockMvc
        .perform(get("/empresas/incidentes").with(httpBasic("sysshell@shell.com", "senha")).contentType("application/json"))
        .andExpect(status().isOk()).andReturn();

    String empresasJsonResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Incidente[] incidenteConverted = objectMapper.readValue(empresasJsonResponse, Incidente[].class);

    assertEquals("05014725000152", incidenteConverted[0].getCnpjEmpresa());
    assertEquals("03021302000134", incidenteConverted[5].getCnpjEmpresa());

  }

  @Test
  void shouldListIncidentesFilterByCNPJAndReturnStatus200Ok() throws Exception {
    MvcResult result = mockMvc
        .perform(get("/empresas/incidentes?tipo=cnpj&valor=03021302000134").with(httpBasic("sysshell@shell.com", "senha")).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn();

    String empresasJsonResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Incidente[] incidenteConverted = objectMapper.readValue(empresasJsonResponse, Incidente[].class);

    assertEquals("03021302000134", incidenteConverted[0].getCnpjEmpresa());
    assertEquals("Vazamento de óleo no setor 4", incidenteConverted[0].getComentario());
    assertEquals("03021302000134", incidenteConverted[1].getCnpjEmpresa());
    assertEquals("Container rachado no pier 76", incidenteConverted[1].getComentario());

  }

  @Test
  void shouldReturnEmpresaFavoritaAndReturnStatus200Ok() throws Exception {
    MvcResult result = mockMvc
        .perform(get("/empresas/favorita").with(httpBasic("josecarlos@etc.br", "senha")).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn();

    String empresaFavoritaJsonResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

    EmpresaFavoritaDTO empresaFavoritaConverted = objectMapper.readValue(empresaFavoritaJsonResponse, EmpresaFavoritaDTO.class);

    assertEquals("05014725000152", empresaFavoritaConverted.getCnpj());
    assertEquals("Shell", empresaFavoritaConverted.getFantasia());
    assertEquals(NivelPerigo.OK, empresaFavoritaConverted.getNivelPerigo());
    assertEquals("Tudo ok!", empresaFavoritaConverted.getComentario());

  }
  
}
