//package paquete.agentes;
//
//import jade.core.Agent;
//
//public class AgentePaciente extends Agent {
//    protected void setup() {
//        System.out.println("Agente Paciente listo.");
//    }
//}
package paquete.agentes;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgentePaciente extends Agent {

    private AID agenteNutricionistaAID; // Direccion del nutricionista
    private AID agentePredictorAID;     // Direccion del predictor

    @Override
    protected void setup() {
        System.out.println("[Paciente] -> Agente " + getLocalName() + " iniciado.");

        // 1. Buscar a los agentes de servicio en las paginas amarillas
        buscarAgentesServicio();

        // 2. Activar el comportamiento para recibir respuestas de la comunicacion
        addBehaviour(new EscucharRespuestas());
        
        // --- PROXIMA CONEXION CON LA GUI DE ISA ---
        // Aqui mas adelante añadiremos una linea para que este agente abra 
        // de forma automatica la VentanaPaciente de Isa pasandole su propia referencia.
    }

    @Override
    protected void takeDown() {
        System.out.println("[Paciente] -> Agente " + getLocalName() + " finalizado.");
    }

    // Metodo para localizar al Nutricionista y al Predictor en el DF
    private void buscarAgentesServicio() {
        // Busqueda del Nutricionista
        DFAgentDescription templateNutri = new DFAgentDescription();
        ServiceDescription sdNutri = new ServiceDescription();
        sdNutri.setType("calculo-nutricional");
        templateNutri.addServices(sdNutri);

        // Busqueda del Predictor
        DFAgentDescription templatePred = new DFAgentDescription();
        ServiceDescription sdPred = new ServiceDescription();
        sdPred.setType("prediccion-diabetes");
        templatePred.addServices(sdPred);

        try {
            // Buscamos Nutricionista
            DFAgentDescription[] resultNutri = DFService.search(this, templateNutri);
            if (resultNutri.length > 0) {
                agenteNutricionistaAID = resultNutri[0].getName();
                System.out.println("[Paciente] -> Agente Nutricionista localizado.");
            }

            // Buscamos Predictor
            DFAgentDescription[] resultPred = DFService.search(this, templatePred);
            if (resultPred.length > 0) {
                agentePredictorAID = resultPred[0].getName();
                System.out.println("[Paciente] -> Agente Predictor localizado.");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    /**
     * Metodo publico que llamara Isa desde el boton de su ventana (Swing).
     * Este metodo inicia la cadena de mensajes mandando los datos al Nutricionista.
     */
    public void solicitarAnalisis(String datosFormulario) {
        if (agenteNutricionistaAID != null) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(agenteNutricionistaAID);
            msg.setContent(datosFormulario);
            send(msg);
            System.out.println("[Paciente] -> Enviando datos iniciales al Nutricionista...");
        } else {
            System.out.println("[Paciente] -> Error: No se puede enviar, Nutricionista no disponible.");
        }
    }

    // Bucle para capturar las respuestas que vayan volviendo de los otros agentes
    private class EscucharRespuestas extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if (msg != null) {
                String remitente = msg.getSender().getLocalName();
                System.out.println("[Paciente] -> Respuesta recibida de: " + remitente);

                // CASO A: Nos responde el Nutricionista con sus calculos hechos
                if (msg.getSender().equals(agenteNutricionistaAID)) {
                    System.out.println("[Paciente] -> El Nutricionista dice: " + msg.getContent());
                    
                    // Ahora que tenemos los datos nutricionales, se los mandamos al Predictor (IA)
                    if (agentePredictorAID != null) {
                        ACLMessage msgPred = new ACLMessage(ACLMessage.REQUEST);
                        msgPred.addReceiver(agentePredictorAID);
                        // Pasamos los datos combinados
                        msgPred.setContent(msg.getContent() + " + Datos Clinicos"); 
                        myAgent.send(msgPred);
                        System.out.println("[Paciente] -> Enviando expediente completo al Predictor...");
                    }
                }
                
                // CASO B: Nos responde el Predictor con el veredicto final de Weka
                else if (msg.getSender().equals(agentePredictorAID)) {
                    String veredictoFinal = msg.getContent();
                    System.out.println("[Paciente] -> El Predictor (IA) determina: " + veredictoFinal);
                    
                    // --- CONEXION CON LA GUI DE ISA ---
                    // Aqui es donde diremos: ventana.mostrarResultado(veredictoFinal);
                    // Para que salte el pop-up en la pantalla del usuario.
                }
            } else {
                block();
            }
        }
    }
}