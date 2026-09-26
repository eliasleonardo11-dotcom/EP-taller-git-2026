package py.edu.uc.lp3.minecraft;

public class Jugador extends Entidad {

    private int experiencia;

    public Jugador(String nombre, int vida, int experiencia) {
        super(nombre, vida);

        if (experiencia < 0) {
            throw new IllegalArgumentException("La experiencia no puede ser negativa");
        }

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
