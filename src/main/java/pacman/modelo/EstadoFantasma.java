package pacman.modelo;

/**
 * Patrón State (GRASP Polimorfismo): cada estado de un fantasma es un objeto que sabe cómo
 * se comporta el fantasma mientras está en ese estado, en lugar de un switch dentro de Fantasma.
 *
 * Cada estado CONOCE a su fantasma (lo recibe al crearse) y actúa sobre él; ningún método
 * recibe el estado del fantasma por parámetro. Los datos propios de un estado, como el tiempo
 * que le queda asustado, viven en el estado.
 */
public abstract class EstadoFantasma {

    protected final Fantasma fantasma;

    protected EstadoFantasma(Fantasma fantasma) {
        this.fantasma = fantasma;
    }

    /** Hacia dónde se mueve el fantasma en este paso mientras está en este estado. */
    abstract Direccion elegirDireccion();

    /** Pasa el tiempo de un paso; el estado decide si el fantasma cambia a otro estado. */
    abstract void pasarTiempo();

    /** Un súper punto asusta al fantasma y puede dar media vuelta. Un estado puede negarse. */
    void asustar(int duracion) {
        fantasma.cambiarA(new Asustado(fantasma, duracion));
        fantasma.permitirMediaVuelta();
    }

    /** Si Pac-Man lo toca, ¿se lo puede comer? */
    public abstract boolean puedeSerComido();

    /** Si Pac-Man lo toca, ¿le quita una vida? */
    public abstract boolean esPeligroso();

    /** Nombre del estado para la vista: PERSIGUIENDO, ASUSTADO o REGRESANDO. */
    public abstract String getNombre();

    @Override
    public String toString() {
        return getNombre();
    }
}
