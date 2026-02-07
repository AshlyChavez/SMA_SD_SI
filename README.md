# Sistema de Delivery Multiagente con JADE

Sistema distribuido de gestion de pedidos de delivery implementado con JADE (Java Agent DEvelopment Framework).

## Participantes
- Ashly Chavez 22200012
- 
## Descripcion

Este proyecto simula un sistema de delivery donde agentes autonomos colaboran para gestionar pedidos desde la solicitud hasta la entrega. Los agentes se comunican mediante mensajes ACL siguiendo el estandar FIPA.

## Arquitectura

El sistema consta de 4 agentes:

- **Cliente**: Crea pedidos y los envia al coordinador
- **Coordinador**: Gestiona peticiones y asigna tareas
- **Restaurante**: Prepara los pedidos solicitados
- **Repartidor**: Entrega los pedidos al cliente

## Agente Cliente

El Agente Cliente es responsable de:
- Crear pedidos con ID, plato y direccion
- Buscar el servicio de coordinacion en Yellow Pages (DF)
- Enviar mensajes REQUEST al coordinador
- Recibir confirmacion del pedido

## Estructura del Proyecto
```
delivery-jade/
├── src/
│   ├── Main.java
│   └── agentes/
│       ├── ClienteAgent.java
│       ├── CoordinadorAgent.java
│       ├── RestauranteAgent.java
│       └── RepartidorAgent.java
├── lib/
│   └── jade.jar
└── README.md
```

Parametros:
- Posicion 0: ID del pedido
- Posicion 1: Nombre del plato
- Posicion 2: Direccion de entrega

## Ejemplo de Salida
```
Coordinador iniciado
Coordinador registrado en Yellow Pages
Agente Cliente iniciado: Cliente1
Pedido creado:
   ID: PED001
   Plato: Pizza Margarita
   Direccion: Calle Falsa 123
Coordinador encontrado: Coordinador
Pedido enviado al coordinador
Pedido recibido: PED001|Pizza Margarita|Calle Falsa 123
Confirmacion enviada
CONFIRMACION: Pedido aceptado
Cliente satisfecho. Cerrando agente.
```
