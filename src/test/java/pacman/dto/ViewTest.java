package pacman.dto;

import org.junit.jupiter.api.Test;
import pacman.modelo.*;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class ViewTest {

    private static final String[] TABLERO = {
        "#####",
        "#o.F#",
        "#P..#",
        "#####",
    };

    @Test
    void elFantasmaGeneraSuViewConPosicionYEstado() {
        Laberinto lab = new Laberinto(TABLERO);
        Fantasma f = new Fantasma(lab, new Posicion(1, 3), new PacMan(lab, new Posicion(2, 1)), new Random(1));
        f.asustar(5);
        FantasmaView v = f.toView();
        assertEquals(1, v.getFila());
        assertEquals(3, v.getColumna());
        assertEquals("ASUSTADO", v.getEstado());
    }

    @Test
    void elJuegoGeneraSuViewConTodoLoQueNecesitaLaVista() {
        Juego j = new Juego(new Laberinto(TABLERO), new Random(1));
        j.iniciar();
        PartidaView v = j.toView();
        assertEquals(3, v.getVidas());
        assertEquals(0, v.getPuntaje());
        assertEquals("EN_CURSO", v.getEstado());
        assertEquals(2, v.getFilaPacMan());
        assertEquals(1, v.getColumnaPacMan());
        assertEquals(1, v.getFantasmas().size());
        assertEquals("PERSIGUIENDO", v.getFantasmas().get(0).getEstado());
        assertEquals(4, v.getTablero().length);
        assertEquals('#', v.getTablero()[0][0]);
        assertEquals('o', v.getTablero()[1][1]);
        assertEquals('.', v.getTablero()[1][2]);
        assertEquals(' ', v.getTablero()[1][3]);   // origen del fantasma: pasillo sin punto
    }

    @Test
    void elTableroDelViewReflejaLosPuntosComidos() {
        Juego j = new Juego(new Laberinto(TABLERO), new Random(1));
        j.iniciar();
        j.cambiarDireccionPacMan(Direccion.DERECHA);
        j.actualizar();
        assertEquals(' ', j.toView().getTablero()[2][2]);
    }

    @Test
    void losViewsTienenConstructorVacioYSetters() {
        PartidaView p = new PartidaView();
        p.setPuntaje(100);
        assertEquals(100, p.getPuntaje());
        FantasmaView f = new FantasmaView();
        f.setEstado("REGRESANDO");
        assertEquals("REGRESANDO", f.getEstado());
    }
}
