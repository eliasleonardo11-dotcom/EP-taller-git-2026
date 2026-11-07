package py.edu.uc.lp3.domain.minecraft;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Entidad {
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private int vida;

    protected Entidad() {
    }

    public Entidad(String nombre, int vida) {
        renombrar(nombre);
        if (vida < 0) {
            throw new ReglaMinecraftException("La vida no puede ser negativa");
        }
        this.vida = vida;
    }

    protected final void renombrar(String nombre) {
        if (nombre == null || nombre.isBlank() || nombre.length() > 100) {
            throw new ReglaMinecraftException("El nombre debe tener entre 1 y 100 caracteres y no estar en blanco");
        }
        this.nombre = nombre;
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
            throw new ReglaMinecraftException("El daño debe ser positivo");
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
