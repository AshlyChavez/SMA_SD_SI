import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.*;
import jade.wrapper.AgentController;

public class Main {
    public static void main(String[] args) {
        try {
            // 1️⃣ Runtime de JADE
            Runtime rt = Runtime.instance();

            // 2️⃣ Perfil con GUI
            Profile p = new ProfileImpl();
            p.setParameter(Profile.GUI, "true");

            // 3️⃣ Contenedor principal
            AgentContainer contenedor = rt.createMainContainer(p);

            // 4️⃣ AGENTE COORDINADOR (Persona 2)
            AgentController coordinador = contenedor.createNewAgent(
                    "Coordinador1",
                    "agentes.CoordinadorAgent",
                    null
            );
            coordinador.start();

            // 5️⃣ AGENTE CLIENTE (Persona 1)
            AgentController cliente = contenedor.createNewAgent(
                    "Cliente1",
                    "agentes.ClienteAgente",
                    new Object[]{"PED001", "Pizza Margarita", "Calle Falsa 123"}
            );
            cliente.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}