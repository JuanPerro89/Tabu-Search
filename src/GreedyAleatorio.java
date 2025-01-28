import java.util.ArrayList;
import java.util.PriorityQueue;

public class GreedyAleatorio {
    public Configuracion parametros;
    public TSP datos;

    public GreedyAleatorio(Configuracion parametros, TSP datos)
    {
        this.parametros = parametros;
        this.datos = datos;
    }
    public ArrayList<Ciudad> resolverTSP(String rutaArchivo) throws Exception {
        ArrayList<Ciudad> ciudades = TSP.leerCiudadesDesdeFichero(rutaArchivo);
        PriorityQueue<Ciudad> ciudadesPorRecorrer = new PriorityQueue<>();

        // Obtener la concentridad de cada ciudad
        int n = ciudades.size();
        for (int i = 0; i < n; i++) {
            Ciudad ciudadActual = ciudades.get(i);
            int distanciaRestoCiudadActual = 0;
            for (int j = 0; j < n; j++) {
                distanciaRestoCiudadActual += ciudadActual.distancia(ciudades.get(j));
            }
            ciudadActual.setDistanciaResto(distanciaRestoCiudadActual);
            ciudadesPorRecorrer.add(ciudadActual);
        }

        ArrayList<Ciudad> vectorSolucion = new ArrayList<>();
        ArrayList<Ciudad> bufferCiudades = new ArrayList<>();


        Ciudad ciudadAnterior = null;
        while (!ciudadesPorRecorrer.isEmpty() || !bufferCiudades.isEmpty()) {
            // Llenar bufferCiudades con k ciudades
            while (bufferCiudades.size() < parametros.k && !ciudadesPorRecorrer.isEmpty()) {
                bufferCiudades.add(ciudadesPorRecorrer.poll());
            }

            // Escoger una ciudad aleatoria
            int posCiudadAleatoria = datos.generadorAleatorio.nextInt(Integer.min(parametros.k, bufferCiudades.size()));

            if(parametros.logs)
            {
                parametros.archivoLog.println(bufferCiudades+" -> "+bufferCiudades.get(posCiudadAleatoria));
            }


            Ciudad ciudadActual = bufferCiudades.remove(posCiudadAleatoria);
            vectorSolucion.add(ciudadActual);


            if (vectorSolucion.size() >= 2) {
                datos.distanciaRecorridaTotal += ciudadActual.distancia(ciudadAnterior);
            }

            ciudadAnterior = ciudadActual;
        }

        datos.distanciaRecorridaTotal += ciudadAnterior.distancia(vectorSolucion.get(0));

        return vectorSolucion;
    }
    public ArrayList<Ciudad> HacerGreadyAleatorio(ArrayList<Ciudad> ListaCiudades) throws Exception {
        PriorityQueue<Ciudad> ciudadesPorRecorrer = new PriorityQueue<>();

        datos.distanciaRecorridaTotal = 0;
        // Obtener la concentridad de cada ciudad
        int n = ListaCiudades.size();
        for (int i = 0; i < n; i++) {
            Ciudad ciudadActual = ListaCiudades.get(i);
            int distanciaRestoCiudadActual = 0;
            for (int j = 0; j < n; j++) {
                distanciaRestoCiudadActual += ciudadActual.distancia(ListaCiudades.get(j));
            }
            ciudadActual.setDistanciaResto(distanciaRestoCiudadActual);
            ciudadesPorRecorrer.add(ciudadActual);
        }

        ArrayList<Ciudad> vectorSolucion = new ArrayList<>();
        ArrayList<Ciudad> bufferCiudades = new ArrayList<>();

        Ciudad ciudadAnterior = null;
        while (!ciudadesPorRecorrer.isEmpty() || !bufferCiudades.isEmpty()) {
            // Llenar bufferCiudades con k ciudades
            while (bufferCiudades.size() < parametros.k && !ciudadesPorRecorrer.isEmpty()) {
                bufferCiudades.add(ciudadesPorRecorrer.poll());
            }

            // Escoger una ciudad aleatoria
            int posCiudadAleatoria = datos.generadorAleatorio.nextInt(Integer.min(parametros.k, bufferCiudades.size()));

            Ciudad ciudadActual = bufferCiudades.remove(posCiudadAleatoria);

            if (vectorSolucion.size() >= 2) {
                datos.distanciaRecorridaTotal += ciudadActual.distancia(ciudadAnterior);
            }


            ciudadAnterior = ciudadActual;
        }

        datos.distanciaRecorridaTotal += ciudadAnterior.distancia(vectorSolucion.get(0));


        return vectorSolucion;
    }



    @Override
    public String toString() {
        return null;
    }
}