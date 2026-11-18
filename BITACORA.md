# BITACORA — POO-08 y corrección

## Asistente de IA utilizado

ChatGPT

## Modelo

GPT-5.6 Sol

## Resumen de prompts

- Interpretación de la consigna POO-06.
- Reorganización del proyecto siguiendo el template de paquetes.
- Implementación de constructores simples y sobrecargados.
- Revisión de herencia, sobreescritura y polimorfismo.
- Creación y prueba de endpoints REST.
- Revisión del README y preparación de la entrega.

## Mejora de POO-04 según la nueva rúbrica

Se revisó la entrega anterior de POO-04 utilizando la rúbrica vigente.

Cambios revisados o incorporados:

- reorganización de paquetes;
- separación entre dominio y capa REST;
- revisión de encapsulamiento e invariantes;
- documentación de herencia y sobreescritura;
- incorporación de constructores sobrecargados;
- actualización de documentación y forma de prueba.

Se utilizó ChatGPT con el modelo GPT-5.6 Sol para revisar la entrega original, compararla con la rúbrica nueva y preparar la documentación actualizada.

## POO-08 · CRUD, H2 y explosiones

- **Asistente** OpenAI Codex.
- **Modelo** `gpt-6.1-sol`.
- **Resumen del prompt** Continuar el repositorio del taller y aplicar POO-08 al modelado existente. La consigna pidió un CRUD de la familia, H2, dos reglas para una simulación persistida, excepciones del dominio, una interfaz, una colección de Insomnia y la documentación para entregar.
- **Asistencia** Codex revisó la consigna y la rúbrica, implementó y verificó el servicio sobre el modelo de Minecraft, y preparó la colección y la documentación.
- **Seguimiento** Se pidió comprobar todos los requisitos de la consigna y la rúbrica. Codex revisó el arranque desde Git Bash, agregó pruebas reproducibles con H2 y ajustó el recorrido de Insomnia.
- **Seguimiento de pruebas** Se pidió revisar la sección de verificación de las especificaciones. Codex reprodujo el conflicto concurrente contra H2 y documentó cómo repetir la prueba.

## Corrección de POO-08 · 18 de noviembre de 2026

- **Asistente:** OpenAI Codex, modelo GPT-6.
- **Pedido:** mejorar primero POO-08 usando el PDF de corrección del profesor; POO-09 fue aportado como contexto y queda para otra etapa.
- **Revisión:** se leyó la corrección del commit original (18,5/24), se contrastó con el código y se identificaron las observaciones de capas, excepciones en entidades, vida imposible y título de la bitácora.
- **Cambios:** capa de servicio con transacciones y repositorio, reglas y excepciones fuera de las entidades, validación de vida 1–20 al crear y actualizar, tope SQL de 20, documentación y pruebas de regresión.
- **Entendimiento para defender:** crear requiere estar vivo; morir durante una explosión es un resultado válido. HTTP se atiende en controller; las decisiones y transacciones corresponden al service; el repositorio accede a H2. La fórmula de daño es una simplificación explícita.
- **Validación:** `.\mvnw.cmd test` terminó con 6 pruebas, 0 fallos y 0 errores sobre H2 en memoria. Se configuró la carga inicial del agente de Mockito para que la prueba funcione con el JDK local. Se agregaron los pedidos 20–23 en Insomnia para reproducir las altas ilegales. El trabajo de APIs cruzadas aún no se implementa.
- **Arranque:** comprobado con `spring-boot:run` en el puerto 18080 y una base H2 temporal en memoria; Tomcat inició correctamente. Se detuvo el servicio al terminar.
