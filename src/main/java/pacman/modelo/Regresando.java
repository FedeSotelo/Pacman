package pacman.modelo;

/**
 * Pac-Man se comió al fantasma: vuelve a su origen sin poder ser comido ni hacer daño.
 * Al llegar, retoma la persecución.
 */
public class Regresando extends EstadoFantasma {

    public Regresando(Fantasma fantasma) {
        super(fantasma);
    }

    @Override
    Direccion elegirDireccion() {
        return fantasma.direccionHaciaSuOrigen();
    }

    @Override
    void pasarTiempo() {
        if (fantasma.estaEnSuOrigen()) fantasma.cambiarA(new Persiguiendo(fantasma));
    }

    /** Mientras regresa no se asusta (lo pide el enunciado). */
    @Override
    void asustar(int duracion) {
        // no hace nada
    }

    @Override
    public boolean puedeSerComido() {
        return false;
    }

    @Override
    public boolean esPeligroso() {
        return false;
    }

    @Override
    public String getNombre() {
        return "REGRESANDO";
    }
}
