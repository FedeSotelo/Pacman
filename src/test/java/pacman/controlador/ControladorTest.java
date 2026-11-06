package pacman.controlador;

import org.junit.jupiter.api.Test;
import pacman.dto.PartidaView;
import pacman.modelo.Direccion;
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
        Controlador c = Controlador.getInstance();
        c.nuevaPartida();
        int fila = c.getPartida().getFilaPacMan();
        int col = c.getPartida().getColumnaPacMan();
        c.moverPacMan(Direccion.DERECHA);
        c.tick();
        assertEquals(fila, c.getPartida().getFilaPacMan());
        assertEquals(col + 1, c.getPartida().getColumnaPacMan());
        assertEquals(10, c.getPartida().getPuntaje());
    }

    @Test
    void pausarYReanudar() {
        Controlador c = Controlador.getInstance();
        c.nuevaPartida();
        c.pausar();
        assertEquals("PAUSADA", c.getPartida().getEstado());
        c.reanudar();
        assertEquals("EN_CURSO", c.getPartida().getEstado());
    }

    @Test
    void reiniciarVuelveAEmpezar() {
        Controlador c = Controlador.getInstance();
        c.nuevaPartida();
        int col = c.getPartida().getColumnaPacMan();
        c.moverPacMan(Direccion.DERECHA);
        c.tick();
        c.reiniciar();
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
