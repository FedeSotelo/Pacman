package pacman;

import pacman.vista.VentanaJuego;

/** Punto de entrada: crea la ventana del juego y la muestra. */
public class Aplicacion {

    public static void main(String[] args) {
        VentanaJuego ventana = new VentanaJuego();
        ventana.setVisible(true);
    }
}
