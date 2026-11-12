package pacman.controlador;

import org.junit.jupiter.api.Test;
import pacman.dto.PartidaView;
import pacman.modelo.EstadoPartida;
import pacman.excepciones.PartidaException;
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
    void moverPacManYTickAvanzanLaPartida() throws PartidaException {
        Controlador c = Controlador.getInstance();
        c.nuevaPartida();
        int fila = c.getPartida().getFilaPacMan();
        int col = c.getPartida().getColumnaPacMan();
        c.moverDerecha();
        c.tick();
        assertEquals(fila, c.getPartida().getFilaPacMan());
        assertEquals(col + 1, c.getPartida().getColumnaPacMan());
        assertEquals(10, c.getPartida().getPuntaje());
    }

    @Test
    void moverIzquierdaArribaYAbajoSeAplicanAPacMan() throws PartidaException {
        Controlador c = Controlador.getInstance();
        c.nuevaPartida();
        int fila = c.getPartida().getFilaPacMan();
        int col = c.getPartida().getColumnaPacMan();

        c.moverIzquierda();
        c.tick();
        assertEquals(col - 1, c.getPartida().getColumnaPacMan());

        // En el laberinto por defecto, desde el origen de Pac-Man hay pared arriba y abajo:
        // pedir esas direcciones no lo hace atravesar paredes, sigue hacia la izquierda.
        c.moverArriba();
        c.tick();
        assertEquals(fila, c.getPartida().getFilaPacMan());
        c.moverAbajo();
        c.tick();
        assertEquals(fila, c.getPartida().getFilaPacMan());
    }

    @Test
    void pausarYReanudar() throws PartidaException {
        Controlador c = Controlador.getInstance();
        c.nuevaPartida();
        c.pausar();
        assertEquals("PAUSADA", c.getPartida().getEstado());
        c.reanudar();
        assertEquals("EN_CURSO", c.getPartida().getEstado());
    }

    @Test
    void reiniciarVuelveAEmpezar() throws PartidaException {
        Controlador c = Controlador.getInstance();
        c.nuevaPartida();
        int col = c.getPartida().getColumnaPacMan();
        c.moverDerecha();
        c.tick();
        assertThrows(PartidaException.class, c::reiniciar);   // sólo se reinicia al terminar
        c.nuevaPartida();
        assertEquals(0, c.getPartida().getPuntaje());
        assertEquals(col, c.getPartida().getColumnaPacMan());
    }

    @Test
    void recibeLasNotificacionesDelJuegoYLasReenviaALaVista() {
        Controlador c = Controlador.getInstance();
        int[] avisos = {0};
        Runnable oyente = () -> avisos[0]++;
        c.agregarListener(oyente);
        c.nuevaPartida();
        c.tick();
        c.quitarListener(oyente);
        assertEquals(2, avisos[0]);
    }
}
