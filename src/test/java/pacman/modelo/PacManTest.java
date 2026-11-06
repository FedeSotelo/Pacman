package pacman.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PacManTest {

    // Pasillo horizontal con una bifurcación hacia arriba en la columna 3
    private final Laberinto lab = new Laberinto(new String[] {
        "######",
        "###.##",
        "#....#",
        "######",
    });

    private PacMan pacman() {
        return new PacMan(lab, new Posicion(2, 1));
    }

    @Test
    void arrancaEnSuPosicionInicialSinDireccion() {
        PacMan p = pacman();
        assertEquals(new Posicion(2, 1), p.getPosicion());
        assertNull(p.getDireccion());
    }

    @Test
    void sinDireccionIndicadaNoSeMueve() {
        PacMan p = pacman();
        p.mover();
        assertEquals(new Posicion(2, 1), p.getPosicion());
    }

    @Test
    void avanzaEnLaDireccionDeseadaSiNoHayPared() {
        PacMan p = pacman();
        p.cambiarDireccion(Direccion.DERECHA);
        p.mover();
        assertEquals(new Posicion(2, 2), p.getPosicion());
        assertEquals(Direccion.DERECHA, p.getDireccion());
    }

    @Test
    void noAtraviesaParedes() {
        PacMan p = pacman();
        p.cambiarDireccion(Direccion.ARRIBA); // (1,1) es pared
        p.mover();
        assertEquals(new Posicion(2, 1), p.getPosicion());
    }

    @Test
    void siLaDireccionDeseadaEstaBloqueadaSigueEnLaActual() {
        PacMan p = pacman();
        p.cambiarDireccion(Direccion.DERECHA);
        p.mover();                          // (2,2)
        p.cambiarDireccion(Direccion.ABAJO);   // (3,2) es pared
        p.mover();
        assertEquals(new Posicion(2, 3), p.getPosicion());
        assertEquals(Direccion.DERECHA, p.getDireccion());
    }

    @Test
    void doblaCuandoLaDireccionDeseadaSeHabilita() {
        PacMan p = pacman();
        p.cambiarDireccion(Direccion.DERECHA);
        p.mover();                          // (2,2)
        p.cambiarDireccion(Direccion.ARRIBA);  // (1,2) es pared: sigue a la derecha
        p.mover();                          // (2,3)
        assertEquals(new Posicion(2, 3), p.getPosicion());
        p.mover();                          // (1,3) ya es pasillo: dobla
        assertEquals(new Posicion(1, 3), p.getPosicion());
        assertEquals(Direccion.ARRIBA, p.getDireccion());
    }

    @Test
    void volverAlOrigenRestauraLaPosicionInicial() {
        PacMan p = pacman();
        p.cambiarDireccion(Direccion.DERECHA);
        p.mover();
        p.volverAlOrigen();
        assertEquals(new Posicion(2, 1), p.getPosicion());
        assertNull(p.getDireccion());
    }
}
