# 🍃 EcoNexo Gastronómico

**EcoNexo** es una plataforma tecnológica desarrollada como proyecto de tesis para la **Tecnicatura en Programación (UTN FRC)**. El objetivo es mitigar el desperdicio de alimentos en el sector gastronómico de la ciudad de **Córdoba** mediante logística de proximidad, conectando comercios donantes con organizaciones receptoras mediante conductores voluntarios, priorizando la seguridad bromatológica y la reducción de la huella de carbono.

## 🛠️ Stack Tecnológico

* **Backend:** Java 17 + Spring Boot 3.x + Hibernate 6
* **Base de Datos:** PostgreSQL + PostGIS (Gestión de geolocalización y datos espaciales)
* **Infraestructura Local:** Docker (Contenerización de servicios)
* **Frontend:** Angular 19 + Tailwind CSS v4
* **Seguridad:** Spring Security + JWT (JSON Web Tokens)

---

## 🏗️ Estructura del Proyecto (Scaffolding)

El sistema está dividido en dos aplicaciones independientes que se comunican mediante una API REST, aplicando el patrón de transformación de casos (el Backend y Frontend operan en `camelCase`, pero la transferencia de red se realiza en `snake_case`).

### Backend (Spring Boot - Arquitectura en Capas)
```text
src/main/java/com/utn/econexo
├── config/       # Configuraciones globales (Swagger, CORS, Jackson)
├── controller/   # Endpoints de la API REST (@RestController)
├── dto/          # Objetos de transferencia (Request/Response)
├── exception/    # Manejo global de errores (@ControllerAdvice)
├── mapper/       # Conversión entre Entidades y DTOs
├── model/        # Entidades JPA y Enums (@Entity)
├── repository/   # Acceso a base de datos (Spring Data JPA)
├── security/     # Lógica de autenticación, filtros y JwtUtils
└── service/      # Interfaces de negocio y sus implementaciones
```

### Frontend (Angular - Feature-Driven Architecture)
```text
src/
├── environments/   # Variables de entorno (Desarrollo y Producción)
├── app/
│   ├── core/       # Guards, Interceptors (Case Transform) y Servicios Singletons
│   ├── shared/     # Componentes UI reutilizables, Pipes y Modelos (Interfaces)
│   └── features/   # Módulos de negocio aislados:
│       ├── auth/          # Login y Registro
│       ├── donations/     # Publicación y listado de excedentes
│       ├── organizations/ # Perfiles de ONGs y Comercios
│       └── map/           # Vista interactiva de proximidad
```
***
## 🚀 Instalación y Ejecución Local
### 1. Requisitos Previos
* Java Development Kit (JDK) 17
* Node.js (v22+) y Angular CLI
* Docker Desktop

### 2. Levantar la Base de Datos Espacial
El proyecto requiere PostgreSQL con PostGIS. Ejecutá este comando en tu terminal reemplazando ```<tu_usuario>``` y ```<tu_password>``` por las credenciales que desees usar localmente:

```text
docker run --name econexo-db -e POSTGRES_USER=<tu_usuario> -e POSTGRES_PASSWORD=<tu_password> -e POSTGRES_DB=econexo_db -p 5432:5432 -d postgis/postgis:16-3.4
```

(Este comando levanta el motor, crea la base de datos ```econexo_db``` y le inyecta las capacidades espaciales automáticamente).

### 3. Configuración del Backend
Por seguridad, las credenciales no están versionadas. Debés inyectar las siguientes variables de entorno en la configuración de ejecución de tu IDE (Run/Debug Configurations) para que el proyecto pueda levantar correctamente:

* ```DB_USER```: ```<tu_usuario>```
* ```DB_PASSWORD```: ```<tu_password>```
* ```PRIVATE_KEY```: ```<tu_clave_secreta_para_firmar_jwts>``` (Ej: una cadena alfanumérica segura)
* ```USER_GENERATOR```: ```<tu_generador>```
* ```JWT_EXPIRATION_MINUTES```: ```<tu_expiracion>```
*

Una vez seteadas, ejecutá la clase ```EconexoApplication.java```. El sistema creará automáticamente un usuario administrador por defecto (admin@econexo.com / admin1234).

### 4. Documentación de la API (Swagger)
El backend cuenta con documentación interactiva generada con OpenAPI. Con la aplicación en ejecución, podés visualizar y probar los endpoints ingresando a:
http://localhost:8080/swagger-ui/index.html

### 5. Ejecución del Frontend
Posicionate en el directorio del frontend, instalá las dependencias y levantá el servidor de desarrollo:

```text
npm install
ng serve
```
***
## 📅 Roadmap de Desarrollo (Sprints)
El ciclo de vida del proyecto está estructurado en 6 Sprints incrementales:

* [x] **Sprint 0: Setup y Arquitectura**. Configuración inicial, despliegue de base de datos con PostGIS, variables de entorno y scaffolding completo de Backend y Frontend.
* [ ] **Sprint 1: Identidad y Seguridad**. Implementación del módulo de autenticación (Spring Security + JWT) y gestión de perfiles (Comercios, ONGs, Conductores).
* [ ] **Sprint 2: Core de Donaciones**. Flujo completo de publicación de excedentes alimentarios, catálogo, actualización de estados y panel de gestión.
* [ ] **Sprint 3: Motor Espacial**. Integración de mapas, uso de PostGIS para el cálculo de distancias y lógicas de asignación por proximidad.
* [ ] **Sprint 4: Trazabilidad e Impacto**. Registro bromatológico, control de estados de entrega y dashboard de métricas ambientales (Kg salvados, CO2 evitado).
* [ ] **Sprint 5: Cumplimiento Legal y Certificación.** Implementación de la lógica para la Ley Donal, generación de comprobantes y emisión de certificados fiscales de donación.
* [ ] **Sprint 6: Cierre, Refinamiento y Despliegue.** Pruebas integrales E2E, auditoría de código, correcciones finales de UI/UX, y despliegue a producción (Deploy).