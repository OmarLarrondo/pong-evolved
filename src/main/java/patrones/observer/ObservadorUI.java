package patrones.observer;

import io.vavr.control.Option;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import mvc.modelo.items.Item;
import mvc.vista.VistaJuego;

/**
 * Observador encargado de actualizar la interfaz gráfica del usuario (UI)
 * en respuesta a los eventos del juego.
 *
 * <p>Esta clase implementa {@link ObservadorJuego} y se comunica con la
 * {@link VistaJuego} para reflejar en pantalla los cambios producidos en
 * el modelo: actualizaciones de puntaje, fin de juego, avance de nivel
 * y generación de ítems.</p>
 *
 * <p>Su propósito principal es mantener la UI sincronizada con el estado
 * interno del juego sin acoplar la lógica visual al modelo.</p>
 *
 * @author Equipo-polimorfo
 * @version 1.0
 */
public class ObservadorUI implements ObservadorJuego {

    private static final double DURACION_MENSAJE_GAME_OVER = 4.0;

    private final VistaJuego vistaJuego;
    private final Option<Runnable> callbackDetenerGameLoop;
    private final Option<Runnable> callbackVolverMenu;

    /**
     * Constructor que inicializa el observador con la vista del juego.
     *
     * @param vistaJuego la vista que sera actualizada por este observador
     */
    public ObservadorUI(final VistaJuego vistaJuego) {
        this(vistaJuego, Option.none(), Option.none());
    }

    /**
     * Constructor completo que inicializa el observador con callbacks funcionales.
     *
     * @param vistaJuego la vista que sera actualizada por este observador
     * @param callbackDetenerGameLoop callback para detener el game loop cuando el juego termina
     * @param callbackVolverMenu callback para navegar al menu principal
     */
    public ObservadorUI(
            final VistaJuego vistaJuego,
            final Option<Runnable> callbackDetenerGameLoop,
            final Option<Runnable> callbackVolverMenu) {
        this.vistaJuego = vistaJuego;
        this.callbackDetenerGameLoop = callbackDetenerGameLoop;
        this.callbackVolverMenu = callbackVolverMenu;
    }

    /**{@inheritDoc}*/
    @Override
    public void alcambiarPuntaje(int jugador, int nuevoPuntaje) {
        if (vistaJuego != null) {
            vistaJuego.actualizarPuntaje(jugador, nuevoPuntaje);
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void alTerminarJuego(int ganador) {
        Option.of(vistaJuego)
            .peek(vista -> mostrarMensajeYVolverMenu(ganador));
    }

    /**
     * Muestra el mensaje de fin de juego y programa el retorno al menu principal.
     * <p>
     * Este metodo muestra el mensaje de victoria por {@value DURACION_MENSAJE_GAME_OVER}
     * segundos, y luego navega automaticamente al menu principal.
     * El game loop continua ejecutandose pero no actualiza el juego porque
     * el estado {@code juegoActivo} esta en false.
     * </p>
     *
     * @param ganador el identificador del jugador ganador (0 para empate)
     */
    private void mostrarMensajeYVolverMenu(final int ganador) {
        final String mensaje = calcularMensajeVictoria(ganador);

        vistaJuego.mostrarMensajeCentral(mensaje, DURACION_MENSAJE_GAME_OVER);

        programarRetornoMenu();
    }

    /**
     * Calcula el mensaje de victoria basado en el ganador.
     *
     * @param ganador el identificador del jugador ganador
     * @return el mensaje de victoria correspondiente
     */
    private String calcularMensajeVictoria(final int ganador) {
        return ganador == 0 ? "EMPATE" : "GANADOR: JUGADOR " + ganador;
    }

    /**
     * Programa el retorno automatico al menu principal despues de la duracion del mensaje.
     */
    private void programarRetornoMenu() {
        callbackVolverMenu.forEach(callback -> {
            final Timeline timeline = new Timeline(
                new KeyFrame(
                    Duration.seconds(DURACION_MENSAJE_GAME_OVER),
                    event -> Platform.runLater(callback)
                )
            );
            timeline.play();
        });
    }

    /**{@inheritDoc}*/
    @Override
    public void alCompletarNivel() {
        if (vistaJuego != null) {
            vistaJuego.mostrarMensajeCentral("NIVEL COMPLETADO");
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void alGenrarItem(Item item) {
        if (vistaJuego != null) {
            vistaJuego.mostrarMensajeCentral("POWER-UP");
        }
    }
}
