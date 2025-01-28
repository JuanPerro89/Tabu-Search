import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BusquedaLocal {
    private Configuracion parametrosProblema;
    private TSP datosProblema;
    private int periodoReevalucion;
    private int tamEntornoDinamico;
    private double HacerPorcentaje=0.01;

    public BusquedaLocal(Configuracion config, TSP datosProblema) throws Exception {
        this.parametrosProblema = config;
        this.datosProblema = datosProblema;
        this.tamEntornoDinamico = (int)(parametrosProblema.nIteraciones * parametrosProblema.inicioEntornoPorcentaje*HacerPorcentaje);
        this.periodoReevalucion = (int)(parametrosProblema.nIteraciones * parametrosProblema.progresionDisminucionEntorno*HacerPorcentaje);
    }

    public ArrayList<Ciudad> ResolverTSPBusquedaLocal(ArrayList<Ciudad> listaCiudades)
    {
        int it = 1;
        boolean haMejorado = false;
        double[] mejorIntercambio = new double[3];

        if(parametrosProblema.logs)
        {
            parametrosProblema.archivoLog.println(datosProblema.distanciaRecorridaTotal);
        }

        do {
            do {
                mejorIntercambio[0] = 0;
                mejorIntercambio[1] = 0;
                mejorIntercambio[2] = 0;

                haMejorado = false;
                for(int vecino = 0; vecino < tamEntornoDinamico; vecino++)
                {
                    double[] intercambioActual = diferenciaDosOpt(listaCiudades);
                    if(intercambioActual[0]>=0) { //Si la distancia entre las dos ciudades es negativa (la nueva distancia es mayor) no se hace la evaluacion
                        // Una solucion es mejor que otra si la diferencia entre arcos es positiva
                        if (mejorIntercambio[0] < intercambioActual[0] ) {
                            haMejorado = true;
                            mejorIntercambio = intercambioActual;
                        }
                    }
                }
                it++; // Se hace una iteracion cuando se hace el bucle for del entorno dinamico

                if(haMejorado)
                {
                    Collections.swap(listaCiudades, (int)mejorIntercambio[1], (int)mejorIntercambio[2]);
                    datosProblema.distanciaRecorridaTotal -= mejorIntercambio[0];
                    if(parametrosProblema.logs)
                    {
                        parametrosProblema.archivoLog.println(it+", "+datosProblema.distanciaRecorridaTotal+
                                "// Intercambio("
                                    +(int)mejorIntercambio[1] +
                                    ", " +(int)mejorIntercambio[2] +
                                ")");
                    }
                }
            }while(it % periodoReevalucion != 0 && haMejorado);

            tamEntornoDinamico *= (1 - parametrosProblema.disminucionEntornoPorcentaje*HacerPorcentaje);
            if(parametrosProblema.logs)
            {
                parametrosProblema.archivoLog.println("// Entorno dinamico ="+tamEntornoDinamico);
            }

        }while(it <= parametrosProblema.nIteraciones && haMejorado);

        return listaCiudades;
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
