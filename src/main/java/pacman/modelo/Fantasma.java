package pacman.modelo;

import java.util.List;
import java.util.Random;
import pacman.dto.FantasmaView;

/**
 * Enemigo autónomo. Conoce el laberinto, a Pac-Man (su objetivo) y su estado actual.
 * Cómo se mueve y cómo cambia de estado lo resuelve el objeto estado (patrón State):
 * Fantasma no tiene ningún switch sobre su estado, sólo le delega.
 */
public class Fantasma extends Personaje {

    private final PacMan objetivo;
    private final Random random;
    private EstadoFantasma estado;

    public Fantasma(Laberinto laberinto, Posicion posicionInicial, PacMan objetivo, Random random) {
        super(laberinto, posicionInicial);
        this.objetivo = objetivo;
        this.random = random;
        this.estado = new Persiguiendo(this);
    }

    public Fantasma(Laberinto laberinto, Posicion posicionInicial, PacMan objetivo) {
        this(laberinto, posicionInicial, objetivo, new Random());
    }

    // ---- movimiento: la dirección la elige el estado actual ----

    @Override
    protected Direccion elegirDireccion() {
        if (direccionesPosibles().isEmpty()) return null;
        return estado.elegirDireccion();
    }

    Direccion direccionHaciaPacMan() {
        return masCercanaA(objetivo.getPosicion());
    }

    Direccion direccionHaciaSuOrigen() {
        return masCercanaA(getPosicionInicial());
    }

    Direccion direccionAlAzar() {
        List<Direccion> posibles = direccionesPosibles();
        return posibles.get(random.nextInt(posibles.size()));
    }

    /**
     * Direcciones en las que puede avanzar: no da media vuelta salvo en un callejón sin salida.
     * Sin esta regla, al buscar siempre la celda más cercana quedaría yendo y viniendo detrás de una pared.
     */
    private List<Direccion> direccionesPosibles() {
        List<Direccion> disponibles = getLaberinto().direccionesDisponibles(getPosicion());
        Direccion actual = getDireccion();
        if (actual != null && disponibles.size() > 1) disponibles.remove(actual.opuesta());
        return disponibles;
    }

    /** Entre las posibles, la que lo deja más cerca del destino. */
    private Direccion masCercanaA(Posicion destino) {
        Direccion mejor = null;
        int mejorDistancia = Integer.MAX_VALUE;
        for (Direccion d : direccionesPosibles()) {
            int distancia = getPosicion().mover(d).distanciaA(destino);
            if (distancia < mejorDistancia) {
                mejorDistancia = distancia;
                mejor = d;
            }
        }
        return mejor;
    }

    // ---- cambios de estado ----

    /** Pasa el tiempo de un paso: el estado actual decide si se termina. */
    public void pasarTiempo() {
        estado.pasarTiempo();
    }

    /** Un súper punto lo asusta, salvo que su estado actual no lo permita. */
    public void asustar(int duracion) {
        estado.asustar(duracion);
    }

    /** Pac-Man lo comió: regresa a su origen y puede dar media vuelta para tomar el camino más corto. */
    public void serComido() {
        cambiarA(new Regresando(this));
        permitirMediaVuelta();
    }

    @Override
    public void volverAlOrigen() {
        super.volverAlOrigen();
        cambiarA(new Persiguiendo(this));
    }

    /** Lo usan los estados para pasar al siguiente. */
    void cambiarA(EstadoFantasma nuevo) {
        estado = nuevo;
    }

    /** Al cambiar de estado puede invertir el sentido en el próximo paso. */
    void permitirMediaVuelta() {
        olvidarDireccion();
    }

    // ---- consultas: las responde el estado ----

    public boolean puedeSerComido() {
        return estado.puedeSerComido();
    }

    public boolean esPeligroso() {
        return estado.esPeligroso();
    }

    public boolean estaPersiguiendo() {
        return estado instanceof Persiguiendo;
    }

    public boolean estaAsustado() {
        return estado instanceof Asustado;
    }

    public boolean estaRegresando() {
        return estado instanceof Regresando;
    }

    public EstadoFantasma getEstado() {
        return estado;
    }

    /** El fantasma es el experto en su propio estado: él arma su DTO. */
    public FantasmaView toView() {
        Posicion p = getPosicion();
        return new FantasmaView(p.getFila(), p.getColumna(), estado.getNombre());
    }
}
