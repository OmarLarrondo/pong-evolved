package patrones.builder;

/**
 * La enumeración {@code TipoBloque} define los distintos tipos de bloques
 * que pueden existir en el Pong. 
 * <p>
 * Cada tipo de bloque puede tener un comportamiento o una resistencia distinta
 * ante las colisiones con la pelota u otros objetos del juego.
 * </p>
 *
 * @author  Equipo-polimorfo
 * @version 1.0
 */
public enum TipoBloque {

    /**
     * Bloque que se destruye con un solo impacto.
     */
    DESTRUCTIBLE,

    /**
     * No puede ser destruido, es una barrera básicamente 
     */
    INDESTRUCTIBLE,

    /**
     * Bloque que al ser destruido otorga un bonus al player.
     */
    BONUS,

    /**
     * Bloque que requiere múltiples impactos para ser destruido.
     */
    MULTI_GOLPE
}
