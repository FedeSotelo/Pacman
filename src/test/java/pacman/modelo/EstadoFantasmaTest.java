package pacman.modelo;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/** Cada estado del fantasma (patrón State) probado por separado. */
class EstadoFantasmaTest {

    // Pasillo horizontal en la fila 1
    private final Laberinto lab = new Laberinto(new String[] {
        "#######",
        "#.....#",
        "#######",
    });

    private Fantasma fantasmaEn(int col, PacMan pacman) {
        return new Fantasma(lab, new Posicion(1, col), pacman, new Random(3));
    }

    // ---- Persiguiendo ----

    @Test
    void persiguiendoEsPeligrosoYNoSePuedeComer() {
        Fantasma f = fantasmaEn(1, new PacMan(lab, new Posicion(1, 5)));
        EstadoFantasma e = new Persiguiendo(f);
        assertTrue(e.esPeligroso());
        assertFalse(e.puedeSerComido());
        assertEquals("PERSIGUIENDO", e.getNombre());
    }

    @Test
    void persiguiendoEligeIrHaciaPacMan() {
        Fantasma f = fantasmaEn(3, new PacMan(lab, new Posicion(1, 1)));
        assertEquals(Direccion.IZQUIERDA, new Persiguiendo(f).elegirDireccion());
    }

    @Test
    void persiguiendoNoCambiaConElTiempo() {
        Fantasma f = fantasmaEn(1, new PacMan(lab, new Posicion(1, 5)));
        for (int i = 0; i < 50; i++) f.pasarTiempo();
        assertTrue(f.estaPersiguiendo());
    }

    // ---- Asustado ----

    @Test
    void asustadoSePuedeComerYNoEsPeligroso() {
        Fantasma f = fantasmaEn(1, new PacMan(lab, new Posicion(1, 5)));
        EstadoFantasma e = new Asustado(f, 5);
        assertTrue(e.puedeSerComido());
        assertFalse(e.esPeligroso());
        assertEquals("ASUSTADO", e.getNombre());
    }

    @Test
    void asustadoLlevaSuPropioTiempoRestante() {
        Fantasma f = fantasmaEn(1, new PacMan(lab, new Posicion(1, 5)));
        f.asustar(3);
        Asustado asustado = (Asustado) f.getEstado();
        assertEquals(3, asustado.getTiempoRestante());
        f.pasarTiempo();
        assertEquals(2, asustado.getTiempoRestante());
    }

    @Test
    void asustarDeNuevoReiniciaElTiempo() {
        Fantasma f = fantasmaEn(1, new PacMan(lab, new Posicion(1, 5)));
        f.asustar(3);
        f.pasarTiempo();
        f.pasarTiempo();
        f.asustar(3);
        f.pasarTiempo();
        f.pasarTiempo();
        assertTrue(f.estaAsustado());
    }

    @Test
    void asustadoSeMueveAunqueNoSePuedaElegirHaciaPacMan() {
        Fantasma f = fantasmaEn(3, new PacMan(lab, new Posicion(1, 5)));
        f.asustar(10);
        Direccion d = f.getEstado().elegirDireccion();
        assertTrue(d == Direccion.IZQUIERDA || d == Direccion.DERECHA);
    }

    // ---- Regresando ----

    @Test
    void regresandoNiSePuedeComerNiEsPeligroso() {
        Fantasma f = fantasmaEn(1, new PacMan(lab, new Posicion(1, 5)));
        EstadoFantasma e = new Regresando(f);
        assertFalse(e.puedeSerComido());
        assertFalse(e.esPeligroso());
        assertEquals("REGRESANDO", e.getNombre());
    }

    @Test
    void regresandoEligeIrHaciaSuOrigenAunquePacManEsteDelOtroLado() {
        PacMan pacman = new PacMan(lab, new Posicion(1, 5));
        Fantasma f = fantasmaEn(1, pacman);
        f.mover();
        f.mover();                          // (1,3), Pac-Man a la derecha y el origen a la izquierda
        f.serComido();
        assertEquals(Direccion.IZQUIERDA, f.getEstado().elegirDireccion());
    }

    @Test
    void regresandoIgnoraElSusto() {
        Fantasma f = fantasmaEn(1, new PacMan(lab, new Posicion(1, 5)));
        f.mover();
        f.serComido();
        f.asustar(10);
        assertTrue(f.estaRegresando());
    }

    @Test
    void regresandoFueraDelOrigenSigueRegresando() {
        Fantasma f = fantasmaEn(1, new PacMan(lab, new Posicion(1, 5)));
        f.mover();                          // (1,2), fuera del origen
        f.serComido();
        f.pasarTiempo();
        assertTrue(f.estaRegresando());
    }

    // ---- DTO ----

    @Test
    void elDtoDelFantasmaUsaElNombreDelEstado() {
        Fantasma f = fantasmaEn(1, new PacMan(lab, new Posicion(1, 5)));
        f.asustar(5);
        assertEquals("ASUSTADO", f.toView().getEstado());
    }
}
