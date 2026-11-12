package pacman.vista;

import pacman.controlador.Controlador;
import pacman.dto.PartidaView;
import pacman.excepciones.PartidaException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Ventana principal (Frame). Usa BorderLayout para ubicar tres contenedores:
 * arriba la barra de información (JLabel), en el centro el tablero y abajo la botonera (JButton).
 * Los eventos de teclado, botones y menú se atienden con clases internas que llaman al controlador.
 */
public class VentanaJuego extends JFrame {

    /** Milisegundos entre cada paso de la simulación. */
    private static final int MS_POR_PASO = 160;

    private static final Color FONDO = Color.BLACK;
    private static final Font FUENTE = new Font(Font.MONOSPACED, Font.BOLD, 16);

    private final PanelTablero panelTablero;
    private final JLabel lblPuntaje = new JLabel();
    private final JLabel lblVidas = new JLabel();
    private final JLabel lblEstado = new JLabel();
    private final JButton btnPausa = new JButton("Pausar");
    private final JButton btnReiniciar = new JButton("Reiniciar");
    private final JMenuItem itemNuevaPartida = new JMenuItem("Nueva partida");
    private final Timer timer;

    /** Evita mostrar el aviso de fin de partida más de una vez. */
    private boolean finAvisado;

    public VentanaJuego() {
        super("Pac-Man Básico");
        char[][] tablero = Controlador.getInstance().getPartida().getTablero();
        panelTablero = new PanelTablero(tablero.length, tablero[0].length);

        setLayout(new BorderLayout());
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(panelTablero, BorderLayout.CENTER);
        add(crearBotonera(), BorderLayout.SOUTH);
        setJMenuBar(crearMenu());
        actualizarVista();   // las etiquetas necesitan texto antes de calcular el tamaño de la ventana

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addKeyListener(new Teclado());
        Controlador.getInstance().agregarListener(this::actualizarVista);
        timer = new Timer(MS_POR_PASO, e -> Controlador.getInstance().tick());
    }

    /** Arranca una partida nueva y el reloj del juego. */
    public void comenzar() {
        Controlador.getInstance().nuevaPartida();
        timer.start();
    }

    // ---- armado de los contenedores ----

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 8));
        barra.setBackground(FONDO);
        for (JLabel lbl : new JLabel[] {lblPuntaje, lblVidas, lblEstado}) {
            lbl.setFont(FUENTE);
            lbl.setForeground(Color.WHITE);
            barra.add(lbl);
        }
        lblEstado.setForeground(Color.YELLOW);
        return barra;
    }

    private JPanel crearBotonera() {
        JPanel botonera = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        botonera.setBackground(FONDO);
        botonera.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.DARK_GRAY));
        btnPausa.addActionListener(new PausaListener());
        btnReiniciar.addActionListener(new ReiniciarListener());
        for (JButton btn : new JButton[] {btnPausa, btnReiniciar}) {
            // si el botón se quedara con el foco, las flechas dejarían de llegar a la ventana
            btn.setFocusable(false);
            botonera.add(btn);
        }
        return botonera;
    }

    private JMenuBar crearMenu() {
        JMenu menuJuego = new JMenu("Juego");
        itemNuevaPartida.addActionListener(new ReiniciarListener());
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(new SalirListener());
        menuJuego.add(itemNuevaPartida);
        menuJuego.addSeparator();
        menuJuego.add(itemSalir);
        JMenuBar barraMenu = new JMenuBar();
        barraMenu.add(menuJuego);
        return barraMenu;
    }

    // ---- actualización de la vista: se invoca cada vez que el controlador avisa un cambio ----

    private void actualizarVista() {
        PartidaView partida = Controlador.getInstance().getPartida();
        String estado = partida.getEstado();
        boolean terminada = "GANADA".equals(estado) || "PERDIDA".equals(estado);

        lblPuntaje.setText("Puntaje: " + partida.getPuntaje());
        lblVidas.setText("Vidas: " + partida.getVidas());
        lblEstado.setText(textoDeEstado(estado));
        btnPausa.setText("PAUSADA".equals(estado) ? "Reanudar" : "Pausar");
        btnPausa.setEnabled(!terminada);
        // el enunciado permite reiniciar al finalizar la partida, ganada o perdida
        btnReiniciar.setEnabled(terminada);
        itemNuevaPartida.setEnabled(terminada);
        panelTablero.repaint();

        if (!terminada) {
            finAvisado = false;
        } else if (!finAvisado) {
            finAvisado = true;
            // se muestra después del paso actual para no frenar el Timer en medio de un tick
            SwingUtilities.invokeLater(() -> avisarFinDePartida(estado, partida.getPuntaje()));
        }
    }

    private static String textoDeEstado(String estado) {
        switch (estado) {
            case "PAUSADA": return "PAUSA";
            case "GANADA":  return "¡GANASTE!";
            case "PERDIDA": return "GAME OVER";
            default:        return "";
        }
    }

    private void avisarFinDePartida(String estado, int puntaje) {
        String titulo = "GANADA".equals(estado) ? "¡Ganaste!" : "Game Over";
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Puntaje final: " + puntaje + "\n¿Jugar de nuevo?",
                titulo, JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) return;
        try {
            Controlador.getInstance().reiniciar();
        } catch (PartidaException ex) {
            mostrarError(ex);
        }
    }

    private void mostrarError(PartidaException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Acción no permitida", JOptionPane.WARNING_MESSAGE);
    }

    // ---- eventos: clases internas que usan los métodos de la ventana ----

    private class Teclado extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            try {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:    case KeyEvent.VK_W: Controlador.getInstance().moverArriba();    break;
                    case KeyEvent.VK_DOWN:  case KeyEvent.VK_S: Controlador.getInstance().moverAbajo();     break;
                    case KeyEvent.VK_LEFT:  case KeyEvent.VK_A: Controlador.getInstance().moverIzquierda(); break;
                    case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: Controlador.getInstance().moverDerecha();   break;
                    case KeyEvent.VK_P:     alternarPausa(); break;
                    case KeyEvent.VK_R:     Controlador.getInstance().reiniciar(); break;
                    default: break;
                }
            } catch (PartidaException ex) {
                // con el teclado no abrimos un diálogo en medio del juego: alcanza con un aviso sonoro
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    private class PausaListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                alternarPausa();
            } catch (PartidaException ex) {
                mostrarError(ex);
            }
        }
    }

    private class ReiniciarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Controlador.getInstance().reiniciar();
            } catch (PartidaException ex) {
                mostrarError(ex);
            }
        }
    }

    private class SalirListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
            System.exit(0);
        }
    }

    /** Si está pausada la reanuda; si no, intenta pausarla (falla con una partida terminada). */
    private void alternarPausa() throws PartidaException {
        if ("PAUSADA".equals(Controlador.getInstance().getPartida().getEstado())) Controlador.getInstance().reanudar();
        else Controlador.getInstance().pausar();
    }
}
