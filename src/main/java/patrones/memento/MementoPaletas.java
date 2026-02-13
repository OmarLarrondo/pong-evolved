package patrones.memento;

import mvc.modelo.entidades.paleta.Paleta;
import mvc.modelo.entidades.paleta.ConfigPaleta;

/**
 * Implementación del patrón Memento para guardar y restaurar el estado
 * completo de las paletas del juego.
 * <p>
 * Esta clase captura y externaliza el estado interno de las paletas sin
 * violar el encapsulamiento, permitiendo restaurar el objeto a este estado
 * posteriormente.
 * </p>
 *
 * @author Equipo-polimorfo
 * @version 1.1
 */
public final class MementoPaletas {

    private final ConfigPaleta estadoJugador1;
    private final ConfigPaleta estadoJugador2;
    private final double posicionXJugador1;
    private final double posicionYJugador1;
    private final double posicionXJugador2;
    private final double posicionYJugador2;

    /**
     * Crea un memento capturando el estado actual de ambas paletas.
     *
     * @param jugador1 la paleta del primer jugador
     * @param jugador2 la paleta del segundo jugador
     */
    public MementoPaletas(Paleta jugador1, Paleta jugador2) {
        this.estadoJugador1 = capturarEstado(jugador1);
        this.posicionXJugador1 = jugador1.obtenerX();
        this.posicionYJugador1 = jugador1.obtenerY();

        this.estadoJugador2 = capturarEstado(jugador2);
        this.posicionXJugador2 = jugador2.obtenerX();
        this.posicionYJugador2 = jugador2.obtenerY();
    }

    /**
     * Captura el estado de configuración de una paleta.
     *
     * @param paleta la paleta cuyo estado se capturará
     * @return un objeto ConfigPaleta con el estado capturado
     */
    private ConfigPaleta capturarEstado(Paleta paleta) {
        return new ConfigPaleta(
            paleta.obtenerColisionEnX(),
            paleta.obtenerCentro(),
            (int)paleta.obtenerAlto(),
            paleta.obtenerGrosor(),
            (int)paleta.obtenerVelocidad(),
            paleta.obtenerLimiteNorte(),
            paleta.obtenerLimiteSur(),
            paleta.obtenerLadoPantalla(),
            paleta.obtenerColorPrimario(),
            paleta.obtenerColorSecundario()
        );
    }

    /**
     * Restaura el estado guardado a la paleta del jugador 1.
     *
     * @param paleta la paleta del jugador 1 a restaurar
     */
    public void restaurarJugador1(Paleta paleta) {
        paleta.configurar(estadoJugador1);
        paleta.setEstadoOriginal(estadoJugador1);
    }

    /**
     * Restaura el estado guardado a la paleta del jugador 2.
     *
     * @param paleta la paleta del jugador 2 a restaurar
     */
    public void restaurarJugador2(Paleta paleta) {
        paleta.configurar(estadoJugador2);
        paleta.setEstadoOriginal(estadoJugador2);
    }

    /**
     * Obtiene la posición X guardada del jugador 1.
     *
     * @return la coordenada X del jugador 1
     */
    public double obtenerPosicionXJugador1() {
        return posicionXJugador1;
    }

    /**
     * Obtiene la posición Y guardada del jugador 1.
     *
     * @return la coordenada Y del jugador 1
     */
    public double obtenerPosicionYJugador1() {
        return posicionYJugador1;
    }

    /**
     * Obtiene la posición X guardada del jugador 2.
     *
     * @return la coordenada X del jugador 2
     */
    public double obtenerPosicionXJugador2() {
        return posicionXJugador2;
    }

    /**
     * Obtiene la posición Y guardada del jugador 2.
     *
     * @return la coordenada Y del jugador 2
     */
    public double obtenerPosicionYJugador2() {
        return posicionYJugador2;
    }
}