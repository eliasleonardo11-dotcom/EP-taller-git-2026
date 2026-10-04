# CYT646 — POO-04. Taller de GIT y práctica POO + API REST — Entrega mejorada

**Alumno:** Elías Pont  
**Materia:** Lenguaje de Programación 3  
**Dominio:** Minecraft  
**Repositorio:** https://github.com/eliasleonardo11-dotcom/EP-taller-git-2026

## 1. Objetivo

Publicar un servicio HTTP propio con Spring Boot y API REST utilizando el modelado orientado a objetos de Minecraft, aplicando herencia, sobreescritura, ocultamiento de la información y polimorfismo.

## 2. Organización actual del proyecto

El proyecto fue reorganizado para separar el dominio de la capa REST.

```text
src/main/java/py/edu/uc/lp3/
├── EpTallerGit2026Application.java
├── domain/
│   └── minecraft/
│       ├── Entidad.java
│       ├── Hostil.java
│       ├── NoHostil.java
│       ├── Jugador.java
│       ├── Zombie.java
│       ├── Creeper.java
│       └── Aldeano.java
└── rest/
    └── controller/
        ├── IndexController.java
        └── EntidadController.java
