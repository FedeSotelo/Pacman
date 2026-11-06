package pacman.modelo;

import java.util.Objects;

/** Objeto de valor inmutable: una celda (fila, columna) del tablero. */
public final class Posicion {

    private final int fila;
    private final int columna;

    public Posicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    /** Devuelve una nueva posición desplazada una celda en la dirección dada. */
    public Posicion mover(Direccion d) {
        return new Posicion(fila + d.getDeltaFila(), columna + d.getDeltaColumna());
    }

    /** Distancia Manhattan: la que usan los fantasmas para acercarse a Pac-Man. */
    public int distanciaA(Posicion otra) {
        return Math.abs(fila - otra.fila) + Math.abs(columna - otra.columna);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Posicion)) return false;
        Posicion p = (Posicion) o;
        return fila == p.fila && columna == p.columna;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, columna);
    }

    @Override
    public String toString() {
        return "(" + fila + "," + columna + ")";
    }
}
