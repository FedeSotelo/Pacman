package pacman.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de la partida: todo lo que la vista necesita para dibujar un cuadro.
 * Sin lógica de negocio. El tablero usa '#' pared, '.' punto, 'o' súper punto, ' ' pasillo.
 */
public class PartidaView implements Serializable {

    private int vidas;
    private int puntaje;
    private String estado;   // EN_CURSO, PAUSADA, GANADA o PERDIDA
    private int filaPacMan;
    private int columnaPacMan;
    private char[][] tablero;
    private List<FantasmaView> fantasmas = new ArrayList<>();

    public PartidaView() {
    }

    public int getVidas() { return vidas; }
    public void setVidas(int vidas) { this.vidas = vidas; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getFilaPacMan() { return filaPacMan; }
    public void setFilaPacMan(int filaPacMan) { this.filaPacMan = filaPacMan; }

    public int getColumnaPacMan() { return columnaPacMan; }
    public void setColumnaPacMan(int columnaPacMan) { this.columnaPacMan = columnaPacMan; }

    public char[][] getTablero() { return tablero; }
    public void setTablero(char[][] tablero) { this.tablero = tablero; }

    public List<FantasmaView> getFantasmas() { return fantasmas; }
    public void setFantasmas(List<FantasmaView> fantasmas) { this.fantasmas = fantasmas; }

    @Override
    public String toString() {
        return "Partida " + estado + " vidas=" + vidas + " puntaje=" + puntaje
                + " pacman=(" + filaPacMan + "," + columnaPacMan + ") fantasmas=" + fantasmas;
    }
}
