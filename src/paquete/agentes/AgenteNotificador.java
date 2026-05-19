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

        // Registro en las paginas amarillas para que el Predictor nos encuentre
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("notificacion-alertas"); // Palabra clave para este servicio
        sd.setName("ServicioNotificacionJADE");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println("🚨 [Notificador] -> Registrado en el DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Activamos el bucle para escuchar alertas de peligro
        addBehaviour(new EscucharAlertas());
    }

    @Override
    protected void takeDown() {
        // Nos borramos del DF al apagar el agente
        try {
            DFService.deregister(this);
            System.out.println("❌ [Notificador] -> Eliminado del DF.");
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
                System.out.println("🚨 [Notificador] -> Alerta recibida de: " + msg.getSender().getLocalName());

                // Comprobamos si es un mensaje de aviso (INFORM o REQUEST)
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    String contenido = msg.getContent();
                    
                    System.out.println("🚨 [Notificador] -> MENSAJE DE EMERGENCIA: " + contenido);
                    System.out.println("🚨 [Notificador] -> Simulando envio de alerta al movil del paciente...");
                    
                    // Aqui es donde se pondra el codigo de la API de Telegram mas adelante
                    System.out.println("📲 [Notificador] -> Alerta enviada con exito.");
                }
            } else {
                // Si no hay alertas en el buzon, dormimos el hilo
                block();
            }
        }
    }
}