package py.edu.uc.lp3.domain.minecraft;

public class FueraDeAlcanceException extends ReglaMinecraftException {
    public FueraDeAlcanceException(int distancia, int alcance) {
        super("El Zombie está a " + distancia + " bloques y el alcance es de " + alcance);
    }
}
