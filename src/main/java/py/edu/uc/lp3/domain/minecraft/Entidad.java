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
        this.vida = vida;
    }

    protected final void renombrar(String nombre) {
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

    public void actualizarVida(int vida) {
        this.vida = vida;
    }

    public String desaparecer() {
        return estaViva()
                ? nombre + " todavía está vivo"
                : nombre + " desapareció";
    }

    public abstract String moverse();

    public abstract String comportamiento();
}
