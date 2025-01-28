import java.util.*;
import java.util.stream.Collectors;

public class TabuMejorado {
    private Configuracion parametrosProblema;
    private TSP datosProblema;
    private Random generadorAleatorio;
    private double periodoReevalucion;
    private double tamEntornoDinamico;
    private int movimientosEmpeoriamientoConsecutivo;
    //private int tenenciaTabu;
    private double oscilacionEstrategica;
    private double HacerPorcentaje=0.01;

    int[][] matrizMemoria;
    LinkedList<int[]> memoriaCortoPlazo;

    public TabuMejorado(Configuracion config, int semilla, TSP datosProblema) throws Exception {
        this.parametrosProblema = config;
        this.generadorAleatorio = new Random(semilla);
        this.datosProblema=datosProblema;
        this.tamEntornoDinamico = (int)(parametrosProblema.nIteraciones * parametrosProblema.inicioEntornoPorcentaje*HacerPorcentaje);
        this.periodoReevalucion = (int)(parametrosProblema.nIteraciones * parametrosProblema.progresionDisminucionEntorno*HacerPorcentaje);
        this.movimientosEmpeoriamientoConsecutivo=(int)(parametrosProblema.nIteraciones*parametrosProblema.movimientosEmpeoramientoConsecutivo*HacerPorcentaje);
        this.oscilacionEstrategica=parametrosProblema.OscilacionEstrategica *HacerPorcentaje;
    }

