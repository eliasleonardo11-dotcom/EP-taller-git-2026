package py.edu.uc.lp3.minecraft;

public class Aldeano extends NoHostil {

    public Aldeano(String nombre, int vida) {
        super(nombre, vida);
    }

    @Override
    public String moverse() {
        return getNombre() + " camina por la aldea";
    }

    @Override
    public String interactuar() {
        return getNombre() + " ofrece intercambios";
    }

    @Override
    public String comportamiento() {
        return "El aldeano comercia con el jugador";
    }
}
