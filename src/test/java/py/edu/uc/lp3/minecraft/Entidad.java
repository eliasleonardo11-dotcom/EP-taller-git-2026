package py.edu.uc.lp3.minecraft;

public abstract class Entidad {

    private String nombre;
    private int vida;

    public Entidad(String nombre, int vida) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        if (vida < 0) {
            throw new IllegalArgumentException("La vida no puede ser negativa");
        }

        this.nombre = nombre;
        this.vida = vida;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    public boolean estaViva() {
        return vida > 0;
    }

    public void recibirDanio(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("El daño debe ser positivo");
        }

        vida = Math.max(0, vida - cantidad);
    }

    public String desaparecer() {
        return estaViva()
                ? nombre + " todavía está vivo"
                : nombre + " desapareció";
    }

    public abstract String moverse();

    public abstract String comportamiento();
}
