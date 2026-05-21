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

    // Comportamiento para evaluar los carbohidratos de todo el dia
    private class EscucharPeticiones extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    
                    String datosRecibidos = msg.getContent();
                    
                    // Rompemos el string por las comas
                    String[] partes = datosRecibidos.split(",");
                    
                    // Extraemos solo lo que le importa al Nutricionista segun su posicion
                    String nombre = partes[0];
                    double carbDesayuno = Double.parseDouble(partes[3]);
                    double carbComida = Double.parseDouble(partes[6]);
                    double carbCena = Double.parseDouble(partes[9]);
                    
                    System.out.println("[Nutricionista] -> Analizando diario dietético de " + nombre + "...");
                    
                    //  Evaluamos si se pasa de la carga glucemica recomendada en cada comida
                    boolean exceso = false;
                    
                    if (carbDesayuno > 60.0) {
                        System.out.println("[Nutricionista] -> Alerta: Desayuno demasiado alto en carbohidratos (" + carbDesayuno + "g).");
                        exceso = true;
                    }
                    if (carbComida > 75.0) {
                        System.out.println("[Nutricionista] -> Alerta: Comida excesivamente pesada (" + carbComida + "g).");
                        exceso = true;
                    }
                    if (carbCena > 45.0) {
                        System.out.println("[Nutricionista] -> Alerta: Cena con demasiados carbohidratos (" + carbCena + "g).");
                        exceso = true;
                    }
                    
                    if (!exceso) {
                        System.out.println("[Nutricionista] -> OK: Consumo equilibrado dentro de los límites en todas las comidas.");
                    }
                    
                    // Devolvemos los datos intactos al Paciente para que la IA haga el diagnostico de diabetes
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