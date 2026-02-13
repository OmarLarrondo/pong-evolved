package mvc.modelo.enums;

/**
 * Enumeración que representa los diferentes modos de juego disponibles
 * en el sistema Pong Evolved.
 * <p>Cada valor indica un modo de juego disponible.</p>
 * <ul>
 *   <li>{@link #CLASICO}: Modo clásico (dos paletas y una pelota).</li>
 *   <li>{@link #DOS_JUGADORES}: Modo dos jugadores 
 *       (dos paletas y <b>dos pelotas</b>, cada una con movimiento independiente; 
 *       se pueden agregar distintos tipos de bloques y más elementos).</li>
 *   <li>{@link #CONTRA_IA}: Un jugador contra una IA, configurable en distintas dificultades.</li>
 *   <li>{@link #MODO_CONSTRUCTOR}: Modo constructor, permite la creación de niveles personalizados.</li>
 * </ul>
 *
 * @author Equipo-polimorfo
 * @version 1.0
 */
public enum ModoJuego {

    /** Modo clásico de juego. */
    CLASICO,
    
    /** Modo de dos jugadores usando dos pelotas independientes. */
    DOS_JUGADORES,

    /** Un jugador contra una inteligencia artificial. */
    CONTRA_IA,

    /** Permite la creación de niveles personalizados. */
    MODO_CONSTRUCTOR
}
