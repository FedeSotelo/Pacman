package pacman.vista;

import pacman.dto.FantasmaView;
import pacman.dto.PartidaView;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

/**
 * Dibuja una partida a partir del DTO PartidaView. No conoce el modelo ni toma decisiones
 * del juego: sólo pinta lo que le pasa la ventana.
 */
public class PanelTablero extends JPanel {

    public static final int TAM_CELDA = 28;

    private static final Color FONDO = new Color(0x15, 0x17, 0x1f);
    private static final Color PARED = new Color(0x22, 0x3a, 0x45);
    private static final Color BORDE_PARED = new Color(0x3f, 0xb8, 0xa8);
    private static final Color PUNTO = new Color(0xf1, 0xe9, 0xd2);
    private static final Color SUPER_PUNTO = new Color(0xf4, 0xa2, 0x3c);
    private static final Color JUGADOR = new Color(0x7c, 0xe0, 0x8a);
    private static final Color JUGADOR_BRILLO = new Color(0xd4, 0xff, 0xda);
    private static final Color ASUSTADO = new Color(0x9a, 0xa5, 0xc4);
    private static final Color[] ENEMIGOS = {
        new Color(0xb0, 0x5c, 0xe0),   // violeta
        new Color(0x4f, 0x8c, 0xe8),   // azul acero
        new Color(0xd9, 0x4f, 0x7a),   // frambuesa
        new Color(0xc9, 0xb4, 0x3a),   // mostaza
    };

    private PartidaView partida;
    private boolean esperandoInicio = true;
    private int pulso;   // contador para animar los súper puntos

    public PanelTablero() {
        setBackground(FONDO);
        setDoubleBuffered(true);
    }

    /** Recibe el estado a dibujar y repinta. */
    public void mostrar(PartidaView partida, boolean esperandoInicio) {
        this.partida = partida;
        this.esperandoInicio = esperandoInicio;
        repaint();
    }

    /** Avanza la animación de los súper puntos (la llama el timer de la ventana). */
    public void animar() {
        pulso++;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (partida == null || partida.getTablero() == null) return new Dimension(19 * TAM_CELDA, 19 * TAM_CELDA);
        char[][] t = partida.getTablero();
        return new Dimension(t[0].length * TAM_CELDA, t.length * TAM_CELDA);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (partida == null || partida.getTablero() == null) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        dibujarTablero(g2, partida.getTablero());
        dibujarJugador(g2, partida.getFilaPacMan(), partida.getColumnaPacMan());
        dibujarFantasmas(g2, partida.getFantasmas());
        dibujarCartel(g2);

        g2.dispose();
    }

    // ---- tablero ----

