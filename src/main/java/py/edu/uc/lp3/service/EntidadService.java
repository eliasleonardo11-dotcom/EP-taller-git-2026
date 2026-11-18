package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import py.edu.uc.lp3.domain.minecraft.*;

@Service
public class EntidadService {
    public Zombie zombie(String nombre, int vida, int rango) {
        HostilService.validarDatos(nombre, vida, rango, true);
        return new Zombie(nombre, vida, rango);
    }
    public Aldeano aldeano(String nombre, int vida) {
        HostilService.validarDatos(nombre, vida, 0, true);
        return new Aldeano(nombre, vida);
    }
}
