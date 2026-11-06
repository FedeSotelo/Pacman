package pacman.modelo;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LaberintoTest {

    private static final String[] DIBUJO = {
        "#####",
        "#o.o#",
        "#.P.#",
        "#F#.#",
        "#####",
    };

    private Laberinto laberinto() {
        return new Laberinto(DIBUJO);
    }

    @Test
    void conoceSusDimensiones() {
        assertEquals(5, laberinto().getFilas());
        assertEquals(5, laberinto().getColumnas());
    }

    @Test
    void reconoceParedes() {
        assertTrue(laberinto().esPared(new Posicion(0, 0)));
        assertTrue(laberinto().esPared(new Posicion(3, 2)));
        assertFalse(laberinto().esPared(new Posicion(1, 1)));
    }

    @Test
    void fueraDelTableroSeConsideraPared() {
        assertTrue(laberinto().esPared(new Posicion(-1, 2)));
        assertTrue(laberinto().esPared(new Posicion(2, 5)));
    }

    @Test
    void lasCeldasDePasilloTienenPuntos() {
        Laberinto lab = laberinto();
        assertTrue(lab.getCelda(new Posicion(1, 2)).tienePunto());
        assertFalse(lab.getCelda(new Posicion(1, 2)).getPunto().esSuper());
        assertTrue(lab.getCelda(new Posicion(1, 1)).getPunto().esSuper());
    }

    @Test
    void losOrigenesDePersonajesNoTienenPunto() {
        Laberinto lab = laberinto();
        assertFalse(lab.getCelda(new Posicion(2, 2)).tienePunto());
        assertFalse(lab.getCelda(new Posicion(3, 1)).tienePunto());
    }

    @Test
    void cuentaLosPuntosRestantes() {
        assertEquals(6, laberinto().puntosRestantes());
    }

    @Test
    void quitarUnPuntoLoDescuenta() {
        Laberinto lab = laberinto();
        Punto p = lab.quitarPuntoEn(new Posicion(1, 1));
        assertTrue(p.esSuper());
        assertFalse(lab.getCelda(new Posicion(1, 1)).tienePunto());
        assertEquals(5, lab.puntosRestantes());
    }

    @Test
    void reponerPuntosRestauraElTableroOriginal() {
        Laberinto lab = laberinto();
        lab.getCelda(new Posicion(1, 1)).quitarPunto();
        lab.getCelda(new Posicion(1, 2)).quitarPunto();
        lab.reponerPuntos();
        assertEquals(6, lab.puntosRestantes());
        assertTrue(lab.getCelda(new Posicion(1, 1)).getPunto().esSuper());
    }

    @Test
    void direccionesDisponiblesExcluyeLasParedes() {
        List<Direccion> dirs = laberinto().direccionesDisponibles(new Posicion(2, 2));
        assertEquals(List.of(Direccion.ARRIBA, Direccion.IZQUIERDA, Direccion.DERECHA), dirs);
    }

    @Test
    void conoceLosOrigenesDeLosPersonajes() {
        Laberinto lab = laberinto();
        assertEquals(new Posicion(2, 2), lab.getPosicionInicialPacMan());
        assertEquals(List.of(new Posicion(3, 1)), lab.getPosicionesInicialesFantasmas());
    }

    @Test
    void elLaberintoPorDefectoTieneCuatroSuperPuntosYCuatroFantasmas() {
        Laberinto lab = Laberinto.porDefecto();
        long superPuntos = 0;
        for (int f = 0; f < lab.getFilas(); f++)
            for (int c = 0; c < lab.getColumnas(); c++) {
                Celda celda = lab.getCelda(new Posicion(f, c));
                if (celda.tienePunto() && celda.getPunto().esSuper()) superPuntos++;
            }
        assertEquals(4, superPuntos);
        assertEquals(4, lab.getPosicionesInicialesFantasmas().size());
    }
}
