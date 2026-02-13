package patrones.strategy.colision;

import mvc.modelo.entidades.ObjetoJuego;
import mvc.modelo.entidades.pelota.Pelota;
import mvc.modelo.enums.LadoHorizontal;

/**
 * Estrategia de colision entre la pelota y las paredes del campo de juego.
 * <p>
 * Implementa el patron Strategy para manejar rebotes en paredes superior/inferior
 * y deteccion de puntos cuando la pelota sale por los lados izquierdo/derecho.
 * </p>
 *
 * @author Equipo-polimorfo
 * @version 1.0
 */
public class EstrategiaColisionPelotaPared implements EstrategiaColision {

    private final double anchoCanvas;
    private final double altoCanvas;

    /**
     * Constructor que inicializa la estrategia con las dimensiones del campo.
     *
     * @param anchoCanvas ancho del canvas de juego
     * @param altoCanvas alto del canvas de juego
     */
    public EstrategiaColisionPelotaPared(final double anchoCanvas, final double altoCanvas) {
        this.anchoCanvas = anchoCanvas;
        this.altoCanvas = altoCanvas;
    }

    /**
     * Maneja la colision de la pelota con las paredes.
     * <p>
     * Invierte la velocidad vertical si colisiona con paredes superior/inferior.
     * Reinicia la posicion de la pelota si sale por los lados izquierdo/derecho.
     * </p>
     *
     * @param obj1 la pelota
     * @param obj2 no utilizado (las paredes no son objetos)
     */
    @Override
    public void manejarColision(final ObjetoJuego obj1, final ObjetoJuego obj2) {
        if (!(obj1 instanceof Pelota pelota)) {
            return;
        }

        io.vavr.control.Try.run(() -> {
            final double x = pelota.obtenerX();
            final double y = pelota.obtenerY();
            final double radio = pelota.obtenerAncho() / 2.0;

            if (y - radio <= 0) {
                pelota.invertirY();
            }

            if (y + radio >= altoCanvas) {
                pelota.invertirY();
            }

            if (x - radio <= 0) {
                pelota.restaurarEstado();
                pelota.inicializaDireccionLateral(LadoHorizontal.IZQUIERDA);
            } else if (x + radio >= anchoCanvas) {
                pelota.restaurarEstado();
                pelota.inicializaDireccionLateral(LadoHorizontal.DERECHA);
            }
        });
    }

    /**
     * Verifica si la pelota ha colisionado con alguna pared del campo.
     *
     * @param obj1 la pelota a verificar
     * @param obj2 no utilizado (las paredes no son objetos)
     * @return true si la pelota esta en contacto con alguna pared
     */
    @Override
    public boolean verificarColision(final ObjetoJuego obj1, final ObjetoJuego obj2) {
        if (!(obj1 instanceof Pelota pelota)) {
            return false;
        }

        return io.vavr.control.Try.of(() -> {
            final double x = pelota.obtenerX();
            final double y = pelota.obtenerY();
            final double radio = pelota.obtenerAncho() / 2.0;

            return (y - radio <= 0) ||
                   (y + radio >= altoCanvas) ||
                   (x - radio <= 0) ||
                   (x + radio >= anchoCanvas);
        }).getOrElse(false);
    }

    /**
     * Verifica si la pelota salio por el lado izquierdo del campo.
     *
     * @param pelota la pelota a verificar
     * @return true si la pelota salio por la izquierda
     */
    public boolean pelotaSalioPorIzquierda(final Pelota pelota) {
        final double x = pelota.obtenerX();
        final double radio = pelota.obtenerAncho() / 2.0;
        return x - radio <= 0;
    }

    /**
     * Verifica si la pelota salio por el lado derecho del campo.
     *
     * @param pelota la pelota a verificar
     * @return true si la pelota salio por la derecha
     */
    public boolean pelotaSalioPorDerecha(final Pelota pelota) {
        final double x = pelota.obtenerX();
        final double radio = pelota.obtenerAncho() / 2.0;
        return x + radio >= anchoCanvas;
    }
}
