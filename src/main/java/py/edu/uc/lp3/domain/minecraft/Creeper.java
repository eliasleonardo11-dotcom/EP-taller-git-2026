package py.edu.uc.lp3.domain.minecraft;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CREEPER")
public class Creeper extends Hostil {
    private static final int DANIO_MAXIMO = 12;
    private static final int REDUCCION_POR_BLOQUE = 3;

    protected Creeper() {
    }

    public Creeper(String nombre) {
        this(nombre, 20, 3);
    }

    public Creeper(String nombre, int vida, int rangoAggro) {
        super(nombre, vida, rangoAggro);
    }

    public int explotarContra(Zombie objetivo, int distancia) {
        validarDetonacion(objetivo, distancia);
        int danioAplicado = aplicarOndaExplosiva(objetivo, distancia);
        recibirDanio(getVida());
        return danioAplicado;
    }

    private void validarDetonacion(Zombie objetivo, int distancia) {
        if (!estaViva()) {
            throw new CreeperAgotadoException();
        }
        if (distancia < 0) {
            throw new ReglaMinecraftException("La distancia no puede ser negativa");
        }
        if (distancia > getRangoAggro()) {
            throw new FueraDeAlcanceException(distancia, getRangoAggro());
        }
        if (!objetivo.estaViva()) {
            throw new ReglaMinecraftException("El Zombie debe estar vivo");
        }
    }

    private int aplicarOndaExplosiva(Zombie objetivo, int distancia) {
        int potencia = (int) Math.max(1, DANIO_MAXIMO - (long) REDUCCION_POR_BLOQUE * distancia);
        int danioAplicado = Math.min(potencia, objetivo.getVida());
        objetivo.recibirDanio(danioAplicado);
        return danioAplicado;
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
