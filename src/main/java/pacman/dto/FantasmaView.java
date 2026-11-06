package pacman.dto;

import java.io.Serializable;

/** DTO de un fantasma: sólo el estado que la vista necesita para dibujarlo. */
public class FantasmaView implements Serializable {

    private int fila;
    private int columna;
    private String estado;   // PERSIGUIENDO, ASUSTADO o REGRESANDO

    public FantasmaView() {
    }

    public FantasmaView(int fila, int columna, String estado) {
        this.fila = fila;
        this.columna = columna;
        this.estado = estado;
    }

    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }

    public int getColumna() { return columna; }
    public void setColumna(int columna) { this.columna = columna; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Fantasma(" + fila + "," + columna + ") " + estado;
    }
}
