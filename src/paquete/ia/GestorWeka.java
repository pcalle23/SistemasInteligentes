package paquete.ia;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class GestorWeka {

    private Classifier modelo;
    private Instances estructuraDatos;

    public void entrenarModelo() {
        try {
            DataSource source = new DataSource("diabetes.arff");
            Instances datosOriginales = source.getDataSet();

            if (datosOriginales.classIndex() == -1) {
                datosOriginales.setClassIndex(datosOriginales.numAttributes() - 1);
            }

            // PREPROCESAMIENTO 
            Remove removeFilter = new Remove();
            removeFilter.setAttributeIndices("1"); // Weka cuenta desde 1 para las opciones
            removeFilter.setInputFormat(datosOriginales);
            
            //Aplicamos el filtro: no tiene atributo 'nombre'
            estructuraDatos = Filter.useFilter(datosOriginales, removeFilter);

            //Inicializar el algoritmo y entrenar con los datos limpios
            modelo = new J48();
            modelo.buildClassifier(estructuraDatos);
            
            System.out.println("[IA-WEKA] Modelo J48 entrenado correctamente con " 
                    + estructuraDatos.numInstances() + " pacientes (sin atributo String).");

        } catch (Exception e) {
            System.err.println("[IA-WEKA] Error crítico al inicializar/entrenar el modelo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean predecirRiesgo(double edad, double glucosa, double carbohidratos) {
        if (modelo == null || estructuraDatos == null) {
            System.err.println("[IA-WEKA] Error: El modelo no ha sido entrenado.");
            return false; 
        }

        try {
            // atributos (edad, glucosa, carbohidratos, riesgo) 
            Instance nuevaInstancia = new DenseInstance(estructuraDatos.numAttributes());
            nuevaInstancia.setDataset(estructuraDatos);

            //indice: 'edad'
            nuevaInstancia.setValue(0, edad);
            //indice: 'glucosa'
            nuevaInstancia.setValue(1, glucosa);
            //indice: 'carbohidratos'
            nuevaInstancia.setValue(2, carbohidratos);
            //indice: 'riesgo' (Weka lo calcula ahora)

            double resultadoClasificacion = modelo.classifyInstance(nuevaInstancia);
            String prediccion = estructuraDatos.classAttribute().value((int) resultadoClasificacion);
            
            System.out.println("[IA-WEKA] Evaluando paciente -> Edad: " + edad + " | Glucosa: " + glucosa 
                             + " => Predicción: Riesgo " + prediccion.toUpperCase());

            return prediccion.equalsIgnoreCase("ALTO");

        } catch (Exception e) {
            System.err.println("[IA-WEKA] Fallo al intentar predecir la nueva instancia: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("---TEST LOCAL WEKA ---");
        GestorWeka gestor = new GestorWeka();
        gestor.entrenarModelo();

        System.out.println("\n--- PRUEBA 1: Paciente con parámetros críticos ---");
        boolean esRiesgoAlto1 = gestor.predecirRiesgo(55, 190.0, 80.0);
        System.out.println(">> Resultado esperado: true (ALTO) | Resultado obtenido: " + esRiesgoAlto1);

        System.out.println("\n--- PRUEBA 2: Paciente con parámetros saludables ---");
        boolean esRiesgoAlto2 = gestor.predecirRiesgo(25, 85.0, 45.0);
        System.out.println(">> Resultado esperado: false (BAJO) | Resultado obtenido: " + esRiesgoAlto2);
    }
}
