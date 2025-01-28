import java.io.*;
import java.util.*;

public class TSP {

    public String name, type, comment, edge_weight_type;
    public int dimension;
    public double distanciaRecorridaTotal;
    Random generadorAleatorio;

    public TSP(String rutaArchivo, int semilla) throws Exception
    {
        this.generadorAleatorio = new Random(semilla);
        this.distanciaRecorridaTotal = 0;

        BufferedReader archivoTSP = new BufferedReader(new FileReader(rutaArchivo));
        this.name = Main.obtenerDatosDesdeDelimitador(archivoTSP,": ");
        this.comment = Main.obtenerDatosDesdeDelimitador(archivoTSP,": ");
        this.type = Main.obtenerDatosDesdeDelimitador(archivoTSP,": ");
        this.dimension = Integer.parseInt(Main.obtenerDatosDesdeDelimitador(archivoTSP,": "));
        this.edge_weight_type = Main.obtenerDatosDesdeDelimitador(archivoTSP,": ");
    }

    public static ArrayList<Ciudad> leerCiudadesDesdeFichero(String nombreArchivo) throws IOException {

        ArrayList<Ciudad> ciudades = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String line;
            boolean readingCoords = false;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("NODE_COORD_SECTION")&& readingCoords!=true) {
                    readingCoords = true;
                    continue;
                }
                if (readingCoords) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length == 3) {
                        int id = Integer.parseInt(parts[0]);
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        ciudades.add(new Ciudad(id, x, y));
                    }
                }
            }
        }
        return ciudades;
    }

    public static double distanciaSolucion(ArrayList<Ciudad> solucion)
    {
        double distancia = 0;

        for(int i = 0; i < solucion.size() - 1; i++)
        {
            distancia += solucion.get(i).distancia(solucion.get(i+1));
        }

        distancia += solucion.get(0).distancia(solucion.get(solucion.size()-1));

        return distancia;
    }

    @Override
    public String toString() {
        return  "Distancia recorrida: " + distanciaRecorridaTotal +'\n'+
                "Nombre: " + name + '\n' +
                "Comment: " + comment + '\n' +
                "Dimension= " + dimension;
    }
}
