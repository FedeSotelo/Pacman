package pacman.vista;

import javax.swing.SwingUtilities;

/** Punto de entrada del juego. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaJuego ventana = new VentanaJuego();
            ventana.setVisible(true);
            ventana.comenzar();
        });
    }
}
