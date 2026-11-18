package py.edu.uc.lp3.service.exception;

public class CreeperAgotadoException extends ReglaMinecraftException {
    public CreeperAgotadoException() {
        super("El Creeper ya no tiene vida para explotar");
    }
}
