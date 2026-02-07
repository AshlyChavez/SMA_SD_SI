import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.*;

public class Main {
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();
            Profile p = new ProfileImpl();
            p.setParameter(Profile.GUI, "true");

            AgentContainer contenedor = rt.createMainContainer(p);

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