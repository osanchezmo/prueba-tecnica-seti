# Prueba Técnica SETI - Backend

API REST para la gestión de fondos de inversión. Este proyecto está dividido en dos partes según la especificación técnica (`prueba_tecnica_back_end 4.pdf`). **Esta documentación cubre únicamente la Parte 1.**

Los requisitos y endpoints están definidos en el documento técnico y en la colección Postman incluida en el repositorio.

## Contenido

- [Descripción](#descripción)
- [Arquitectura](#arquitectura)
- [Requisitos y ejecución](#requisitos-y-ejecución)
- [API](#api)
- [Colección Postman](#colección-postman)
- [Estructura del proyecto](#estructura-del-proyecto)

---

## Descripción

La **Parte 1** implementa un sistema de gestión de fondos de inversión:

| Funcionalidad | Descripción |
|---------------|-------------|
| Autenticación | Login con JWT (email y contraseña) |
| Clientes | Registro con saldo inicial de $500.000 COP |
| Fondos | Suscripción y cancelación a fondos de inversión |
| Transacciones | Historial de operaciones por cliente |

**Stack:** Java 17 · Spring Boot 3.4.3 · MongoDB 6 · Maven

---

## Arquitectura

### Diagrama de arquitectura

```mermaid
flowchart TB
    subgraph Cliente["Cliente"]
        HTTP[Peticiones HTTP]
    end

    subgraph App["Aplicación Spring Boot"]
        subgraph Controllers
            AuthCtrl["/api/auth"]
            ClienteCtrl["/api/clientes"]
            FondoCtrl["/api/fondos"]
        end

        subgraph Services
            AuthSvc[AuthService]
            ClienteSvc[ClienteService]
            FondoSvc[FondoService]
        end

        subgraph Repositories
            ClienteRepo[ClienteRepository]
            FondoRepo[FondoRepository]
            TransaccionRepo[TransaccionRepository]
        end
    end

    subgraph DB["Base de datos"]
        MongoDB[(MongoDB)]
    end

    HTTP --> AuthCtrl
    HTTP --> ClienteCtrl
    HTTP --> FondoCtrl

    AuthCtrl --> AuthSvc
    ClienteCtrl --> ClienteSvc
    ClienteCtrl --> FondoSvc
    FondoCtrl --> FondoSvc

    ClienteSvc --> ClienteRepo
    FondoSvc --> FondoRepo
    FondoSvc --> TransaccionRepo

    ClienteRepo --> MongoDB
    FondoRepo --> MongoDB
    TransaccionRepo --> MongoDB

    JWT[JWT Filter] -.->|Protege| ClienteCtrl
    JWT -.->|Protege| FondoCtrl
```

### Modelo de datos

```mermaid
erDiagram
    CLIENTE ||--o{ TRANSACCION : realiza
    FONDO ||--o{ TRANSACCION : registra
    CLIENTE }o--o{ FONDO : suscrito

    CLIENTE {
        string id PK
        string nombre
        string email
        string telefono
        decimal saldo_disponible
        array fondos_suscritos
        set roles
    }

    FONDO {
        string id PK
        string nombre
        decimal monto_minimo
        string categoria
    }

    TRANSACCION {
        string id PK
        string cliente_id FK
        string fondo_id FK
        enum tipo
        decimal monto
        datetime fecha
    }
```

---

## Requisitos y ejecución

### Requisitos

- Java 17+
- Maven 3.6+
- MongoDB (local o remoto)

### Base de datos (opcional)

> [!NOTE]
> El `docker-compose.yml` es **opcional**. Solo es necesario si no tienes MongoDB instalado localmente. Si ya lo tienes configurado en tu computadora, puedes omitir este paso y ajustar las credenciales en `application.properties`.

```bash
docker-compose up -d
```

MongoDB quedará en `localhost:27017` (usuario: `btg_siti`, contraseña: `siti123`, base: `btg_fondos`).

### Ejecución

```bash
mvn clean install
mvn spring-boot:run
```

Aplicación disponible en `http://localhost:8080`.

### Tests

```bash
mvn test
```

El proyecto incluye tests unitarios (services) y de integración (controllers).

### Flujo de prueba rápida

> [!TIP]
> Para validar la solución rápidamente, sigue estos pasos en orden:

1. `docker-compose up -d` (si no tienes MongoDB)
2. `mvn spring-boot:run`
3. Probar la API con **Swagger** (http://localhost:8080/) o **Postman** (importar `BTG Fondos API.postman_collection.json`)
4. Crear cliente → Login → Suscribir a fondo

---

## API

### Documentación interactiva

- **Swagger UI:** http://localhost:8080/
- **OpenAPI:** http://localhost:8080/v3/api-docs

### Colección Postman

> [!NOTE]
> El proyecto incluye la colección **BTG Fondos API.postman_collection.json**, que forma parte de los requisitos del documento técnico. Contiene todos los endpoints de la Parte 1 listos para probar.

**Importar en Postman:**
1. Abrir Postman → File → Import
2. Seleccionar el archivo `BTG Fondos API.postman_collection.json` (en la raíz del proyecto)
3. La colección incluye variables preconfiguradas: `baseUrl` (http://localhost:8080), `token`, `clienteId`

**Flujo de uso (según la colección):**
1. **Crear cliente** → El `clienteId` se guarda automáticamente
2. **Login** → El JWT se guarda automáticamente en `token`
3. **Endpoints protegidos** → Usan el token automáticamente (Suscribir, Cancelar, Historial)

**IDs de fondos disponibles:** 1, 2, 3, 4, 5

| ID | Fondo | Monto mínimo |
|----|-------|--------------|
| 1 | FPV_BTG_PACTUAL_RECAUDADORA | $75.000 COP |
| 2 | FPV_BTG_PACTUAL_ECOPETROL | $125.000 COP |
| 3 | DEUDAPRIVADA | $50.000 COP |
| 4 | FDO-ACCIONES | $250.000 COP |
| 5 | FPV_BTG_PACTUAL_DINAMICA | $100.000 COP |

### Endpoints (Parte 1)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/login` | Login (retorna JWT) | No |
| POST | `/api/clientes/` | Crear cliente | No |
| POST | `/api/fondos/suscribir` | Suscribirse a fondo | JWT |
| POST | `/api/fondos/cancelar` | Cancelar suscripción | JWT |
| GET | `/api/clientes/{id}/transacciones` | Historial de transacciones | JWT |

### Uso del JWT

> [!TIP]
> Para endpoints protegidos, incluye el token en el header `Authorization`:

```
Authorization: Bearer <tu_token_jwt>
```

### Roles

- **CLIENTE:** Solo puede acceder a sus propios datos.
- **ADMIN:** Acceso completo.

---

## Estructura del proyecto

```
src/main/java/com/btg/prueba_tecnica_seti/
├── config/         # Security, MongoDB, OpenAPI
├── controller/     # REST (Auth, Cliente, Fondo)
├── dto/            # Request/Response
├── entity/         # Cliente, Fondo, Transaccion
├── repository/     # Spring Data MongoDB
├── security/       # JWT
└── service/impl/   # Lógica de negocio
```

## Notas

> [!NOTE]
> - Se inicializan **5 fondos predefinidos** automáticamente al arrancar la aplicación.
> - El saldo inicial de cada nuevo cliente es de **$500.000 COP**.
