package py.edu.uc.lp3.domain.minecraft;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "hostiles")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
@Check(constraints = "vida >= 0 and rango_aggro >= 0 and char_length(trim(nombre)) > 0")
public abstract class Hostil extends Entidad implements Descriptible {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private long version;

    @Column(name = "rango_aggro", nullable = false)
    private int rangoAggro;

    protected Hostil() {
    }

    public Hostil(String nombre, int vida, int rangoAggro) {
        super(nombre, vida);
        validarRango(rangoAggro);
        this.rangoAggro = rangoAggro;
    }

    public void configurar(String nombre, int rangoAggro) {
        validarRango(rangoAggro);
        renombrar(nombre);
        this.rangoAggro = rangoAggro;
    }

    private static void validarRango(int rangoAggro) {
        if (rangoAggro < 0) {
            throw new ReglaMinecraftException("El rango de agresión no puede ser negativo");
        }
    }

    public Long getId() {
        return id;
    }

    public int getRangoAggro() {
        return rangoAggro;
    }

    public abstract String atacar();
}
