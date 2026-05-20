package paquete.utils;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javax.swing.SwingUtilities;
import paquete.gui.VentanaPaciente;

/**
 * Clase principal de DiaPredict.
 * Inicializa la plataforma JADE, lanza los 4 agentes y abre la interfaz gráfica.
 *
 * @author Isa (paquete.utils / paquete.gui)
 */
public class Launcher {

    public static void main(String[] args) {

        // 1. Instancia única del Runtime de JADE
        Runtime runtime = Runtime.instance();

        // 2. Perfil de configuración: host local, puerto estándar 1099
        ProfileImpl profile = new ProfileImpl("localhost", 1099, null);

        // 3. Activar la GUI oficial de JADE para monitorizar agentes en tiempo real
        profile.setParameter(Profile.GUI, "true");

        // 4. Crear el contenedor principal de JADE
        AgentContainer mainContainer = runtime.createMainContainer(profile);

        try {
            // ------------------------------------------------------------------
            // AGENTES DEL PROYECTO
            // Alejandro / Marta: implementad la lógica dentro de cada clase
            // agente en paquete.agentes. El Launcher ya os los arranca
            // automáticamente al darle a Run.
            // ------------------------------------------------------------------

            // Agente principal: recibe los datos de la GUI y coordina el flujo
            AgentController agentePaciente = mainContainer.createNewAgent(
                "AgentePaciente",
                "paquete.agentes.AgentePaciente",
                null
            );

            // Agente nutricionista: analiza carbohidratos y glucosa
            AgentController agenteNutricionista = mainContainer.createNewAgent(
                "AgenteNutricionista",
                "paquete.agentes.AgenteNutricionista",
                null
            );

            // Agente predictor: aplica el modelo Weka para predecir riesgo de diabetes
            AgentController agentePredictor = mainContainer.createNewAgent(
                "AgentePredictor",
                "paquete.agentes.AgentePredictor",
                null
            );

            // Agente notificador: comunica el resultado final al paciente
            AgentController agenteNotificador = mainContainer.createNewAgent(
                "AgenteNotificador",
                "paquete.agentes.AgenteNotificador",
                null
            );

            // 5. Arrancar todos los agentes
            agentePaciente.start();
            agenteNutricionista.start();
            agentePredictor.start();
            agenteNotificador.start();

            // 6. Abrir la ventana gráfica en el hilo de Swing
            // Le pasamos el AgentePaciente para que pueda enviarle los datos
            SwingUtilities.invokeLater(() -> {
                VentanaPaciente ventana = new VentanaPaciente(agentePaciente);
                ventana.setVisible(true);
            });

        } catch (StaleProxyException e) {
            System.err.println("[Launcher] Error al crear o arrancar los agentes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
