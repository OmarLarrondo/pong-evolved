package patrones.strategy.movimiento;

import io.vavr.control.Option;
import io.vavr.control.Try;
import mvc.modelo.enums.Direccion;
import mvc.modelo.entidades.paleta.Paleta;
import mvc.modelo.entidades.pelota.Pelota;

/**
 * Estrategia de movimiento parametrizada para la Inteligencia Artificial del juego Pong.
 *
 * <p>Esta estrategia unificada implementa el comportamiento de una IA que puede configurarse
 * con diferentes niveles de dificultad mediante dos parámetros fundamentales:</p>
 *
 * <ul>
 *   <li><b>Retraso de Reacción:</b> Tiempo en segundos que tarda la IA en procesar
 *       y reaccionar a los cambios en la trayectoria de la pelota. Valores más bajos
 *       representan reacciones más rápidas y, por tanto, mayor dificultad.</li>
 *   <li><b>Amplitud de Error:</b> Desviación máxima en píxeles que la IA puede cometer
 *       al predecir el punto de impacto de la pelota. Valores más bajos representan
 *       mayor precisión y, por tanto, mayor dificultad.</li>
 * </ul>
 *
 * <p><b>Algoritmo de decisión:</b></p>
 * <ol>
 *   <li>Actualiza el temporizador de reacción con el tiempo transcurrido</li>
 *   <li>Si el intervalo de reacción ha transcurrido:
 *     <ul>
 *       <li>Predice la posición Y donde la pelota impactará en la coordenada X de la paleta</li>
 *       <li>Añade un error aleatorio a la predicción (simulando imperfección)</li>
 *       <li>Compara la posición actual de la paleta con el objetivo calculado</li>
 *       <li>Retorna la dirección de movimiento apropiada (ARRIBA, ABAJO, o NINGUNA)</li>
 *     </ul>
 *   </li>
 *   <li>Si el intervalo de reacción no ha transcurrido, retorna NINGUNA (no se mueve)</li>
 * </ol>
 *
 * <p>Esta implementación utiliza la biblioteca Vavr para garantizar inmutabilidad,
 * manejo seguro de errores y composición funcional.</p>
 *
 * @author Equipo Polimorfo
 * @version 2.0
 * @see EstrategiaMovimiento
 * @see TemporizadorReaccion
 * @see CalcularTrayectoria
 * @see GeneradorErrorMovimiento
 */
public final class EstrategiaMovimientoIA implements EstrategiaMovimiento {

    private final TemporizadorReaccion temporizador;
    private final CalcularTrayectoria calculador;
    private final GeneradorErrorMovimiento generadorError;
    private final double retrasoReaccion;
    private final double amplitudError;

    /**
     * Construye una nueva estrategia de movimiento para la IA con los parámetros especificados.
     *
     * <p>Los parámetros determinan el nivel de dificultad de la IA:</p>
     * <ul>
     *   <li><b>Retraso bajo + Error bajo:</b> IA muy difícil (reacción rápida y precisa)</li>
     *   <li><b>Retraso alto + Error alto:</b> IA fácil (reacción lenta e imprecisa)</li>
     * </ul>
     *
     * @param retrasoReaccion tiempo en segundos que tarda la IA en reaccionar a cambios
     *                        en la trayectoria de la pelota. Debe ser mayor que 0.
     * @param amplitudError   desviación máxima en píxeles que la IA puede cometer al
     *                        predecir el punto de impacto. Debe estar en el rango [0.0, 40.0].
     * @throws IllegalArgumentException si {@code retrasoReaccion} es menor o igual a 0,
     *                                  o si {@code amplitudError} está fuera del rango válido
     */
    private EstrategiaMovimientoIA(final double retrasoReaccion, final double amplitudError) {
        this.retrasoReaccion = retrasoReaccion;
        this.amplitudError = amplitudError;
        this.temporizador = new TemporizadorReaccion(retrasoReaccion);
        this.calculador = new CalcularTrayectoria();
        this.generadorError = new GeneradorErrorMovimiento(amplitudError);
    }

    /**
     * Método factory para crear una instancia de la estrategia de movimiento de IA
     * de manera segura utilizando programación funcional.
     *
     * <p>Este método encapsula la creación de la estrategia en un contenedor {@code Try}
     * de Vavr, que maneja automáticamente cualquier excepción que pueda ocurrir durante
     * la validación de parámetros.</p>
     *
     * @param retrasoReaccion tiempo en segundos que tarda la IA en reaccionar. Debe ser > 0.
     * @param amplitudError   desviación máxima en píxeles. Debe estar en [0.0, 40.0].
     * @return un {@code Try} que contiene la estrategia creada si la validación fue exitosa,
     *         o una excepción si los parámetros son inválidos
     */
    public static Try<EstrategiaMovimientoIA> crear(
            final double retrasoReaccion,
            final double amplitudError) {
        return Try.of(() -> validarParametros(retrasoReaccion, amplitudError))
                .map(params -> new EstrategiaMovimientoIA(params._1, params._2));
    }

