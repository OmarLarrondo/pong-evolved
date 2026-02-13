package mvc.modelo.enums;

/**
 * Enumeración que representa las direcciones posibles de movimiento
 * para entidades dentro del pong
 *
 * <p>
 * Cada valor indica la dirección vertical que puede tomar un objeto
 * durante su actualización.
 * </p>
 *
 * <ul>
 *   <li>{@link #ARRIBA}: Movimiento hacia arriba en el eje Y .</li>
 *   <li>{@link #ABAJO}: Movimiento hacia abajo en el eje Y.</li>
 *   <li>{@link #NINGUNA}: Sin movimiento vertical.</li>
 * </ul>
 * 
 * @author Equipo-polimorfo
 * @version 1.0
 */
public enum Direccion {
    /** Movimiento hacia arriba. */
    ARRIBA,

    /** Movimiento hacia abajo. */
    ABAJO,

    /** Sin movimiento. */
    NINGUNA
}
