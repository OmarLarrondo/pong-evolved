package patrones.strategy.movimiento;

import mvc.modelo.enums.Direccion;
import mvc.modelo.entidades.paleta.Paleta;
import mvc.modelo.entidades.pelota.Pelota;

/**
 * Define el contrato para las estrategias de movimiento de paletas en el juego Pong.
 *
 * <p>Esta interfaz implementa el patrón Strategy, permitiendo que diferentes algoritmos
 * de movimiento sean intercambiables. Las paletas pueden usar distintas implementaciones
 * de esta interfaz para determinar su comportamiento:</p>
 *
 * <ul>
 *   <li><b>Movimiento de IA:</b> Estrategias que calculan el movimiento óptimo para interceptar la pelota</li>
 *   <li><b>Movimiento del jugador:</b> Estrategias que responden a la entrada del usuario</li>
 *   <li><b>Movimiento personalizado:</b> Cualquier otro comportamiento de movimiento definido</li>
 * </ul>
 *
 * <p><b>Ventajas del patrón Strategy aplicado aquí:</b></p>
 * <ul>
 *   <li>Permite cambiar dinámicamente el comportamiento de las paletas en tiempo de ejecución</li>
 *   <li>Facilita la adición de nuevos tipos de comportamiento sin modificar código existente</li>
 *   <li>Separa el algoritmo de movimiento de la clase {@code Paleta}</li>
 *   <li>Permite tener múltiples niveles de dificultad de IA mediante diferentes implementaciones</li>
 * </ul>
 *
 * @author Equipo Polimorfo
 * @version 2.0
 * @see EstrategiaMovimientoIA
 * @see EstrategiaMovimientoJugador
 * @see Paleta
 */
public interface EstrategiaMovimiento {

    /**
     * Calcula la dirección en la que debe moverse la paleta.
     *
     * <p>Este método es invocado en cada frame del juego para determinar cómo debe
     * moverse la paleta basándose en el estado actual del juego.</p>
     *
     * <p><b>Implementaciones típicas:</b></p>
     * <ul>
     *   <li><b>IA:</b> Analiza la posición y trayectoria de la pelota para calcular
     *       el movimiento óptimo de interceptación</li>
     *   <li><b>Jugador:</b> Lee la entrada del teclado o mouse y retorna la dirección
     *       correspondiente</li>
     * </ul>
     *
     * @param paleta      la paleta que será movida. No debe ser {@code null}.
     * @param pelota      la pelota del juego, usada para calcular el movimiento óptimo. No debe ser {@code null}.
     * @param tiempoDelta tiempo transcurrido desde el último frame en segundos. Debe ser mayor o igual a 0.
     * @return la dirección en la que debe moverse la paleta: {@code ARRIBA}, {@code ABAJO}, o {@code NINGUNA}
     */
    Direccion calcularMovimiento(Paleta paleta, Pelota pelota, double tiempoDelta);
}
