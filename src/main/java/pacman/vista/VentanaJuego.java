package pacman.vista;

import pacman.controlador.Controlador;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Ventana principal. Traduce el teclado en pedidos al controlador y avanza
 * la partida con un Timer; se redibuja cada vez que el controlador avisa un cambio.
 */
public class VentanaJuego extends JFrame {

    /** Milisegundos entre cada paso de la simulación. */
    private static final int MS_POR_PASO = 160;

    private final PanelTablero panel;
    private final Timer timer;

    public VentanaJuego() {
        super("Pac-Man Básico");
        char[][] tablero = Controlador.getInstance().getPartida().getTablero();
        panel = new PanelTablero(tablero.length, tablero[0].length);
        add(panel);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Controlador.getInstance().agregarListener(panel::repaint);
        addKeyListener(new Teclado());
        timer = new Timer(MS_POR_PASO, e -> Controlador.getInstance().tick());
    }

    /** Arranca una partida nueva y el reloj del juego. */
    public void comenzar() {
        Controlador.getInstance().nuevaPartida();
        timer.start();
    }

    private class Teclado extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:    case KeyEvent.VK_W: Controlador.getInstance().moverArriba();    break;
                case KeyEvent.VK_DOWN:  case KeyEvent.VK_S: Controlador.getInstance().moverAbajo();     break;
                case KeyEvent.VK_LEFT:  case KeyEvent.VK_A: Controlador.getInstance().moverIzquierda(); break;
                case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: Controlador.getInstance().moverDerecha();   break;
                case KeyEvent.VK_P:     alternarPausa(); break;
                case KeyEvent.VK_R:     reiniciarSiTermino(); break;
                default: break;
            }
        }
    }

    private void alternarPausa() {
        String estado = Controlador.getInstance().getPartida().getEstado();
        if ("EN_CURSO".equals(estado)) Controlador.getInstance().pausar();
        else if ("PAUSADA".equals(estado)) Controlador.getInstance().reanudar();
    }

    /** Sólo se reinicia al terminar la partida, ganada o perdida. */
    private void reiniciarSiTermino() {
        String estado = Controlador.getInstance().getPartida().getEstado();
        if ("GANADA".equals(estado) || "PERDIDA".equals(estado)) Controlador.getInstance().reiniciar();
    }
}
