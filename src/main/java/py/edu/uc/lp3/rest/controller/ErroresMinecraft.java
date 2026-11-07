package py.edu.uc.lp3.rest.controller;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import py.edu.uc.lp3.domain.minecraft.CreeperAgotadoException;
import py.edu.uc.lp3.domain.minecraft.FueraDeAlcanceException;
import py.edu.uc.lp3.domain.minecraft.ReglaMinecraftException;

@RestControllerAdvice(assignableTypes = {HostilController.class, EntidadController.class})
public class ErroresMinecraft {

    @ExceptionHandler(ReglaMinecraftException.class)
    ResponseEntity<ErrorApi> reglaGeneral(ReglaMinecraftException error) {
        return responder(HttpStatus.BAD_REQUEST, "REGLA_MINECRAFT", error.getMessage());
    }

    @ExceptionHandler(FueraDeAlcanceException.class)
    ResponseEntity<ErrorApi> fueraDeAlcance(FueraDeAlcanceException error) {
        return responder(HttpStatus.UNPROCESSABLE_ENTITY, "FUERA_DE_ALCANCE", error.getMessage());
    }

    @ExceptionHandler(CreeperAgotadoException.class)
    ResponseEntity<ErrorApi> creeperAgotado(CreeperAgotadoException error) {
        return responder(HttpStatus.CONFLICT, "CREEPER_AGOTADO", error.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ErrorApi> inexistente(ResponseStatusException error) {
        return ResponseEntity.status(error.getStatusCode())
                .body(new ErrorApi("HOSTIL_NO_ENCONTRADO", error.getReason()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ErrorApi> pedidoInvalido() {
        return responder(HttpStatus.BAD_REQUEST, "PEDIDO_INVALIDO", "Revisá los campos y tipos del pedido");
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    ResponseEntity<ErrorApi> cambioConcurrente() {
        return responder(HttpStatus.CONFLICT, "CAMBIO_CONCURRENTE", "El hostil cambió durante la operación");
    }

    private static ResponseEntity<ErrorApi> responder(HttpStatus estado, String codigo, String mensaje) {
        return ResponseEntity.status(estado).body(new ErrorApi(codigo, mensaje));
    }

    public record ErrorApi(String codigo, String mensaje) {
    }
}
