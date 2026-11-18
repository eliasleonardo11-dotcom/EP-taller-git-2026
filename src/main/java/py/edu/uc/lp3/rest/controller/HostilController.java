package py.edu.uc.lp3.rest.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.service.HostilService;
import py.edu.uc.lp3.service.HostilService.*;

@RestController
@RequestMapping("/api/hostiles")
public class HostilController {
    private final HostilService servicio;
    public HostilController(HostilService servicio) { this.servicio = servicio; }
    @PostMapping
    public ResponseEntity<EstadoHostil> crear(@RequestBody AltaHostil alta) { var estado = servicio.crear(alta); return ResponseEntity.created(java.net.URI.create("/api/hostiles/" + estado.id())).body(estado); }
    @GetMapping
    public List<EstadoHostil> listar() { return servicio.listar(); }
    @GetMapping("/{id}")
    public EstadoHostil obtener(@PathVariable long id) { return servicio.obtener(id); }
    @PatchMapping("/{id}")
    public EstadoHostil actualizar(@PathVariable long id, @RequestBody CambioHostil cambio) { return servicio.actualizar(id, cambio); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable long id) { servicio.borrar(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/{id}/comportamiento")
    public Map<String,String> describir(@PathVariable long id) { return servicio.describir(id); }
    @PostMapping("/{id}/explosiones")
    public ResultadoExplosion explotar(@PathVariable long id, @RequestBody PedidoExplosion pedido) { return servicio.explotar(id, pedido); }
}
