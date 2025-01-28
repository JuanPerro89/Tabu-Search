import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Tabu {
    private Configuracion parametrosProblema;
    private TSP datosProblema;
    private Random generadorAleatorio;
    private int periodoReevalucion;
    private int tamEntornoDinamico;
    private int movimientosEmpeoriamientoConsecutivo;
    private double HacerPorcentaje=0.01;

    public Tabu(Configuracion config, int semilla, TSP datosProblema) throws Exception {
        this.parametrosProblema = config;
        this.generadorAleatorio = new Random(semilla);
        this.datosProblema=datosProblema;
        this.tamEntornoDinamico = (int)(parametrosProblema.nIteraciones * parametrosProblema.inicioEntornoPorcentaje*HacerPorcentaje);
        this.periodoReevalucion = (int)(parametrosProblema.nIteraciones * parametrosProblema.progresionDisminucionEntorno*HacerPorcentaje);
        this.movimientosEmpeoriamientoConsecutivo=(int)(parametrosProblema.nIteraciones*parametrosProblema.movimientosEmpeoramientoConsecutivo*HacerPorcentaje);
    }

    public ArrayList<Ciudad> ResolverTabu(ArrayList<Ciudad> solucionActual)
    {

        ArrayList<Ciudad> mejorGlobal = null;
        double distanciaGlobal = datosProblema.distanciaRecorridaTotal;
        if(parametrosProblema.logs)
        {
            parametrosProblema.archivoLog.println(1+", "+datosProblema.distanciaRecorridaTotal+"// Mejor actual");
        }


        double[] mejorMomento = new double[3];

        int it = 1;
        int itEstancadas = 1; //iteraciones hechas sin que se mejore la solucion actual
        boolean haMejorado = false;
        do {
            do {
                mejorMomento[0] = Integer.MIN_VALUE;
                mejorMomento[1] = 0;
                mejorMomento[2] = 0;

                haMejorado = false;
                for(int vecino = 0; vecino < tamEntornoDinamico; vecino++)
                {
                    double[] intercambioActual = diferenciaDosOpt(solucionActual); // solucion Actual
                    if(mejorMomento[0]<intercambioActual[0] )
                    {
                        haMejorado = true;
                        mejorMomento = intercambioActual;
                    }
                }
                if(!haMejorado){
                    itEstancadas++;
                }else{
                    itEstancadas=1;
                }
                it++;

                datosProblema.distanciaRecorridaTotal -= mejorMomento[0];
                if(parametrosProblema.logs)
                {
                    parametrosProblema.archivoLog.println(it+", "+datosProblema.distanciaRecorridaTotal+"// Mejor actual");
                }
                Collections.swap(solucionActual, (int)mejorMomento[1], (int)mejorMomento[2]);

                if(datosProblema.distanciaRecorridaTotal <= distanciaGlobal){

                    mejorGlobal = new ArrayList<>(solucionActual);
                    distanciaGlobal -= mejorMomento[0];

                    if(parametrosProblema.logs)
                    {
                        parametrosProblema.archivoLog.println(it+", "+distanciaGlobal+"// Mejor global");
                    }
                }

                if(movimientosEmpeoriamientoConsecutivo<=itEstancadas){
                    try {
                        GreedyAleatorio greedy = new GreedyAleatorio(parametrosProblema,datosProblema);
                        solucionActual = greedy.HacerGreadyAleatorio(solucionActual);
                        datosProblema.distanciaRecorridaTotal = greedy.datos.distanciaRecorridaTotal;
                        if(parametrosProblema.logs)
                        {
                            parametrosProblema.archivoLog.println(it+", "+distanciaGlobal+"// Nueva solucion");
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }while(it % periodoReevalucion != 0); // No hace falta poner &&haMejorado en el bucle porque cuando acaba este debe usarse la mejor solucion, aunque no sea la más óptima

            tamEntornoDinamico *= (1 - parametrosProblema.disminucionEntornoPorcentaje*HacerPorcentaje);

        }while(it <= parametrosProblema.nIteraciones && haMejorado);

        datosProblema.distanciaRecorridaTotal = distanciaGlobal;

        return mejorGlobal;
    }

    private double[] diferenciaDosOpt(ArrayList<Ciudad> listaCiudades) {

        // Guarda la distancia que se hace, la posicion de la ciudad 1 y la posicion de la ciudad 2, en ese orden
        int pos1, pos2;
        int tamSolucion = listaCiudades.size();
        pos1 = datosProblema.generadorAleatorio.nextInt(1,tamSolucion-1);
        pos2 = datosProblema.generadorAleatorio.nextInt(1,tamSolucion-1);

        double distanciaLocalTotalActual = 0;
        double distanciaLocalTotalNueva = 0;

        // Considerar unicamente los arcos relevantes a pos1 y el intercambio de la pos2 en el lugar de pos1
        // Distancia con la ciudad a la izquierda de la posicion 1
        if(((0 < pos1 && pos1 <= listaCiudades.size()-1) || pos1 == listaCiudades.size()-1 )&& pos1-1!=pos2)
        {
            distanciaLocalTotalActual += listaCiudades.get(pos1 - 1).distancia(listaCiudades.get(pos1));
            distanciaLocalTotalNueva += listaCiudades.get(pos1 - 1).distancia(listaCiudades.get(pos2));
        }

        // Distancia con la ciudad a la derecha de la posicion 1
        if(((0 < pos1 && pos1 < listaCiudades.size()-1) || pos1 == 0 )&& pos1+1!=pos2)
        {
            distanciaLocalTotalActual += listaCiudades.get(pos1).distancia(listaCiudades.get(pos1 + 1));
            distanciaLocalTotalNueva += listaCiudades.get(pos2).distancia(listaCiudades.get(pos1 + 1));
        }

        // Considerar unicamente los arcos relevantes a pos2 y el intercambio de la pos1 en el lugar de pos2
        // Distancia con la ciudad a la izquierda de la posicion 2
        if(((0 < pos2 && pos2 <= listaCiudades.size()-1) || pos2 == listaCiudades.size()-1 )&& pos2-1!=pos1)
        {
            distanciaLocalTotalActual += listaCiudades.get(pos2 - 1).distancia(listaCiudades.get(pos2));
            distanciaLocalTotalNueva += listaCiudades.get(pos2 - 1).distancia(listaCiudades.get(pos1));
        }

        // Distancia con la ciudad a la derecha de la posicion 2
        if(((0 < pos2 && pos2 < listaCiudades.size()-1) || pos2 == 0) && pos2+1!=pos1)
        {
            distanciaLocalTotalActual += listaCiudades.get(pos2 + 1).distancia(listaCiudades.get(pos2));
            distanciaLocalTotalNueva += listaCiudades.get(pos2 + 1).distancia(listaCiudades.get(pos1));
        }

        double[] vectorTemporal = new double[3];
        vectorTemporal[0]=distanciaLocalTotalActual-distanciaLocalTotalNueva;
        vectorTemporal[1]=pos1;
        vectorTemporal[2]=pos2;

        return vectorTemporal;
    }


}
