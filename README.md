# Transporte Efectivo

Aplicación web para orientar a las personas que usan el transporte público en Lima: permite consultar líneas, costos de pasaje y paraderos, buscar por nombre, ver los paraderos más cercanos a la ubicación del usuario, revisar alertas de tránsito y registrar quejas o sugerencias.


## ¿Qué problema resuelve?

La información sobre rutas, paraderos, tarifas e incidencias del transporte público suele estar dispersa o desactualizada. Transporte Efectivo la reúne en un solo sitio y ofrece un canal ordenado para que los pasajeros reporten problemas y sean atendidos.

> Es una guía de consulta. **No** incluye seguimiento GPS de buses en tiempo real, cálculo de la ruta óptima, notificaciones automáticas ni pagos en línea.

## Funcionalidades

### Visitante (sin cuenta)
- **Inicio:** buscador, última alerta activa y líneas disponibles.
- **Líneas:** listado con costo de pasaje; cada línea muestra sus paraderos en orden y un mapa del recorrido.
- **Búsqueda:** por nombre de línea o de paradero.
- **Cerca de mí:** muestra en un mapa los paraderos y líneas más cercanos a la ubicación (hasta 5 puntos, radio de 5 km).
- **Alertas:** panel público de avisos de tránsito.
- **Registro e inicio de sesión** con correo y contraseña.

### Pasajero (con cuenta)
- Envía una **queja o sugerencia** sobre una línea activa (comentario de hasta 1000 caracteres).
- Consulta **Mis reportes** (solo los suyos) con su estado: Pendiente o Revisada.
- Cambia su contraseña desde el perfil.

### Administrador
- **Líneas:** crear, editar, activar y desactivar.
- **Paraderos:** crear y editar por línea eligiendo la ubicación en un mapa, activar/desactivar y cambiar su posición en el recorrido.
- **Alertas:** publicar y eliminar avisos.
- **Reportes:** revisar quejas y sugerencias, filtrarlas por tipo, línea o estado y marcarlas como revisadas.

## Tecnologías

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4.1.1 (Spring MVC) |
| Vistas | Thymeleaf |
| Seguridad | Spring Security (roles `ADMIN` y `PASAJERO`, contraseñas con BCrypt) |
| Persistencia | Spring Data JPA + PostgreSQL |
| Validaciones | Jakarta Bean Validation |
| Mapas | Leaflet + OpenStreetMap |
| Construcción | Maven (incluye Maven Wrapper) |

## Requisitos previos

- JDK 25
- PostgreSQL (con una base de datos creada)
- IntelliJ IDEA (recomendado) o cualquier IDE compatible con Maven

## Instalación y ejecución

1. **Clonar el repositorio**
```bash
   git clone https://github.com/nimbusphagia/TransporteEfectivo.git
   cd TransporteEfectivo
```

2. **Crear la base de datos** en PostgreSQL. Por defecto se usa el nombre `transporte_eficiente_db`; puedes usar otro y configurarlo en `DB_NAME`.
```sql
   CREATE DATABASE transporte_eficiente_db;
```

3. **Configurar las variables de entorno.** Copia `.env.example` como `.env` en la raíz del proyecto y completa los valores:
```env
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=transporte_eficiente_db
   DB_USER=postgres
   DB_PASSWORD=tu_contraseña
   SERVER_PORT=8080

   # Cuenta de administrador inicial (opcional)
   ADMIN_NOMBRE=Administrador de prueba
   ADMIN_DNI=87654321
   ADMIN_EMAIL=admin@ejemplo.com
   ADMIN_PASSWORD=una_clave_de_8_a_72_caracteres

   # Cuenta de pasajero de demostración (opcional)
   DEMO_NOMBRE=Pasajero de prueba
   DEMO_DNI=12345678
   DEMO_EMAIL=pasajero@ejemplo.com
   DEMO_PASSWORD=una_clave_de_8_a_72_caracteres
```
   > El archivo `.env` contiene credenciales: **no lo subas al repositorio**. Las cuentas iniciales solo se crean si todos sus campos están completos; el DNI debe tener 8 dígitos y la contraseña entre 8 y 72 caracteres.

4. **Ejecutar la aplicación**
```bash
   ./mvnw spring-boot:run
```
   En Windows: `mvnw.cmd spring-boot:run`. También puedes ejecutar la clase `TransportesApplication` desde IntelliJ IDEA.

5. **Abrir** [http://localhost:8080](http://localhost:8080).

Al iniciar, Hibernate crea o actualiza las tablas automáticamente (`ddl-auto: update`), el sistema registra los roles `ADMIN` y `PASAJERO` y, si la base de datos no tiene líneas, carga **datos de demostración**: cinco líneas (Metropolitano, Corredor Azul, El Rápido, Corredor Rojo y Corredor Morado) con sus paraderos y coordenadas.

## Rutas principales

| Ruta | Acceso | Función |
|---|---|---|
| `/`, `/buscar` | Público | Inicio y búsqueda de líneas y paraderos |
| `/lineas`, `/lineas/{id}/paraderos` | Público | Líneas con costo y paraderos de cada línea |
| `/ubicacion` | Público | Paraderos y líneas cercanos |
| `/alertas` | Público | Alertas de tránsito |
| `/login`, `/registro` | Público | Inicio de sesión y creación de cuenta |
| `/perfil` | Con sesión | Datos de la cuenta y cambio de contraseña |
| `/reportes/**` | Pasajero | Nuevo reporte, «Mis reportes» y detalle |
| `/admin/lineas`, `/admin/paraderos` | Administrador | Gestión de líneas y paraderos |
| `/admin/alertas`, `/admin/reportes` | Administrador | Gestión de alertas y revisión de reportes |

Si una persona con sesión intenta entrar a una pantalla que no corresponde a su rol, se muestra la página de **Acceso denegado (403)**.

## Estructura del proyecto

```
src/main/java/pe/cibertec/transportes/
├── config/        Seguridad, auditoría y carga de datos iniciales
├── controller/    Controladores Spring MVC por módulo
├── dto/           Formularios y objetos de transferencia
├── entity/        Entidades JPA (Rol, Usuario, LineaTransporte, Paradero, Interaccion, AlertaTrafico)
├── exception/     Excepciones propias y GlobalExceptionHandler
├── repository/    Repositorios Spring Data JPA
├── service/       Reglas de negocio
└── util/          Utilidades (cálculo de distancias)

src/main/resources/
├── templates/     Vistas Thymeleaf (fragments, linea, paradero, alerta, interaccion, auth, ubicacion, perfil, error)
├── static/        CSS, JavaScript e imágenes
└── application.yaml
```

La aplicación sigue la arquitectura **controller → service → repository**: los controladores no acceden directamente a los repositorios.

## Modelo de datos

Seis tablas: `rol`, `usuario`, `linea_transporte`, `paradero`, `interaccion` y `alerta_trafico`.

- Cada usuario tiene un rol.
- Cada paradero pertenece a una línea.
- Cada reporte (`interaccion`) pertenece a un usuario y a una línea, con tipo `QUEJA` o `SUGERENCIA` y estado `PENDIENTE` o `REVISADA`.
- Las alertas son independientes.

## Reglas principales

- No se permiten correos ni DNI repetidos.
- No puede haber dos líneas con el mismo nombre.
- No puede haber dos paraderos activos con la misma posición en una línea.
- Las líneas y paraderos desactivados dejan de mostrarse al público, pero se conservan.

## Flujo de trabajo del equipo

- Trabajo en ramas de Git y solicitudes de integración (Pull Request) revisadas por otro integrante.
- Nunca se publican credenciales: `.env` queda fuera del repositorio.
