package py.edu.uc.lp3.domain.minecraft;

public class Jugador extends Entidad {

    private int experiencia;

    public Jugador(String nombre, int vida, int experiencia) {
        super(nombre, vida);


        this.experiencia = experiencia;
    }

    public int getExperiencia() {
        return experiencia;
    }

    @Override
    public String moverse() {
        return getNombre() + " se mueve libremente por el mundo";
    }

    @Override
    public String comportamiento() {
        return "El jugador explora, construye y combate";
    }
}
