package pacman.vista;

import pacman.controlador.Controlador;
import pacman.dto.PartidaView;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Vista principal del juego (la V de MVC).
 *
 * Pasos de construcción de la GUI:
 *   1. Contenedor: esta ventana (JFrame), con un JPanel para la barra de datos y el PanelTablero.
 *   2. Layout: BorderLayout para la ventana (barra al norte, tablero al centro) y GridLayout para la barra.
 *   3. Componentes: JLabel para puntaje, estado y vidas; JMenuBar, JMenu y JMenuItem para el menú.
 *   4. Listeners: clases internas (KeyListener, ActionListener) que traducen cada evento
 *      en una petición al Controlador.
 *
 * La vista sólo conoce el Controlador (Singleton) y el DTO PartidaView, nunca el modelo.
 */
public class VentanaJuego extends JFrame {

    /** Cada cuántos milisegundos avanza un paso la partida. */
    public static final int MS_POR_PASO = 150;

    private static final Color FONDO_BARRA = new Color(0x0e, 0x10, 0x16);
    private static final Color TEXTO = new Color(0xf1, 0xe9, 0xd2);

    private PanelTablero tablero;
    private JLabel lblPuntaje;
    private JLabel lblEstado;
    private JLabel lblVidas;
    private Timer timer;

    /** Antes del primer ENTER la partida no avanza: se ve el tablero con el cartel de inicio. */
    private boolean empezada = false;

