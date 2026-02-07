package agentes;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import java.util.HashMap;
import java.util.Map;

public class RestauranteAgent extends Agent {

    private Map<String, int[]> menu;
    private String nombreRestaurante;

    protected void setup() {
        Object[] args = getArguments();

        if (args != null && args.length >= 1) {
            nombreRestaurante = (String) args[0];

            if (args.length >= 2) {
                inicializarMenuEspecifico((String) args[1]);
            } else {
                inicializarMenuCompleto();
            }
        } else {
            nombreRestaurante = getLocalName();
            inicializarMenuCompleto();
        }


        System.out.println("\n===============================");
        System.out.println("Restaurante iniciado: " + nombreRestaurante);
        System.out.println("Menu:");

        if (menu.isEmpty()) {
            System.out.println("  (vacio)");
        } else {
            menu.forEach((plato, datos) ->
                    System.out.println("  " + plato + ": " + datos[0] + "s, $" + datos[1])
            );
        }

        System.out.println("===============================\n");

        YellowPagesManager.registrarServicio(
                this,
                "servicio-restaurante",
                nombreRestaurante
        );

        addBehaviour(new ProcesarPedidos());
    }

    // Menú completo (backup)
    private void inicializarMenuCompleto() {
        menu = new HashMap<>();
        menu.put("pizza", new int[]{15, 25});
        menu.put("hamburguesa", new int[]{10, 18});
        menu.put("sushi", new int[]{20, 35});
        menu.put("pasta", new int[]{12, 22});
    }


    // Menú específico según argumentos
    private void inicializarMenuEspecifico(String platosPermitidos) {
        menu = new HashMap<>();

        Map<String, int[]> menuMaestro = new HashMap<>();
        menuMaestro.put("pizza", new int[]{15, 25});
        menuMaestro.put("hamburguesa", new int[]{10, 18});
        menuMaestro.put("sushi", new int[]{20, 35});
        menuMaestro.put("pasta", new int[]{12, 22});

        String[] platos = platosPermitidos.split(",");
        for (String plato : platos) {
            plato = plato.trim().toLowerCase();
            if (menuMaestro.containsKey(plato)) {
                menu.put(plato, menuMaestro.get(plato));
            }
        }
    }

    private class ProcesarPedidos extends CyclicBehaviour {

        public void action() {
            ACLMessage msg = receive();

            if (msg == null) {
                block();
                return;
            }

            if (msg.getPerformative() != ACLMessage.REQUEST) {
                return;
            }

            String contenido = msg.getContent();
            String[] partes = contenido.split("\\|");

            String pedidoID = partes[0];
            String plato = partes[1].toLowerCase().trim();
            String direccion = partes[2];

            // LOG AGRUPADO
            StringBuilder log = new StringBuilder();
            log.append("\n[").append(nombreRestaurante).append("] Pedido recibido:\n");
            log.append("  ID: ").append(pedidoID).append("\n");
            log.append("  Plato solicitado: ").append(plato).append("\n");

            // Buscar plato
            String platoEncontrado = buscarPlato(plato);

            // ❌ No tenemos plato
            if (platoEncontrado == null) {
                log.append("  ✗ No tenemos: ").append(plato).append("\n");
                log.append("  (Ignorando pedido)\n");
                System.out.println(log.toString());
                return;
            }

            // ✅ Tenemos plato
            int tiempoPreparacion = menu.get(platoEncontrado)[0];
            int precio = menu.get(platoEncontrado)[1];

            log.append("  ✓ SÍ tenemos: ").append(platoEncontrado).append("\n");
            log.append("  Tiempo: ").append(tiempoPreparacion).append("s\n");
            log.append("  Precio: $").append(precio).append("\n");
            log.append("  Preparando ").append(platoEncontrado).append("...\n");

            System.out.println(log.toString());

            // Simular preparación
            try {
                Thread.sleep(tiempoPreparacion * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("[" + nombreRestaurante + "] ✓ Pedido listo!\n");

            // Buscar repartidor
            DFAgentDescription[] repartidores =
                    YellowPagesManager.buscarServicio(myAgent, "servicio-repartidor");

            if (repartidores == null || repartidores.length == 0) {
                System.out.println("[" + nombreRestaurante + "] ERROR: No hay repartidores disponibles\n");

                ACLMessage fallo = msg.createReply();
                fallo.setPerformative(ACLMessage.FAILURE);
                fallo.setContent("No hay repartidores disponibles");
                send(fallo);
                return;
            }

            // Enviar al repartidor
            AID repartidor = repartidores[0].getName();
            ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
            inform.addReceiver(repartidor);
            inform.setContent(pedidoID + "|" + platoEncontrado + "|" + direccion + "|" + precio);
            inform.setConversationId(msg.getConversationId());
            send(inform);

            System.out.println("[" + nombreRestaurante + "] Pedido enviado a repartidor: "
                    + repartidor.getLocalName() + "\n");
        }
    }

    // Buscar plato en menú
    private String buscarPlato(String platoSolicitado) {
        String platoLower = platoSolicitado.toLowerCase();
        for (String plato : menu.keySet()) {
            if (platoLower.contains(plato)) {
                return plato;
            }
        }
        return null;
    }

    protected void takeDown() {
        YellowPagesManager.desregistrar(this);
        System.out.println("Restaurante cerrado: " + nombreRestaurante);
    }
}