# Trabajo Práctico Grupal 2026: Sistema de Gestión "Epicentro Gourmet"

**Orientación a Objetos II · Hito 2 · Grupo 1**

Aplicación web para gestionar un festival gastronómico: festivales y sus costos, unidades de venta (food trucks y puestos desarmables), empleados, platos, pedidos, liquidación de sueldos y reportes de recaudación y rentabilidad.

## Integrantes del grupo

- Denis Franco Emanuel Flores ([fra1010](https://github.com/fra1010))
- Leandro Ivan Vera ([Lean-IV](https://github.com/Lean-IV))
- Ivan Tolaba ([ivanpablo1989](https://github.com/ivanpablo1989))
- Diego Olmedo ([diego-olm](https://github.com/diego-olm))

---

## Índice

1. [Tecnologías](#tecnologías)
2. [Modelo de dominio](#modelo-de-dominio)
3. [Roles y funcionalidades](#roles-y-funcionalidades)
4. [Casos de uso](#casos-de-uso)
5. [Reglas de negocio](#reglas-de-negocio)
6. [Arquitectura y estructura del proyecto](#arquitectura-y-estructura-del-proyecto)
7. [Puesta en marcha](#puesta-en-marcha)
8. [Usuarios de prueba](#usuarios-de-prueba)
9. [Tests](#tests)

---

## Tecnologías

| Área | Tecnología |
| --- | --- |
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.3.12 (Spring MVC) |
| Vistas | Thymeleaf + `thymeleaf-extras-springsecurity6`, Bootstrap, jQuery |
| Persistencia | Spring Data JPA (Hibernate) + MySQL (`mysql-connector-j` 8.2.0) |
| Seguridad | Spring Security 6 (login por formulario, contraseñas con BCrypt, seguridad por métodos con `@PreAuthorize`) |
| Validación | Spring Validation (Jakarta Bean Validation) |
| Mapeo de objetos | ModelMapper 3.2.0 |
| Correo | Spring Mail (SMTP de Gmail) |
| PDF | OpenPDF 3.0.5 (recibos de sueldo) |
| Utilidades | Lombok |
| Build | Maven (incluye Maven Wrapper, Maven 3.9.9) |

---

## Modelo de dominio

![Diagrama de clases](uml/Diagrama%20Hito%202v3.jpg)

El diagrama fuente editable está en [`uml/Diagrama Hito 2v3.drawio`](uml/Diagrama%20Hito%202v3.drawio).

- **Festival**: tiene un período (`fechaInicio` – `fechaFin`), una temporada y un **Costo** asociado (1 a 1) con los valores base para calcular cánones y sueldos.
- **UnidadVenta** (abstracta): pertenece a un festival, tiene un responsable (`User`), un código único de 10 caracteres alfanuméricos (autogenerado al registrarla desde el formulario), empleados, platos y pedidos. Se especializa en:
  - **FoodTruck**: patente y si tiene conexión eléctrica.
  - **PuestoDesarmable**: cantidad de carpas y tiempo de montaje.
- **Empleado** (con baja lógica): se especializa en **Cajero** (turno `MANANA`/`NOCHE` y antigüedad) y **Cocinero** (categoría `COCINERO`, `COCINERO_AYUDANTE` o `LAVAPLATOS`, y tarea especial).
- **Plato**, **Pedido** e **ItemPedido**: el catálogo de cada unidad y las ventas realizadas.
- **User** y **UserRole**: cuentas de acceso. Cada usuario tiene exactamente un rol (`ADMIN`, `RESPONSABLE` o `EMPLEADO`).

La herencia de `Empleado` y de `UnidadVenta` se mapea con la estrategia `JOINED` de JPA.

---

## Roles y funcionalidades

El rol de cada usuario sale de `UserRole`. Además, los empleados que son **cajeros** reciben la autoridad adicional `CAJERO`, que habilita las pantallas de pedidos y cierre de caja.

### Visitante (sin sesión)

| Funcionalidad | Ruta |
| --- | --- |
| Inicio con los festivales activos | `/` · `/index` |
| Registrar una unidad de venta junto con su usuario responsable | `/unidadVenta/formulariounidadventa` |
| Iniciar sesión | `/login` |

### Administrador (`ADMIN`)

| Funcionalidad | Ruta |
| --- | --- |
| Ver festivales activos y finalizados | `/index` |
| Crear un festival con sus costos | `/festival/new` |
| Unidades de venta de un festival | `/festival/{id}/unidades` · `/festival/{id}/unidades/lista` |
| Ranking de unidades por recaudación | `/festival/{id}/ranking` |
| Unidades con mayor canon (parámetro `x`, por defecto 3) | `/festival/{id}/mayores-canon?x=3` |

### Responsable de unidad (`RESPONSABLE`)

| Funcionalidad | Ruta |
| --- | --- |
| Listado de empleados con filtros combinables, activación y desactivación | `/responsable/empleados` |
| Alta y edición de empleados | `/empleado/new` · `/empleado/edit/{id}` |
| Listado y alta de platos | `/responsable/platos` · `/responsable/platos/nuevo` |
| Menú de reportes | `/responsable/reportes` |
| Canon de la unidad | `/responsable/canon` |
| Sueldos (haberes) por período | `/responsable/haberes` |
| Recaudación por plato | `/responsable/recaudacion` |
| Rentabilidad por período | `/responsable/rentabilidad` |
| Plato estrella | `/responsable/plato-estrella` |

### Empleado (`EMPLEADO`)

| Funcionalidad | Ruta |
| --- | --- |
| Mi información y unidad asignada | `/empleado/mi-informacion` |
| Mi recibo de sueldo | `/empleado/sueldo` |
| Descargar el recibo en PDF | `/empleado/sueldo/pdf` |

### Cajero (empleado con autoridad `CAJERO`)

| Funcionalidad | Ruta |
| --- | --- |
| Registrar un pedido | `/pedido/new` |
| Listar pedidos por día y buscar por número | `/pedido` |
| Cierre de caja por fecha | `/empleado/cierre-caja` |

---

## Casos de uso

Cada caso de uso se desarrolló en una rama propia, con el formato `CU<NombreDelCasoDeUso><Integrante>`, y se integró al proyecto mediante un Pull Request.

### Denis Franco Emanuel Flores
### Ivan Tolaba
### Diego Olmedo
- [CUTraerEmpleadoUnidadVentaDiegoOlmedo](https://github.com/fra1010/TPGrupalOO2Hito2Grupo01/tree/CUTraerEmpleadoUnidadVentaDiegoOlmedo)
- [CUCalcularHaberesDiegoOlmedo](https://github.com/fra1010/TPGrupalOO2Hito2Grupo01/tree/CUCalcularHaberesDiegoOlmedo)
- [CUAltaUnidadVentaDiegoOlmedo](https://github.com/fra1010/TPGrupalOO2Hito2Grupo01/tree/CUAltaUnidadVentaDiegoOlmedo)
- [CUReporteCanonDiegoOlmedo](https://github.com/fra1010/TPGrupalOO2Hito2Grupo01/tree/CUReporteCanonDiegoOlmedo)
- [CUCalcularHaberesEmpleadosUnidadVentaDiegoOlmedo](https://github.com/fra1010/TPGrupalOO2Hito2Grupo01/tree/CUCalcularHaberesEmpleadosUnidadVentaDiegoOlmedo)
- [CUSueldoEmpleadoMasimpresionPDFDiegoOlmedo](https://github.com/fra1010/TPGrupalOO2Hito2Grupo01/tree/CUSueldoEmpleadoMasimpresionPDFDiegoOlmedo)
- [CUReporteRecaudacionDiegoOlmedo](https://github.com/fra1010/TPGrupalOO2Hito2Grupo01/tree/CUReporteRecaudacionDiegoOlmedo)



---

## Reglas de negocio

**Canon de una unidad**

- Base: `superficie × costo por superficie` del festival.
- Food truck con conexión eléctrica: se suma el plus de electricidad.
- Puesto desarmable: se resta `tiempo de montaje × costo de montaje`.

**Sueldos**

- Solo se liquidan empleados activos, dentro del período que se superpone con el festival.
- Las quincenas se calculan según los días del período: hasta 15 días cuentan 1, hasta 30 días cuentan 2 y, por encima de eso, `⌊días / 15⌋ + 1`.
- Total a pagar: `quincenas × 0,5 × (sueldo base + plus)`.
- El plus del cajero es `antigüedad × plus por antigüedad`. El del cocinero depende de su categoría (cocinero, ayudante o lavaplatos).
- Si el período incluye fechas posteriores a hoy, los importes se muestran como proyección.

**Rentabilidad**

- `ingresos − costo de los platos vendidos − sueldos − canon proporcional al período`.
- El canon proporcional es `canon total × días del período / días del festival`.

**Pedidos**

- Solo pueden registrarse con la fecha actual y dentro del período del festival.
- Deben tener al menos un ítem, con cantidad mayor a cero y sin repetir el mismo plato.
- Los platos tienen que estar activos y pertenecer a la unidad del cajero.
- El total de cada ítem es `precio de venta × cantidad`.

**Empleados**

- El alta genera una contraseña aleatoria de 12 caracteres y la envía por correo al email del empleado, que pasa a ser su usuario. Si el envío falla, el alta no se realiza.
- El DNI es único, incluso entre empleados inactivos. Para volver a incorporar a uno inactivo hay que activarlo desde el listado.
- Las bajas son lógicas: se desactiva el empleado y se deshabilita su cuenta de usuario.
- Un responsable solo puede gestionar empleados de su propia unidad.

**Otras**

- La fecha de fin de un festival no puede ser anterior a la de inicio.
- El cierre de caja no admite fechas futuras.
- El plato estrella es el más vendido de la unidad; ante empate se ordena por nombre.

---

## Arquitectura y estructura del proyecto

Aplicación en capas: **controllers → services → repositories → entities**, con DTOs para transportar datos hacia y desde las vistas.

```
.
├── pom.xml
├── mvnw / mvnw.cmd              # Maven Wrapper
├── sql/
│   └── script.sql               # Datos de prueba (ver "Puesta en marcha")
├── uml/                         # Diagrama de clases (.drawio y .jpg)
└── src/
    ├── main/
    │   ├── java/com/unla/TPGrupalOO2Hito2Grupo01/
    │   │   ├── configuration/   # SecurityConfiguration, ModelMapperConfig
    │   │   ├── controllers/     # Home, User, Festival, UnidadVenta, Empleado,
    │   │   │                    # Responsable, Plato, Pedido
    │   │   ├── dtos/            # DTOs de formularios y reportes
    │   │   ├── entities/        # Entidades JPA y enums
    │   │   ├── helpers/         # ViewRouteHelper (nombres de vistas y rutas)
    │   │   ├── repositories/    # Spring Data JPA (consultas JPQL con proyección a DTO)
    │   │   ├── services/        # Interfaces I*
    │   │   │   └── implementation/
    │   │   └── utilidades/      # ReciboPdfService (generación de PDF)
    │   └── resources/
    │       ├── application.yml
    │       ├── static/          # CSS, JS, Bootstrap y jQuery
    │       └── templates/       # Vistas Thymeleaf por módulo
    └── test/                    # Test de carga del contexto
```

---

## Puesta en marcha

### Requisitos

- JDK 21
- MySQL 8
- Una cuenta de Gmail con una contraseña de aplicación, necesaria para que el alta de empleados pueda enviar las credenciales por correo. Sin correo configurado no se pueden dar de alta empleados.
- Maven no hace falta instalarlo: el proyecto incluye `mvnw`.

### 1. Crear la base de datos

El script de datos de prueba apunta a una base llamada `test`:

```sql
CREATE DATABASE test CHARACTER SET utf8mb4;
```

### 2. Configurar las variables de entorno

`application.yml` no contiene credenciales: las toma del entorno.

| Variable | Descripción |
| --- | --- |
| `DB_URL` | URL JDBC de la base, por ejemplo `jdbc:mysql://localhost:3306/test` |
| `DB_USERNAME` | Usuario de MySQL |
| `DB_PASSWORD` | Contraseña de MySQL |
| `MAIL_USERNAME` | Cuenta de Gmail desde la que se envían los correos |
| `MAIL_PASSWORD` | Contraseña de aplicación de esa cuenta |

Linux / macOS:

```bash
export DB_URL="jdbc:mysql://localhost:3306/test"
export DB_USERNAME="root"
export DB_PASSWORD="tu_password"
export MAIL_USERNAME="tu_cuenta@gmail.com"
export MAIL_PASSWORD="tu_password_de_aplicacion"
```

Windows (PowerShell):

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/test"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="tu_password"
$env:MAIL_USERNAME="tu_cuenta@gmail.com"
$env:MAIL_PASSWORD="tu_password_de_aplicacion"
```

> El `.gitignore` excluye `src/main/resources/application-local.yml`, así que también podés guardar tu configuración local en ese archivo y ejecutar con el perfil `local` (`--spring.profiles.active=local`) sin riesgo de subir credenciales.

### 3. Crear las tablas

Hibernate genera el esquema automáticamente (`ddl-auto: update`). Iniciá la aplicación una vez y **detenela** cuando haya arrancado:

```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

### 4. Cargar los datos de prueba (opcional)

Ejecutá **completo** el archivo [`sql/script.sql`](sql/script.sql) en MySQL Workbench, con la aplicación detenida. Tené en cuenta lo que indica el propio script:

- Carga los datos mediante un procedimiento almacenado, por lo que el usuario de MySQL necesita permisos `CREATE ROUTINE`, `EXECUTE` y `ALTER ROUTINE`.
- Las tablas deben estar vacías y ser InnoDB (las crea Hibernate en el paso 3). El script no borra datos: si encuentra filas previas, cancela la carga.
- Ante cualquier error SQL revierte todo (`ROLLBACK`) y propaga el error. Conviene configurar Workbench para que detenga la ejecución ante errores y no correr el script por partes.
- Al terminar elimina el procedimiento e incluye consultas de verificación (conteos y controles de consistencia) para revisar que la carga quedó bien.

Contenido cargado:

- 2 festivales (uno activo y otro finalizado), cada uno con su costo.
- 10 unidades de venta, 5 por festival, cada una con su responsable.
- 51 usuarios y 51 roles.
- 40 empleados, todos activos y con cuenta: por unidad, 1 cajero y 3 cocineros (cocinero, ayudante y lavaplatos).
- 50 platos (5 por unidad).
- 4.900 pedidos y 9.800 ítems: 10 pedidos por día por unidad, con 2 ítems por pedido. En el festival de primavera se cargaron pedidos solo del 01/10 al 05/11 (36 días por unidad → 1.800 pedidos en las unidades 1–5); en el de invierno cubren todo el período (62 días por unidad → 3.100 pedidos en las unidades 6–10).

> Los pedidos del festival de primavera van del 01/10 al 05/11/2026 inclusive (el script rechaza pedidos posteriores a esa fecha), y los del festival de invierno cubren todo su período. Desde la aplicación, los pedidos solo se pueden registrar con la fecha del día.

### 5. Ejecutar la aplicación

```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

La aplicación queda disponible en <http://localhost:8080>.

Para generar el JAR ejecutable:

```bash
./mvnw clean package
java -jar target/TPGrupalOO2Hito2Grupo01-0.0.1-SNAPSHOT.jar
```

> Las consultas de sueldos usan la función `datediff` de MySQL, por lo que la aplicación está pensada para correr sobre MySQL.

---

## Usuarios de prueba

Disponibles al cargar `sql/script.sql`. **La contraseña de todos los usuarios es `1234`.**

### Cómo ingresar

1. Iniciá la aplicación (ver [Puesta en marcha](#puesta-en-marcha)) y abrí <http://localhost:8080/login>.
2. Completá los campos **Usuario** y **Contraseña** con alguno de los ejemplos de la tabla siguiente.
3. Al ingresar, el sistema te lleva al inicio y el menú muestra solo las opciones que corresponden al rol del usuario.

### Ejemplos de acceso por tipo de usuario

| Tipo de usuario | Usuario | Contraseña | Qué puede hacer |
| --- | --- | --- | --- |
| Administrador | `admin` | `1234` | Ver festivales activos y finalizados, crear festivales y consultar unidades, ranking y mayores cánones |
| Responsable | `responsable1` | `1234` | Gestionar los empleados y platos de su unidad (Burger Wheels) y consultar sus reportes |
| Cajero | `cajero.uv01@tp.test` | `1234` | Registrar y listar pedidos, hacer el cierre de caja, y ver su información y su recibo de sueldo |
| Cocinero | `cocinero.uv01@tp.test` | `1234` | Ver su información y su recibo de sueldo (con descarga en PDF) |
| Ayudante de cocina | `ayudante.uv01@tp.test` | `1234` | Ver su información y su recibo de sueldo (con descarga en PDF) |
| Lavaplatos | `lavaplatos.uv01@tp.test` | `1234` | Ver su información y su recibo de sueldo (con descarga en PDF) |

Los ejemplos pertenecen a la **unidad 1** (Burger Wheels). Para probar otra unidad, cambiá el número del usuario: `responsable2`, `cajero.uv02@tp.test`, `cocinero.uv03@tp.test`, y así hasta la 10.

> **Para registrar pedidos**, usá un cajero de las unidades 1 a 5, que pertenecen al festival activo (primavera, hasta el 30/11/2026). El sistema solo acepta pedidos con la fecha del día y dentro del período del festival, por lo que con las unidades 6 a 10 (invierno, finalizado) el alta de pedidos se rechaza.

### Todos los usuarios cargados

| Usuario | Rol | Descripción |
| --- | --- | --- |
| `admin` | `ADMIN` | Administrador del sistema |
| `responsable1` … `responsable10` | `RESPONSABLE` | Responsable de la unidad de venta con el mismo número |
| `cajero.uv01@tp.test` … `cajero.uv10@tp.test` | `EMPLEADO` + `CAJERO` | Cajero de cada unidad |
| `cocinero.uvNN@tp.test` | `EMPLEADO` | Cocinero (`NN` = 01 a 10) |
| `ayudante.uvNN@tp.test` | `EMPLEADO` | Ayudante de cocina |
| `lavaplatos.uvNN@tp.test` | `EMPLEADO` | Lavaplatos |

**Festivales cargados**

| Festival | Período | Estado | Unidades | Pedidos cargados |
| --- | --- | --- | --- | --- |
| Festival Gastronómico Primavera 2026 | 01/10/2026 – 30/11/2026 | Activo | 1 a 5 | 01/10 al 05/11/2026 |
| Festival Gastronómico Invierno 2026 | 01/07/2026 – 31/08/2026 | Finalizado | 6 a 10 | Todo el período |

**Costos cargados**

Son los valores de referencia del enunciado multiplicados por 4,5 (primavera) y 4,3 (invierno).

| Concepto | Primavera | Invierno |
| --- | --- | --- |
| Costo por superficie | 2.250 | 2.150 |
| Costo de montaje | 45 | 43 |
| Plus por electricidad | 9.000 | 8.600 |
| Sueldo base | 450.000 | 430.000 |
| Plus por antigüedad | 22.500 | 21.500 |
| Plus cocinero | 675.000 | 645.000 |
| Plus ayudante | 450.000 | 430.000 |
| Plus lavaplatos | 225.000 | 215.000 |

**Unidades de venta**

| N.º | Código | Nombre | Tipo |
| --- | --- | --- | --- |
| 1 | `A7K2M9P4X1` | Burger Wheels | Food truck (con conexión eléctrica) |
| 2 | `B8L3N0Q5Y2` | Tacos al Paso | Food truck |
| 3 | `C9M4P1R6Z3` | Parrilla del Sur | Puesto desarmable |
| 4 | `D0N5Q2S7A4` | Dulce Festival | Puesto desarmable |
| 5 | `E1P6R3T8B5` | Sabores del Mundo | Food truck (con conexión eléctrica) |
| 6 | `F2Q7S4U9C6` | Burger de Invierno | Food truck |
| 7 | `G3R8T5V0D7` | Tacos de Invierno | Food truck (con conexión eléctrica) |
| 8 | `H4S9U6W1E8` | Parrilla de Invierno | Puesto desarmable |
| 9 | `J5T0V7X2F9` | Dulces de Invierno | Puesto desarmable |
| 10 | `K6U1W8Y3G0` | Sabores de Invierno | Food truck |

Para generar el hash BCrypt de otra contraseña se puede ejecutar la clase `TestBCryptPasswordEncoder`, incluida en el proyecto.

---

## Tests

```bash
./mvnw test
```

Actualmente hay un único test (`contextLoads`) que verifica que el contexto de Spring arranca. Como levanta toda la aplicación, requiere tener definidas las variables de entorno de base de datos y correo.
