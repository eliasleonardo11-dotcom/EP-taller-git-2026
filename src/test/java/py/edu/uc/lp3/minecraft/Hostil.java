package py.edu.uc.lp3.minecraft;

public abstract class Hostil extends Entidad {

    private int rangoAggro;

    public Hostil(String nombre, int vida, int rangoAggro) {
        super(nombre, vida);

        if (rangoAggro < 0) {
            throw new IllegalArgumentException("El rango de agresión no puede ser negativo");
        }

        this.rangoAggro = rangoAggro;
    }

    public int getRangoAggro() {
        return rangoAggro;
    }

    public abstract String atacar();
}
