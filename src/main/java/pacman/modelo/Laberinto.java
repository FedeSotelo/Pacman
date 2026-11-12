package pacman.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pacman.excepciones.LaberintoInvalidoException;

/**
 * Grilla de celdas. Se construye a partir de un dibujo de texto:
 *   '#' pared, '.' punto común, 'o' súper punto, ' ' pasillo vacío,
 *   'P' origen de Pac-Man (sin punto), 'F' origen de un fantasma (sin punto).
 */
public class Laberinto {

    private static final String CARACTERES_VALIDOS = "#.o PF";

    private final String[] dibujo;
    private final int filas;
    private final int columnas;
    private final Celda[][] celdas;
    private Posicion posicionInicialPacMan;
    private final List<Posicion> posicionesInicialesFantasmas = new ArrayList<>();

    /** @throws LaberintoInvalidoException si el dibujo está vacío, no es rectangular, tiene caracteres desconocidos o más de un Pac-Man */
    public Laberinto(String[] dibujo) {
        validar(dibujo);
        this.dibujo = dibujo.clone();
        this.filas = dibujo.length;
        this.columnas = dibujo[0].length();
        this.celdas = new Celda[filas][columnas];
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                char ch = dibujo[f].charAt(c);
                Posicion pos = new Posicion(f, c);
                celdas[f][c] = new Celda(pos, ch == '#', puntoPara(ch));
                if (ch == 'P') posicionInicialPacMan = pos;
                if (ch == 'F') posicionesInicialesFantasmas.add(pos);
            }
        }
    }

    private static void validar(String[] dibujo) {
        if (dibujo == null || dibujo.length == 0 || dibujo[0].isEmpty())
            throw new LaberintoInvalidoException("El dibujo del laberinto está vacío");
        int pacmans = 0;
        for (int f = 0; f < dibujo.length; f++) {
            if (dibujo[f].length() != dibujo[0].length())
                throw new LaberintoInvalidoException("La fila " + f + " no tiene el mismo largo que la primera");
            for (char ch : dibujo[f].toCharArray()) {
                if (CARACTERES_VALIDOS.indexOf(ch) < 0)
                    throw new LaberintoInvalidoException("Carácter desconocido en la fila " + f + ": '" + ch + "'");
                if (ch == 'P') pacmans++;
            }
        }
        if (pacmans > 1)
            throw new LaberintoInvalidoException("El laberinto tiene más de un origen de Pac-Man");
    }

    private static Punto puntoPara(char ch) {
        if (ch == '.') return Punto.comun();
        if (ch == 'o') return Punto.superPunto();
        return null;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public Celda getCelda(Posicion p) {
        return celdas[p.getFila()][p.getColumna()];
    }

    /** Fuera del tablero también se considera pared. */
    public boolean esPared(Posicion p) {
        if (p.getFila() < 0 || p.getFila() >= filas) return true;
        if (p.getColumna() < 0 || p.getColumna() >= columnas) return true;
        return celdas[p.getFila()][p.getColumna()].esPared();
    }

    /** Direcciones en las que se puede avanzar desde p sin chocar una pared. */
    public List<Direccion> direccionesDisponibles(Posicion p) {
        List<Direccion> disponibles = new ArrayList<>();
        for (Direccion d : Direccion.values()) {
            if (!esPared(p.mover(d))) disponibles.add(d);
        }
        return disponibles;
    }

    /** Retira el punto de la celda en p y lo devuelve (null si no había). */
    public Punto quitarPuntoEn(Posicion p) {
        return getCelda(p).quitarPunto();
    }

    public int puntosRestantes() {
        int total = 0;
        for (Celda[] fila : celdas)
            for (Celda celda : fila)
                if (celda.tienePunto()) total++;
        return total;
    }

    /** Vuelve a colocar todos los puntos del dibujo original. */
    public void reponerPuntos() {
        for (int f = 0; f < filas; f++)
            for (int c = 0; c < columnas; c++)
                celdas[f][c].ponerPunto(puntoPara(dibujo[f].charAt(c)));
    }

    /** Representación del tablero actual como caracteres: '#', '.', 'o' o ' ' (sin personajes). */
    public char[][] toCaracteres() {
        char[][] m = new char[filas][columnas];
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Celda celda = celdas[f][c];
                if (celda.esPared()) m[f][c] = '#';
                else if (!celda.tienePunto()) m[f][c] = ' ';
                else m[f][c] = celda.getPunto().esSuper() ? 'o' : '.';
            }
        }
        return m;
    }

    public Posicion getPosicionInicialPacMan() {
        return posicionInicialPacMan;
    }

    public List<Posicion> getPosicionesInicialesFantasmas() {
        return Collections.unmodifiableList(posicionesInicialesFantasmas);
    }

    /** Laberinto de la partida: puntos en todos los pasillos y súper puntos en las esquinas. */
    public static Laberinto porDefecto() {
        return new Laberinto(new String[] {
            "###################",
            "#o.......#.......o#",
            "#.##.###.#.###.##.#",
            "#.................#",
            "#.##.#.#####.#.##.#",
            "#....#...#...#....#",
            "####.###.#.###.####",
            "#....#.......#....#",
            "#.##.#.##F##.#.##.#",
            "#......#FFF#......#",
            "#.##.#.#####.#.##.#",
            "#....#...P...#....#",
            "####.###.#.###.####",
            "#....#...#...#....#",
            "#.##.#.#####.#.##.#",
            "#.................#",
            "#.##.###.#.###.##.#",
            "#o.......#.......o#",
            "###################",
        });
    }
}
