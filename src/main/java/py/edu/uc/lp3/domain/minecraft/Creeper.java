package py.edu.uc.lp3.domain.minecraft;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CREEPER")
public class Creeper extends Hostil {

    protected Creeper() {
    }

    public Creeper(String nombre) {
        this(nombre, 20, 3);
    }

    public Creeper(String nombre, int vida, int rangoAggro) {
        super(nombre, vida, rangoAggro);
    }

    @Override
    public String moverse() {
        return getNombre() + " se acerca silenciosamente";
    }

    @Override
    public String atacar() {
        return getNombre() + " explota cerca del jugador";
    }

    @Override
    public String comportamiento() {
        return "El creeper se acerca y explota";
    }
}
