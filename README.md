# API-REST-Spring-Productos

## Descripción
El API representa la gestión de un inventarios de productos cumpliendo con las operaciones básicas de un CRUD.
La información es obtenida desde una base de datos en MySQL. Además, incorporando spring-security, valida al usuario mediante un "login" y dependiendo del rol del usuario, éste puede realizar dichas tareas.

## Roles de Usuarios

### ADMIN
- Crear usuarios con roles: ADMIN, USER.
- Listar productos.
- Buscar producto por id.
- Crear productos.
- Modificar productos.
- Eliminar productos.

### USER
- Crear usuarios con rol: USER.
- Listar productos.
- Buscar producto por id.

## Endpoints y ejemplos usando Postman
> /login
  Permite iniciar sesión al usuario.

  Usuario o contraeña incorrecta:
  <img width="869" alt="Screen Shot 2024-01-09 at 23 17 36" src="https://github.com/JozRamirez10/API-REST-Spring-Productos/assets/101752395/c5396796-3a9c-40f7-8411-7c7ad2485297">

  Incio de sesión exitoso:
  <img width="869" alt="Screen Shot 2024-01-09 at 23 18 44" src="https://github.com/JozRamirez10/API-REST-Spring-Productos/assets/101752395/a4cc8305-b119-4074-a132-4b2790c32b7b">

> /api/users [GET]
  Lista de usuarios registrados (PUBLIC)
  <img width="869" alt="Screen Shot 2024-01-09 at 23 13 55" src="https://github.com/JozRamirez10/API-REST-Spring-Productos/assets/101752395/b186f328-c08b-4589-9f80-  5770fe2e58d3">

> /api/users [POST]
  Crea un nuevo usuario con la posibilidad de tener el rol ADMIN (ADMIN).
  <img width="869" alt="Screen Shot 2024-01-09 at 23 27 20" src="https://github.com/JozRamirez10/API-REST-Spring-Productos/assets/101752395/4f68b78d-e222-4082-909a-df8086260779">

> /api/users/register [GET]
  Crea un nuevo usuario con el rol USER (USER).
  <img width="869" alt="Screen Shot 2024-01-09 at 23 20 57" src="https://github.com/JozRamirez10/API-REST-Spring-Productos/assets/101752395/95aa8361-7b65-421d-a870-c87f06fb46cd">

> /api/products [GET]
  Lista de productos (ADMIN, USER).  
  <img width="869" alt="Screen Shot 2024-01-09 at 23 22 55" src="https://github.com/JozRamirez10/API-REST-Spring-Productos/assets/101752395/05775979-50f0-46ab-afd5-921bdcf705f2">

> /api/products [POST]
  Agregar nuevo producto (ADMIN).
  <img width="869" alt="Screen Shot 2024-01-09 at 23 29 29" src="https://github.com/JozRamirez10/API-REST-Spring-Productos/assets/101752395/8911068f-143c-42d0-994d-7a09b1c47c23">

> /api/products/{id} [GET]
  Obtiene el producto por ID (ADMIN, USER).
  <img width="869" alt="Screen Shot 2024-01-09 at 23 23 20" src="https://github.com/JozRamirez10/API-REST-Spring-Productos/assets/101752395/dc19c4fe-03c4-44eb-9b44-926f8655b67c">

> /api/products/{id} [PUT]
  Modifica el producto por ID (ADMIN).
  <img width="869" alt="Screen Shot 2024-01-09 at 23 31 41" src="https://github.com/JozRamirez10/API-REST-Spring-Productos/assets/101752395/e4dfa6e9-b379-4e2b-9a5d-5b8e59829e01">

> /api/products/{id} [DELETE]
  Elimina el producto por ID (ADMIN).
  <img width="869" alt="Screen Shot 2024-01-09 at 23 32 08" src="https://github.com/JozRamirez10/API-REST-Spring-Productos/assets/101752395/4b19020b-d218-465f-ad47-6c4d8056a6c2">
  Producto eliminado de la base de datos:
  <img width="355" alt="Screen Shot 2024-01-09 at 23 32 39" src="https://github.com/JozRamirez10/API-REST-Spring-Productos/assets/101752395/7dbdd218-1176-42e8-b573-42a046781c68">







