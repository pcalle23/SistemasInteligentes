package paquete.agentes;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import paquete.gui.VentanaPaciente;

public class AgentePaciente extends Agent {

    private AID agenteNutricionistaAID;
    private AID agentePredictorAID;
    
    // Aqui guardaremos la ventana de Isa para poder usarla al final
    private VentanaPaciente ventana; 

    @Override
    protected void setup() {
        System.out.println("[Paciente] -> Agente " + getLocalName() + " iniciado.");

        // Buscamos los agentes esclavos en las paginas amarillas
        buscarAgentesServicio();

        // IMPORTANTE: Activamos el buzon especial O2A para hablar con la GUI
        setEnabledO2ACommunication(true, 10);

        // Añadimos los dos comportamientos de escucha
        addBehaviour(new EscucharInterfaz());
        addBehaviour(new EscucharRespuestas());
    }

    @Override
    protected void takeDown() {
        System.out.println("[Paciente] -> Agente " + getLocalName() + " finalizado.");
    }

    private void buscarAgentesServicio() {
        DFAgentDescription templateNutri = new DFAgentDescription();
        ServiceDescription sdNutri = new ServiceDescription();
        sdNutri.setType("calculo-nutricional");
        templateNutri.addServices(sdNutri);

        DFAgentDescription templatePred = new DFAgentDescription();
        ServiceDescription sdPred = new ServiceDescription();
        sdPred.setType("prediccion-diabetes");
        templatePred.addServices(sdPred);

        try {
            DFAgentDescription[] resultNutri = DFService.search(this, templateNutri);
            if (resultNutri.length > 0) {
                agenteNutricionistaAID = resultNutri[0].getName();
                System.out.println("[Paciente] -> Agente Nutricionista localizado.");
            }

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
     * COMPORTAMIENTO 1: Escucha cuando el boton de Isa nos mete datos
     */
    private class EscucharInterfaz extends CyclicBehaviour {
        @Override
        public void action() {
            // Sacamos el objeto del buzon O2A de JADE
            Object obj = myAgent.getO2AObject();
            
            if (obj != null) {
                Object[] datos = (Object[]) obj;
                
                // Extraemos las posiciones segun el nuevo array de Isa
                ventana = (VentanaPaciente) datos[0];
                String nombre = (String) datos[1];
                int edad = (int) datos[2];
                double glucosa = (double) datos[3];
                double carbohidratos = (double) datos[4];
                
                System.out.println("[Paciente] -> Recibidos datos de la GUI. Paciente: " + nombre);
                
                // Iniciamos la cadena: mandamos los carbohidratos al Nutricionista
                if (agenteNutricionistaAID != null) {
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver(agenteNutricionistaAID);
                    
                    // Empaquetamos los datos en una linea de texto
                    msg.setContent(nombre + "," + edad + "," + glucosa + "," + carbohidratos);
                    myAgent.send(msg);
                    System.out.println("[Paciente] -> Datos enviados al Nutricionista.");
                } else {
                    System.out.println("[Paciente] -> Error: Nutricionista no disponible.");
                }
            } else {
                block();
            }
        }
    }

    /**
     * COMPORTAMIENTO 2: Escucha las respuestas que vuelven por JADE
     */
    private class EscucharRespuestas extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if (msg != null) {
                String remitente = msg.getSender().getLocalName();
                
                // CASO A: Vuelve el informe del Nutricionista
                if (msg.getSender().equals(agenteNutricionistaAID)) {
                    System.out.println("[Paciente] -> El Nutricionista responde: " + msg.getContent());
                    
                    // Reenviamos el expediente entero al Predictor para que use Weka
                    if (agentePredictorAID != null) {
                        ACLMessage msgPred = new ACLMessage(ACLMessage.REQUEST);
                        msgPred.addReceiver(agentePredictorAID);
                        msgPred.setContent(msg.getContent()); // Lleva los datos originales y el OK del nutri
                        myAgent.send(msgPred);
                        System.out.println("[Paciente] -> Expediente enviado al Predictor (IA).");
                    }
                }
                
                // CASO B: Vuelve el veredicto de la IA del Predictor
                else if (msg.getSender().equals(agentePredictorAID)) {
                    String veredictoFinal = msg.getContent();
                    System.out.println("[Paciente] -> IA determina resultado: " + veredictoFinal);
                    
                    // CONEXION CON LA GUI: Usamos la ventana guardada para pintar el pop-up
                    if (ventana != null) {
                        ventana.mostrarResultado(veredictoFinal);
                        System.out.println("[Paciente] -> Pop-up enviado a la pantalla.");
                    }
                }
            } else {
                block();
            }
        }
    }
}