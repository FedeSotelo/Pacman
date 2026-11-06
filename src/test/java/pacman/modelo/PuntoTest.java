package pacman.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PuntoTest {

    @Test
    void elPuntoComunVale10() {
        assertEquals(10, Punto.comun().getValor());
    }

    @Test
    void elSuperPuntoVale50() {
        assertEquals(50, Punto.superPunto().getValor());
    }
}
