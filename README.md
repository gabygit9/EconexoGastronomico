# 🍃 EcoNexo Gastronómico

**EcoNexo** es una plataforma tecnológica desarrollada como proyecto de tesis para la **Tecnicatura en Programación (UTN FRC)**. El objetivo es mitigar el desperdicio de alimentos en el sector gastronómico de la ciudad de **Córdoba** mediante logística de proximidad, conectando comercios donantes con organizaciones receptoras mediante conductores voluntarios, priorizando la seguridad bromatológica y la reducción de la huella de carbono.

El sistema contempla cuatro roles:
* **Donante** (comercio gastronómico)
* **ONG** (organización receptora)
* **Conductor** (voluntario de logística)
* **Administrador**.

🔗 **Demo en producción:** [https://econexo-mauve.vercel.app](https://econexo-mauve.vercel.app)

## 🛠️ Stack Tecnológico

### Backend
- Java 17 + Spring Boot 3.x + Hibernate 6 (Hibernate Spatial para soporte geoespacial)
- PostgreSQL + PostGIS (geolocalización, cálculo de proximidad y rutas)
- Spring Security + JWT (autenticación stateless)
- iText7 (generación de certificados de donación y reportes en PDF)
- Cloudinary (almacenamiento de imágenes: documentación de conductores, evidencia de entregas, comprobantes)
- Mercado Pago SDK (pagos de donaciones monetarias + webhook de confirmación)
- Spring Mail (notificaciones por correo)
- Springdoc OpenAPI / Swagger (documentación interactiva de la API)
### Frontend
- Angular 19 (standalone components, signals) + Tailwind CSS v4
- Leaflet + Leaflet Routing Machine (mapa y cálculo de rutas del conductor)
- ApexCharts / ng-apexcharts (dashboards de métricas por rol)
- ngx-toastr (notificaciones UI)
- angularx-qrcode / ngx-scanner-qrcode (validación de entregas por código QR)
### Infraestructura
- Docker + Docker Compose (entorno local reproducible)
- Despliegue en producción:
    - **Render** (backend)
    - **Vercel** (frontend)
    - **Neon** (PostgreSQL + PostGIS serverless)

El Backend y el Frontend operan internamente en `camelCase`, aplicando un patrón de transformación de casos para que la transferencia de red se realice en `snake_case`.

---

## 🏗️ Estructura del Proyecto (Scaffolding)

### Backend (Spring Boot - Arquitectura en Capas)
```text
src/main/java/com/utn/econexo
├── config/       # Configuraciones globales (Swagger, CORS, Jackson)
├── controller/   # Endpoints de la API REST (@RestController)
├── dto/          # Objetos de transferencia (Request/Response)
├── exception/    # Manejo global de errores (@ControllerAdvice)
├── mappers/      # Conversión entre Entidades y DTOs
├── model/        # Entidades JPA y Enums (@Entity)
├── repository/   # Acceso a base de datos (Spring Data JPA)
├── security/     # Autenticación, filtros JWT y configuración de seguridad
└── service/      # Interfaces de negocio y sus implementaciones
└── utils/        # PDF (certificados), notificaciones, geocoding, utilidades varias
```

### Frontend (Angular - Feature-Driven Architecture)
```text
src/
├── environments/   # Variables de entorno (Desarrollo y Producción)
├── app/
│   ├── core/       # Guards, servicios singleton (Auth, Logistics, Stats), interceptors
│   ├── shared/     # Componentes UI reutilizables, Pipes y Modelos (Interfaces)
│   └── features/   # Módulos de negocio aislados:
│       ├── auth/          # Login y Registro por rol y recuperación de contraseña
│       ├── dashboard/     # Paneles de control por perfil (Donante, ONG, Conductor, Admin)
│       ├── donations/     # Publicación, listado y detalle de excedentes
│       ├── organizations/ # Perfiles de ONGs y recepción de donaciones
│       └── legal/         # Términos y condiciones, FAQ
```
***
## 🚀 Instalación y Ejecución Local

### 1. Requisitos Previos
* Java Development Kit (JDK) 17
* Node.js (v22+) y Angular CLI
* Docker Desktop

### 2. Levantar todo el entorno con Docker Compose

La forma recomendada de correr el proyecto localmente es con Docker Compose, que levanta la base de datos con PostGIS, el backend y el frontend en una única red.

Creá un archivo `.env` en la raíz del proyecto y ejecutá:

```bash
docker compose up -d --build
```

Esto expone:
- Backend en `http://localhost:8080`
- Frontend en `http://localhost` (o el puerto configurado en `FRONTEND_PORT`)
- Base de datos PostGIS accesible solo dentro de la red interna de Docker
### 3. Variables de entorno necesarias

El `.env` (o las variables de entorno de tu IDE, si corrés el backend fuera de Docker) debe incluir:

```
DB_NAME=econexo_db
DB_USER=<tu_usuario>
DB_PASSWORD=<tu_password>
DB_HOST=postgres-db
DB_PORT=5432
 
SERVER_PORT=8080
FRONTEND_PORT=80
 
PRIVATE_KEY=<clave secreta para firmar JWTs>
USER_GENERATOR=<nombre del generador de tokens>
JWT_EXPIRATION_MINUTES=<minutos de expiración>
 
CLOUDINARY_CLOUD_NAME=<tu cloud name>
CLOUDINARY_API_KEY=<tu api key>
CLOUDINARY_API_SECRET=<tu api secret>
 
GOOGLE_MAPS_API_KEY=<tu api key de Google Maps>
 
MAIL_USERNAME=<correo emisor de notificaciones>
MAIL_PASSWORD=<contraseña de aplicación>
 
MERCADO_PAGO_ACCESS_TOKEN=<access token de Mercado Pago (sandbox o producción)>
```

> ⚠️ Todas las credenciales de ejemplo en este README son placeholders.

Una vez levantada la app por primera vez, el sistema crea automáticamente un usuario administrador por defecto:

```
admin@econexo.com / admin1234
```

### 4. Correr sin Docker (desarrollo backend/frontend por separado)

Si preferís correr los servicios sueltos durante el desarrollo activo:

**Backend:** cargá las variables de entorno de arriba en la configuración de ejecución de tu IDE y ejecutá la clase `EconexoApplication.java`. Necesitás una instancia de PostgreSQL con PostGIS corriendo — podés levantar solo ese contenedor con `docker compose up -d postgres-db`, o de forma standalone con:

```bash
docker run --name econexo-db -e POSTGRES_USER=<tu_usuario> -e POSTGRES_PASSWORD=<tu_password> -e POSTGRES_DB=econexo_db -p 5432:5432 -d postgis/postgis:16-3.4
```

(Este comando levanta el motor, crea la base de datos `econexo_db` y le inyecta las capacidades espaciales de PostGIS automáticamente).

**Frontend:**
```bash
cd Frontend/econexo
npm install
ng serve
```

### 5. Documentación de la API (Swagger)

Con el backend en ejecución:
```
http://localhost:8080/swagger-ui/index.html
```
---

## ☁️ Despliegue en Producción

El proyecto está desplegado usando servicios con capa gratuita, separando cada componente:

| Componente | Servicio | Notas |
|---|---|---|
| Base de datos | [Neon](https://neon.com) | PostgreSQL serverless con extensión PostGIS habilitada manualmente (`CREATE EXTENSION postgis;`) |
| Backend | [Render](https://render.com) | Web Service con Dockerfile propio (`Backend/Dockerfile`) |
| Frontend | [Vercel](https://vercel.com) | Build estático de Angular (`Frontend/econexo`) |

### Pasos generales para replicar el despliegue

1. **Neon**: crear un proyecto Postgres 15, habilitar PostGIS desde el SQL Editor (`CREATE EXTENSION IF NOT EXISTS postgis;`), y copiar la connection string.
2. **Render**: crear un Web Service apuntando al repo, con Root Directory `Backend` y Dockerfile Path `Dockerfile`. Cargar todas las variables de entorno (ver lista arriba) más:
- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL=jdbc:postgresql://<host-neon>/<db>?sslmode=require`
- `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` (separados de la URL, no embebidos)
- `SPRING_JPA_HIBERNATE_DDL_AUTO=update` en el primer deploy (para que Hibernate cree el esquema), y luego cambiar a `validate` una vez confirmado el esquema, para blindar contra cambios accidentales.
- `TZ=America/Argentina/Buenos_Aires` (evita el desfasaje de 3hs en timestamps, ya que los contenedores corren en UTC por defecto).
3. **Vercel**: crear un proyecto apuntando al repo, con Root Directory `Frontend/econexo`, Build Command `npm run build -- --configuration production` y Output Directory `dist/econexo/browser`. Actualizar `environment.ts` con la URL pública del backend de Render antes del deploy.
4. **CORS**: agregar el dominio de Vercel a `allowedOrigins` en `CorsConfig.java` del backend.
5. **Webhook de Mercado Pago**: configurar en el panel de Developers de Mercado Pago la URL `https://<tu-backend>.onrender.com/api/v1/payments/webhook` como notification URL, sobre el evento de Merchant Orders.
> 💡 El plan free de Render "duerme" el servicio tras ~15 min de inactividad (cold start de hasta 1 min en la siguiente request). Si vas a hacer una demo en vivo, conviene "despertar" el backend visitando cualquier endpoint unos minutos antes.

***
## 📅 Roadmap de Desarrollo (Sprints)
El ciclo de vida del proyecto está estructurado en 6 Sprints incrementales, **todos completados**:

- ✅ **Sprint 0: Setup y Arquitectura**. Configuración inicial, despliegue de base de datos con PostGIS, variables de entorno y scaffolding completo de Backend y Frontend.
- ✅ **Sprint 1: Identidad y Seguridad**. Implementación del módulo de autenticación (Spring Security + JWT) y gestión de perfiles (Comercios, ONGs, Conductores, Admin).
- ✅ **Sprint 2: Core de Donaciones**. Flujo completo de publicación de excedentes alimentarios, catálogo de productos, máquina de estados de donaciones y panel de gestión por rol.
- ✅ **Sprint 3: Motor Espacial**. Integración de mapas con Leaflet, uso de PostGIS para cálculo de distancias, asignación por proximidad y cálculo de rutas del conductor.
- ✅ **Sprint 4: Trazabilidad e Impacto**. Registro bromatológico (temperatura, firma, evidencia fotográfica), control de estados de entrega, validación por QR y dashboards de métricas de impacto por rol.
- ✅ **Sprint 5: Cumplimiento Legal y Certificación.** Generación de certificados de donación en PDF (Ley N° 25.989 y N° 27.454), reportes unificados por período, e integración de pagos (Mercado Pago + webhook de confirmación automática).
- ✅ **Sprint 6 — Cierre, Refinamiento y Despliegue.** Ajustes de UI/UX responsive (mobile-first en todos los paneles), pruebas E2E del flujo completo, y despliegue a producción (Render + Vercel + Neon).

---

## 📄 Licencia

Proyecto académico desarrollado en el marco de la Tecnicatura en Programación, UTN Facultad Regional Córdoba.