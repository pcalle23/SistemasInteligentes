package paquete.agentes;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgenteNotificador extends Agent {

    @Override
    protected void setup() {
        System.out.println("[Notificador] -> Agente " + getLocalName() + " iniciado.");

        // Registro en el DF (Paginas Amarillas)
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("notificacion-alertas");
        sd.setName("ServicioNotificacionJADE");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println("[Notificador] -> Registrado en el DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Añadimos el comportamiento para estar siempre escuchando
        addBehaviour(new EscucharAlertas());
    }

    @Override
    protected void takeDown() {
        try {
            // Borramos el rastro al cerrar
            DFService.deregister(this);
            System.out.println("[Notificador] -> Eliminado del DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    // Bucle para procesar las alertas que mande el Predictor
    private class EscucharAlertas extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if (msg != null) {
                // Comprobamos si es un mensaje de aviso (INFORM)
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    String alertaText = msg.getContent();
                    
                    // Simulacion visual del sistema de emergencias en la consola
                    System.out.println("\n--- [SISTEMA DE ALERTA MEDICA] ---");
                    System.out.println("AVISO DE EMERGENCIA: " + alertaText);
                    System.out.println("Simulando envio de mensaje SMS y Telegram al medico de guardia...");
                    System.out.println("-----------------------------------\n");
                }
            } else {
                // Si no hay alertas en el buzon, dormimos el hilo
                block();
            }
        }
    }
}