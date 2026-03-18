package com.btg.prueba_tecnica_seti.Controller;

import com.btg.prueba_tecnica_seti.entity.Cliente;
import com.btg.prueba_tecnica_seti.repository.ClienteRepository;
import com.btg.prueba_tecnica_seti.repository.FondoRepository;
import com.btg.prueba_tecnica_seti.repository.TransaccionRepository;
import com.btg.prueba_tecnica_seti.service.impl.NotificationService;
import com.btg.prueba_tecnica_seti.utils.Constantes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class FondoControllerTest {

    @MockitoBean
    private JavaMailSender mailSender;

    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private FondoRepository fondoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private String baseUrl = "/api/fondos";


    @BeforeEach
    public void setUp() {
        mockMvc  = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void suscribirFondo_DeberiaLanzarExcepcionSiClienteNoExiste() throws Exception {

        clienteRepository.deleteAll();

        String expectedResponse = "{\"success\":false,\"message\":\"Cliente no encontrado\",\"data\":null}";

        String requestBody = "{\n" +
                "    \"clienteId\": \"1\",\n" +
                "    \"fondoId\": \"1\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl +"/suscribir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((MockMvcResultMatchers.content().string(containsString(expectedResponse))));
    }

    @Test
    void suscribirFondo_DeberiaLanzarExcepcionSiFondoNoExist() throws Exception {

        String expectedResponse = "{\"success\":false,\"message\":\"Fondo no encontrado\",\"data\":null}";

        String requestBody = "{\n" +
                "    \"clienteId\": \"1\",\n" +
                "    \"fondoId\": \"10\"\n" +
                "}";

        clienteRepository.save(Cliente
                .builder()
                .id("1")
                .email("hola@gmail.com")
                //.roles(Set.of(Roles.CLIENTE.name()))
                .nombre("Oscar")
                .telefono("1234")
                .saldoDisponible(Constantes.SALDO_INICIAL_CLIENTE)
                .fondosSuscritos(new ArrayList<>())
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl +"/suscribir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((MockMvcResultMatchers.content().string(containsString(expectedResponse))));
    }

    @Test
    void suscribirFondo_DeberiaLanzarExcepcionSiClienteYaEstaSuscritoAlFondo() throws Exception {

        String expectedResponse = "{\"success\":false,\"message\":\"El cliente ya está suscrito a este fondo\",\"data\":null}";

        String requestBody = "{\n" +
                "    \"clienteId\": \"1\",\n" +
                "    \"fondoId\": \"1\"\n" +
                "}";

        clienteRepository.save(Cliente
                .builder()
                .id("1")
                .email("hola@gmail.com")
                //.roles(Set.of(Roles.CLIENTE.name()))
                .nombre("Oscar")
                .telefono("1234")
                .saldoDisponible(Constantes.SALDO_INICIAL_CLIENTE)
                .fondosSuscritos(List.of("1"))
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl +"/suscribir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((MockMvcResultMatchers.content().string(containsString(expectedResponse))));
    }

    @Test
    void suscribirFondo_DeberiaLanzarExcepcionSiClienteNotieneSaldoDisponible() throws Exception {

        String expectedResponse = "{\"success\":false,\"message\":\"No tiene saldo disponible para vincularse al fondo FPV_BTG_PACTUAL_RECAUDADORA\",\"data\":null}";

        String requestBody = "{\n" +
                "    \"clienteId\": \"1\",\n" +
                "    \"fondoId\": \"1\"\n" +
                "}";

        clienteRepository.save(Cliente
                .builder()
                .id("1")
                .email("hola@gmail.com")
                //.roles(Set.of(Roles.CLIENTE.name()))
                .nombre("Oscar")
                .telefono("1234")
                .saldoDisponible(new BigDecimal("5000"))
                .fondosSuscritos(new ArrayList<>())
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl +"/suscribir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((MockMvcResultMatchers.content().string(containsString(expectedResponse))));
    }

    @Test
    void suscribirFondo_DeberiaLanzarTransaccionExitosa() throws Exception {

        String expectedResponse = "{\"success\":true,\"message\":\"Suscripción exitosa\",\"data\":{\"id\":\"68b5a1eb7b8871ef6d0d59d0\",\"clienteId\":\"1\",\"fondoId\":\"1\",\"nombreFondo\":\"FPV_BTG_PACTUAL_RECAUDADORA\",\"tipo\":\"APERTURA\",\"monto\":75000,\"fechaTransaccion\":\"2025-09-01T08:38:51.849164\"}}";

        String requestBody = "{\n" +
                "    \"clienteId\": \"1\",\n" +
                "    \"fondoId\": \"1\"\n" +
                "}";

        clienteRepository.save(Cliente
                .builder()
                .id("1")
                .email("hola@gmail.com")
                //.roles(Set.of(Roles.CLIENTE.name()))
                .nombre("Oscar")
                .telefono("1234")
                .saldoDisponible(Constantes.SALDO_INICIAL_CLIENTE)
                .fondosSuscritos(new ArrayList<>())
                .build());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/suscribir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Respuesta: " + responseContent);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(responseContent);

        // Validaciones raíz
        assertTrue(json.get("success").asBoolean());
        assertEquals("Suscripción exitosa", json.get("message").asText());
        assertEquals(3, json.size(), "El JSON raíz tiene campos extra");

        // Validaciones del objeto "data"
        JsonNode data = json.get("data");
        assertNotNull(data);

        // Validar formato ObjectId del campo "id"
        String id = data.get("id").asText();
        assertThat("El id no tiene formato de ObjectId", id, matchesPattern("^[a-f0-9]{24}$"));

        // Validaciones de campos esperados
        assertEquals("1", data.get("clienteId").asText());
        assertEquals("1", data.get("fondoId").asText());
        assertEquals("FPV_BTG_PACTUAL_RECAUDADORA", data.get("nombreFondo").asText());
        assertEquals("APERTURA", data.get("tipo").asText());
        assertEquals(75000, data.get("monto").asInt());

        // Validar formato de fecha
        String fechaTransaccion = data.get("fechaTransaccion").asText();
        assertThat("La fecha no tiene formato ISO 8601", fechaTransaccion,
                matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+$"));

        // Validar que no existan campos extra
        assertEquals(7, data.size(), "El objeto data tiene campos extra");
    }

    @Test
    void cancelarSuscripcion_DeberiaLanzarExcepcionSiClienteNoExiste() throws Exception {

        clienteRepository.deleteAll();

        String expectedResponse = "{\"success\":false,\"message\":\"Cliente no encontrado\",\"data\":null}";

        String requestBody = "{\n" +
                "    \"clienteId\": \"1\",\n" +
                "    \"fondoId\": \"1\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl +"/cancelar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((MockMvcResultMatchers.content().string(containsString(expectedResponse))));
    }

    @Test
    void cancelarSuscripcion_DeberiaLanzarExcepcionSiFondoNoExist() throws Exception {

        String expectedResponse = "{\"success\":false,\"message\":\"Fondo no encontrado\",\"data\":null}";

        String requestBody = "{\n" +
                "    \"clienteId\": \"1\",\n" +
                "    \"fondoId\": \"10\"\n" +
                "}";

        clienteRepository.save(Cliente
                .builder()
                .id("1")
                .email("hola@gmail.com")
                //.roles(Set.of(Roles.CLIENTE.name()))
                .nombre("Oscar")
                .telefono("1234")
                .saldoDisponible(Constantes.SALDO_INICIAL_CLIENTE)
                .fondosSuscritos(new ArrayList<>())
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl +"/cancelar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((MockMvcResultMatchers.content().string(containsString(expectedResponse))));
    }

    @Test
    void cancelarSuscripcion_DeberiaLanzarExcepcionSiClienteNoEstaSuscritoAlFondo() throws Exception {

        String expectedResponse = "{\"success\":false,\"message\":\"El cliente no está suscrito a este fondo\",\"data\":null}";

        String requestBody = "{\n" +
                "    \"clienteId\": \"1\",\n" +
                "    \"fondoId\": \"1\"\n" +
                "}";

        clienteRepository.save(Cliente
                .builder()
                .id("1")
                .email("hola@gmail.com")
                //.roles(Set.of(Roles.CLIENTE.name()))
                .nombre("Oscar")
                .telefono("1234")
                .saldoDisponible(Constantes.SALDO_INICIAL_CLIENTE)
                .fondosSuscritos(List.of("2"))
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl +"/cancelar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((MockMvcResultMatchers.content().string(containsString(expectedResponse))));
    }

    @Test
    void cancelarSuscripcion_DeberiaLanzarTransaccionExitosa() throws Exception {

        String requestBody = "{\n" +
                "    \"clienteId\": \"1\",\n" +
                "    \"fondoId\": \"1\"\n" +
                "}";

        clienteRepository.save(Cliente
                .builder()
                .id("1")
                .email("hola@gmail.com")
                //.roles(Set.of(Roles.CLIENTE.name()))
                .nombre("Oscar")
                .telefono("1234")
                .saldoDisponible(Constantes.SALDO_INICIAL_CLIENTE)
                .fondosSuscritos(List.of("1"))
                .build());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/cancelar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Respuesta: " + responseContent);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(responseContent);

        // Validaciones de la raíz del JSON
        assertTrue(json.get("success").asBoolean());
        assertEquals("Cancelación exitosa", json.get("message").asText());
        assertEquals(3, json.size(), "El JSON raíz tiene campos extra");

        // Validaciones del objeto "data"
        JsonNode data = json.get("data");
        assertNotNull(data);

        String id = data.get("id").asText();
        assertThat("El id no tiene formato de ObjectId", id, matchesPattern("^[a-f0-9]{24}$"));

        assertEquals("1", data.get("clienteId").asText());
        assertEquals("1", data.get("fondoId").asText());
        assertEquals("FPV_BTG_PACTUAL_RECAUDADORA", data.get("nombreFondo").asText());
        assertEquals("CANCELACION", data.get("tipo").asText());
        assertEquals(75000, data.get("monto").asInt());

        String fechaTransaccion = data.get("fechaTransaccion").asText();
        assertThat("La fecha no tiene formato ISO 8601", fechaTransaccion,
                matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+$"));

        assertEquals(7, data.size(), "El objeto data tiene campos extra");
    }
}
