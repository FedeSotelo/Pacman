package pacman.modelo;

/** Una celda del tablero: puede ser pared o pasillo, y un pasillo puede contener un punto. */
public class Celda {

    private final Posicion posicion;
    private final boolean pared;
    private Punto punto;

    public Celda(Posicion posicion, boolean pared, Punto punto) {
        this.posicion = posicion;
        this.pared = pared;
        this.punto = pared ? null : punto;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public boolean esPared() {
        return pared;
    }

    public boolean tienePunto() {
        return punto != null;
    }

    public Punto getPunto() {
        return punto;
    }

    /** Retira el punto de la celda y lo devuelve (null si no había). */
    public Punto quitarPunto() {
        Punto p = punto;
        punto = null;
        return p;
    }

    void ponerPunto(Punto p) {
        if (!pared) punto = p;
    }
}
