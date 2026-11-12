package pacman.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import pacman.dto.FantasmaView;
import pacman.dto.PartidaView;
import pacman.excepciones.LaberintoInvalidoException;
import pacman.excepciones.PartidaException;

/**
 * Fachada del dominio: coordina laberinto, Pac-Man y fantasmas, lleva vidas, puntaje
 * y estado de la partida, y notifica a los observadores en cada cambio.
 */
public class Juego {

    public static final int VIDAS_INICIALES = 3;
    public static final int PUNTOS_POR_FANTASMA = 200;
    public static final int DURACION_ASUSTADO = 40;

    private final Laberinto laberinto;
    private final PacMan pacman;
    private final List<Fantasma> fantasmas = new ArrayList<>();
    private final List<JuegoListener> listeners = new ArrayList<>();

    private int vidas;
    private int puntaje;
    private EstadoPartida estado;

    public Juego() {
        this(Laberinto.porDefecto(), new Random());
    }

    /** @throws LaberintoInvalidoException si el laberinto no tiene origen de Pac-Man */
    public Juego(Laberinto laberinto, Random random) {
        if (laberinto.getPosicionInicialPacMan() == null)
            throw new LaberintoInvalidoException("El laberinto no tiene origen de Pac-Man ('P')");
        this.laberinto = laberinto;
        this.pacman = new PacMan(laberinto, laberinto.getPosicionInicialPacMan());
        for (Posicion origen : laberinto.getPosicionesInicialesFantasmas()) {
            fantasmas.add(new Fantasma(laberinto, origen, pacman, random));
        }
        iniciar();   // el juego siempre tiene un estado válido, aunque la vista consulte antes de nuevaPartida()
    }

    // ---- ciclo de vida de la partida ----

    public void iniciar() {
        vidas = VIDAS_INICIALES;
        puntaje = 0;
        laberinto.reponerPuntos();
        pacman.volverAlOrigen();
        for (Fantasma f : fantasmas) f.volverAlOrigen();
        estado = EstadoPartida.EN_CURSO;
        notificar();
    }

    /** El enunciado permite reiniciar al finalizar la partida, ganada o perdida. */
    public void reiniciar() throws PartidaException {
        if (!estaTerminada())
            throw new PartidaException("Sólo se puede reiniciar una partida terminada");
        iniciar();
    }

    public void pausar() throws PartidaException {
        if (estado != EstadoPartida.EN_CURSO)
            throw new PartidaException("Sólo se puede pausar una partida en curso");
        estado = EstadoPartida.PAUSADA;
        notificar();
    }

    public void reanudar() throws PartidaException {
        if (estado != EstadoPartida.PAUSADA)
            throw new PartidaException("Sólo se puede reanudar una partida pausada");
        estado = EstadoPartida.EN_CURSO;
        notificar();
    }

    public void cambiarDireccionPacMan(Direccion d) throws PartidaException {
        if (d == null)
            throw new PartidaException("Hay que indicar una dirección");
        if (estaTerminada())
            throw new PartidaException("La partida terminó: no se puede mover a Pac-Man");
        pacman.cambiarDireccion(d);
    }

    public boolean estaTerminada() {
        return estado == EstadoPartida.GANADA || estado == EstadoPartida.PERDIDA;
    }

    /** Un paso de la simulación. Sólo tiene efecto con la partida en curso. */
    public void actualizar() {
        if (estado != EstadoPartida.EN_CURSO) return;
        moverPersonajes();
        comerPunto();
        verificarFinDePartida();
        notificar();
    }

    /** Asusta a todos los fantasmas; cada uno decide si puede asustarse. */
    public void asustarFantasmas(int duracion) {
        for (Fantasma f : fantasmas) f.asustar(duracion);
    }

    // ---- pasos internos de actualizar() ----

    private void moverPersonajes() {
        pacman.mover();
        for (Fantasma f : fantasmas) f.mover();
        resolverEncuentros();
        for (Fantasma f : fantasmas) f.pasarTiempo();
    }

    /**
     * Cada fantasma sabe si se encontró con Pac-Man y si puede ser comido o es peligroso.
     * Juego actualiza lo que le pertenece: el puntaje y las vidas.
     */
    private void resolverEncuentros() {
        for (Fantasma f : fantasmas) {
            if (!f.seEncuentraCon(pacman)) continue;
            if (f.puedeSerComido()) {
                f.serComido();
                puntaje += PUNTOS_POR_FANTASMA;
            } else if (f.esPeligroso()) {
                perderVida();
                return;   // se reiniciaron las posiciones: no hay más encuentros en este paso
            }
        }
    }

    private void perderVida() {
        vidas--;
        if (vidas <= 0) {
            estado = EstadoPartida.PERDIDA;
            return;
        }
        pacman.volverAlOrigen();
        for (Fantasma f : fantasmas) f.volverAlOrigen();
    }

    /** Pac-Man come el punto de su celda; Juego suma su valor y, si es súper, asusta a los fantasmas. */
    private void comerPunto() {
        if (estado != EstadoPartida.EN_CURSO) return;
        Punto punto = pacman.comerPunto();
        if (punto == null) return;
        puntaje += punto.getValor();
        if (punto.esSuper()) asustarFantasmas(DURACION_ASUSTADO);
    }

    private void verificarFinDePartida() {
        if (estado == EstadoPartida.EN_CURSO && laberinto.puntosRestantes() == 0) {
            estado = EstadoPartida.GANADA;
        }
    }

    // ---- observadores ----

    public void agregarListener(JuegoListener l) {
        listeners.add(l);
    }

    private void notificar() {
        for (JuegoListener l : listeners) l.onCambioDeEstado(this);
    }

    // ---- DTO para la vista ----

    /** Arma el DTO con el estado completo de la partida. Juego es el experto en esa información. */
    public PartidaView toView() {
        PartidaView v = new PartidaView();
        v.setVidas(vidas);
        v.setPuntaje(puntaje);
        v.setEstado(estado.name());
        v.setFilaPacMan(pacman.getPosicion().getFila());
        v.setColumnaPacMan(pacman.getPosicion().getColumna());
        v.setTablero(laberinto.toCaracteres());
        List<FantasmaView> vistas = new ArrayList<>();
        for (Fantasma f : fantasmas) vistas.add(f.toView());
        v.setFantasmas(vistas);
        return v;
    }

    // ---- consultas ----

    public Laberinto getLaberinto() {
        return laberinto;
    }

    public PacMan getPacMan() {
        return pacman;
    }

    public List<Fantasma> getFantasmas() {
        return Collections.unmodifiableList(fantasmas);
    }

    public int getVidas() {
        return vidas;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public EstadoPartida getEstado() {
        return estado;
    }
}
