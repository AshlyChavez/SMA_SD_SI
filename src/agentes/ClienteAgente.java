package agentes;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;

public class ClienteAgente extends Agent {

    private String pedidoID;
    private String plato;
    private String direccion;

    protected void setup() {
        System.out.println("Agente Cliente iniciado: " + getLocalName());

        Object[] args = getArguments();
        if (args != null && args.length == 3) {
            pedidoID = (String) args[0];
            plato = (String) args[1];
            direccion = (String) args[2];

            System.out.println("Pedido creado:");
            System.out.println("   ID: " + pedidoID);
            System.out.println("   Plato: " + plato);
            System.out.println("   Direccion: " + direccion);

            addBehaviour(new SolicitarPedido());
        } else {
            System.err.println("ERROR: Debe proporcionar [ID, plato, direccion]");
            doDelete();
        }
    }

    private class SolicitarPedido extends Behaviour {

        private AID coordinador;
        private int paso = 0;
        private boolean pedidoConfirmado = false;

        public void action() {
            switch (paso) {
                case 0:
                    coordinador = buscarCoordinador();
                    if (coordinador != null) {
                        System.out.println("Coordinador encontrado: " + coordinador.getName());
                        paso = 1;
                    } else {
                        System.out.println("Esperando coordinador...");
                        block(1000);
                    }
                    break;

                case 1:
                    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                    request.addReceiver(coordinador);
                    request.setContent(pedidoID + "|" + plato + "|" + direccion);
                    request.setConversationId("pedido-" + pedidoID);
                    send(request);
                    System.out.println("Pedido enviado al coordinador");
                    paso = 2;
                    break;

                case 2:
                    ACLMessage respuesta = receive();
                    if (respuesta != null) {
                        if (respuesta.getPerformative() == ACLMessage.INFORM) {
                            System.out.println("CONFIRMACION: " + respuesta.getContent());
                            pedidoConfirmado = true;
                        } else if (respuesta.getPerformative() == ACLMessage.FAILURE) {
                            System.out.println("ERROR: " + respuesta.getContent());
                        }
                        paso = 3;
                    } else {
                        block();
                    }
                    break;
            }
        }

        public boolean done() {
            return paso == 3;
        }

        public int onEnd() {
            if (pedidoConfirmado) {
                System.out.println("Cliente satisfecho. Cerrando agente.");
            }
            myAgent.doDelete();
            return super.onEnd();
        }
    }

    private AID buscarCoordinador() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("servicio-coordinacion");
        template.addServices(sd);

        try {
            DFAgentDescription[] resultados = DFService.search(this, template);
            if (resultados.length > 0) {
                return resultados[0].getName();
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void takeDown() {
        System.out.println("Agente Cliente terminado: " + getLocalName());
    }
}