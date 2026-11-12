package pacman.excepciones;

/**
 * Se lanza cuando se pide una acción que la partida no permite en su estado actual
 * (por ejemplo pausar una partida terminada o reiniciar una que sigue en curso).
 * Es chequeada: quien llama está obligado a decidir qué hacer si la acción no se puede realizar.
 */
public class PartidaException extends Exception {

    public PartidaException(String mensaje) {
        super(mensaje);
    }
}
