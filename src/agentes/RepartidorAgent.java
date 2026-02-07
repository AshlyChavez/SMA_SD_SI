package agentes;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;

public class RepartidorAgent extends Agent {

    private Map<String, Zona> zonas;

    protected void setup() {
        System.out.println("🛵 Repartidor iniciado: " + getLocalName());

        inicializarZonas();

        YellowPagesManager.registrarServicio(
                this,
                "servicio-repartidor",
                "repartidor-delivery"
        );

        addBehaviour(new EntregarPedido());
    }

    private void inicializarZonas() {
        zonas = new HashMap<>();
        zonas.put("norte", new Zona("Zona Norte", 5, 10));
        zonas.put("centro", new Zona("Zona Centro", 2, 5));
        zonas.put("sur", new Zona("Zona Sur", 8, 15));
    }

    private class EntregarPedido extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();

            if (msg == null) {
                block();
                return;
            }

            if (msg.getPerformative() != ACLMessage.INFORM) return;

            String[] partes = msg.getContent().split("\\|");

            String pedidoID = partes[0];
            String plato = partes[1];
            String direccion = partes[2];
            int precioPlato = Integer.parseInt(partes[3]);

            // ⭐ MEJORA 1: confirmar recepción del pedido
            System.out.println("🛵 Pedido recibido para entrega: " + pedidoID);

            Zona zona = determinarZona(direccion);

            // ⭐ MEJORA 2: mostrar zona detectada
            System.out.println("📍 Zona detectada: " + zona.nombre);

            int tiempoEntrega = zona.distancia * 3;
            int total = precioPlato + zona.costo;

            String ticket = generarTicket(
                    pedidoID, plato, direccion,
                    precioPlato, zona, tiempoEntrega, total
            );

            System.out.println(ticket);

            ACLMessage respuesta = new ACLMessage(ACLMessage.INFORM);
            respuesta.addReceiver(msg.getSender()); // Cliente
            respuesta.setContent(ticket);
            send(respuesta);

            // ⭐ MEJORA 3: confirmar envío al cliente
            System.out.println("📨 Ticket enviado al cliente\n");
        }
    }

    private Zona determinarZona(String direccion) {
        String dir = direccion.toLowerCase();
        if (dir.contains("norte")) return zonas.get("norte");
        if (dir.contains("sur")) return zonas.get("sur");
        return zonas.get("centro"); // default
    }

    private String generarTicket(String id, String plato, String direccion,
                                 int precioPlato, Zona zona,
                                 int tiempo, int total) {

        return "\n================= TICKET =================\n" +
                "Repartidor: " + getLocalName() + "\n" +
                "Pedido: " + id + "\n" +
                "Plato: " + plato + "\n" +
                "Direccion: " + direccion + "\n\n" +
                "Zona: " + zona.nombre + "\n" +
                "Distancia: " + zona.distancia + " km\n" +
                "Envio: $" + zona.costo + "\n" +
                "Tiempo estimado: " + tiempo + " min\n\n" +
                "Precio plato: $" + precioPlato + "\n" +
                "TOTAL A PAGAR: $" + total + "\n" +
                "==========================================\n";
    }

    protected void takeDown() {
        YellowPagesManager.desregistrar(this);
        System.out.println("Repartidor finalizado");
    }

    private static class Zona {
        String nombre;
        int distancia;
        int costo;

        Zona(String nombre, int distancia, int costo) {
            this.nombre = nombre;
            this.distancia = distancia;
            this.costo = costo;
        }
    }
}
