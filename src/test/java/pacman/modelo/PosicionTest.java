package pacman.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PosicionTest {

    @Test
    void moverArribaDisminuyeLaFila() {
        assertEquals(new Posicion(2, 5), new Posicion(3, 5).mover(Direccion.ARRIBA));
    }

    @Test
    void moverDerechaAumentaLaColumna() {
        assertEquals(new Posicion(3, 6), new Posicion(3, 5).mover(Direccion.DERECHA));
    }

    @Test
    void moverNoModificaLaPosicionOriginal() {
        Posicion p = new Posicion(3, 5);
        p.mover(Direccion.ABAJO);
        assertEquals(new Posicion(3, 5), p);
    }

    @Test
    void distanciaEsManhattan() {
        assertEquals(7, new Posicion(1, 1).distanciaA(new Posicion(4, 5)));
    }

    @Test
    void dosPosicionesConMismasCoordenadasSonIguales() {
        assertEquals(new Posicion(2, 2), new Posicion(2, 2));
        assertEquals(new Posicion(2, 2).hashCode(), new Posicion(2, 2).hashCode());
        assertNotEquals(new Posicion(2, 2), new Posicion(2, 3));
    }
}
