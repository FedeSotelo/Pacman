package pacman.controlador;

import org.junit.jupiter.api.Test;
import pacman.dto.PartidaView;
import pacman.modelo.EstadoPartida;
import static org.junit.jupiter.api.Assertions.*;

class ControladorTest {

    @Test
    void esSingleton() {
        assertSame(Controlador.getInstance(), Controlador.getInstance());
    }

    @Test
    void nuevaPartidaDejaElJuegoEnCurso() {
        Controlador.getInstance().nuevaPartida();
        PartidaView v = Controlador.getInstance().getPartida();
        assertEquals("EN_CURSO", v.getEstado());
        assertEquals(3, v.getVidas());
        assertEquals(0, v.getPuntaje());
        assertEquals(4, v.getFantasmas().size());
    }

    @Test
    void moverPacManYTickAvanzanLaPartida() {
        Controlador.getInstance().nuevaPartida();
        int fila = Controlador.getInstance().getPartida().getFilaPacMan();
        int col = Controlador.getInstance().getPartida().getColumnaPacMan();
        Controlador.getInstance().moverDerecha();
        Controlador.getInstance().tick();
        assertEquals(fila, Controlador.getInstance().getPartida().getFilaPacMan());
        assertEquals(col + 1, Controlador.getInstance().getPartida().getColumnaPacMan());
        assertEquals(10, Controlador.getInstance().getPartida().getPuntaje());
    }

    @Test
    void moverIzquierdaArribaYAbajoSeAplicanAPacMan() {
        Controlador.getInstance().nuevaPartida();
        int fila = Controlador.getInstance().getPartida().getFilaPacMan();
        int col = Controlador.getInstance().getPartida().getColumnaPacMan();

        Controlador.getInstance().moverIzquierda();
        Controlador.getInstance().tick();
        assertEquals(col - 1, Controlador.getInstance().getPartida().getColumnaPacMan());

        // En el laberinto por defecto, desde el origen de Pac-Man hay pared arriba y abajo:
        // pedir esas direcciones no lo hace atravesar paredes, sigue hacia la izquierda.
        Controlador.getInstance().moverArriba();
        Controlador.getInstance().tick();
        assertEquals(fila, Controlador.getInstance().getPartida().getFilaPacMan());
        Controlador.getInstance().moverAbajo();
        Controlador.getInstance().tick();
        assertEquals(fila, Controlador.getInstance().getPartida().getFilaPacMan());
    }

    @Test
    void pausarYReanudar() {
        Controlador.getInstance().nuevaPartida();
        Controlador.getInstance().pausar();
        assertEquals("PAUSADA", Controlador.getInstance().getPartida().getEstado());
        Controlador.getInstance().reanudar();
        assertEquals("EN_CURSO", Controlador.getInstance().getPartida().getEstado());
    }

    @Test
    void reiniciarVuelveAEmpezar() {
        Controlador.getInstance().nuevaPartida();
        int col = Controlador.getInstance().getPartida().getColumnaPacMan();
        Controlador.getInstance().moverDerecha();
        Controlador.getInstance().tick();
        Controlador.getInstance().reiniciar();
        assertEquals(0, Controlador.getInstance().getPartida().getPuntaje());
        assertEquals(col, Controlador.getInstance().getPartida().getColumnaPacMan());
    }

    @Test
    void recibeLasNotificacionesDelJuegoYLasReenviaALaVista() {
        int[] avisos = {0};
        Runnable oyente = () -> avisos[0]++;
        Controlador.getInstance().agregarListener(oyente);
        Controlador.getInstance().nuevaPartida();
        Controlador.getInstance().tick();
        Controlador.getInstance().quitarListener(oyente);
        assertEquals(2, avisos[0]);
    }
}
