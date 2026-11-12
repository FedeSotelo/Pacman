package pacman.modelo;

/** El personaje que controla el jugador. */
public class PacMan extends Personaje {

    private Direccion direccionDeseada;

    public PacMan(Laberinto laberinto, Posicion posicionInicial) {
        super(laberinto, posicionInicial);
    }

    /** El jugador indicó una dirección: se aplicará apenas no haya pared en ese sentido. */
    public void cambiarDireccion(Direccion d) {
        direccionDeseada = d;
    }

    /** Gira hacia donde pidió el jugador si puede; si no, sigue en la dirección que traía. */
    @Override
    protected Direccion elegirDireccion() {
        if (puedeAvanzarHacia(direccionDeseada)) return direccionDeseada;
        return getDireccion();
    }

    /** Come el punto de la celda en la que está; devuelve el punto comido o null si no había. */
    public Punto comerPunto() {
        return getLaberinto().quitarPuntoEn(getPosicion());
    }

    @Override
    public void volverAlOrigen() {
        super.volverAlOrigen();
        direccionDeseada = null;
    }
}
