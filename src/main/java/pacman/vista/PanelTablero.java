package pacman.vista;

import pacman.controlador.Controlador;
import pacman.dto.FantasmaView;
import pacman.dto.PartidaView;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

/**
 * Componente que dibuja el laberinto, Pac-Man y los fantasmas a partir del DTO que entrega el controlador.
 * No conoce ninguna clase del modelo: sólo PartidaView y FantasmaView.
 * El puntaje, las vidas y los botones son componentes aparte de la ventana.
 */
public class PanelTablero extends JPanel {

    static final int TAM_CELDA = 28;

    private static final Color FONDO = Color.BLACK;
    private static final Color PARED = new Color(33, 33, 222);
    private static final Color PUNTO = new Color(255, 184, 151);
    private static final Color PACMAN = Color.YELLOW;
    private static final Color ASUSTADO = new Color(40, 40, 255);
    private static final Color[] COLORES_FANTASMAS = {
        Color.RED, new Color(255, 184, 255), Color.CYAN, new Color(255, 184, 82)
    };

    public PanelTablero(int filas, int columnas) {
        setPreferredSize(new Dimension(columnas * TAM_CELDA, filas * TAM_CELDA));
        setBackground(FONDO);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        PartidaView partida = Controlador.getInstance().getPartida();
        dibujarTablero(g2, partida.getTablero());
        dibujarPacMan(g2, partida.getFilaPacMan(), partida.getColumnaPacMan());
        dibujarFantasmas(g2, partida.getFantasmas());
        if ("PAUSADA".equals(partida.getEstado())) dibujarPausa(g2);
    }

    private void dibujarTablero(Graphics2D g, char[][] tablero) {
        for (int f = 0; f < tablero.length; f++) {
            for (int c = 0; c < tablero[f].length; c++) {
                int x = c * TAM_CELDA;
                int y = f * TAM_CELDA;
                switch (tablero[f][c]) {
                    case '#':
                        g.setColor(PARED);
                        g.fillRoundRect(x + 2, y + 2, TAM_CELDA - 4, TAM_CELDA - 4, 8, 8);
                        break;
                    case '.':
                        g.setColor(PUNTO);
                        g.fillOval(x + TAM_CELDA / 2 - 3, y + TAM_CELDA / 2 - 3, 6, 6);
                        break;
                    case 'o':
                        g.setColor(PUNTO);
                        g.fillOval(x + TAM_CELDA / 2 - 8, y + TAM_CELDA / 2 - 8, 16, 16);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void dibujarPacMan(Graphics2D g, int fila, int columna) {
        g.setColor(PACMAN);
        g.fillArc(columna * TAM_CELDA + 3, fila * TAM_CELDA + 3, TAM_CELDA - 6, TAM_CELDA - 6, 30, 300);
    }

    private void dibujarFantasmas(Graphics2D g, List<FantasmaView> fantasmas) {
        for (int i = 0; i < fantasmas.size(); i++) {
            FantasmaView f = fantasmas.get(i);
            int x = f.getColumna() * TAM_CELDA + 3;
            int y = f.getFila() * TAM_CELDA + 3;
            int tam = TAM_CELDA - 6;
            if (!"REGRESANDO".equals(f.getEstado())) {
                // cuando regresa a su origen sólo se ven los ojos
                g.setColor("ASUSTADO".equals(f.getEstado())
                        ? ASUSTADO : COLORES_FANTASMAS[i % COLORES_FANTASMAS.length]);
                g.fillArc(x, y, tam, tam, 0, 180);
                g.fillRect(x, y + tam / 2, tam, tam / 2);
            }
            g.setColor(Color.WHITE);
            g.fillOval(x + 4, y + 6, 6, 7);
            g.fillOval(x + tam - 10, y + 6, 6, 7);
            g.setColor(Color.BLUE.darker());
            g.fillOval(x + 6, y + 8, 3, 4);
            g.fillOval(x + tam - 8, y + 8, 3, 4);
        }
    }

    /** Oscurece el tablero mientras la partida está en pausa. */
    private void dibujarPausa(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
        g.setColor(PACMAN);
        FontMetrics fm = g.getFontMetrics();
        String texto = "PAUSA";
        g.drawString(texto, (getWidth() - fm.stringWidth(texto)) / 2, getHeight() / 2);
    }
}
