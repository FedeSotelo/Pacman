package pacman.modelo;

/** Un punto comestible del laberinto. Conoce su valor y si es un súper punto. */
public class Punto {

    public static final int VALOR_COMUN = 10;
    public static final int VALOR_SUPER = 50;

    private final int valor;
    private final boolean esSuper;

    private Punto(int valor, boolean esSuper) {
        this.valor = valor;
        this.esSuper = esSuper;
    }

    public static Punto comun() {
        return new Punto(VALOR_COMUN, false);
    }

    public static Punto superPunto() {
        return new Punto(VALOR_SUPER, true);
    }

    public int getValor() {
        return valor;
    }

    /** Un súper punto, además de sumar, asusta a los fantasmas. */
    public boolean esSuper() {
        return esSuper;
    }
}
