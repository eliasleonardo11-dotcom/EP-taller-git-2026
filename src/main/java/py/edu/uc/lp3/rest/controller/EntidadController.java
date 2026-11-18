package py.edu.uc.lp3.rest.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.minecraft.Aldeano;
import py.edu.uc.lp3.domain.minecraft.Entidad;
import py.edu.uc.lp3.domain.minecraft.Zombie;

@RestController
public class EntidadController {

    private final py.edu.uc.lp3.service.EntidadService servicio;

    public EntidadController(py.edu.uc.lp3.service.EntidadService servicio) { this.servicio = servicio; }

    @GetMapping("/api/entidades/zombie")
    public Map<String, Object> crearZombie(
            @RequestParam String nombre,
            @RequestParam int vida,
            @RequestParam(defaultValue = "10") int rangoAggro) {

        Entidad entidad = servicio.zombie(nombre, vida, rangoAggro);

        return respuesta(entidad);
    }

    @GetMapping("/api/entidades/aldeano")
    public Map<String, Object> crearAldeano(
            @RequestParam String nombre,
            @RequestParam int vida) {

        Entidad entidad = servicio.aldeano(nombre, vida);

        return respuesta(entidad);
    }


    @GetMapping("/api/entidades/zombie/simple")
    public Map<String, Object> crearZombieSimple(
            @RequestParam String nombre) {

        Entidad entidad = servicio.zombie(nombre, 20, 10);
        return respuesta(entidad);
    }

    @GetMapping("/api/entidades/aldeano/simple")
    public Map<String, Object> crearAldeanoSimple(
            @RequestParam String nombre) {

        Entidad entidad = servicio.aldeano(nombre, 20);
        return respuesta(entidad);
    }

    private Map<String, Object> respuesta(Entidad entidad) {
        return Map.of(
                "tipo", entidad.getClass().getSimpleName(),
                "nombre", entidad.getNombre(),
                "vida", entidad.getVida(),
                "viva", entidad.estaViva(),
                "movimiento", entidad.moverse(),
                "comportamiento", entidad.comportamiento()
        );
    }
}