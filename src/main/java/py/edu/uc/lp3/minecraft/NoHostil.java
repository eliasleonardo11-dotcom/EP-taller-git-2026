package py.edu.uc.lp3.minecraft;

public abstract class NoHostil extends Entidad {

    public NoHostil(String nombre, int vida) {
        super(nombre, vida);
    }

    public abstract String interactuar();
}
