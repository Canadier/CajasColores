package basico.android.cftic.edu.cajascolores;

public class Puntuacion {

    private String nombre;
    private long tiempo;

    public Puntuacion(String nombre, long tiempo) {
        this.nombre = nombre;
        this.tiempo = tiempo;
    }

    public Puntuacion() {

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getTiempo() {
        return tiempo;
    }

    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }

    @Override
    public String toString() {
        return "Puntuacion{" +
                "nombre='" + nombre + '\'' +
                ", tiempo=" + tiempo +
                '}';
    }


}
