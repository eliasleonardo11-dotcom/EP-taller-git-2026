package py.edu.uc.lp3.rest.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, String> index() {
        return Map.of(
                "alumno", "Elias Pont",
                "dominio", "Minecraft",
                "estado", "API funcionando"
        );
    }
}