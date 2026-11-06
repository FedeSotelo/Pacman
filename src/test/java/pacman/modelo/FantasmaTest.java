package pacman.modelo;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class FantasmaTest {

    // Cruz: pasillo horizontal en la fila 2 y vertical en la columna 3
    private final Laberinto lab = new Laberinto(new String[] {
        "#######",
        "###.###",
        "#.....#",
        "###.###",
        "#######",
    });

    private PacMan pacmanEn(int fila, int col) {
        return new PacMan(lab, new Posicion(fila, col));
    }

    private Fantasma fantasmaEn(int fila, int col, PacMan pacman) {
        return new Fantasma(lab, new Posicion(fila, col), pacman, new Random(7));
    }

    /** Un paso completo del fantasma tal como lo ejecuta Juego: mover y luego pasar el tiempo. */
    private void paso(Fantasma f) {
        f.mover();
        f.pasarTiempo();
    }

    @Test
    void arrancaPersiguiendo() {
        Fantasma f = fantasmaEn(2, 1, pacmanEn(2, 5));
        assertTrue(f.estaPersiguiendo());
        assertTrue(f.esPeligroso());
        assertFalse(f.puedeSerComido());
    }

    @Test
    void persiguiendoAvanzaHaciaPacMan() {
        Fantasma f = fantasmaEn(2, 1, pacmanEn(2, 5));
        f.mover();
        assertEquals(new Posicion(2, 2), f.getPosicion());
    }

    @Test
    void enUnCruceEligeLaDireccionQueMasLoAcerca() {
        Fantasma f = fantasmaEn(2, 3, pacmanEn(3, 3));
        f.mover();
        assertEquals(new Posicion(3, 3), f.getPosicion());
    }

    @Test
    void noDaMediaVueltaAunquePacManQuedeDetras() {
        Fantasma f = fantasmaEn(2, 1, pacmanEn(2, 1));
        f.mover();                          // única salida: (2,2) mirando a la derecha
        f.mover();                          // Pac-Man quedó atrás, pero sigue hacia adelante
        assertEquals(new Posicion(2, 3), f.getPosicion());
    }

    @Test
    void enUnCallejonSinSalidaDaMediaVuelta() {
        Laberinto callejon = new Laberinto(new String[] {
            "#####",
            "#...#",
            "#####",
        });
        PacMan pacman = new PacMan(callejon, new Posicion(1, 3));
        Fantasma f = new Fantasma(callejon, new Posicion(1, 2), pacman, new Random(7));
        f.mover();                          // (1,3), sin salida a la derecha
        f.mover();
        assertEquals(new Posicion(1, 2), f.getPosicion());
    }

    @Test
    void asustarLoPasaAEstadoAsustado() {
        Fantasma f = fantasmaEn(2, 1, pacmanEn(2, 5));
        f.asustar(3);
        assertTrue(f.estaAsustado());
        assertTrue(f.puedeSerComido());
        assertFalse(f.esPeligroso());
    }

    @Test
    void asustadoSeMueveAUnaCeldaDisponible() {
        Fantasma f = fantasmaEn(2, 3, pacmanEn(2, 5));
        f.asustar(10);
        f.mover();
        assertEquals(1, f.getPosicion().distanciaA(new Posicion(2, 3)));
        assertFalse(lab.esPared(f.getPosicion()));
    }

    @Test
    void asustadoVuelveAPerseguirAlAgotarseElTiempo() {
        Fantasma f = fantasmaEn(2, 3, pacmanEn(2, 5));
        f.asustar(2);
        paso(f);
        assertTrue(f.estaAsustado());
        paso(f);
        assertTrue(f.estaPersiguiendo());
    }

    @Test
    void serComidoLoPasaARegresando() {
        Fantasma f = fantasmaEn(2, 1, pacmanEn(2, 5));
        f.asustar(5);
        f.serComido();
        assertTrue(f.estaRegresando());
        assertFalse(f.puedeSerComido());
        assertFalse(f.esPeligroso());
    }

    @Test
    void regresandoNoPuedeAsustarse() {
        Fantasma f = fantasmaEn(2, 1, pacmanEn(2, 5));
        f.serComido();
        f.asustar(5);
        assertTrue(f.estaRegresando());
    }

    @Test
    void regresandoVuelveAlOrigenYRetomaLaPersecucion() {
        Fantasma f = fantasmaEn(2, 1, pacmanEn(2, 5));
        f.mover();
        f.mover();                          // (2,3)
        f.serComido();
        paso(f);                            // (2,2)
        assertTrue(f.estaRegresando());
        paso(f);                            // (2,1) = origen
        assertEquals(new Posicion(2, 1), f.getPosicion());
        assertTrue(f.estaPersiguiendo());
    }

    @Test
    void volverAlOrigenTambienRestauraLaPersecucion() {
        Fantasma f = fantasmaEn(2, 1, pacmanEn(2, 5));
        f.mover();
        f.asustar(5);
        f.volverAlOrigen();
        assertEquals(new Posicion(2, 1), f.getPosicion());
        assertTrue(f.estaPersiguiendo());
    }

    @Test
    void seEncuentraConPacManSiQuedanEnLaMismaCelda() {
        PacMan pacman = pacmanEn(2, 3);
        Fantasma f = fantasmaEn(2, 2, pacman);
        f.mover();                          // (2,3), donde está Pac-Man
        assertTrue(f.seEncuentraCon(pacman));
    }

    @Test
    void seEncuentraConPacManSiSeCruzan() {
        PacMan pacman = pacmanEn(2, 2);
        Fantasma f = fantasmaEn(2, 1, pacman);
        pacman.cambiarDireccion(Direccion.IZQUIERDA);
        pacman.mover();                     // Pac-Man (2,2) -> (2,1)
        f.mover();                          // fantasma (2,1) -> (2,2)
        assertTrue(f.seEncuentraCon(pacman));
    }

    @Test
    void noSeEncuentraConPacManSiEstaLejos() {
        PacMan pacman = pacmanEn(2, 5);
        Fantasma f = fantasmaEn(2, 1, pacman);
        f.mover();
        assertFalse(f.seEncuentraCon(pacman));
    }
}
