package agentes;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

import java.util.*;

public class CoordinadorAgent extends Agent {



    protected void setup() {
        System.out.println("Agente Coordinador iniciado: " + getLocalName());

        // Registrar servicio
        YellowPagesManager.registrarServicio(
                this,
                "servicio-coordinacion",
                "coordinador-delivery"
        );

        addBehaviour(new RecibirPedidos());
    }

    private class RecibirPedidos extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {

                String contenido = msg.getContent();
                String[] partes = contenido.split("\\|");

                String pedidoID = partes[0];
                String plato = partes[1].toLowerCase();
                String direccion = partes[2];

                System.out.println("Pedido recibido: " + pedidoID + " - " + plato);

                DFAgentDescription[] restaurantes =
                        YellowPagesManager.buscarServicio(myAgent, "servicio-restaurante");

                if (restaurantes != null && restaurantes.length > 0) {

                    // Enviar a TODOS los restaurantes disponibles
                    // El primero que tenga el plato responderá
                    for (DFAgentDescription desc : restaurantes) {
                        AID restaurante = desc.getName();

                        ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
                        req.addReceiver(restaurante);
                        req.setContent(contenido);
                        req.setConversationId(msg.getConversationId());

                        send(req);
                    }

                    System.out.println("Pedido enviado a " + restaurantes.length + " restaurantes");

                } else {
                    ACLMessage fallo = msg.createReply();
                    fallo.setPerformative(ACLMessage.FAILURE);
                    fallo.setContent("No hay restaurantes disponibles");
                    send(fallo);
                }
            } else {
                block();
            }
        }
    }

    protected void takeDown() {
        YellowPagesManager.desregistrar(this);
        System.out.println("Coordinador terminado");
    }
}
