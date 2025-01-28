import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class Main {

    /**
     * Devuelve la cadena a la derecha de un delimitador determinado
     * @param lector Archivo de referencia
     * @param delimitador Trozo de texto desde el que emppezar a obtener texto
     * @return
     */
    public static String obtenerDatosDesdeDelimitador(BufferedReader lector, String delimitador) throws IOException {
        String data = lector.readLine();
        int delimiterPos = data.indexOf(delimitador);
        if (delimiterPos == -1) {
            return "";  // Devolver una cadena vacia si el delimitador no es encontrado
        }
        return data.substring(delimiterPos + delimitador.length());
    }

    /*String filePath = new File("").getAbsolutePath();
    filePath = filePath.concat("/src/config.txt");*/
    public static void main(String[] args)
    {
        try
        {
            // Configurar archivo de salida (PS: Este archivo se guarda en la ruta del proyecto)
            PrintWriter archivoResultados = new PrintWriter(new FileWriter("output.txt"));
            String fecha = new Date().toString();
            archivoResultados.println(fecha);


            Configuracion config = new Configuracion(args[0]);
            archivoResultados.println(config.algoritmo);
            archivoResultados.println(config);
            archivoResultados.println("-----------------------------------");

            for(int semilla : config.semillas)
            {
                for(String archivo : config.archivosTSP)
                {
                    TSP datos = new TSP(archivo,semilla);
                    if(config.logs)
                    {
                        String nombreArchivo = config.algoritmo+"-"+datos.name+"-"+semilla+".log";
                        config.archivoLog = new PrintWriter(new FileWriter(nombreArchivo));
                    }

                    ArrayList<Ciudad> v;
                    long tiempoInicio = System.nanoTime();
                    switch(config.algoritmo)
                    {
                        case "GREEDY":
                        {
                            GreedyAleatorio problema = new GreedyAleatorio(config,datos);
                            v = problema.resolverTSP(archivo);
                            break;
                        }
                        case "LOCAL":
                        {
                            boolean logsActivado = config.logs;

                            // Quitar los logs unicamente para el algoritmo greedy
                            if(logsActivado) {config.logs = false;}

                            GreedyAleatorio problemaInicial = new GreedyAleatorio(config,datos);
                            v = problemaInicial.resolverTSP(archivo);

                            config.logs = logsActivado;
                            BusquedaLocal problema = new BusquedaLocal(config,datos);
                            v = problema.ResolverTSPBusquedaLocal(v);
                            break;
                        }
                        case "TABU":
                        {
                            boolean logsActivado = config.logs;

                            // Quitar los logs unicamente para el algoritmo greedy
                            if(logsActivado) {config.logs = false;}

                            GreedyAleatorio problemaInicial = new GreedyAleatorio(config,datos);
                            v = problemaInicial.resolverTSP(archivo);

                            config.logs = logsActivado;
                            Tabu problema = new Tabu(config,semilla,datos);
                            v = problema.ResolverTabu(v);
                            break;
                        }
                        case "TABU_2":
                        {
                            boolean logsActivado = config.logs;

                            // Quitar los logs unicamente para el algoritmo greedy
                            if(logsActivado) {config.logs = false;}

                            GreedyAleatorio problemaInicial = new GreedyAleatorio(config,datos);
                            v = problemaInicial.resolverTSP(archivo);

                            config.logs = logsActivado;
                            TabuMejorado problema = new TabuMejorado(config,semilla,datos);
                            v = problema.ResolverTabu(v);
                            break;
                        }
                    }
                    long tiempoEjecucion = System.nanoTime() - tiempoInicio;
                    if(config.logs)
                    {
                        config.archivoLog.close();
                    }

                    // Guardar los datos de ejecucion del problema
                    archivoResultados.println();
                    archivoResultados.println(datos);
                    archivoResultados.println("Semilla = "+semilla);
                    archivoResultados.println("Tiempo ejecucion(ns)= "+tiempoEjecucion);
                    archivoResultados.println("-----------------------------------");
                }
            }

            archivoResultados.close();
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}