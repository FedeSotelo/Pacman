package pacman.modelo;

/**
 * Lo que comparten Pac-Man y los fantasmas: conocen el laberinto en el que se mueven,
 * su posición, su origen y su dirección, y se mueven a sí mismos.
 *
 * mover() es un método plantilla: el personaje recuerda de dónde venía y avanza una celda.
 * Lo único que cambia entre Pac-Man y un fantasma es cómo eligen la dirección (polimorfismo).
 */
public abstract class Personaje {

    private final Laberinto laberinto;
    private final Posicion posicionInicial;
    private Posicion posicion;
    private Posicion posicionAnterior;
    private Direccion direccion;

    protected Personaje(Laberinto laberinto, Posicion posicionInicial) {
        this.laberinto = laberinto;
        this.posicionInicial = posicionInicial;
        this.posicion = posicionInicial;
        this.posicionAnterior = posicionInicial;
    }

    /** Avanza una celda en la dirección que elija el personaje; si hay pared, se queda quieto. */
    public final void mover() {
        posicionAnterior = posicion;
        avanzar(elegirDireccion());
    }

    /** Cada personaje decide hacia dónde quiere ir en este paso (puede ser null: quedarse quieto). */
    protected abstract Direccion elegirDireccion();

    private void avanzar(Direccion d) {
        if (!puedeAvanzarHacia(d)) return;
        posicion = posicion.mover(d);
        direccion = d;
    }

    protected boolean puedeAvanzarHacia(Direccion d) {
        return d != null && !laberinto.esPared(posicion.mover(d));
    }

    /** Olvida la dirección actual: en el próximo paso puede dar media vuelta. */
    protected void olvidarDireccion() {
        direccion = null;
    }

    /**
     * Dos personajes se encuentran si quedaron en la misma celda o si se cruzaron
     * (intercambiaron celdas en el mismo paso).
     */
    public boolean seEncuentraCon(Personaje otro) {
        boolean mismaCelda = posicion.equals(otro.posicion);
        boolean seCruzaron = posicion.equals(otro.posicionAnterior) && posicionAnterior.equals(otro.posicion);
        return mismaCelda || seCruzaron;
    }

    public void volverAlOrigen() {
        posicion = posicionInicial;
        posicionAnterior = posicionInicial;
        direccion = null;
    }

    public boolean estaEnSuOrigen() {
        return posicion.equals(posicionInicial);
    }

    protected Laberinto getLaberinto() {
        return laberinto;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public Posicion getPosicionInicial() {
        return posicionInicial;
    }

    public Direccion getDireccion() {
        return direccion;
    }
}
