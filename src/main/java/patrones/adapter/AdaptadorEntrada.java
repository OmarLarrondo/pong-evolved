package patrones.adapter;

/**
 * Interfaz {@code AdaptadorEntrada} que define un contrato para adaptar
 * las entradas del usuario al sistema de control del juego Pong.
 * 
 * <p>Este adaptador permite abstraer la fuente de entrada (por ejemplo,
 * teclado o mouse) para que el controlador del juego
 * no dependa directamente de una implementación específica.</p>
 * 
 * <p>Se utiliza el patrón <strong>Adapter</strong> para desacoplar la lógica
 * del juego del dispositivo de entrada concreto. Cada implementación de esta
 * interfaz traduce las señales de entrada en métodos booleanos simples que
 * indican el estado de las teclas o del mouse.</p>
 * 
 *  <p>Esta interfaz es utilizada por las clases del controlador para consultar el estado
 *  de las entradas del jugador de manera uniforme, sin importar el dispositivo concreto.</p>
 * 
 * <p>Métodos principales:</p>
 * <ul>
 *   <li>{@link #arribaPresionado()} — Detecta si se ha presionado la tecla o mouse para mover hacia arriba.</li>
 *   <li>{@link #abajoPresionado()} — Detecta si se ha presionado la tecla o mouse para mover hacia abajo.</li>
 *   <li>{@link #pausaPresionado()} — Detecta si se ha presionado la tecla o mouse de pausa.</li>
 * </ul>
 * 
 * @author Equipo-polimorfo
 * @version 1.0
 */
public interface AdaptadorEntrada {
    /**
     * Verifica si el mouse o tecla para mover hacia arriba
     * está actualmente presionado.
     *
     * @return {@code true} si el jugador ha presionado la acción de subir;
     *         {@code false} en caso contrario.
     */
    boolean arribaPresionado();

    /**
     * Verifica si el mouse o tecla para mover hacia abajo
     * está actualmente presionado.
     *
     * @return {@code true} si el jugador ha presionado la acción de bajar;
     *         {@code false} en caso contrario.
     */
    boolean abajoPresionado();

    /**
     * Verifica si el mouse o tecla de pausa está presionado.
     *
     * @return {@code true} si se ha presionado la pausa;
     *         {@code false} en caso contrario.
     */
    boolean pausaPresionado();
}
