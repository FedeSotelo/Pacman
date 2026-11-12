package pacman.modelo;

/**
 * Pac-Man comió un súper punto: el fantasma se mueve al azar y puede ser comido.
 * El estado lleva su propio tiempo restante; al agotarse, el fantasma vuelve a perseguir.
 */
public class Asustado extends EstadoFantasma {

    private int tiempoRestante;

    public Asustado(Fantasma fantasma, int duracion) {
        super(fantasma);
        this.tiempoRestante = duracion;
    }

    @Override
    Direccion elegirDireccion() {
        return fantasma.direccionAlAzar();
    }

    @Override
    void pasarTiempo() {
        tiempoRestante--;
        if (tiempoRestante <= 0) fantasma.cambiarA(new Persiguiendo(fantasma));
    }

    @Override
    public boolean puedeSerComido() {
        return true;
    }

    @Override
    public boolean esPeligroso() {
        return false;
    }

    @Override
    public String getNombre() {
        return "ASUSTADO";
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }
}