    private void dibujarTablero(Graphics2D g2, char[][] t) {
        int filas = t.length;
        int columnas = t[0].length;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                int x = c * TAM_CELDA;
                int y = f * TAM_CELDA;
                switch (t[f][c]) {
                    case '#': dibujarPared(g2, t, f, c, x, y); break;
                    case '.': dibujarPunto(g2, x, y); break;
                    case 'o': dibujarSuperPunto(g2, x, y); break;
                    default:  break;
                }
            }
        }
    }

    /** Pared rellena, con un borde sólo del lado que da a un pasillo. */
    private void dibujarPared(Graphics2D g2, char[][] t, int f, int c, int x, int y) {
        g2.setColor(PARED);
        g2.fillRect(x, y, TAM_CELDA, TAM_CELDA);
        g2.setColor(BORDE_PARED);
        g2.setStroke(new BasicStroke(2f));
        int fin = TAM_CELDA - 1;
        if (!esPared(t, f - 1, c)) g2.drawLine(x, y + 1, x + fin, y + 1);
        if (!esPared(t, f + 1, c)) g2.drawLine(x, y + fin - 1, x + fin, y + fin - 1);
        if (!esPared(t, f, c - 1)) g2.drawLine(x + 1, y, x + 1, y + fin);
        if (!esPared(t, f, c + 1)) g2.drawLine(x + fin - 1, y, x + fin - 1, y + fin);
    }

    private static boolean esPared(char[][] t, int f, int c) {
        if (f < 0 || f >= t.length || c < 0 || c >= t[0].length) return true;
        return t[f][c] == '#';
    }

    private void dibujarPunto(Graphics2D g2, int x, int y) {
        int d = 6;
        g2.setColor(PUNTO);
        g2.fillOval(x + (TAM_CELDA - d) / 2, y + (TAM_CELDA - d) / 2, d, d);
    }

    private void dibujarSuperPunto(Graphics2D g2, int x, int y) {
        int d = 14 + (pulso / 3) % 2 * 4;   // late suavemente
        g2.setColor(SUPER_PUNTO);
        g2.fillOval(x + (TAM_CELDA - d) / 2, y + (TAM_CELDA - d) / 2, d, d);
    }

    // ---- personajes ----

    private void dibujarJugador(Graphics2D g2, int fila, int col) {
        int m = 4;
        int x = col * TAM_CELDA + m;
        int y = fila * TAM_CELDA + m;
        int d = TAM_CELDA - 2 * m;
        g2.setColor(JUGADOR);
        g2.fillOval(x, y, d, d);
        g2.setColor(JUGADOR_BRILLO);
        g2.fillOval(x + d / 4, y + d / 5, d / 3, d / 3);
    }

    private void dibujarFantasmas(Graphics2D g2, List<FantasmaView> fantasmas) {
        for (int i = 0; i < fantasmas.size(); i++) {
            FantasmaView f = fantasmas.get(i);
            int x = f.getColumna() * TAM_CELDA + 3;
            int y = f.getFila() * TAM_CELDA + 3;
            int lado = TAM_CELDA - 6;
            switch (f.getEstado()) {
                case "REGRESANDO":
                    dibujarOjos(g2, x, y, lado);   // sólo los ojos vuelven a casa
                    break;
                case "ASUSTADO":
                    g2.setColor(ASUSTADO);
                    g2.fillRoundRect(x, y, lado, lado, 10, 10);
                    g2.setColor(FONDO);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(x + 5, y + lado - 7, x + lado - 5, y + lado - 7);
                    break;
                default:
                    g2.setColor(ENEMIGOS[i % ENEMIGOS.length]);
                    g2.fillRoundRect(x, y, lado, lado, 10, 10);
                    dibujarOjos(g2, x, y, lado);
                    break;
            }
        }
    }

    private void dibujarOjos(Graphics2D g2, int x, int y, int lado) {
        int ojo = 7;
        int yOjo = y + lado / 4;
        g2.setColor(Color.WHITE);
        g2.fillOval(x + lado / 4 - ojo / 2, yOjo, ojo, ojo);
        g2.fillOval(x + 3 * lado / 4 - ojo / 2, yOjo, ojo, ojo);
        g2.setColor(FONDO);
        g2.fillOval(x + lado / 4 - 1, yOjo + 2, 3, 3);
        g2.fillOval(x + 3 * lado / 4 - 1, yOjo + 2, 3, 3);
    }

    // ---- carteles ----

    private void dibujarCartel(Graphics2D g2) {
        if (esperandoInicio) {
            cartel(g2, "¡Listo!", "ENTER para empezar · flechas para moverse", JUGADOR);
            return;
        }
        switch (partida.getEstado()) {
            case "PAUSADA": cartel(g2, "Pausa", "P o ESPACIO para seguir", PUNTO); break;
            case "GANADA":  cartel(g2, "¡Ganaste!", "Puntaje: " + partida.getPuntaje() + " · ENTER para jugar de nuevo", JUGADOR); break;
            case "PERDIDA": cartel(g2, "Perdiste", "Puntaje: " + partida.getPuntaje() + " · ENTER para jugar de nuevo", ENEMIGOS[2]); break;
            default: break;
        }
    }

    private void cartel(Graphics2D g2, String titulo, String detalle, Color color) {
        int w = getWidth();
        int h = getHeight();
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, w, h);

        Font fTitulo = getFont().deriveFont(Font.BOLD, 34f);
        Font fDetalle = getFont().deriveFont(Font.PLAIN, 14f);
        int alto = 110;
        int y0 = (h - alto) / 2;
        g2.setColor(FONDO);
        g2.fillRoundRect(24, y0, w - 48, alto, 16, 16);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(24, y0, w - 48, alto, 16, 16);

        g2.setFont(fTitulo);
        centrado(g2, titulo, y0 + 50);
        g2.setColor(PUNTO);
        g2.setFont(fDetalle);
        centrado(g2, detalle, y0 + 84);
    }

    private void centrado(Graphics2D g2, String texto, int y) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, (getWidth() - fm.stringWidth(texto)) / 2, y);
    }
}
