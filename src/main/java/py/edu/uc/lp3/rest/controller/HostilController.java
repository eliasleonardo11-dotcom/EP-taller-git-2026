package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import py.edu.uc.lp3.domain.minecraft.Creeper;
import py.edu.uc.lp3.domain.minecraft.Descriptible;
import py.edu.uc.lp3.domain.minecraft.Hostil;
import py.edu.uc.lp3.domain.minecraft.ReglaMinecraftException;
import py.edu.uc.lp3.domain.minecraft.Zombie;
import py.edu.uc.lp3.repository.HostilRepository;

@RestController
@RequestMapping("/api/hostiles")
public class HostilController {
    private final HostilRepository hostiles;

    public HostilController(HostilRepository hostiles) {
        this.hostiles = hostiles;
    }

    @PostMapping
    public ResponseEntity<EstadoHostil> crear(@RequestBody AltaHostil alta) {
        EstadoHostil estado = EstadoHostil.de(hostiles.save(alta.construir()));
        return ResponseEntity.created(URI.create("/api/hostiles/" + estado.id())).body(estado);
    }

    @GetMapping
    public List<EstadoHostil> listar() {
        return hostiles.findAllByOrderByIdAsc().stream().map(EstadoHostil::de).toList();
    }

    @GetMapping("/{id}")
    public EstadoHostil obtener(@PathVariable long id) {
        return EstadoHostil.de(buscar(id));
    }

    @PatchMapping("/{id}")
    @Transactional
    public EstadoHostil actualizar(@PathVariable long id, @RequestBody CambioHostil cambio) {
        Hostil hostil = buscar(id);
        if (cambio.nombre() == null && cambio.rangoAggro() == null) {
            throw new ReglaMinecraftException("Enviá un nombre o un rangoAggro para actualizar");
        }
        String nombre = cambio.nombre() == null ? hostil.getNombre() : cambio.nombre();
        int rangoAggro = cambio.rangoAggro() == null ? hostil.getRangoAggro() : cambio.rangoAggro();
        hostil.configurar(nombre, rangoAggro);
        return EstadoHostil.de(hostil);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> borrar(@PathVariable long id) {
        hostiles.delete(buscar(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comportamiento")
    public Map<String, String> describir(@PathVariable long id) {
        Descriptible hostil = buscar(id);
        return Map.of("comportamiento", hostil.comportamiento());
    }

    @PostMapping("/{id}/explosiones")
    @Transactional
    public ResultadoExplosion explotar(@PathVariable long id, @RequestBody PedidoExplosion pedido) {
        if (pedido.zombieId() == null || pedido.distancia() == null) {
            throw new ReglaMinecraftException("Indicá zombieId y distancia");
        }
        if (!(buscar(id) instanceof Creeper creeper)) {
            throw new ReglaMinecraftException("El hostil de la URL debe ser un Creeper");
        }
        if (!(buscar(pedido.zombieId()) instanceof Zombie zombie)) {
            throw new ReglaMinecraftException("El objetivo debe ser un Zombie");
        }

        int danio = creeper.explotarContra(zombie, pedido.distancia());
        return new ResultadoExplosion(danio, EstadoHostil.de(creeper), EstadoHostil.de(zombie));
    }

    private Hostil buscar(long id) {
        return hostiles.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el hostil " + id));
    }

    public enum TipoHostil {
        CREEPER, ZOMBIE
    }

    public record AltaHostil(TipoHostil tipo, String nombre, Integer vida, Integer rangoAggro) {
        Hostil construir() {
            if (tipo == null || vida == null || rangoAggro == null) {
                throw new ReglaMinecraftException("Indicá tipo, nombre, vida y rangoAggro");
            }
            return switch (tipo) {
                case CREEPER -> new Creeper(nombre, vida, rangoAggro);
                case ZOMBIE -> new Zombie(nombre, vida, rangoAggro);
            };
        }
    }

    public record CambioHostil(String nombre, Integer rangoAggro) {
    }

    public record PedidoExplosion(Long zombieId, Integer distancia) {
    }

    public record ResultadoExplosion(int danio, EstadoHostil creeper, EstadoHostil zombie) {
    }

    public record EstadoHostil(long id, TipoHostil tipo, String nombre, int vida, int rangoAggro,
                               boolean vivo) {
        static EstadoHostil de(Hostil hostil) {
            TipoHostil tipo = hostil instanceof Creeper ? TipoHostil.CREEPER : TipoHostil.ZOMBIE;
            return new EstadoHostil(hostil.getId(), tipo, hostil.getNombre(), hostil.getVida(),
                    hostil.getRangoAggro(), hostil.estaViva());
        }
    }
}
