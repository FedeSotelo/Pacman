package pacman.modelo;

/** Las cuatro direcciones en las que se mueven los personajes. */
public enum Direccion {
    ARRIBA(-1, 0),
    ABAJO(1, 0),
    IZQUIERDA(0, -1),
    DERECHA(0, 1);

    private final int deltaFila;
    private final int deltaColumna;

    Direccion(int deltaFila, int deltaColumna) {
        this.deltaFila = deltaFila;
        this.deltaColumna = deltaColumna;
    }

    public int getDeltaFila() {
        return deltaFila;
    }

    public int getDeltaColumna() {
        return deltaColumna;
    }

    public Direccion opuesta() {
        switch (this) {
            case ARRIBA: return ABAJO;
            case ABAJO: return ARRIBA;
            case IZQUIERDA: return DERECHA;
            default: return IZQUIERDA;
        }
    }
}
