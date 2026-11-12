package pacman.excepciones;

/**
 * Se lanza al construir un laberinto (o un juego) a partir de un dibujo mal armado.
 * Es un error de programación y no del jugador, por eso no es chequeada.
 */
public class LaberintoInvalidoException extends RuntimeException {

    public LaberintoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
