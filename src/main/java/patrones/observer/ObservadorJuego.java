package patrones.observer;

import mvc.modelo.items.Item;

/**
 * La interfaz {@code Observer} define los métodos que deben implementar
 * los objetos interesados en recibir notificaciones de eventos generados
 * dentro del juego. 
 * <p>
 * Forma parte de la implementación del patrón de diseño <b>Observer</b>,
 * donde los observadores se suscriben a un sujeto (observable) para 
 * reaccionar a ciertos cambios o sucesos.
 * </p>
 *
 * @see Observable
 * @author Equipo-polimorfo
 * @version 1.0
 */
public interface ObservadorJuego {

    /**
     * Se invoca cuando el puntaje de un jugador cambia.
     *
     * @param jugador el identificador del jugador cuyo puntaje cambió
     * @param nuevoPuntaje el nuevo valor del puntaje
     */
    void alcambiarPuntaje(int jugador, int nuevoPuntaje);

    /**
     * Se invoca cuando el juego ha finalizado.
     *
     * @param ganador el identificador del jugador ganador
     */
    void alTerminarJuego(int ganador);

    /**
     * Se invoca cuando un nivel es completado exitosamente.
     */
    void alCompletarNivel();

    /**
     * Se invoca cuando se genera un nuevo ítem en el juego.
     *
     * @param item el objeto {@link Item} que ha sido generado
     */
    void alGenrarItem(Item item);
}
