package paquete.agentes;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgenteNutricionista extends Agent {

    @Override
    protected void setup() {
        System.out.println("[Nutricionista] -> Agente " + getLocalName() + " iniciado.");

        // Registro en las paginas amarillas (DF) para que el Paciente nos encuentre
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID()); 
        
        ServiceDescription sd = new ServiceDescription();
        sd.setType("calculo-nutricional"); // Clave para la busqueda
        sd.setName("ServicioNutricionJADE");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println("🤖 [Nutricionista] -> Registrado en el DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Activamos el comportamiento para escuchar mensajes
        addBehaviour(new EscucharPeticiones());
    }

    @Override
    protected void takeDown() {
        // Al morir el agente, nos borramos del DF para no dejar basura
        try {
            DFService.deregister(this);
            System.out.println("❌ [Nutricionista] -> Eliminado del DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    // Bucle para atender los mensajes que vayan llegando
    private class EscucharPeticiones extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            
            if (msg != null) {
                System.out.println("🤖 [Nutricionista] -> Mensaje recibido de: " + msg.getSender().getLocalName());
                
                // Si nos piden una simulacion/calculo (REQUEST)
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    String contenido = msg.getContent();
                    System.out.println("🤖 [Nutricionista] -> Datos: " + contenido);
                    
                    // Respondemos al agente que nos escribio
                    ACLMessage respuesta = msg.createReply();
                    respuesta.setPerformative(ACLMessage.INFORM);
                    
                    // Mas adelante cambiaremos este texto por los datos reales procesados
                    respuesta.setContent("Resultado-Nutricional: OK.");
                    
                    myAgent.send(respuesta);
                    System.out.println("🤖 [Nutricionista] -> Respuesta enviada.");
                }
            } else {
                // Si no hay mensajes, bloqueamos para no saturar la CPU
                block();
            }
        }
    }
}