    public ArrayList<Ciudad> ResolverTabu(ArrayList<Ciudad> solucionActual)
    {
        //La matriz cuadrada que guardara la MCP y la MLP, la MCP se almacena en la diagonal inferior y la MLP en la superior
        matrizMemoria=new int[solucionActual.size()+1][solucionActual.size()+1];
        memoriaCortoPlazo = new LinkedList<int[]>();

        // Hago que la diagonal sea negativa para que no de problemas
        for(int i=0;i<solucionActual.size()+1;i++){
            matrizMemoria[i][i]=-1;
        }

        ArrayList<Ciudad> mejorGlobal = null;
        double distanciaGlobal = datosProblema.distanciaRecorridaTotal;

        double[] mejorMomento = new double[3];

        int it = 1;
        int itEstancadas = 1; //iteraciones hechas sin que se mejore la solucion actual

        boolean haMejorado = false;
        do {
            do {
                mejorMomento[0] = 0;
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
                    parametrosProblema.archivoLog.println(it+", "+datosProblema.distanciaRecorridaTotal);
                }
                Collections.swap(solucionActual, (int)mejorMomento[1], (int)mejorMomento[2]);

                Iterator<int[]> iterador = memoriaCortoPlazo.iterator();
                while(iterador.hasNext()) {
                    int[] memoria = iterador.next();
                    memoria[0]--;

                    if(memoria[0] == 0)
                    {
                        iterador.remove();
                    }
                }

                int[] ultimaSolucion = new int[3];
                int[] ultimaCiudad = new int[3];
                ultimaSolucion[0] = parametrosProblema.tenenciaTabu;
                ultimaSolucion[1] = (int)Math.min(mejorMomento[1],mejorMomento[2]);
                ultimaSolucion[2] = (int)Math.max(mejorMomento[1],mejorMomento[2]);

                ultimaCiudad[0] = parametrosProblema.tenenciaTabu;
                ultimaCiudad[1] = Math.min(solucionActual.get((int)mejorMomento[1]).id,solucionActual.get((int)mejorMomento[2]).id);
                ultimaCiudad[2] = Math.max(solucionActual.get((int)mejorMomento[1]).id,solucionActual.get((int)mejorMomento[2]).id);

                memoriaCortoPlazo.add(ultimaSolucion);
                memoriaCortoPlazo.add(ultimaCiudad);
/*
                if(mejorMomento[1]<mejorMomento[2]){ // Guardo la tenencia tabu en la mejor solucion obtenida para la iteracion, siempre tengo en cuenta el mayor como las filas y el menor como las columnas
                    matrizMemoria[(int) mejorMomento[2]][(int) mejorMomento[1]]=parametrosProblema.tenenciaTabu;
                }else{
                    matrizMemoria[(int) mejorMomento[1]][(int) mejorMomento[2]]=parametrosProblema.tenenciaTabu;
                }

                if(solucionActual.get((int) mejorMomento[1]).id<solucionActual.get((int) mejorMomento[2]).id){ // Hago lo mismo que en los condicionales anteriores pero con las ciudades
                    matrizMemoria[solucionActual.get((int) mejorMomento[2]).id][solucionActual.get((int) mejorMomento[1]).id]=parametrosProblema.tenenciaTabu;
                }else{
                    matrizMemoria[solucionActual.get((int) mejorMomento[1]).id][solucionActual.get((int) mejorMomento[2]).id]=parametrosProblema.tenenciaTabu;
                }
*/
                for(int i=1;i<solucionActual.size();i++){ //Recorro la lista del recorrido actual para actualizar la MLP
                    if(i!=(solucionActual.size()-1)){
                        if(solucionActual.get(i).id<solucionActual.get(i+1).id){// En la MLP se accede a las posiciones de manera inversa a la MCP por la naturaleza de la diagonal, el numero menor son las filas y el mayor las columnas
                            matrizMemoria[solucionActual.get(i).id][solucionActual.get(i+1).id]++;
                        }else{
                            matrizMemoria[solucionActual.get(i+1).id][solucionActual.get(i).id]++;
                        }
                    }
                }


                if(datosProblema.distanciaRecorridaTotal <= distanciaGlobal){
                    mejorGlobal = new ArrayList<>(solucionActual);
                    distanciaGlobal = datosProblema.distanciaRecorridaTotal;

                    if(parametrosProblema.logs)
                    {
                        parametrosProblema.archivoLog.println(it+", "+distanciaGlobal+"// Mejor global");
                    }
                }

                if(movimientosEmpeoriamientoConsecutivo<=itEstancadas)
                {
                    double numeroAleatorio = generadorAleatorio.nextDouble();
                    if(numeroAleatorio<=oscilacionEstrategica){
                        solucionActual = intensificacion(solucionActual);
                        if(parametrosProblema.logs)
                        {
                            parametrosProblema.archivoLog.println(it+", "+distanciaGlobal+"// intensificacion "+"("+numeroAleatorio+")");
                        }
                    }else{
                        solucionActual = diversificacion(solucionActual);
                        if(parametrosProblema.logs)
                        {
                            parametrosProblema.archivoLog.println(it+", "+distanciaGlobal+"// diversificacion"+"("+numeroAleatorio+")");
                        }
                    }
                }
            }while(it <= parametrosProblema.nIteraciones && it % periodoReevalucion != 0); // No hace falta poner &&haMejorado en el bucle, a diferencia de la busqueda local porque cuando acaba este debe usarse la mejor solucion, aunque no sea la más óptima

            tamEntornoDinamico *= (1 - parametrosProblema.disminucionEntornoPorcentaje*HacerPorcentaje);

        }while(it <= parametrosProblema.nIteraciones); // Habia antes un &&ha mejorado pero lo he quitado porque no se hasta que punto hace falta

        datosProblema.distanciaRecorridaTotal = distanciaGlobal;

