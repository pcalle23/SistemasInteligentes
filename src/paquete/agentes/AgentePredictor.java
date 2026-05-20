package paquete.agentes;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import paquete.ia.GestorWeka; // Conectamos con el codigo de Pablo

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
                    
                    // Separamos el texto recibido (nombre,edad,glucosa,carbohidratos)
                    String[] partes = datosPaciente.split(",");
                    String nombre = partes[0];
                    double edad = Double.parseDouble(partes[1]);
                    double glucosa = Double.parseDouble(partes[2]);
                    double carbohidratos = Double.parseDouble(partes[3]);

                    System.out.println("[Predictor] -> Consultando a Weka para el paciente " + nombre + "...");

                    // LLAMADA REAL A LA IA DE PABLO
                    boolean esRiesgoAlto = motorIA.predecirRiesgo(edad, glucosa, carbohidratos);

                    // Preparamos la respuesta de vuelta al AgentePaciente
                    ACLMessage respuesta = msg.createReply();
                    respuesta.setPerformative(ACLMessage.INFORM);

                    if (esRiesgoAlto) {
                        respuesta.setContent("Resultado para " + nombre + ": ALTO RIESGO DE DIABETES");
                        
                        // Si da alto riesgo, mandamos la alerta urgente al Notificador
                        if (agenteNotificadorAID != null) {
                            ACLMessage mensajeAlerta = new ACLMessage(ACLMessage.INFORM);
                            mensajeAlerta.addReceiver(agenteNotificadorAID);
                            mensajeAlerta.setContent("Urgente: El paciente " + nombre + " presenta un riesgo alto de diabetes.");
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