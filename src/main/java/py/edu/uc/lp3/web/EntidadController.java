package py.edu.uc.lp3.web;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.minecraft.Aldeano;
import py.edu.uc.lp3.minecraft.Entidad;
import py.edu.uc.lp3.minecraft.Zombie;

@RestController
public class EntidadController {

    @GetMapping("/api/entidades/zombie")
    public Map<String, Object> crearZombie(
            @RequestParam String nombre,
            @RequestParam int vida,
            @RequestParam(defaultValue = "10") int rangoAggro) {

        Entidad entidad = new Zombie(nombre, vida, rangoAggro);

        return respuesta(entidad);
    }

    @GetMapping("/api/entidades/aldeano")
    public Map<String, Object> crearAldeano(
            @RequestParam String nombre,
            @RequestParam int vida) {

        Entidad entidad = new Aldeano(nombre, vida);

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