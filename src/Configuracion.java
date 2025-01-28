import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Configuracion {
    public String algoritmo;
    public ArrayList<Integer> semillas;
    public int k;
    public boolean logs; // Atributo que escribira por pantalla o en un fichero segun este indicado en el fichero de configuracion

    // Para ejecutar una misma configuracion del problema para muchos archivos.tsp
    public String[] archivosTSP;
    public int nIteraciones;
    public int inicioEntornoPorcentaje; //El porcentaje que se hace sobre el numero de iteraciones para tener el numero del entorno dinamico
    public int disminucionEntornoPorcentaje; // El porcentaje sobre el que se disminuye progresivamente el entorno dinamico
    public int progresionDisminucionEntorno; // Cada cuando se disminuye el entorno dinamico
    public int movimientosEmpeoramientoConsecutivo; // El porcentaje de movimientos sobre el total de iteraciones que no mejora la solucion actual que se debe hacer para asignarle otra solución
    public int tenenciaTabu;
    public int OscilacionEstrategica;
    public PrintWriter archivoLog;


    public Configuracion(String rutaArchivo) throws Exception
    {
        BufferedReader archivoConfiguracion = new BufferedReader(new FileReader(rutaArchivo));
        semillas = new ArrayList<>();

        // Separar las semillas por espacios
        String linea = Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": ");
        Scanner listaNumeros = new Scanner(linea);
        while(listaNumeros.hasNextInt())
        {
            semillas.add(listaNumeros.nextInt());
        }

        // Separar los nombres de los archivos .tsp por espacios
        linea = Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": ");
        this.archivosTSP = linea.split("\\s+");

        // Escribir la ruta absoluta de los archivos. Los archivos .tsp deberán ser guardados en la ruta del proyecto
        String rutaAbsoluta = new File("").getAbsolutePath();
        rutaAbsoluta+="\\";
        for(int i = 0; i < archivosTSP.length; i++)
        {
            archivosTSP[i] = rutaAbsoluta + archivosTSP[i];
        }

        this.logs = Boolean.parseBoolean(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));

        // Procesar el resto de los parametros
        this.algoritmo = Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": ");
        switch(algoritmo)
        {
            case "GREEDY":
            {
                this.k = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                //this.nIteraciones = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                break;
            }
            case "LOCAL":
            {
                this.k = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.nIteraciones = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.inicioEntornoPorcentaje = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.disminucionEntornoPorcentaje = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.progresionDisminucionEntorno = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                break;
            }
            case "TABU":
            {
                this.k = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.nIteraciones = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.inicioEntornoPorcentaje = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.disminucionEntornoPorcentaje = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.progresionDisminucionEntorno = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.movimientosEmpeoramientoConsecutivo=Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                break;
            }
            case "TABU_2":
            {
                this.k = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.nIteraciones = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.inicioEntornoPorcentaje = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.disminucionEntornoPorcentaje = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.progresionDisminucionEntorno = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.movimientosEmpeoramientoConsecutivo=Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.tenenciaTabu=Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                this.OscilacionEstrategica =Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoConfiguracion,": "));
                break;
            }
        }
    }

    @Override
    public String toString()
    {
        switch(algoritmo)
        {
            case "GREEDY":
            {
                return "k: "+k+'\n';
            }
            case "LOCAL":
            {
                return "k: "+k+'\n'+
                        "nIteraciones: "+nIteraciones+'\n'+
                        "inicioEntornoPorcentaje = "+inicioEntornoPorcentaje+'\n'+
                        "disminucionEntornoPorcentaje = "+disminucionEntornoPorcentaje+'\n'+
                        "progresionDisminucionEntorno = "+progresionDisminucionEntorno;

            }
            case "TABU":
            {
                return "k: "+k+'\n'+
                        "nIteraciones: "+nIteraciones+'\n'+
                        "inicioEntornoPorcentaje = "+inicioEntornoPorcentaje+'\n'+
                        "disminucionEntornoPorcentaje = "+disminucionEntornoPorcentaje+'\n'+
                        "progresionDisminucionEntorno = "+progresionDisminucionEntorno+'\n'+
                        "movimientosEmpeoramientoConsecutivo = "+movimientosEmpeoramientoConsecutivo;
            }
            case "TABU_2":
            {
                return "k: "+k+'\n'+
                        "nIteraciones: "+nIteraciones+'\n'+
                        "inicioEntornoPorcentaje = "+inicioEntornoPorcentaje+'\n'+
                        "disminucionEntornoPorcentaje = "+disminucionEntornoPorcentaje+'\n'+
                        "progresionDisminucionEntorno = "+progresionDisminucionEntorno+'\n'+
                        "movimientosEmpeoramientoConsecutivo = "+movimientosEmpeoramientoConsecutivo+'\n'+
                        "tenenciaTabu = "+tenenciaTabu+'\n'+
                        "oscilacionEstrategica = "+OscilacionEstrategica;
            }
        }
        return null;
    }
}
