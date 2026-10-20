# TCG Marketplace - Microservices Architecture

Plataforma backend distribuida basada en arquitectura de microservicios para la gestión y comercialización de cartas coleccionables (*Trading Card Games* como Pokémon TCG, Magic: The Gathering y Yu-Gi-Oh!). El sistema implementa un patrón API Gateway como único punto de entrada, desacoplando los servicios de negocio y orquestando la persistencia mediante contenedores Docker.

---

## 🏛️ Arquitectura del Sistema

La solución está construida sobre un ecosistema desacoplado que opera dentro de una red aislada de Docker:

```
                  +-----------------------------------+
                  |        Cliente HTTP (Postman)     |
                  +-----------------------------------+
                                    |
                                    | HTTP / Port 8080
                                    v
                  +-----------------------------------+
                  |            api-gateway            |
                  |     (Spring Cloud Gateway MVC)    |
                  +-----------------------------------+
                                    |
                                    | Red interna Docker: bridge
                                    | http://catalogo-service:8081
                                    v
                  +-----------------------------------+
                  |         catalogo-service          |
                  |     (Spring Boot Web / Data JPA)  |
                  +-----------------------------------+
                                    |
                                    | TCP / Port 5432
                                    v
                  +-----------------------------------+
                  |            postgres-db            |
                  |       (PostgreSQL Database)       |
                  +-----------------------------------+
```

### Componentes

1. **`api-gateway` (Puerto 8080):** Actúa como proxy inverso unificado. Maneja políticas globales de CORS y enruta el tráfico hacia los servicios internos correspondientes.
2. **`catalogo-service` (Puerto 8081 interno):** Microservicio encargado del catálogo de cartas coleccionables, implementando operaciones CRUD mediante Spring Data JPA.
3. **`postgres-db` (Puerto 5432):** Motor relacional PostgreSQL encargado de la persistencia de datos bajo la base `tcg_examen_db`.

---

## 🛠️ Stack Tecnológico

* **Lenguaje:** Java 21 (Eclipse Temurin LTS)
* **Framework:** Spring Boot 3.4.2 / Spring Cloud 2024.0.0
* **Persistencia:** Spring Data JPA / Hibernate
* **Base de Datos:** PostgreSQL 16
* **Contenerización y Orquestación:** Docker & Docker Compose
* **Gestor de Dependencias:** Apache Maven Wrapper (`mvnw`)

---

## 📋 Prerrequisitos

Antes de desplegar el entorno, verifique que su sistema disponga de:
* [Docker Desktop](https://www.docker.com/) (versión 24 o superior) con Docker Compose habilitado.
* Git (versión 2.30 o superior).
* Cliente HTTP de pruebas (Postman, Thunder Client o cURL).

---

## 🚀 Guía de Despliegue y Ejecución

Siga estos pasos para clonar, compilar y ejecutar todo el clúster:

1. **Clonar el repositorio:**
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd tcg-marketplace
   ```

2. **Levantar los servicios con Docker Compose:**
   Ejecute el comando en la raíz del proyecto para descargar imágenes base, compilar los artefactos `.jar` en etapas múltiples (*multi-stage build*) y levantar los contenedores en segundo plano:
   ```bash
   docker compose up -d --build
   ```

3. **Verificación de Contenedores:**
   Compruebe el estado operativo de los tres contenedores:
   ```bash
   docker compose ps
   ```
   *Debe confirmar que `postgres-db`, `catalogo-service` y `api-gateway` se encuentren en estado `Up`.*

4. **Detención del Sistema:**
   Para detener los contenedores preservando los datos:
   ```bash
   docker compose down
   ```
   Para reiniciar eliminando volúmenes de datos:
   ```bash
   docker compose down -v
   ```

---

## 📡 Documentación de Endpoints

Todas las peticiones públicas deben canalizarse a través del **API Gateway** (`http://localhost:8080`):

| Método | Endpoint | Descripción | Código Exitoso |
| :--- | :--- | :--- | :---: |
| **GET** | `/api/cartas` | Listar todas las cartas en inventario | `200 OK` |
| **GET** | `/api/cartas/{id}` | Obtener el detalle de una carta por ID | `200 OK` |
| **POST** | `/api/cartas` | Registrar una nueva carta en el catálogo | `200 OK` / `201 Created` |
| **PUT** | `/api/cartas/{id}` | Actualizar datos y stock de una carta | `200 OK` |
| **DELETE** | `/api/cartas/{id}` | Eliminar una carta del sistema | `204 No Content` |
| **GET** | `/actuator/health` | Verificación de salud del Gateway | `200 OK` |

---

## 🧪 Pruebas Funcionales (Comandos cURL)

### 1. Insertar una nueva carta (POST)
```bash
curl -X POST http://localhost:8080/api/cartas \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Charizard Base Set 1st Edition",
    "juego": "Pokemon TCG",
    "precio": 450.00,
    "stock": 3
  }'
```

### 2. Consultar catálogo completo (GET)
```bash
curl -X GET http://localhost:8080/api/cartas
```

### 3. Consultar por ID (GET)
```bash
curl -X GET http://localhost:8080/api/cartas/1
```

### 4. Actualizar registro (PUT)
```bash
curl -X PUT http://localhost:8080/api/cartas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Charizard Base Set 1st Edition Shadowless",
    "juego": "Pokemon TCG",
    "precio": 650.00,
    "stock": 2
  }'
```

### 5. Eliminar registro (DELETE)
```bash
curl -X DELETE http://localhost:8080/api/cartas/1
```

---

## 🌿 Convención Git Flow

El proyecto sigue una estrategia rigurosa de control de versiones basada en Git Flow:
* `main`: Código probado y listo para producción, etiquetado con tags semánticos (`v1.0.0`).
* `develop`: Rama base de desarrollo e integración continua.
* `feature/*`: Ramas temporales de desarrollo para componentes específicos (`feature/catalogo-service`, `feature/api-gateway`, `feature/docker-compose`).