package py.edu.uc.lp3.service;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import py.edu.uc.lp3.domain.minecraft.Creeper;
import py.edu.uc.lp3.domain.minecraft.Descriptible;
import py.edu.uc.lp3.domain.minecraft.Hostil;
import py.edu.uc.lp3.service.exception.ReglaMinecraftException;
import py.edu.uc.lp3.domain.minecraft.Zombie;
import py.edu.uc.lp3.repository.HostilRepository;

@org.springframework.stereotype.Service
public class HostilService {
    private final HostilRepository hostiles;

    public HostilService(HostilRepository hostiles) {
        this.hostiles = hostiles;
    }

    @Transactional
    public EstadoHostil crear(AltaHostil alta) {
        return EstadoHostil.de(hostiles.save(alta.construir()));
    }

    public List<EstadoHostil> listar() {
        return hostiles.findAllByOrderByIdAsc().stream().map(EstadoHostil::de).toList();
    }

    public EstadoHostil obtener(long id) {
        return EstadoHostil.de(buscar(id));
    }

    @Transactional
    public EstadoHostil actualizar(long id, CambioHostil cambio) {
        Hostil hostil = buscar(id);
        if (cambio.nombre() == null && cambio.rangoAggro() == null) {
            throw new ReglaMinecraftException("Enviá un nombre o un rangoAggro para actualizar");
        }
        String nombre = cambio.nombre() == null ? hostil.getNombre() : cambio.nombre();
        int rangoAggro = cambio.rangoAggro() == null ? hostil.getRangoAggro() : cambio.rangoAggro();
        validarDatos(nombre, hostil.getVida(), rangoAggro, true);
        hostil.configurar(nombre, rangoAggro);
        return EstadoHostil.de(hostil);
    }

    @Transactional
    public void borrar(long id) {
        hostiles.delete(buscar(id));
    }

    public Map<String, String> describir(long id) {
        Descriptible hostil = buscar(id);
        return Map.of("comportamiento", hostil.comportamiento());
    }

    @Transactional
    public ResultadoExplosion explotar(long id, PedidoExplosion pedido) {
        if (pedido.zombieId() == null || pedido.distancia() == null) {
            throw new ReglaMinecraftException("Indicá zombieId y distancia");
        }
        if (!(buscar(id) instanceof Creeper creeper)) {
            throw new ReglaMinecraftException("El hostil de la URL debe ser un Creeper");
        }
        if (!(buscar(pedido.zombieId()) instanceof Zombie zombie)) {
            throw new ReglaMinecraftException("El objetivo debe ser un Zombie");
        }

        int danio = aplicarExplosion(creeper, zombie, pedido.distancia());
        return new ResultadoExplosion(danio, EstadoHostil.de(creeper), EstadoHostil.de(zombie));
    }

    public static void validarDatos(String nombre, int vida, int rango, boolean exigirVivo) {
        if (nombre == null || nombre.isBlank() || nombre.length() > 100)
            throw new ReglaMinecraftException("El nombre debe tener entre 1 y 100 caracteres y no estar en blanco");
        if (vida < (exigirVivo ? 1 : 0) || vida > 20)
            throw new ReglaMinecraftException("La vida al crear o actualizar debe estar entre 1 y 20");
        if (rango < 0) throw new ReglaMinecraftException("El rango de agresión no puede ser negativo");
    }

    public static int aplicarExplosion(Creeper creeper, Zombie zombie, int distancia) {
        if (!creeper.estaViva()) throw new py.edu.uc.lp3.service.exception.CreeperAgotadoException();
        if (distancia < 0) throw new ReglaMinecraftException("La distancia no puede ser negativa");
        if (distancia > creeper.getRangoAggro())
            throw new py.edu.uc.lp3.service.exception.FueraDeAlcanceException(distancia, creeper.getRangoAggro());
        if (!zombie.estaViva()) throw new ReglaMinecraftException("El Zombie debe estar vivo");
        int potencia = (int) Math.max(1, 12 - (long) 3 * distancia);
        int danio = Math.min(potencia, zombie.getVida());
        zombie.actualizarVida(zombie.getVida() - danio);
        creeper.actualizarVida(0);
        return danio;
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
            validarDatos(nombre, vida, rangoAggro, true);
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