    /**
     * Método factory simplificado que lanza excepciones directamente en caso de error.
     *
     * <p>Útil cuando se desea manejar las excepciones de forma imperativa en lugar
     * de usar el estilo funcional con {@code Try}.</p>
     *
     * @param retrasoReaccion tiempo en segundos que tarda la IA en reaccionar. Debe ser > 0.
     * @param amplitudError   desviación máxima en píxeles. Debe estar en [0.0, 40.0].
     * @return una nueva instancia de {@code EstrategiaMovimientoIA}
     * @throws IllegalArgumentException si los parámetros no son válidos
     */
    public static EstrategiaMovimientoIA crearDirecto(
            final double retrasoReaccion,
            final double amplitudError) {
        validarParametros(retrasoReaccion, amplitudError);
        return new EstrategiaMovimientoIA(retrasoReaccion, amplitudError);
    }

    /**
     * Valida los parámetros de configuración de la estrategia.
     *
     * @param retrasoReaccion tiempo de reacción en segundos
     * @param amplitudError   amplitud del error en píxeles
     * @return una tupla con los parámetros validados
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    private static io.vavr.Tuple2<Double, Double> validarParametros(
            final double retrasoReaccion,
            final double amplitudError) {
        if (retrasoReaccion <= 0) {
            throw new IllegalArgumentException(
                    String.format("El retraso de reacción debe ser mayor que 0. Valor recibido: %.3f",
                            retrasoReaccion));
        }
        if (amplitudError < 0.0 || amplitudError > 40.0) {
            throw new IllegalArgumentException(
                    String.format("La amplitud del error debe estar en el rango [0.0, 40.0]. Valor recibido: %.2f",
                            amplitudError));
        }
        return io.vavr.Tuple.of(retrasoReaccion, amplitudError);
    }

    /**
     * Calcula la dirección de movimiento óptima para la paleta controlada por la IA.
     *
     * <p>Este método implementa el algoritmo de decisión de la IA, que consiste en:</p>
     * <ol>
     *   <li>Actualizar el temporizador de reacción</li>
     *   <li>Verificar si ha transcurrido el intervalo de reacción</li>
     *   <li>Si puede reaccionar: predecir posición de impacto + error, y determinar dirección</li>
     *   <li>Si no puede reaccionar: permanecer inmóvil</li>
     * </ol>
     *
     * <p>La implementación utiliza programación funcional con {@code Option} de Vavr
     * para manejar el caso de "puede reaccionar" vs "no puede reaccionar" de forma elegante.</p>
     *
     * @param paleta      la paleta controlada por la IA. No debe ser {@code null}.
     * @param pelota      la pelota del juego. No debe ser {@code null}.
     * @param tiempoDelta tiempo transcurrido desde el último frame en segundos. Debe ser >= 0.
     * @return la dirección de movimiento calculada: {@code ARRIBA}, {@code ABAJO}, o {@code NINGUNA}
     * @throws NullPointerException si {@code paleta} o {@code pelota} son {@code null}
     */
    @Override
    public Direccion calcularMovimiento(
            final Paleta paleta,
            final Pelota pelota,
            final double tiempoDelta) {

        temporizador.actualizar(tiempoDelta);

        return Option.when(temporizador.puedeReaccionar(), () -> {
                    final double yPredicho = calculador.predecirPosicionImpacto(
                            pelota,
                            paleta.obtenerX());
                    final double yObjetivo = yPredicho + generadorError.generarError();
                    return determinarDireccion(paleta.obtenerY(), yObjetivo);
                })
                .getOrElse(Direccion.NINGUNA);
    }

    /**
     * Determina la dirección de movimiento comparando la posición actual con el objetivo.
     *
     * <p>Función pura que implementa la lógica de decisión de movimiento:</p>
     * <ul>
     *   <li>Si la paleta está por encima del objetivo → mover ARRIBA</li>
     *   <li>Si la paleta está por debajo del objetivo → mover ABAJO</li>
     *   <li>Si la paleta está en el objetivo → NO MOVERSE</li>
     * </ul>
     *
     * @param yActual   posición Y actual de la paleta
     * @param yObjetivo posición Y objetivo calculada (con error incluido)
     * @return la dirección de movimiento apropiada
     */
    private Direccion determinarDireccion(final double yActual, final double yObjetivo) {
        if (yActual > yObjetivo) {
            return Direccion.ARRIBA;
        } else if (yActual < yObjetivo) {
            return Direccion.ABAJO;
        } else {
            return Direccion.NINGUNA;
        }
    }

    /**
     * Obtiene el valor del retraso de reacción configurado para esta estrategia.
     *
     * @return el tiempo de reacción en segundos
     */
    public double obtenerRetrasoReaccion() {
        return retrasoReaccion;
    }

    /**
     * Obtiene el valor de la amplitud de error configurado para esta estrategia.
     *
     * @return la amplitud de error en píxeles
     */
    public double obtenerAmplitudError() {
        return amplitudError;
    }

    /**
     * Retorna una representación en cadena de texto de esta estrategia.
     *
     * @return una cadena descriptiva con los parámetros de configuración
     */
    @Override
    public String toString() {
        return String.format("EstrategiaMovimientoIA[retraso=%.3fs, error=%.1fpx]",
                retrasoReaccion, amplitudError);
    }
}