        return mejorGlobal;
    }

    private double[] diferenciaDosOpt(ArrayList<Ciudad> listaCiudades) {

        boolean valido=false;
        // Guarda la distancia que se hace, la posicion de la ciudad 1 y la posicion de la ciudad 2, en ese orden
        int pos1=0, pos2=0;

        while(!valido) { // genera posiciones hasta que estas son validas teniendo en cuenta la MCP (diagonal inferior de la matriz)
            //System.out.println("["+pos1+", "+pos2+"]");
            pos1 = datosProblema.generadorAleatorio.nextInt(1, listaCiudades.size()-1);
            pos2 = datosProblema.generadorAleatorio.nextInt(1, listaCiudades.size()-1);
            /*
            System.out.println(pos1 + "," + pos2);
            System.out.println(listaCiudades.get(pos1).id + "," + listaCiudades.get(pos2).id);
            System.out.println();
            */

            boolean posicionesValidas=false;
            if(pos1!=pos2) { // Compruebo que no es la diagonal las posiciones
                if (pos1 < pos2) {
                    if(matrizMemoria[pos2][pos1]==0){
                        posicionesValidas=true;
                    }
                }else{
                    if(matrizMemoria[pos1][pos2]==0){
                        posicionesValidas=true;
                    }
                }

                if(posicionesValidas && listaCiudades.get(pos1).id!=listaCiudades.get(pos2).id){ // Compruebo aqui tambien que no sea la misma ciudad (en el caso de que sea justamente el final y el principio)
                    if(listaCiudades.get(pos1).id<listaCiudades.get(pos2).id){
                        if(matrizMemoria[listaCiudades.get(pos2).id][listaCiudades.get(pos1).id]==0){
                            valido=true;
                        }
                    }else{
                        if(matrizMemoria[listaCiudades.get(pos1).id][listaCiudades.get(pos2).id]==0){
                            valido=true;
                        }
                    }
                }
            }
        }
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

    ArrayList<Ciudad> intensificacion(ArrayList<Ciudad> listaCiudades)
    {
        //System.out.println("intensificacion");
        Map<Integer,Ciudad> ciudadesRestantes = listaCiudades.stream().collect(Collectors.toMap(ciudad -> ciudad.id, ciudad->ciudad));
        ArrayList<Ciudad> nuevaSolucion = new ArrayList<>();
        datosProblema.distanciaRecorridaTotal = 0;

        // Valor aleatorio de la ciudad inicial debe estar entre 1 y el numero de ciudades
        int ciudadActual = datosProblema.generadorAleatorio.nextInt(1,listaCiudades.size());
        while(!ciudadesRestantes.isEmpty())
        {
            int ciudadSiguiente = 0;
            int repeticionesCiudad = 0;

            //System.out.println("inicioIteracion: "+ciudadActual);
            // Buscar la ciudad mas recorrida entre los arcos de las ciudades anteriores al id de ciudadActual
            for(int i = 1; i < ciudadActual; i++)
            {
                //System.out.println("("+i+", "+matrizMemoria[i][ciudadActual]+") : ("+ciudadSiguiente+", "+repeticionesCiudad+")");
                if(matrizMemoria[i][ciudadActual] > repeticionesCiudad && ciudadesRestantes.containsKey(i))
                {
                    repeticionesCiudad = matrizMemoria[i][ciudadActual];
                    ciudadSiguiente = i;
                }
            }

            // Buscar la ciudad mas recorrida entre las ciudades posteriores al id de ciudadActual
            for(int i = ciudadActual + 1; i <= listaCiudades.size(); i++)
            {
                //System.out.println("("+i+", "+matrizMemoria[ciudadActual][i]+") : ("+ciudadSiguiente+", "+repeticionesCiudad+")");
                if(matrizMemoria[ciudadActual][i] > repeticionesCiudad && ciudadesRestantes.containsKey(i))
                {
                    repeticionesCiudad = matrizMemoria[ciudadActual][i];
                    ciudadSiguiente = i;
                }
            }

            // Si el resto de ciudades han sido escogidas, elegir una ciudad cualquiera
            if(ciudadSiguiente == 0)
            {
                ArrayList<Integer> llaves = new ArrayList<>(ciudadesRestantes.keySet());
                llaves.remove((Object)ciudadActual);
                if(!llaves.isEmpty())
                {
                    ciudadSiguiente = llaves.get(datosProblema.generadorAleatorio.nextInt(llaves.size()));
                }
            }

            // Añadir la ciudad a la solucion
            nuevaSolucion.add(ciudadesRestantes.get(ciudadActual));

            // Actualizar distancia
            if(nuevaSolucion.size() != listaCiudades.size())
            {
                datosProblema.distanciaRecorridaTotal += ciudadesRestantes.get(ciudadActual)
                        .distancia(ciudadesRestantes.get(ciudadSiguiente));
            }
            else
            {
                datosProblema.distanciaRecorridaTotal += ciudadesRestantes.get(ciudadActual)
                        .distancia(nuevaSolucion.get(0));
            }

            // Eliminar la ciudad del mapa
            ciudadesRestantes.remove(ciudadActual);

            // Preparar la siguiente iteracion
            ciudadActual = ciudadSiguiente;
        }
        return nuevaSolucion;
    }

    ArrayList<Ciudad> diversificacion(ArrayList<Ciudad> listaCiudades) {
        //System.out.println("diversificacion");
        Map<Integer, Ciudad> ciudadesRestantes = listaCiudades.stream().collect(Collectors.toMap(ciudad -> ciudad.id, ciudad -> ciudad));
        ArrayList<Ciudad> nuevaSolucion = new ArrayList<>();
        datosProblema.distanciaRecorridaTotal = 0;

        // Valor aleatorio de la ciudad inicial debe estar entre 1 y el numero de ciudades
        int ciudadActual = datosProblema.generadorAleatorio.nextInt(1, listaCiudades.size());
        while (!ciudadesRestantes.isEmpty()) {
            ArrayList<Integer> ciudadesCercanas = new ArrayList<>();

            // Almacenar todas la frecuencia de aparicion de todas las ciudades respecto a ciudadActual
            for(int i = 1; i < ciudadActual; i++)
            {
                ciudadesCercanas.add(matrizMemoria[i][ciudadActual]);
            }

            for(int i = ciudadActual+1; i <= listaCiudades.size(); i++)
            {
                ciudadesCercanas.add(matrizMemoria[ciudadActual][i]);
            }


            ArrayList<Integer> ciudadesOrdenadas = new ArrayList<>(ciudadesCercanas);
            Collections.sort(ciudadesOrdenadas);

            // TODO: quitar, solo es para hacer pruebas
            int iteracionesAleatorias = 0;

            // Ordenar ciudades de menor a mayor por frecuencia de aparicion y escoger la
            int ciudadSiguiente;
            do
            {
                int numeroAleatorio = datosProblema.generadorAleatorio.nextInt(1,(int)Math.pow(ciudadesOrdenadas.size(),2));
                int posAleatoria = (int)(-Math.sqrt(numeroAleatorio)+ciudadesOrdenadas.size());
                int elementoAleatorio = ciudadesOrdenadas.get(posAleatoria);

                ciudadSiguiente = ciudadesCercanas.indexOf(elementoAleatorio);

                // Convertir posicion de la lista en un id de ciudad
                if(ciudadSiguiente < ciudadActual-1)
                {
                    ciudadSiguiente += 1;
                }
                else
                {
                    ciudadSiguiente += 2;
                }

                iteracionesAleatorias++;
                ciudadesOrdenadas.remove(posAleatoria);
            }
            while((!ciudadesRestantes.containsKey(ciudadSiguiente)
                    || ciudadSiguiente == ciudadActual
                    || ciudadSiguiente == 0)
            && ciudadesOrdenadas.size() > 1);

            if(ciudadesOrdenadas.size() == 1)
            {
                ArrayList<Integer> llaves = new ArrayList<>(ciudadesRestantes.keySet());
                llaves.remove((Object)ciudadActual);
                if(!llaves.isEmpty())
                {
                    ciudadSiguiente = llaves.get(datosProblema.generadorAleatorio.nextInt(llaves.size()));
                }
            }

            // Añadir la ciudad a la solucion
            nuevaSolucion.add(ciudadesRestantes.get(ciudadActual));

            //System.out.println(nuevaSolucion.get(nuevaSolucion.size()-1)+": "+iteracionesAleatorias);

            // Actualizar distancia
            if (nuevaSolucion.size() != listaCiudades.size()) {
                datosProblema.distanciaRecorridaTotal += ciudadesRestantes.get(ciudadActual)
                        .distancia(ciudadesRestantes.get(ciudadSiguiente));
            } else {
                datosProblema.distanciaRecorridaTotal += ciudadesRestantes.get(ciudadActual)
                        .distancia(nuevaSolucion.get(0));
            }

            // Eliminar la ciudad del mapa
            ciudadesRestantes.remove(ciudadActual);

            // Preparar la siguiente iteracion
            ciudadActual = ciudadSiguiente;
        }
        return nuevaSolucion;

    }
}
