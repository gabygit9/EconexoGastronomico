🍃 EcoNexo Gastronómico

EcoNexo es una plataforma tecnológica desarrollada como proyecto de tesis, orientada a mitigar el desperdicio de alimentos en el sector gastronómico mediante logística de proximidad. Conecta de forma eficiente a comercios donantes con conductores voluntarios y organizaciones receptoras, priorizando la seguridad bromatológica y la reducción de la huella de carbono.

El proyecto inicia su fase piloto enfocada en la ciudad de Córdoba.

🛠️ Stack Tecnológico

La plataforma está construida utilizando una arquitectura moderna y escalable:

Backend: Java 17 + Spring Boot 3.5.11

Base de Datos: PostgreSQL con extensión PostGIS (Manejo de datos espaciales y geolocalización)

Infraestructura Local: Docker

Seguridad: Spring Security + JWT (JSON Web Tokens)

Frontend (Próximamente): Angular 19 + Tailwind CSS

🚀 Características Principales (Roadmap)

[x] Diseño de Arquitectura y Base de Datos.

[x] Diseño UX/UI (Handoff).

[x] Configuración inicial del servidor y bases de datos espaciales.

[ ] Módulo de Autenticación: Registro de perfiles (Comercio, Conductor, ONG) y Login.

[ ] Core de Donaciones: Publicación de excedentes, asignación basada en proximidad y confirmación de recepción.

[ ] Motor de Geolocalización: Cálculo de distancias y rutas usando PostGIS.

[ ] Trazabilidad Bromatológica: Registro de temperatura y cadena de frío.

[ ] Módulo de Impacto Ambiental: Dashboard de Kgs salvados y CO2 evitado.

[ ] Cumplimiento Legal (Ley Donal): Emisión de certificados fiscales.

⚙️ Requisitos Previos (Para desarrollo local)

Para ejecutar este proyecto en tu entorno local, necesitas tener instalado:

Java Development Kit (JDK) 17

Docker Desktop

🏗️ Instalación y Ejecución

1. Levantar la Base de Datos

El proyecto requiere una instancia de PostgreSQL con la extensión PostGIS habilitada. Puedes levantarla rápidamente mediante Docker ejecutando:

docker run --name econexo-db -e POSTGRES_PASSWORD=tu_password_local -p 5432:5432 -d postgis/postgis:16-3.4


(Nota: Asegúrate de crear la base de datos econexo_db y ejecutar CREATE EXTENSION postgis; en tu consola SQL inicial).

2. Configurar Variables de Entorno

Por seguridad, las credenciales no están versionadas en el código. Debes inyectar las siguientes variables de entorno en la configuración de ejecución de tu IDE antes de arrancar la aplicación:

DB_USER: (ej. postgres)

DB_PASSWORD: (el password configurado en tu contenedor Docker)

3. Ejecutar la Aplicación

Una vez configuradas las variables y con el contenedor corriendo, ejecuta la clase principal EconexoApplication.java. El servidor arrancará por defecto en el puerto 8080.