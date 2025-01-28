import java.util.Objects;
import java.util.PriorityQueue;

// Clase para representar una ciudad
class Ciudad implements Comparable<Ciudad> {
    int id, distanciaRestoCiudades;
    double x, y;

    Ciudad(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.distanciaRestoCiudades=0;
    }

    double distancia(Ciudad otra) {
        double distancia = Math.sqrt(Math.pow(this.x - otra.x, 2) + Math.pow(this.y - otra.y, 2));
        return distancia;
    }

    public void setDistanciaResto(int distanciaResto) {
        this.distanciaRestoCiudades = distanciaResto;
    }
/*
    @Override
    public String toString() {
        return String.format("Ciudad %d: (%.2f, %.2f): %dkm", id, x, y,distanciaRestoCiudades);
    }
*/
    @Override
    public String toString() {
        return String.format("%d", id);
    }

        @Override
        public int compareTo(Ciudad otraCiudad) {
            return Integer.compare(this.distanciaRestoCiudades, otraCiudad.distanciaRestoCiudades);
        }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
    }

}