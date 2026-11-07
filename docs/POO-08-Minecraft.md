# POO-08 · Especificaciones de Minecraft

**Alumno** Elías Pont  
**Asignatura** Lenguaje de Programación 3

[Commit de la solución](https://github.com/eliasleonardo11-dotcom/EP-taller-git-2026/commit/ad8d65d13bdc430a3f65c22a3e3cff49d5d37801)

## Objetivo

Gestionar los hostiles de una partida de Minecraft mediante una API HTTP y guardar su estado en H2. La familia persistida tiene una clase base `Hostil` y dos subtipos, `Creeper` y `Zombie`. La simulación cambia la vida de ambos como una sola operación.

## URLs

La aplicación se ejecuta en `http://localhost:8080`.

| Método y ruta | Entrada | Resultado |
| --- | --- | --- |
| `GET /` | — | 200 y estado del servicio. |
| `POST /api/hostiles` | `{"tipo":"CREEPER","nombre":"Chispa","vida":20,"rangoAggro":3}` | 201, `Location` y estado con ID asignado. Para un Zombie usá `"tipo":"ZOMBIE"`. |
| `GET /api/hostiles` | — | 200 y lista ordenada por ID. |
| `GET /api/hostiles/{id}` | ID del hostil | 200 y estado persistido, o 404. |
| `PATCH /api/hostiles/{id}` | `{"nombre":"Caminante nocturno"}`, `rangoAggro` o ambos | 200 y estado actualizado, 400 si el cambio es inválido o 404. |
| `DELETE /api/hostiles/{id}` | ID del hostil | 204 y eliminación física de la fila, o 404. |
| `GET /api/hostiles/{id}/comportamiento` | ID del hostil | 200 y mensaje del protocolo `Descriptible`, o 404. |
| `POST /api/hostiles/{id}/explosiones` | ID del Creeper en la URL. Cuerpo de ejemplo `{"zombieId":2,"distancia":1}` | 200 con `danio`, `creeper` y `zombie`, o un error identificado en JSON. |

## Invariantes y reglas

- El nombre tiene entre 1 y 100 caracteres y contiene algún carácter distinto de espacio en blanco. La vida y el rango son enteros no negativos. `PATCH` modifica solo la configuración y requiere al menos un campo con valor.
- El Creeper y el Zombie deben tener vida. El alcance de la explosión es el rango del Creeper. La distancia es un entero no negativo y el límite del alcance se incluye.
- La potencia dentro del alcance es `max(1, 12 - 3 × distancia)`. El daño aplicado se limita a la vida restante del Zombie y el Creeper queda con vida cero. Si falla la operación, las dos filas conservan sus estados previos.

## Respuestas de error

El JSON de error contiene `codigo` y `mensaje`.

| HTTP | Código | Cómo provocarlo |
| --- | --- | --- |
| 400 | `REGLA_MINECRAFT` | Crear un Zombie con vida negativa. |
| 400 | `PEDIDO_INVALIDO` | Enviar un JSON que no se pueda leer, por ejemplo con un subtipo desconocido. |
| 404 | `HOSTIL_NO_ENCONTRADO` | Consultar el ID 0. |
| 422 | `FUERA_DE_ALCANCE` | Intentar detonar a 4 bloques con un Creeper de rango 3. |
| 409 | `CREEPER_AGOTADO` | Intentar otra detonación con el Creeper después de una explosión exitosa. |
| 409 | `CAMBIO_CONCURRENTE` | Modificar una fila que otra operación ya cambió durante la misma ventana de escritura. |

## Cómo probarlo

1. Arrancá con `./mvnw spring-boot:run`, o con `.\mvnw.cmd spring-boot:run` en PowerShell.
2. En Insomnia, importá `insomnia/POO-08-Minecraft.json` con **Import → File**. Ejecutá los pedidos 01–03 y cargá los IDs de las dos altas en `creeper_id` y `zombie_id` del entorno.
3. Ejecutá 04–11 para verificar lecturas, actualización, POST ilegal, 404 y comportamiento. Los listados 04 y 09 deben contener los mismos IDs.
4. Ejecutá 12–15. A 4 bloques, esperá 422. A 1 bloque, esperá 9 puntos de daño. Confirmá con el listado que el Creeper quedó con 0 y el Zombie con 11. El siguiente intento devuelve 409.
5. Antes de borrar, podés reiniciar el servicio y repetir el listado para verificar que las dos vidas se conservan en el archivo de H2. También podés consultar `SELECT * FROM hostiles;` en `http://localhost:8080/h2-console`, con `jdbc:h2:file:./data/minecraft`, usuario `sa` y contraseña vacía.
6. Ejecutá 16–18 para verificar el borrado y retirar los datos de ejemplo.
