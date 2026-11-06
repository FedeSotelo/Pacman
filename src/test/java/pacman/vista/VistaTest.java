package pacman.vista;

import org.junit.jupiter.api.Test;
import pacman.controlador.Controlador;
import pacman.dto.FantasmaView;
import pacman.dto.PartidaView;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * La vista se prueba sin abrir ventanas: el panel se dibuja sobre una imagen en memoria.
 * Además se verifica la regla de MVC: la vista nunca usa clases del modelo.
 */
class VistaTest {

    private static final int T = PanelTablero.TAM_CELDA;

    @Test
    void laVistaNoDependeDelModelo() throws IOException {
        Path carpeta = Paths.get("src", "main", "java", "pacman", "vista");
        List<Path> fuentes;
        try (Stream<Path> s = Files.list(carpeta)) {
            fuentes = s.filter(p -> p.toString().endsWith(".java")).collect(Collectors.toList());
        }
        assertFalse(fuentes.isEmpty());
        for (Path p : fuentes) {
            String codigo = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
            assertFalse(codigo.contains("pacman.modelo"), p.getFileName() + " no debe usar el modelo");
        }
    }

    @Test
    void dibujaLaPartidaRealDelControladorEnTodosLosEstados() {
        Controlador.getInstance().nuevaPartida();
        PanelTablero panel = new PanelTablero();

        panel.mostrar(Controlador.getInstance().getPartida(), true);           // cartel de inicio
        assertNotNull(dibujar(panel));

        panel.mostrar(Controlador.getInstance().getPartida(), false);          // en curso
        assertNotNull(dibujar(panel));

        Controlador.getInstance().pausar();
        panel.mostrar(Controlador.getInstance().getPartida(), false);          // en pausa
        assertNotNull(dibujar(panel));
        Controlador.getInstance().reanudar();
    }

    @Test
    void dibujaAlJugadorEnSuCelda() {
        PanelTablero panel = new PanelTablero();
        panel.mostrar(partidaChica("EN_CURSO"), false);
        BufferedImage img = dibujar(panel);
        Color centroJugador = new Color(img.getRGB(2 * T + T / 2 + 4, 1 * T + T / 2 + 4));
        Color centroPasillo = new Color(img.getRGB(1 * T + T / 2 + 4, 1 * T + T / 2 + 4));
        assertNotEquals(centroPasillo, centroJugador);
    }

    @Test
    void dibujaFantasmasEnCadaEstadoYLosCartelesDeFin() {
        PanelTablero panel = new PanelTablero();
        for (String estado : new String[] {"GANADA", "PERDIDA", "PAUSADA", "EN_CURSO"}) {
            panel.mostrar(partidaChica(estado), false);
            assertNotNull(dibujar(panel), estado);
        }
    }

    @Test
    void sinPartidaNoFalla() {
        PanelTablero panel = new PanelTablero();
        panel.setSize(200, 200);
        assertNotNull(dibujar(panel));
    }

    // ---- ayudas ----

    private static BufferedImage dibujar(PanelTablero panel) {
        if (panel.getWidth() == 0) panel.setSize(panel.getPreferredSize());
        BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        panel.paint(img.getGraphics());
        return img;
    }

    /** Un pasillo de 5 celdas con el jugador en (1,2) y un fantasma en cada estado. */
    private static PartidaView partidaChica(String estado) {
        PartidaView v = new PartidaView();
        v.setEstado(estado);
        v.setVidas(2);
        v.setPuntaje(120);
        v.setTablero(new char[][] {
            "#######".toCharArray(),
            "#. .o.#".toCharArray(),
            "#######".toCharArray(),
        });
        v.setFilaPacMan(1);
        v.setColumnaPacMan(2);
        v.getFantasmas().add(new FantasmaView(1, 3, "PERSIGUIENDO"));
        v.getFantasmas().add(new FantasmaView(1, 4, "ASUSTADO"));
        v.getFantasmas().add(new FantasmaView(1, 5, "REGRESANDO"));
        return v;
    }
}
