# 🚀 Prueba Técnica BTG Pactual

Este proyecto contiene la solución a la prueba técnica para el rol de **Ingeniero de Desarrollo Back End**, propuesta por **SETI**. El proyecto se divide en dos partes principales: la implementación de una **API REST** para la gestión de fondos y la resolución de consultas **SQL**.

## 🛠️ Tecnologías Utilizadas
- Java
- Maven
- Spring Boot
- MongoDB
- PostgreSQL
- Docker

## 📋 Requirimientos

- Java 17
- maven
- PostgreSQL
- MongoDB
- Docker

## 🐳 Levantar Contenedores necesarios

### ⚙️ Configuración y Ejecución

#### ⬇️ Clonar el repositorio:

>```shell
> git clone <URL_DEL_REPOSITORIO>
> cd <NOMBRE_DEL_REPOSITORIO>
>```

#### 🔧 Iniciar los servicios:

Ejecuta el siguiente comando en la raíz del proyecto para crear y arrancar los contenedores en segundo plano:

>```shell
> docker-compose up -d
>```

Este comando leerá el archivo **docker-compose.yml** y creará dos contenedores: uno para **PostgreSQL** y otro para **MongoDB**.

### 🧹 Eliminar contenedores y datos

En caso de que quieras detener y eliminar completamente el contenedor, junto con los volúmenes asociados (datos creados durante las pruebas), puedes usar:

>```shell
> docker-compose down --volumes --rmi all
>```

## 🧩 Parte 1: Fondos

Esta sección contiene el código correspondiente a la primera parte del desafío.
Este desarrollo fue realizado utilizando **Spring Boot 3.5.5**, **Java 17** y **MongoDB 6**.
A continuación, se presentan las funcionalidades del sistema, las reglas de negocio y las actividades solicitadas.

### Funcionalidades del sistema:

- [x] Suscribirse a un nuevo fondo (apertura).
- [x] Cancelar la suscripción a un fondo actual.
- [x] Ver historial de transacciones (aperturas y cancelaciones).
- [x] Enviar notificación por email o SMS según preferencia del usuario al suscribirse a un
  fondo.

### Reglas de negocio:
- [x] Monto inicial del cliente: COP $500.000.
- [x] Cada transacción debe tener un identificador único.
- [x] Cada fondo tiene un monto mínimo de vinculación.
- [x] Al cancelar una suscripción, el valor de vinculación se retorna al cliente.
- [x] Si no hay saldo suficiente, mostrar: “No tiene saldo disponible para vincularse al fondo <Nombre del fondo>”


### 💡Solución código

#### Endpoints de la API

A continuación, se listan los endpoints principales disponibles en esta aplicación, junto con una breve descripción de su funcionalidad.

##### 🔗 Autenticación (/api/auth)
| Método |  Endpoint  |                         Descripción                          |
|:------:|:----------:|:------------------------------------------------------------:|
|  POST  |  /login    | Autentica con email y contraseña, retorna JWT Bearer token   |

##### 🔗 Clientes (/api/clientes)
| Método |          Endpoint          |                         Descripción                          |
|:------:|:--------------------------:|:------------------------------------------------------------:|
|  POST  |             /              |   Crea un nuevo cliente con saldo inicial de $500.000 COP (público)    |
|  GET   | /{clienteId}/transacciones | Obtiene el historial de transacciones (requiere autenticación) | 

##### 🔗 Fondos (/api/fondos)
| Método |            Endpoint            |                                Descripción                                |
|:------:|:------------------------------:|:-------------------------------------------------------------------------:|
|  POST  |           /suscribir           |         Suscribirse a un fondo (requiere autenticación)                   |
|  POST  |           /cancelar            | Cancelar suscripción a un fondo (requiere autenticación)                  |

#### 🔒 Autenticación, Autorización y Roles

La API implementa **autenticación JWT**, **autorización por roles** y **encriptación de contraseñas** (BCrypt):

- **CLIENTE**: Solo puede gestionar sus propios datos (suscribirse, cancelar, ver su historial).
- **ADMIN**: Puede operar sobre cualquier cliente.

**Flujo de uso:**
1. Registrar cliente: `POST /api/clientes` con `password` (mínimo 6 caracteres).
2. Iniciar sesión: `POST /api/auth/login` con `email` y `password`.
3. Usar el token en las peticiones: `Authorization: Bearer <token>`.

#### 📮 Colección Postman

Se incluye una colección de Postman para ejecutar todos los endpoints:

