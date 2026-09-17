# Gestión de Vuelos — Ejercicio 18

Aplicación web MVC desarrollada con **Spring Boot + Spring MVC + Thymeleaf + Spring Data JPA**,
como actividad académica de la asignatura Desarrollo Web. Implementa un CRUD completo para las
entidades **Usuario** y **Vuelo**, autenticación con Spring Security, recuperación de clave por
correo electrónico, y reportes/consultas parametrizadas para ambas entidades.

> Esta aplicación **no** es una API REST: todos los controladores retornan vistas HTML
> renderizadas del lado del servidor con Thymeleaf.

## Datos de la actividad

| Dato | Información |
|---|---|
| Asignatura | Desarrollo Web |
| Actividad | Spring Boot MVC con Thymeleaf: desarrollo web basado en framework |
| Ejercicio asignado | N.º 18 — Vuelo |
| Guía utilizada | Spring Web MVC (carpeta de guías suministrada por el docente) |

## Tecnologías

- Java 21
- Spring Boot 3.3.5
- Spring MVC
- Spring Data JPA (Hibernate)
- Thymeleaf + thymeleaf-extras-springsecurity6
- Spring Security (login por formulario, roles ADMIN/OPERADOR)
- Spring Boot Starter Mail (recuperación de clave)
- MySQL 8
- Maven

## Arquitectura

El proyecto sigue una arquitectura por capas:

- **entity**: clases JPA que representan las tablas (`Usuario`, `Vuelo`, `TokenRecuperacion`) y los enums `Rol`, `EstadoVuelo`.
- **repository**: interfaces Spring Data JPA con las consultas parametrizadas (reportes).
- **service / service.impl**: lógica de negocio y validaciones.
- **controller**: controladores `@Controller` que reciben peticiones HTTP y retornan nombres de plantillas Thymeleaf.
- **config**: configuración de Spring Security y carga de datos iniciales.
- **templates**: vistas HTML con Thymeleaf, organizadas por entidad.

## Entidades

**Usuario** (común, exigida por la actividad): `id`, `nombre`, `clave`, `rol`, `email`, `activo`.

**Vuelo** (ejercicio 18): `id`, `numero`, `aerolinea`, `agenciaViajes`, `fechaCompra`, `fechaSalida`,
`fechaLlegada`, `estado`, `valor`, `cliente`, `puesto`, `avion`, `aeropuertoSalida`,
`aeropuertoLlegada`, `piloto`.

## Funcionalidades

- CRUD completo de Usuario y de Vuelo (crear, listar, ver detalle, editar, eliminar).
- Autenticación con Spring Security (login por formulario contra la tabla `usuarios`, claves cifradas con BCrypt).
- Roles: `ADMIN` (acceso total, incluida la gestión de usuarios) y `OPERADOR` (gestión de vuelos y reportes).
- Recuperación de clave por correo electrónico (token de un solo uso, vigencia de 30 minutos).
- Reportes parametrizados:
    - **Vuelo**: por aerolínea y rango de fechas de salida; por estado y rango de valor; por ruta (aeropuertos de salida/llegada).
    - **Usuario**: por rol y estado (activo/inactivo); por texto en nombre o correo.

## Requisitos previos

- JDK 21
- Maven (el proyecto incluye Maven Wrapper, no es obligatorio tenerlo instalado aparte)
- MySQL 8 corriendo localmente (por ejemplo, con Laragon) o accesible remotamente
- Una cuenta de Gmail con verificación en dos pasos activada, para generar una
  [contraseña de aplicación](https://myaccount.google.com/apppasswords) (solo necesaria para
  probar el envío real de correos de recuperación de clave)

## Configuración de la base de datos

1. Crea la base de datos ejecutando el script incluido en el repositorio:
   También puedes omitir este paso: con `spring.jpa.hibernate.ddl-auto=update` (configuración
   por defecto de este proyecto), Hibernate crea automáticamente las tablas al arrancar la
   aplicación, siempre que la base de datos `gestion_vuelos` ya exista vacía.

2. Ajusta la conexión en `src/main/resources/application.yml` si tu configuración de MySQL
   difiere de la que trae el proyecto (usuario `root`, sin clave, puerto `3306`):
```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/gestion_vuelos?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true
       username: root
       password:
```

## Variables de entorno

Para el envío real de correos de recuperación de clave, define estas variables de entorno
antes de ejecutar la aplicación (en IntelliJ: Run → Edit Configurations → Environment variables):

| Variable | Descripción |
|---|---|
| `MAIL_USERNAME` | Correo de Gmail remitente |
| `MAIL_PASSWORD` | Contraseña de aplicación de Gmail (16 caracteres, generada en la cuenta de Google) |

Si estas variables no se configuran, la aplicación funciona igual: el enlace de recuperación
se escribe en los logs de la consola en vez de enviarse por correo (controlado por
`app.mail.habilitado` en `application.yml`).

## Cómo ejecutar el proyecto

1. Clona el repositorio:
```bash
   git clone git@github.com:johannmoreno/gestion-vuelos.git
   cd gestion-vuelos
```
2. Asegúrate de que MySQL esté corriendo y la base de datos `gestion_vuelos` exista.
3. Ejecuta con el wrapper de Maven:
```bash
   ./mvnw spring-boot:run
```
(En Windows: `mvnw.cmd spring-boot:run`)
4. Abre el navegador en: http://localhost:8080

## Usuarios de prueba

Al arrancar por primera vez sobre una base de datos vacía, la aplicación crea automáticamente:

| Usuario | Clave | Rol |
|---|---|---|
| `admin` | `Admin12345` | ADMIN |
| `operador` | `Operador123` | OPERADOR |

También se cargan 5 vuelos de ejemplo con distintos estados, aerolíneas y rutas, para poder
probar de inmediato el listado, el detalle y los reportes.

## Estructura del proyecto
gestion-vuelos/
├── db/schema.sql
├── src/main/java/com/desarrolloweb/vuelos/
│ ├── entity/
│ ├── repository/
│ ├── service/ (+ service/impl/)
│ ├── controller/
│ └── config/
└── src/main/resources/
├── application.yml
├── static/css/
└── templates/

## Despliegue

La aplicación está desplegada y disponible públicamente en Railway:

**https://gestion-vuelos-production.up.railway.app**

## Autor

- **Nombre:** Johann Moreno
- **Código:** 7502423002
- **Repositorio:** https://github.com/johannmoreno/gestion-vuelos