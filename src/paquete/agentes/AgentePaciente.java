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
    
    // guardamos la ventana de Isa
    private VentanaPaciente ventana; 

    @Override
    protected void setup() {
        System.out.println("[Paciente] -> Agente " + getLocalName() + " iniciado.");

        // Buscamos los agentes en las paginas amarillas
        buscarAgentesServicio();

        //  Activamos el buzon especial O2A para hablar con la GUI
        setEnabledO2ACommunication(true, 10);

        // Anadimos los dos comportamientos de escucha
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
     * Escucha cuando el boton de Isa nos mete datos
     */
    private class EscucharInterfaz extends CyclicBehaviour {
        @Override
        public void action() {
            // Sacamos el objeto del buzon O2A de JADE
            Object obj = myAgent.getO2AObject();
            
            if (obj != null) {
                Object[] datos = (Object[]) obj;
                
                // Extraemos las posiciones segun el nuevo array de Isa (11 campos)
                ventana = (VentanaPaciente) datos[0];
                String nombre = (String) datos[1];
                int edad = (int) datos[2];
                
                // Mañana
                double glucosaAyunas = (double) datos[3];
                double carbDesayuno = (double) datos[4];
                double glucosaPostDesayuno = (double) datos[5];
                
                // Tarde
                double glucosaComida = (double) datos[6];
                double carbComida = (double) datos[7];
                double glucosaPostComida = (double) datos[8];
                
                // Noche
                double glucosaCena = (double) datos[9];
                double carbCena = (double) datos[10];
                double glucosaPostCena = (double) datos[11];
                
                System.out.println("[Paciente] -> Recibidos datos diarios de la GUI. Paciente: " + nombre);
                
                // Solucion a la condicion de carrera: si no encontro agentes al arrancar, los busca ahora
                if (agenteNutricionistaAID == null || agentePredictorAID == null) {
                    buscarAgentesServicio();
                }
                
                // Iniciamos la cadena: mandamos al Nutricionista empaquetado
                if (agenteNutricionistaAID != null) {
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver(agenteNutricionistaAID);
                    
                    // Empaquetamos los 11 datos en una sola linea de texto separada por comas
                    String paqueteDatos = nombre + "," + edad + "," + 
                                          glucosaAyunas + "," + carbDesayuno + "," + glucosaPostDesayuno + "," + 
                                          glucosaComida + "," + carbComida + "," + glucosaPostComida + "," + 
                                          glucosaCena + "," + carbCena + "," + glucosaPostCena;
                                          
                    msg.setContent(paqueteDatos);
                    myAgent.send(msg);
                    System.out.println("[Paciente] -> Diario completo enviado al Nutricionista.");
                } else {
                    System.out.println("[Paciente] -> Error: Nutricionista no disponible.");
                }
            } else {
                block();
            }
        }
    }

    /**
     * Escucha las respuestas que vuelven por JADE
     */
    private class EscucharRespuestas extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if (msg != null) {
                // CASO A: Vuelve el informe del Nutricionista
                if (msg.getSender().equals(agenteNutricionistaAID)) {
                    System.out.println("[Paciente] -> El Nutricionista responde: Dieta analizada correctamente.");
                    
                    // Reenviamos el expediente entero al Predictor para que use Weka
                    if (agentePredictorAID != null) {
                        ACLMessage msgPred = new ACLMessage(ACLMessage.REQUEST);
                        msgPred.addReceiver(agentePredictorAID);
                        msgPred.setContent(msg.getContent()); // Lleva el paquete de datos original
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