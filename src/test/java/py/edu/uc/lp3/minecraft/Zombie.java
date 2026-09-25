package py.edu.uc.lp3.minecraft;

public class Zombie extends Hostil {

    public Zombie(String nombre, int vida, int rangoAggro) {
        super(nombre, vida, rangoAggro);
    }

    @Override
    public String moverse() {
        return getNombre() + " avanza lentamente hacia el jugador";
    }

    @Override
    public String atacar() {
        return getNombre() + " golpea al jugador";
    }

    @Override
    public String comportamiento() {
        return "El zombie persigue y ataca al jugador";
    }
}
