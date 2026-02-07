package agentes;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;

public class YellowPagesManager {

    public static void registrarServicio(Agent agent, String tipo, String nombre) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipo);
        sd.setName(nombre);

        dfd.addServices(sd);

        try {
            DFService.register(agent, dfd);
            System.out.println(agent.getLocalName() + " registrado en DF como " + tipo);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    public static DFAgentDescription[] buscarServicio(Agent agent, String tipo) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipo);
        template.addServices(sd);

        try {
            return DFService.search(agent, template);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void desregistrar(Agent agent) {
        try {
            DFService.deregister(agent);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
