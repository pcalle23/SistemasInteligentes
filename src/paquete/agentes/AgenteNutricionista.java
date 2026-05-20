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

        // Nos registramos en el DF para que el Paciente nos encuentre
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID()); 
        
        ServiceDescription sd = new ServiceDescription();
        sd.setType("calculo-nutricional"); 
        sd.setName("ServicioNutricionJADE");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println("[Nutricionista] -> Registrado en el DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new EscucharPeticiones());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println("[Nutricionista] -> Eliminado del DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    // Comportamiento para evaluar los carbohidratos
    private class EscucharPeticiones extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    
                    String datosRecibidos = msg.getContent();
                    
                    // Rompemos el string por las comas para sacar las variables
                    // Formato esperado: nombre,edad,glucosa,carbohidratos
                    String[] partes = datosRecibidos.split(",");
                    
                    String nombre = partes[0];
                    double carbohidratos = Double.parseDouble(partes[3]);
                    
                    System.out.println("[Nutricionista] -> Analizando dieta de " + nombre + "...");
                    
                    // Logica basica de nutricion
                    if (carbohidratos > 80.0) {
                        System.out.println("[Nutricionista] -> Alerta: Consumo de carbohidratos muy alto (" + carbohidratos + "g).");
                    } else {
                        System.out.println("[Nutricionista] -> Consumo de carbohidratos dentro de los limites.");
                    }
                    
                    // Devolvemos los datos intactos al Paciente para que se los pase a la IA
                    ACLMessage respuesta = msg.createReply();
                    respuesta.setPerformative(ACLMessage.INFORM);
                    respuesta.setContent(datosRecibidos);
                    
                    myAgent.send(respuesta);
                    System.out.println("[Nutricionista] -> Informe devuelto al Paciente.");
                }
            } else {
                block();
            }
        }
    }
}