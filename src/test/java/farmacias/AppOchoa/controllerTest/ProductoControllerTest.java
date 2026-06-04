package farmacias.AppOchoa.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import farmacias.AppOchoa.controller.ProductoController;
import farmacias.AppOchoa.dto.producto.ProductoCreateDTO;
import farmacias.AppOchoa.dto.producto.ProductoResponseDTO;
import farmacias.AppOchoa.services.ProductoService;
import farmacias.AppOchoa.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("user", "fake-token");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(jwtUtil.extractFarmaciaId("fake-token")).thenReturn(1L);
    }

    @Test
    @DisplayName("GET /api/v1/productos/{id} - Debería retornar 200 y el JSON del producto")
    void obtenerPorId_Exito() throws Exception {
        Long idProducto = 1L;
        ProductoResponseDTO responseDTO = new ProductoResponseDTO();
        responseDTO.setNombre("Paracetamol Test");
        responseDTO.setPrecioVenta(BigDecimal.valueOf(15.50));

        when(productoService.obtenerPorId(anyLong(), anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/productos/{id}", idProducto)
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Paracetamol Test"))
                .andExpect(jsonPath("$.precioVenta").value(15.50));
    }

    @Test
    @DisplayName("POST /api/v1/productos - Debería retornar 201 Created")
    void agregarProducto_Exito() throws Exception {
        ProductoCreateDTO createDTO = new ProductoCreateDTO();
        createDTO.setNombre("Nuevo Producto");
        createDTO.setCategoriaId(1L);
        createDTO.setPresentacionId(1L);
        createDTO.setPrecioCompra(BigDecimal.valueOf(10));
        createDTO.setPrecioVenta(BigDecimal.valueOf(20));
        createDTO.setCodigoBarras("ABC-123");
        createDTO.setIva(BigDecimal.ZERO);

        ProductoResponseDTO responseDTO = new ProductoResponseDTO();
        responseDTO.setNombre("Nuevo Producto");

        when(productoService.agregarProducto(anyLong(), any(ProductoCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/productos")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Nuevo Producto"));
    }

    @Test
    @DisplayName("DELETE /api/v1/productos/{id} - Debería retornar 204 No Content")
    void eliminarProducto_Exito() throws Exception {
        Long idProducto = 1L;

        mockMvc.perform(delete("/api/v1/productos/{id}", idProducto)
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNoContent());
    }
}