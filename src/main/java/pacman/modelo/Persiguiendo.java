package pacman.modelo;

/** Estado normal: el fantasma va hacia Pac-Man y, si lo alcanza, le quita una vida. */
public class Persiguiendo extends EstadoFantasma {

    public Persiguiendo(Fantasma fantasma) {
        super(fantasma);
    }

    @Override
    Direccion elegirDireccion() {
        return fantasma.direccionHaciaPacMan();
    }

    @Override
    void pasarTiempo() {
        // persiguiendo no cambia de estado con el tiempo
    }

    @Override
    public boolean puedeSerComido() {
        return false;
    }

    @Override
    public boolean esPeligroso() {
        return true;
    }

    @Override
    public String getNombre() {
        return "PERSIGUIENDO";
    }
}
