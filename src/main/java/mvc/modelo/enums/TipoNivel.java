package mvc.modelo.enums;

/**
 * Enumeración {@code TipoNivel} que representa los diferentes tipos de niveles 
 * disponibles en el juego Pong.
 * 
 * <p>
 * Cada tipo define una modalidad distinta de juego que puede afectar
 * la dinámica, los oponentes o las condiciones del nivel.
 * </p>
 *
 * @author  Equipo-polimorfo
 * @version 1.0
 */
public enum TipoNivel {

    /**
     * Nivel clásico (1 vs 1) donde ambos jugadores son controlados por humanos.
     */
    CLASICO,

    /**
     * Nivel en el que un jugador humano compite contra una inteligencia artificial.
     */
    INTELIGENTE,

    /**
     * Nivel creado y configurado manualmente por el usuario.
     */
    PERSONALIZADO
}
