package paquete.agentes;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import paquete.ia.GestorWeka; 

public class AgentePredictor extends Agent {

    private AID agenteNotificadorAID;
    private GestorWeka motorIA; // Guardamos el gestor de Weka

    @Override
    protected void setup() {
        System.out.println("[Predictor] -> Agente " + getLocalName() + " iniciado.");

        // Inicializamos y entrenamos el modelo de Weka de Pablo al arrancar
        motorIA = new GestorWeka();
        motorIA.entrenarModelo();

        // Registro en las paginas amarillas
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("prediccion-diabetes");
        sd.setName("ServicioPrediccionJADE");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println("[Predictor] -> Registrado en el DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Buscamos al notificador
        buscarAgenteNotificador();

        addBehaviour(new EscucharPeticionesPrediccion());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println("[Predictor] -> Eliminado del DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private void buscarAgenteNotificador() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("notificacion-alertas");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                agenteNotificadorAID = result[0].getName();
                System.out.println("[Predictor] -> Agente Notificador encontrado.");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private class EscucharPeticionesPrediccion extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    
                    String datosPaciente = msg.getContent();
                    
                    // Separamos el texto recibido(11 campos)
                    String[] partes = datosPaciente.split(",");
                    
                    String nombre = partes[0];
                    double edad = Double.parseDouble(partes[1]);
                    
                    // Extraemos solo las glucosas (ignoramos los carbohidratos en las posiciones 3, 6 y 9)
                    double glucosaAyunas = Double.parseDouble(partes[2]);
                    double glucosaPostDesayuno = Double.parseDouble(partes[4]);
                    double glucosaComida = Double.parseDouble(partes[5]);
                    double glucosaPostComida = Double.parseDouble(partes[7]);
                    double glucosaCena = Double.parseDouble(partes[8]);
                    double glucosaPostCena = Double.parseDouble(partes[10]);

                    System.out.println("[Predictor] -> Consultando a Weka para el paciente " + nombre + " con sus 6 tomas de glucosa...");

                    // Llamada a la ia
                    boolean esRiesgoAlto = motorIA.predecirRiesgo(edad, glucosaAyunas, glucosaPostDesayuno, glucosaComida, glucosaPostComida, glucosaCena, glucosaPostCena);

                    // Preparamos la respuesta de vuelta al AgentePaciente
                    ACLMessage respuesta = msg.createReply();
                    respuesta.setPerformative(ACLMessage.INFORM);

                    if (esRiesgoAlto) {
                        respuesta.setContent("Resultado para " + nombre + ": ALTO RIESGO DE DIABETES");
                        
                        // Si da alto riesgo, mandamos la alerta urgente al Notificador
                        if (agenteNotificadorAID != null) {
                            ACLMessage mensajeAlerta = new ACLMessage(ACLMessage.INFORM);
                            mensajeAlerta.addReceiver(agenteNotificadorAID);
                            mensajeAlerta.setContent("Urgente: El paciente " + nombre + " presenta un riesgo alto de diabetes según criterios ADA.");
                            myAgent.send(mensajeAlerta);
                        }
                    } else {
                        respuesta.setContent("Resultado para " + nombre + ": RIESGO BAJO. Todo correcto.");
                    }

                    myAgent.send(respuesta);
                    System.out.println("[Predictor] -> Prediccion enviada al AgentePaciente.");
                }
            } else {
                block();
            }
        }
    }
}