package pacman.modelo;

/** Patrón Observer: quien quiera enterarse de los cambios del juego (por ejemplo la vista). */
public interface JuegoListener {

    void onCambioDeEstado(Juego juego);
}