    public VentanaJuego() {
        super("TPO POO · Grupo 4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        crearComponentes();
        asignarListeners();

        refrescar();
        pack();
        setLocationRelativeTo(null);
        timer.start();
    }

    // ================= construcción =================

    private void crearComponentes() {
        Container contenedor = getContentPane();
        contenedor.setLayout(new BorderLayout());

        lblPuntaje = crearEtiqueta(JLabel.LEFT);
        lblEstado = crearEtiqueta(JLabel.CENTER);
        lblVidas = crearEtiqueta(JLabel.RIGHT);

        JPanel barra = new JPanel(new GridLayout(1, 3));
        barra.setBackground(FONDO_BARRA);
        barra.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        barra.add(lblPuntaje);
        barra.add(lblEstado);
        barra.add(lblVidas);

        tablero = new PanelTablero();
        tablero.setFocusable(true);   // para recibir los eventos de teclado

        contenedor.add(barra, BorderLayout.NORTH);
        contenedor.add(tablero, BorderLayout.CENTER);

        setJMenuBar(crearMenu());
        timer = new Timer(MS_POR_PASO, new PasoDelJuego());
    }

    private JMenuBar crearMenu() {
        JMenuItem itNueva = new JMenuItem("Nueva partida");
        itNueva.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itNueva.addActionListener(new AccionNuevaPartida());

        JMenuItem itPausa = new JMenuItem("Pausar / reanudar");
        itPausa.addActionListener(new AccionPausa());

        JMenuItem itSalir = new JMenuItem("Salir");
        itSalir.addActionListener(new AccionSalir());

        JMenuItem itControles = new JMenuItem("Controles");
        itControles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        itControles.addActionListener(new AccionControles());

        JMenu juego = new JMenu("Juego");
        juego.add(itNueva);
        juego.add(itPausa);
        juego.addSeparator();
        juego.add(itSalir);

        JMenu ayuda = new JMenu("Ayuda");
        ayuda.add(itControles);

        JMenuBar barraMenu = new JMenuBar();
        barraMenu.add(juego);
        barraMenu.add(ayuda);
        return barraMenu;
    }

    private JLabel crearEtiqueta(int alineacion) {
        JLabel l = new JLabel(" ", alineacion);
        l.setForeground(TEXTO);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 15f));
        return l;
    }

    private void asignarListeners() {
        tablero.addKeyListener(new ManejadorTeclado());
        addWindowListener(new ManejadorVentana());
        Controlador.getInstance().agregarListener(new RefrescoDePantalla());
    }

    // ================= acciones de la ventana =================

    /** Muestra lo que devuelve el Controlador (el DTO PartidaView). */
    private void refrescar() {
        PartidaView partida = Controlador.getInstance().getPartida();
        lblPuntaje.setText("Puntaje  " + partida.getPuntaje());
        lblVidas.setText("Vidas  " + "● ".repeat(Math.max(0, partida.getVidas())).trim());
        lblEstado.setText(textoEstado(partida.getEstado()));
        tablero.mostrar(partida, !empezada);
    }

    private String textoEstado(String estado) {
        if (!empezada) return "Listo";
        if (estado.equals("PAUSADA")) return "En pausa";
        if (estado.equals("GANADA")) return "¡Victoria!";
        if (estado.equals("PERDIDA")) return "Derrota";
        return "Jugando";
    }

    /** ENTER: empieza la primera partida o, si la anterior terminó, arranca otra. */
    private void empezarOReiniciar() {
        if (!empezada) {
            empezada = true;
            Controlador.getInstance().nuevaPartida();
            return;
        }
        String estado = Controlador.getInstance().getPartida().getEstado();
        if (estado.equals("GANADA") || estado.equals("PERDIDA")) {
            Controlador.getInstance().reiniciar();
        }
    }

    private void nuevaPartida() {
        empezada = true;
        Controlador.getInstance().reiniciar();
    }

    private void alternarPausa() {
        if (!empezada) return;
        String estado = Controlador.getInstance().getPartida().getEstado();
        if (estado.equals("EN_CURSO")) Controlador.getInstance().pausar();
        else if (estado.equals("PAUSADA")) Controlador.getInstance().reanudar();
    }

    private void mostrarControles() {
        boolean pausar = empezada && Controlador.getInstance().getPartida().getEstado().equals("EN_CURSO");
        if (pausar) Controlador.getInstance().pausar();
        JOptionPane.showMessageDialog(this,
                "Flechas o W A S D: mover\n"
                + "P o ESPACIO: pausar / reanudar\n"
                + "ENTER: empezar, o jugar de nuevo al terminar\n"
                + "F2: nueva partida\n\n"
                + "Punto: 10 · Súper punto: 50 · Enemigo asustado: 200",
                "Controles", JOptionPane.INFORMATION_MESSAGE);
        if (pausar) Controlador.getInstance().reanudar();
    }

    // ================= listeners (clases internas) =================
    // Cada clase interna escucha un evento y usa los atributos y métodos de la ventana
    // (su clase externa) para responder.

    /** Teclado: flechas o WASD mueven, P o ESPACIO pausan, ENTER empieza o reinicia. */
    private class ManejadorTeclado extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    Controlador.getInstance().moverArriba();
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    Controlador.getInstance().moverAbajo();
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    Controlador.getInstance().moverIzquierda();
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    Controlador.getInstance().moverDerecha();
                    break;
                case KeyEvent.VK_P:
                case KeyEvent.VK_SPACE:
                    alternarPausa();
                    break;
                case KeyEvent.VK_ENTER:
                    empezarOReiniciar();
                    break;
                default:
                    break;
            }
        }
    }

    /** Timer: en cada paso avanza la partida (si empezó) y anima el tablero. */
    private class PasoDelJuego implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (empezada) Controlador.getInstance().tick();
            tablero.animar();
        }
    }

    /** Observer: el Controlador avisa que el juego cambió y la ventana se vuelve a dibujar. */
    private class RefrescoDePantalla implements Runnable {
        @Override
        public void run() {
            refrescar();
        }
    }

    /** Cada vez que la ventana se activa, el tablero toma el foco para recibir el teclado. */
    private class ManejadorVentana extends WindowAdapter {
        @Override
        public void windowActivated(WindowEvent e) {
            tablero.requestFocusInWindow();
        }
    }

    private class AccionNuevaPartida implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            nuevaPartida();
        }
    }

    private class AccionPausa implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            alternarPausa();
        }
    }

    private class AccionSalir implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private class AccionControles implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            mostrarControles();
        }
    }
}
