package farmacias.AppOchoa.configTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import farmacias.AppOchoa.util.JwtUtil;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * B1: verifica que spring.data.web.pageable.max-page-size recorta el tamaño de
 * página solicitado. Usa un controlador de prueba que devuelve el pageSize
 * resuelto, aislando el binding de la propiedad sin depender de un servicio real.
 */
@WebMvcTest(PageableMaxSizeTest.PageEchoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(PageableMaxSizeTest.PageEchoController.class)
@DisplayName("Tope de tamaño de página (B1)")
class PageableMaxSizeTest {

    @Autowired
    private MockMvc mockMvc;

    // La SecurityConfig de la app entra en el slice y su filtro JWT exige este bean
    @MockBean
    private JwtUtil jwtUtil;

    @RestController
    static class PageEchoController {
        @GetMapping("/test/pageable-echo")
        String echo(Pageable pageable) {
            return String.valueOf(pageable.getPageSize());
        }
    }

    @Test
    @DisplayName("Un size excesivo se recorta al máximo configurado (100)")
    void sizeExcesivoSeRecorta() throws Exception {
        mockMvc.perform(get("/test/pageable-echo").param("size", "99999"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    @DisplayName("Un size dentro del límite se respeta")
    void sizeDentroDelLimiteSeRespeta() throws Exception {
        mockMvc.perform(get("/test/pageable-echo").param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(content().string("50"));
    }
}
