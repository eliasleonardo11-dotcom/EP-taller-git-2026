package py.edu.uc.lp3;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import py.edu.uc.lp3.domain.minecraft.Aldeano;
import py.edu.uc.lp3.domain.minecraft.Creeper;
import py.edu.uc.lp3.domain.minecraft.Descriptible;
import py.edu.uc.lp3.domain.minecraft.Hostil;
import py.edu.uc.lp3.domain.minecraft.Zombie;
import py.edu.uc.lp3.repository.HostilRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:poo08;LOCK_TIMEOUT=15000",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class EpTallerGit2026ApplicationTests {
    private static final String HOSTILES = "/api/hostiles";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    @Autowired
    private HostilRepository hostiles;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void limpiarHostilesDePrueba() {
        hostiles.deleteAll();
    }

    @Test
    void completaCrudYGuardaLosDosResultadosDeLaExplosion() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        long creeper = crear("CREEPER", "Chispa", 3);
        long zombie = crear("ZOMBIE", "Caminante", 10);
        mvc.perform(get(HOSTILES))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("CREEPER"))
                .andExpect(jsonPath("$[1].tipo").value("ZOMBIE"));

        mvc.perform(patch(HOSTILES + "/{id}", zombie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Caminante nocturno\"}"))
                .andExpect(status().isOk());
        mvc.perform(get(HOSTILES + "/{id}", zombie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Caminante nocturno"))
                .andExpect(jsonPath("$.rangoAggro").value(10));
        mvc.perform(get(HOSTILES + "/{id}/comportamiento", zombie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comportamiento").value("El zombie persigue y ataca al jugador"));

        explotar(creeper, zombie, 4)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("FUERA_DE_ALCANCE"));
        assertThat(vidaEnH2(creeper)).isEqualTo(20);
        assertThat(vidaEnH2(zombie)).isEqualTo(20);

        explotar(creeper, zombie, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.danio").value(9))
                .andExpect(jsonPath("$.creeper.vida").value(0))
                .andExpect(jsonPath("$.zombie.vida").value(11));
        mvc.perform(get(HOSTILES + "/{id}", creeper))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vida").value(0));
        mvc.perform(get(HOSTILES + "/{id}", zombie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vida").value(11));
        assertThat(vidaEnH2(creeper)).isZero();
        assertThat(vidaEnH2(zombie)).isEqualTo(11);

        explotar(creeper, zombie, 1)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("CREEPER_AGOTADO"));
        assertThat(vidaEnH2(zombie)).isEqualTo(11);
        mvc.perform(delete(HOSTILES + "/{id}", zombie)).andExpect(status().isNoContent());
        mvc.perform(get(HOSTILES + "/{id}", zombie))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("HOSTIL_NO_ENCONTRADO"));
    }

    @Test
    void rechazaEstadosInvalidosEnJsonDominioYH2() throws Exception {
        long zombie = crear("ZOMBIE", "Caminante", 10);
        mvc.perform(post(HOSTILES).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"ZOMBIE","nombre":"Inválido","vida":-1,"rangoAggro":10}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REGLA_MINECRAFT"));
        mvc.perform(post(HOSTILES).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"ESQUELETO","nombre":"Inválido","vida":20,"rangoAggro":10}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("PEDIDO_INVALIDO"));
        mvc.perform(patch(HOSTILES + "/{id}", zombie).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Cambio inválido","rangoAggro":-1}
                                """))
                .andExpect(status().isBadRequest());
        assertThatThrownBy(() -> jdbc.update("UPDATE hostiles SET vida = -1 WHERE id = ?", zombie))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThat(hostiles.findAll()).singleElement().satisfies(hostil -> {
            assertThat(hostil.getNombre()).isEqualTo("Caminante");
            assertThat(hostil.getRangoAggro()).isEqualTo(10);
            assertThat(hostil.getVida()).isEqualTo(20);
        });
    }

    @Test
    void aplicaLasReglasDeDominioEnElLimiteDelAlcanceYDeLaVida() {
        Hostil creeper = new Creeper("Chispa");
        Hostil zombie = new Zombie("Caminante");
        Descriptible aldeano = new Aldeano("Comerciante");
        assertThat(creeper.getRangoAggro()).isEqualTo(3);
        assertThat(zombie.getRangoAggro()).isEqualTo(10);
        assertThat(creeper.atacar()).isNotEqualTo(zombie.atacar());
        assertThat(aldeano.comportamiento()).contains("comercia");

        Creeper detonador = new Creeper("Chispa", 1, Integer.MAX_VALUE);
        Zombie objetivo = new Zombie("Caminante", 2, 0);
        assertThat(detonador.explotarContra(objetivo, Integer.MAX_VALUE)).isEqualTo(1);
        assertThat(detonador.getVida()).isZero();
        assertThat(objetivo.getVida()).isEqualTo(1);
        assertThat(new Creeper("Chispa", 1, 0).explotarContra(objetivo, 0)).isEqualTo(1);
        assertThat(objetivo.getVida()).isZero();
    }

    @Test
    void revierteTodaLaExplosionAnteUnCambioConcurrente() throws Exception {
        long creeper = crear("CREEPER", "Chispa", 3);
        long zombie = crear("ZOMBIE", "Caminante", 10);

        try (var executor = Executors.newSingleThreadExecutor();
             var conexion = jdbc.getDataSource().getConnection()) {
            conexion.setAutoCommit(false);
            try (var consulta = conexion.prepareStatement(
                    "UPDATE hostiles SET nombre = ?, version = version + 1 WHERE id = ?")) {
                consulta.setString(1, "Nombre concurrente");
                consulta.setLong(2, zombie);
                assertThat(consulta.executeUpdate()).isEqualTo(1);
            }

            // La explosión lee la versión anterior y espera al escribir la fila bloqueada.
            var respuesta = executor.submit(() -> explotar(creeper, zombie, 1));
            esperarEscrituraBloqueada();
            conexion.commit();

            respuesta.get(10, TimeUnit.SECONDS)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.codigo").value("CAMBIO_CONCURRENTE"));
        }

        assertThat(vidaEnH2(creeper)).isEqualTo(20);
        assertThat(vidaEnH2(zombie)).isEqualTo(20);
        mvc.perform(get(HOSTILES + "/{id}", zombie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nombre concurrente"));
    }

    private void esperarEscrituraBloqueada() throws InterruptedException {
        long limite = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        do {
            int bloqueadas = jdbc.queryForObject("""
                    SELECT COUNT(*) FROM INFORMATION_SCHEMA.SESSIONS WHERE BLOCKER_ID IS NOT NULL
                    """, Integer.class);
            if (bloqueadas > 0) {
                return;
            }
            Thread.sleep(25);
        } while (System.nanoTime() < limite);
        throw new AssertionError("La explosión no llegó a esperar por la fila bloqueada");
    }

    private long crear(String tipo, String nombre, int rango) throws Exception {
        String contenido = mvc.perform(post(HOSTILES).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"%s","nombre":"%s","vida":20,"rangoAggro":%d}
                                """.formatted(tipo, nombre, rango)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.tipo").value(tipo))
                .andReturn().getResponse().getContentAsString();
        return json.readTree(contenido).get("id").asLong();
    }

    private ResultActions explotar(long creeper, long zombie, int distancia) throws Exception {
        return mvc.perform(post(HOSTILES + "/{id}/explosiones", creeper)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"zombieId":%d,"distancia":%d}
                        """.formatted(zombie, distancia)));
    }

    private int vidaEnH2(long id) {
        return jdbc.queryForObject("SELECT vida FROM hostiles WHERE id = ?", Integer.class, id);
    }
}
