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

    @Override
    public void mover() {
        posicionAnterior = posicion;
        if (avanzar(direccionDeseada)) return;
        avanzar(direccion);
    }

    /** Come el punto de la celda en la que está; devuelve el punto comido o null si no había. */
    public Punto comerPunto() {
        return laberinto.quitarPuntoEn(posicion);
    }

    @Override
    public void volverAlOrigen() {
        super.volverAlOrigen();
        direccionDeseada = null;
    }
}
