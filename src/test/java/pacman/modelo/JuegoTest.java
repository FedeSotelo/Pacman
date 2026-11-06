package pacman.modelo;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class JuegoTest {

    /** Dos súper puntos, 10 comunes, Pac-Man y un fantasma en extremos opuestos. */
    private static final String[] TABLERO = {
        "########",
        "#o....o#",
        "#.####.#",
        "#P....F#",
        "########",
    };

    /** Pasillo cerrado: el fantasma sólo puede moverse hacia Pac-Man. El punto de (2,1) evita la victoria. */
    private static final String[] PASILLO = {
        "#####",
        "#P.F#",
        "#.###",
        "#####",
    };

    private Juego juegoCon(String[] dibujo) {
        Juego j = new Juego(new Laberinto(dibujo), new Random(1));
        j.iniciar();
        return j;
    }

    // ---- inicio ----

    @Test
    void iniciarDejaLaPartidaEnCursoConTresVidasYSinPuntaje() {
        Juego j = juegoCon(TABLERO);
        assertEquals(EstadoPartida.EN_CURSO, j.getEstado());
        assertEquals(3, j.getVidas());
        assertEquals(0, j.getPuntaje());
        assertEquals(12, j.getLaberinto().puntosRestantes());
    }

    @Test
    void losPersonajesArrancanEnSusOrigenes() {
        Juego j = juegoCon(TABLERO);
        assertEquals(new Posicion(3, 1), j.getPacMan().getPosicion());
        assertEquals(1, j.getFantasmas().size());
        assertEquals(new Posicion(3, 6), j.getFantasmas().get(0).getPosicion());
    }

    // ---- puntos ----

    @Test
    void comerUnPuntoComunSuma10YLoQuitaDelTablero() {
        Juego j = juegoCon(TABLERO);
        j.cambiarDireccionPacMan(Direccion.DERECHA);
        j.actualizar();
        assertEquals(new Posicion(3, 2), j.getPacMan().getPosicion());
        assertEquals(10, j.getPuntaje());
        assertEquals(11, j.getLaberinto().puntosRestantes());
    }

    @Test
    void comerUnSuperPuntoSuma50YAsustaALosFantasmas() {
        Juego j = juegoCon(TABLERO);
        j.cambiarDireccionPacMan(Direccion.ARRIBA);
        j.actualizar();   // (2,1): punto común
        j.actualizar();   // (1,1): súper punto
        assertEquals(60, j.getPuntaje());
        assertTrue(j.getFantasmas().get(0).estaAsustado());
    }

    @Test
    void unFantasmaRegresandoNoSeAsusta() {
        Juego j = juegoCon(TABLERO);
        j.getFantasmas().get(0).serComido();
        j.asustarFantasmas(10);
        assertTrue(j.getFantasmas().get(0).estaRegresando());
    }

    // ---- colisiones ----

    @Test
    void colisionarConUnFantasmaPersiguiendoPierdeUnaVidaYReiniciaPosiciones() {
        Juego j = juegoCon(PASILLO);
        j.cambiarDireccionPacMan(Direccion.DERECHA);
        j.actualizar();   // ambos van a (1,2)
        assertEquals(2, j.getVidas());
        assertEquals(new Posicion(1, 1), j.getPacMan().getPosicion());
        assertEquals(new Posicion(1, 3), j.getFantasmas().get(0).getPosicion());
        assertEquals(EstadoPartida.EN_CURSO, j.getEstado());
    }

    @Test
    void elCruceEntrePacManYUnFantasmaTambienEsColision() {
        Juego j = juegoCon(PASILLO);
        Fantasma f = j.getFantasmas().get(0);
        // Dejamos al fantasma pegado a Pac-Man: Pac-Man (1,1), fantasma (1,2)
        f.asustar(1);                          // se mueve al azar: única opción (1,2)
        j.actualizar();                        // Pac-Man quieto; fantasma a (1,2) y vuelve a perseguir
        assertEquals(new Posicion(1, 2), f.getPosicion());
        assertEquals(3, j.getVidas());         // asustado: no quita vida
        j.cambiarDireccionPacMan(Direccion.DERECHA);
        j.actualizar();                        // se cruzan: Pac-Man a (1,2), fantasma a (1,1)
        assertEquals(2, j.getVidas());
    }

    @Test
    void comerUnFantasmaAsustadoSuma200YLoMandaAlOrigen() {
        Juego j = juegoCon(PASILLO);
        j.asustarFantasmas(10);
        j.cambiarDireccionPacMan(Direccion.DERECHA);
        j.actualizar();   // ambos van a (1,2)
        assertEquals(10 + 200, j.getPuntaje());
        assertEquals(3, j.getVidas());
        assertTrue(j.getFantasmas().get(0).estaRegresando());
    }

    @Test
    void unFantasmaRegresandoNoQuitaVidasYRetomaLaPersecucionAlLlegar() {
        Juego j = juegoCon(PASILLO);
        j.asustarFantasmas(10);
        j.cambiarDireccionPacMan(Direccion.DERECHA);
        j.actualizar();   // lo come en (1,2)
        j.actualizar();   // Pac-Man a (1,3); fantasma vuelve a (1,3), su origen
        assertEquals(3, j.getVidas());
        assertEquals(EstadoFantasma.PERSIGUIENDO, j.getFantasmas().get(0).getEstado());
    }

    // ---- fin de partida ----

    @Test
    void perderLasTresVidasTerminaLaPartidaEnDerrota() {
        Juego j = juegoCon(PASILLO);
        for (int i = 0; i < 3; i++) {
            j.cambiarDireccionPacMan(Direccion.DERECHA);
            j.actualizar();
        }
        assertEquals(0, j.getVidas());
        assertEquals(EstadoPartida.PERDIDA, j.getEstado());
    }

    @Test
    void comerTodosLosPuntosTerminaLaPartidaEnVictoria() {
        Juego j = juegoCon(new String[] {"#####", "#P..#", "#####"});
        j.cambiarDireccionPacMan(Direccion.DERECHA);
        j.actualizar();
        assertEquals(EstadoPartida.EN_CURSO, j.getEstado());
        j.actualizar();
        assertEquals(EstadoPartida.GANADA, j.getEstado());
        assertEquals(20, j.getPuntaje());
    }

    @Test
    void unaPartidaTerminadaNoSeActualiza() {
        Juego j = juegoCon(new String[] {"#####", "#P..#", "#####"});
        j.cambiarDireccionPacMan(Direccion.DERECHA);
        j.actualizar();
        j.actualizar();
        j.actualizar();
        assertEquals(EstadoPartida.GANADA, j.getEstado());
        assertEquals(new Posicion(1, 3), j.getPacMan().getPosicion());
    }

    // ---- pausa y reinicio ----

    @Test
    void mientrasEstaPausadaNadaSeMueve() {
        Juego j = juegoCon(TABLERO);
        j.cambiarDireccionPacMan(Direccion.DERECHA);
        j.pausar();
        assertEquals(EstadoPartida.PAUSADA, j.getEstado());
        j.actualizar();
        assertEquals(new Posicion(3, 1), j.getPacMan().getPosicion());
        j.reanudar();
        j.actualizar();
        assertEquals(new Posicion(3, 2), j.getPacMan().getPosicion());
    }

    @Test
    void soloSePuedePausarUnaPartidaEnCurso() {
        Juego j = juegoCon(new String[] {"#####", "#P..#", "#####"});
        j.cambiarDireccionPacMan(Direccion.DERECHA);
        j.actualizar();
        j.actualizar();   // ganada
        j.pausar();
        assertEquals(EstadoPartida.GANADA, j.getEstado());
    }

    @Test
    void reiniciarVuelveATodoElEstadoInicial() {
        Juego j = juegoCon(PASILLO);
        for (int i = 0; i < 3; i++) {
            j.cambiarDireccionPacMan(Direccion.DERECHA);
            j.actualizar();
        }
        j.reiniciar();
        assertEquals(EstadoPartida.EN_CURSO, j.getEstado());
        assertEquals(3, j.getVidas());
        assertEquals(0, j.getPuntaje());
        assertEquals(2, j.getLaberinto().puntosRestantes());
        assertEquals(new Posicion(1, 1), j.getPacMan().getPosicion());
    }

    // ---- observadores ----

    @Test
    void notificaALosListenersEnCadaCambio() {
        Juego j = new Juego(new Laberinto(TABLERO), new Random(1));
        List<String> eventos = new ArrayList<>();
        j.agregarListener(juego -> eventos.add(juego.getEstado().name()));
        j.iniciar();
        j.actualizar();
        j.pausar();
        assertEquals(List.of("EN_CURSO", "EN_CURSO", "PAUSADA"), eventos);
    }

    // ---- laberinto real ----

    @Test
    void unJuegoRecienCreadoYaTieneUnEstadoValido() {
        Juego j = new Juego();
        assertEquals(EstadoPartida.EN_CURSO, j.getEstado());
        assertNotNull(j.toView());
    }

    @Test
    void enElLaberintoRealLosFantasmasAlcanzanAPacManQuieto() {
        Juego j = new Juego(Laberinto.porDefecto(), new Random(1));
        for (int i = 0; i < 100 && j.getVidas() == 3; i++) j.actualizar();
        assertEquals(2, j.getVidas());
    }

    @Test
    void enElLaberintoRealUnFantasmaComidoLlegaASuOrigen() {
        Laberinto lab = Laberinto.porDefecto();
        PacMan pacman = new PacMan(lab, lab.getPosicionInicialPacMan());
        Fantasma f = new Fantasma(lab, new Posicion(9, 9), pacman, new Random(1));
        for (int i = 0; i < 6; i++) f.mover();        // sale de la casa y queda del otro lado de la pared
        assertNotEquals(f.getPosicionInicial(), f.getPosicion());
        f.serComido();
        for (int i = 0; i < 100 && f.estaRegresando(); i++) {
            f.mover();
            f.pasarTiempo();
        }
        assertEquals(f.getPosicionInicial(), f.getPosicion());
        assertEquals(EstadoFantasma.PERSIGUIENDO, f.getEstado());
    }
}
