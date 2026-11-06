package pacman.modelo;

/**
 * Lo que comparten Pac-Man y los fantasmas: conocen el laberinto en el que se mueven,
 * su posición, su origen y su dirección. Cada uno decide por sí mismo cómo moverse.
 */
public abstract class Personaje {

    protected final Laberinto laberinto;
    protected final Posicion posicionInicial;
    protected Posicion posicion;
    protected Posicion posicionAnterior;
    protected Direccion direccion;

    protected Personaje(Laberinto laberinto, Posicion posicionInicial) {
        this.laberinto = laberinto;
        this.posicionInicial = posicionInicial;
        this.posicion = posicionInicial;
        this.posicionAnterior = posicionInicial;
    }

    /** Avanza una celda según las reglas de cada personaje. */
    public abstract void mover();

    /** Intenta avanzar una celda en la dirección dada; devuelve false si hay pared. */
    protected boolean avanzar(Direccion d) {
        if (d == null) return false;
        Posicion destino = posicion.mover(d);
        if (laberinto.esPared(destino)) return false;
        posicion = destino;
        direccion = d;
        return true;
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
