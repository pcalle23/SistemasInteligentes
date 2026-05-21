package paquete.ia;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

// Imports para el preprocesamiento multivariante
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;

public class GestorWeka {

    private Classifier modelo;
    
    // Ahora necesitamos guardar dos estructuras y el filtro a nivel global
    private Instances estructuraOriginal;
    private Instances estructuraFiltrada;
    private AttributeSelection filtroMultivariante;

    public void entrenarModelo() {
        try {
            // 1. Cargar datos
            DataSource source = new DataSource("diabetes.arff");
            estructuraOriginal = source.getDataSet();

            if (estructuraOriginal.classIndex() == -1) {
                estructuraOriginal.setClassIndex(estructuraOriginal.numAttributes() - 1);
            }

            // 2. Configurar el Filtro Multivariante (CFS + GreedyStepwise)
            filtroMultivariante = new AttributeSelection();
            CfsSubsetEval evaluador = new CfsSubsetEval();
            GreedyStepwise busqueda = new GreedyStepwise();
            busqueda.setSearchBackwards(true); // Búsqueda hacia atrás, ideal en medicina
            
            filtroMultivariante.setEvaluator(evaluador);
            filtroMultivariante.setSearch(busqueda);
            filtroMultivariante.setInputFormat(estructuraOriginal);
            
            // 3. Aplicar el filtro para crear el dataset optimizado
            estructuraFiltrada = Filter.useFilter(estructuraOriginal, filtroMultivariante);
            
            System.out.println("Preprocesamiento completado. Columnas reducidas de " + estructuraOriginal.numAttributes() + " a " + estructuraFiltrada.numAttributes() + ".");

            // 4. Entrenar el J48 SÓLO con los datos filtrados
            modelo = new J48();
            modelo.buildClassifier(estructuraFiltrada);
                        
            System.out.println("Modelo J48 entrenado correctamente.");

        } catch (Exception e) {
            System.err.println("[Weka]: Error crítico al inicializar/entrenar el modelo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean predecirRiesgo(double edad, double glucosaAyunas, double glucosaPostDesayuno, 
                                  double glucosaComida, double glucosaPostComida, double glucosaCena, double glucosaPostCena) {
                                      
        if (modelo == null || estructuraOriginal == null) {
            System.err.println("[Weka]: Error: El modelo no ha sido entrenado.");
            return false; 
        }

        try {
            // Crear el paciente con TODAS las variables (Estructura Original)
            Instance nuevaInstancia = new DenseInstance(estructuraOriginal.numAttributes());
            nuevaInstancia.setDataset(estructuraOriginal);

            nuevaInstancia.setValue(0, edad);
            nuevaInstancia.setValue(1, glucosaAyunas);
            nuevaInstancia.setValue(2, glucosaPostDesayuno);
            nuevaInstancia.setValue(3, glucosaComida);
            nuevaInstancia.setValue(4, glucosaPostComida);
            nuevaInstancia.setValue(5, glucosaCena);
            nuevaInstancia.setValue(6, glucosaPostCena);

            // Creamos un dataset temporal de 1 solo paciente para poder pasarle el filtro
            Instances datasetTemporal = new Instances(estructuraOriginal, 0);
            datasetTemporal.add(nuevaInstancia);
            
            // Pasamos al paciente por el mismo filtro que usamos en el entrenamiento
            Instances datasetFiltradoTemporal = Filter.useFilter(datasetTemporal, filtroMultivariante);
            
            // Extraemos al paciente ya filtrado (ahora tiene menos columnas)
            Instance pacienteFiltrado = datasetFiltradoTemporal.firstInstance();

            // 3. Predicción con el paciente optimizado
            double resultadoClasificacion = modelo.classifyInstance(pacienteFiltrado);
            String prediccion = estructuraFiltrada.classAttribute().value((int) resultadoClasificacion);
            
            System.out.println("Evaluando paciente filtrado -> Predicción: Diágnostico " + prediccion.toUpperCase());

            return prediccion.equalsIgnoreCase("positivo");

        } catch (Exception e) {
            System.err.println("[Weka]: Fallo al intentar predecir la nueva instancia: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        
    }
}