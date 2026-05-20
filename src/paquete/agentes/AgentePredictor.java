package paquete.agentes;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgentePredictor extends Agent {

    private AID agenteNotificadorAID; // Guardaremos aqui la direccion del notificador

    @Override
    protected void setup() {
        System.out.println("[Predictor] -> Agente " + getLocalName() + " iniciado.");

        // 1. Registro en las paginas amarillas para que el Paciente nos encuentre
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("prediccion-diabetes"); // Palabra clave de este agente
        sd.setName("ServicioPrediccionJADE");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println("[Predictor] -> Registrado en el DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // 2. Buscar al AgenteNotificador en las paginas amarillas
        // Lo hacemos al arrancar para tener su direccion guardada desde el principio
        buscarAgenteNotificador();

        // 3. Activar el comportamiento para escuchar al AgentePaciente
        addBehaviour(new EscucharPeticionesPrediccion());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println("❌ [Predictor] -> Eliminado del DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    // Metodo para buscar al notificador usando la palabra clave de su servicio
    private void buscarAgenteNotificador() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("notificacion-alertas"); // Buscamos este tipo exacto
        template.addServices(sd);

        try {
            // Buscamos en el DF agentes que cumplan la plantilla
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                agenteNotificadorAID = result[0].getName();
                System.out.println("[Predictor] -> Agente Notificador encontrado con exito.");
            } else {
                System.out.println("[Predictor] -> No se encontro ningun Agente Notificador libre.");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    // Bucle para atender las peticiones de prediccion
    private class EscucharPeticionesPrediccion extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if (msg != null) {
                System.out.println("[Predictor] -> Peticion recibida de: " + msg.getSender().getLocalName());

                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    String datosPaciente = msg.getContent();
                    System.out.println("[Predictor] -> Procesando datos con la IA: " + datosPaciente);

                    // --- CONEXION CON LA IA DE PABLO ---
                    // De momento simulamos el resultado de Weka como "ALTO" de forma fija.
                    // Cuando Pablo termine, aqui llamaremos a su clase GestorWeka.
                    boolean esRiesgoAlto = true; 

                    // Preparar respuesta para el AgentePaciente (el que nos pregunto)
                    ACLMessage respuesta = msg.createReply();
                    respuesta.setPerformative(ACLMessage.INFORM);

                    if (esRiesgoAlto) {
                        respuesta.setContent("Resultado: ALTO RIESGO");
                        
                        // Si hay peligro, enviamos una alerta directa al AgenteNotificador
                        if (agenteNotificadorAID != null) {
                            ACLMessage mensajeAlerta = new ACLMessage(ACLMessage.INFORM);
                            mensajeAlerta.addReceiver(agenteNotificadorAID);
                            mensajeAlerta.setContent("Alerta: Paciente con riesgo alto detectado.");
                            myAgent.send(mensajeAlerta);
                            System.out.println("[Predictor] -> Alerta de emergencia enviada al Notificador.");
                        }
                    } else {
                        respuesta.setContent("Resultado: BAJO RIESGO");
                    }

                    // Respondemos al Paciente para que lo pinte en la pantalla de Isa
                    myAgent.send(respuesta);
                    System.out.println("[Predictor] -> Respuesta enviada al Paciente.");
                }
            } else {
                block();
            }
        }
    }
}
