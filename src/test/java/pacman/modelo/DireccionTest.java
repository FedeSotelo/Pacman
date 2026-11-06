package pacman.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DireccionTest {

    @Test
    void laOpuestaDeArribaEsAbajo() {
        assertEquals(Direccion.ABAJO, Direccion.ARRIBA.opuesta());
        assertEquals(Direccion.ARRIBA, Direccion.ABAJO.opuesta());
    }

    @Test
    void laOpuestaDeIzquierdaEsDerecha() {
        assertEquals(Direccion.DERECHA, Direccion.IZQUIERDA.opuesta());
        assertEquals(Direccion.IZQUIERDA, Direccion.DERECHA.opuesta());
    }
}
