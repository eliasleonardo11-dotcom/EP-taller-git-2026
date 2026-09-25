package py.edu.uc.lp3.minecraft;

public class Creeper extends Hostil {

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
