package pacman.modelo;

import java.util.List;
import java.util.Random;
import pacman.dto.FantasmaView;

/**
 * Enemigo autónomo. Conoce el laberinto, a Pac-Man (su objetivo) y su propio estado,
 * y con eso decide hacia dónde moverse y cómo cambia de estado.
 */
public class Fantasma extends Personaje {

    private final PacMan objetivo;
    private final Random random;
    private EstadoFantasma estado = EstadoFantasma.PERSIGUIENDO;
    private int tiempoAsustado;

    public Fantasma(Laberinto laberinto, Posicion posicionInicial, PacMan objetivo, Random random) {
        super(laberinto, posicionInicial);
        this.objetivo = objetivo;
        this.random = random;
    }

    public Fantasma(Laberinto laberinto, Posicion posicionInicial, PacMan objetivo) {
        this(laberinto, posicionInicial, objetivo, new Random());
    }

    // ---- movimiento ----

    @Override
    public void mover() {
        posicionAnterior = posicion;
        avanzar(elegirDireccion());
    }

    /** Asustado se mueve al azar; persiguiendo va hacia Pac-Man; regresando va hacia su origen. */
    private Direccion elegirDireccion() {
        List<Direccion> posibles = direccionesPosibles();
        if (posibles.isEmpty()) return null;
        switch (estado) {
            case ASUSTADO:   return posibles.get(random.nextInt(posibles.size()));
            case REGRESANDO: return masCercanaA(posibles, posicionInicial);
            default:         return masCercanaA(posibles, objetivo.getPosicion());
        }
    }

    /**
     * Direcciones en las que puede avanzar: no da media vuelta salvo en un callejón sin salida.
     * Sin esta regla, al buscar siempre la celda más cercana quedaría yendo y viniendo detrás de una pared.
     */
    private List<Direccion> direccionesPosibles() {
        List<Direccion> disponibles = laberinto.direccionesDisponibles(posicion);
        if (direccion != null && disponibles.size() > 1) disponibles.remove(direccion.opuesta());
        return disponibles;
    }

    /** Entre las posibles, la que lo deja más cerca del objetivo. */
    private Direccion masCercanaA(List<Direccion> posibles, Posicion destino) {
        Direccion mejor = null;
        int mejorDistancia = Integer.MAX_VALUE;
        for (Direccion d : posibles) {
            int distancia = posicion.mover(d).distanciaA(destino);
            if (distancia < mejorDistancia) {
                mejorDistancia = distancia;
                mejor = d;
            }
        }
        return mejor;
    }

    // ---- cambios de estado ----

    /** Pasa el tiempo de un paso: se acaba el susto o, si regresaba y llegó a su origen, vuelve a perseguir. */
    public void pasarTiempo() {
        if (estado == EstadoFantasma.ASUSTADO) {
            tiempoAsustado--;
            if (tiempoAsustado <= 0) estado = EstadoFantasma.PERSIGUIENDO;
        } else if (estado == EstadoFantasma.REGRESANDO && posicion.equals(posicionInicial)) {
            estado = EstadoFantasma.PERSIGUIENDO;
        }
    }

    /** Se asusta salvo que esté regresando. Al cambiar de estado puede dar media vuelta. */
    public void asustar(int duracion) {
        if (estado == EstadoFantasma.REGRESANDO) return;
        estado = EstadoFantasma.ASUSTADO;
        tiempoAsustado = duracion;
        direccion = null;
    }

    /** Pac-Man lo comió: regresa a su origen y puede dar media vuelta para tomar el camino más corto. */
    public void serComido() {
        estado = EstadoFantasma.REGRESANDO;
        direccion = null;
    }

    @Override
    public void volverAlOrigen() {
        super.volverAlOrigen();
        estado = EstadoFantasma.PERSIGUIENDO;
    }

    // ---- consultas ----

    /** Sólo un fantasma asustado puede ser comido. */
    public boolean puedeSerComido() {
        return estado == EstadoFantasma.ASUSTADO;
    }

    /** Sólo un fantasma que persigue le quita una vida a Pac-Man. */
    public boolean esPeligroso() {
        return estado == EstadoFantasma.PERSIGUIENDO;
    }

    public boolean estaAsustado() {
        return estado == EstadoFantasma.ASUSTADO;
    }

    public boolean estaRegresando() {
        return estado == EstadoFantasma.REGRESANDO;
    }

    public EstadoFantasma getEstado() {
        return estado;
    }

    /** El fantasma es el experto en su propio estado: él arma su DTO. */
    public FantasmaView toView() {
        return new FantasmaView(posicion.getFila(), posicion.getColumna(), estado.name());
    }
}
