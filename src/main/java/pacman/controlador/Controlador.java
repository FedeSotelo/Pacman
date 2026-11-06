package pacman.controlador;

import pacman.dto.PartidaView;
import pacman.modelo.Direccion;
import pacman.modelo.Juego;
import pacman.modelo.JuegoListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton: único punto de acceso de la vista al negocio (patrón MVC).
 * Se usa como Controlador.getInstance().metodo(); nunca guardado en una variable.
 * No contiene reglas del juego: traduce acciones del usuario en llamadas a Juego
 * y devuelve el estado como DTO (PartidaView) para que la vista no toque el modelo.
 */
public class Controlador implements JuegoListener {

    private static Controlador instancia;

    private Juego juego;
    private final List<Runnable> listeners = new ArrayList<>();

    private Controlador() {
        juego = new Juego();
        juego.agregarListener(this);
    }

    public static Controlador getInstance() {
        if (instancia == null) instancia = new Controlador();
        return instancia;
    }

    // ---- acciones del usuario ----

    public void nuevaPartida() {
        juego.iniciar();
    }

    public void pausar() {
        juego.pausar();
    }

    public void reanudar() {
        juego.reanudar();
    }

    public void reiniciar() {
        juego.reiniciar();
    }

    public void moverPacMan(Direccion d) {
        juego.cambiarDireccionPacMan(d);
    }

    /** Un paso de la simulación; la vista lo invoca periódicamente (por ejemplo con un javax.swing.Timer). */
    public void tick() {
        juego.actualizar();
    }

    // ---- consulta para la vista ----

    public PartidaView getPartida() {
        return juego.toView();
    }

    // ---- notificaciones: la vista se registra para enterarse de que hay que redibujar ----

    public void agregarListener(Runnable l) {
        listeners.add(l);
    }

    public void quitarListener(Runnable l) {
        listeners.remove(l);
    }

    @Override
    public void onCambioDeEstado(Juego juego) {
        for (Runnable l : new ArrayList<>(listeners)) l.run();
    }
}
