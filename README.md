# Sistema de Delivery Multiagente con JADE

Sistema distribuido de gestion de pedidos de delivery implementado con JADE (Java Agent DEvelopment Framework).

## Participantes
- Ashly Chavez 22200012
- Ashly Surichaqui 22200207
- Marilu Huarcaya 22200122
- Monica Mendoza 22200059
## Descripcion

Este proyecto simula un sistema de delivery donde agentes autonomos colaboran para gestionar pedidos desde la solicitud hasta la entrega. Los agentes se comunican mediante mensajes ACL siguiendo el estandar FIPA.

## Arquitectura

El sistema consta de 4 agentes:

- **Cliente**: Crea pedidos y los envia al coordinador
- **Coordinador**: Gestiona peticiones y asigna tareas
- **Restaurante**: Prepara los pedidos solicitados
- **Repartidor**: Entrega los pedidos al cliente

Flujo general: Cliente → Coordinador → Restaurante → Repartidor → Cliente

## Agente Cliente

El Agente Cliente es responsable de:
- Crear pedidos con ID, plato y direccion
- Buscar el servicio de coordinacion en Yellow Pages (DF)
- Enviar mensajes REQUEST al coordinador
- Recibir confirmacion del pedido

## Agente Restaurante

El Agente Restaurante se encarga de:
- Registrarse como **"servicio-restaurante"** en Yellow Pages  
- Mantener un **menú hardcodeado** con tiempos de preparación y precios:  
  - Pizza: 15 seg, $25  
  - Hamburguesa: 10 seg, $18  
  - Sushi: 20 seg, $35  
- Recibir pedidos desde el Coordinador  
- Simular la preparación de los platos (esperar X segundos según el tiempo del menú)  
- Buscar agentes **Repartidor** en Yellow Pages  
- Enviar un mensaje INFORM al Repartidor con los detalles del pedido   

## Agente Repartidor

El Agente Repartidor se encarga de:
- Registrarse como "servicio-repartidor" en Yellow Pages
- Recibir pedidos listos desde el Restaurante
- Calcular distancia, costo de envío y tiempo estimado
- Calcular el total a pagar
- Enviar la confirmación final al Cliente

Zonas de entrega:
- Zona Norte: 5 km, $10
- Zona Centro: 2 km, $5
- Zona Sur: 8 km, $15

## Estructura del Proyecto
```
delivery-jade/
├── src/
│   ├── Main.java
│   └── agentes/
│       ├── ClienteAgent.java
│       ├── CoordinadorAgent.java
│       ├── YellowPagesManager.java
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