- **Ubicación:** `BTG_Fondos_API.postman_collection.json`
- **Importar:** En Postman → File → Import → seleccionar el archivo JSON
- **Variables:** `baseUrl` (http://localhost:8080), `token` y `clienteId` se actualizan automáticamente
- **Flujo completo:** La carpeta "Flujo completo (Runner)" permite ejecutar todo el flujo con Collection Runner

#### ⚙️ Configuracion de notificaciones

Esta sección detalla cómo configurar los servicios de notificaciones de la aplicación, incluyendo las credenciales y ajustes para el envío de correos electrónicos y mensajes SMS.

##### 📧 Configuración de Email

Para el envío de correos electrónicos, la aplicación utiliza el servicio de email gratuito de **Mailtrap**. Es importante aclarar que, para que el envío sea válido, se requeriría un **dominio** propio, el cual no está configurado en este proyecto. Por lo tanto, con esta implementación, los correos no llegarán a cuentas personales.

En su lugar, **Mailtrap** intercepta todos los correos electrónicos salientes y los envía a una bandeja de entrada virtual (**Sandbox**). Los correos capturados se pueden visualizar directamente en la interfaz de **Mailtrap**. Esto es ideal para el desarrollo y las pruebas, ya que permite verificar el contenido y el formato de los correos sin enviarlos a usuarios reales.

Las propiedades clave que debes configurar en **application.properties** son:

- **spring.mail.host:** El host de SMTP proporcionado por Mailtrap.
- **spring.mail.port:** El puerto de SMTP.
- **spring.mail.username:** El nombre de usuario de tu bandeja de entrada de Mailtrap.
- **spring.mail.password:** La contraseña de tu bandeja de entrada.

>```properties
>spring.mail.host=sandbox.smtp.mailtrap.io
>spring.mail.port=587
>spring.mail.username=
>spring.mail.password=
>```

> [!NOTE]  
> Ten en cuenta que se puede utilizar cualquier servidor **SMTP**; sin embargo, para este ejemplo se utilizó **Mailtrap**.

##### 📱 Configuración de SMS

Para el envío de mensajes de texto (**SMS**), se ha integrado **Infobip** utilizando su plan gratuito de prueba. Es importante tener en cuenta que este plan ofrece 15 mensajes de texto gratuitos. Por lo tanto, considera esta limitación al momento de realizar las pruebas.

Las propiedades clave que debes configurar en **application.properties** son:

- **infobip.api.key:** La clave de tu API para autenticarte.
- **infobip.base.url:** La URL base de tu cuenta de Infobip.
- **infobip.sender:** El número de teléfono desde el cual se envían los mensajes.

>```properties
>infobip.api.key=
>infobip.base.url=
>infobip.sender=
>```

## 🧩 Parte 2: Consultas SQL

Esta sección contiene la consulta **SQL** para la segunda parte del desafío, basándose en el esquema de la base de datos. A continuación, se muestra el diagrama diseñado para representar dicha estructura.

>```mermaid
>erDiagram
>   cliente {
>       int id PK
>       varchar nombre
>       varchar apellidos
>       varchar ciudad
>   }
>
>   producto {
>       int id PK
>       varchar nombre
>       varchar tipoProducto
>   }
>
>   sucursal {
>       int id PK
>       varchar nombre
>       varchar ciudad
>   }
>
>   inscripcion {
>       int idProducto PK, FK
>       int idCliente PK, FK
>   }
>
>   disponibilidad {
>       int idSucursal PK, FK
>       int idProducto PK, FK
>   }
>
>   visitan {
>       int idSucursal PK, FK
>       int idCliente PK, FK
>       date fechaVisita
>   }
>
>   cliente ||--o{ inscripcion : "tiene"
>   cliente ||--o{ visitan : "visita"
>   producto ||--o{ inscripcion : "es inscrito en"
>   producto ||--o{ disponibilidad : "esta disponible en"
>   sucursal ||--o{ disponibilidad : "ofrece"
>   sucursal ||--o{ visitan : "es visitada por"
>```

### 🐘️Solución SQL

> [!IMPORTANT]
> Es necesario ejecutar el script **parte-2-schema-data.sql** para realizar la creación y población de las tablas requeridas para esta sección.

A continuación, se presentan los comandos necesarios para ejecutar el script mencionado anteriormente desde la terminal. También es posible ejecutarlo utilizando un cliente de base de datos.

#### 🐳 Ejecutar comandos sin Docker
>```shell
> psql -U <nombre_usuario> -d <nombre_bd> < <ruta_archivo>
>```

#### 🐳 Ejecutar comandos con Docker
>```shell
> docker exec -i <nombre_contenedor> psql -U <nombre_usuario> -d <nombre_bd> < <ruta_archivo>
>```


La siguiente consulta resuelve el problema de **obtener los nombres de los clientes que tienen inscrito algún producto disponible solo en las sucursales que visitan**.

>```sql
> SELECT DISTINCT C.nombre
> FROM cliente AS C
> JOIN inscripcion AS i ON c.id = i.idCliente
> JOIN disponibilidad AS d ON i.idProducto = d.idProducto
> JOIN visitan AS v ON v.idSucursal = d.idSucursal AND v.idCliente = c.id
> WHERE i.idProducto IN (
>         SELECT idProducto
>         FROM disponibilidad
>         GROUP BY idProducto
>         HAVING COUNT(idSucursal) = 1
>);
>```