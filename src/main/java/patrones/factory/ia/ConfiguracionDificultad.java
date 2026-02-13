package patrones.factory.ia;

import io.vavr.control.Try;

import java.util.Objects;

/**
 * Clase inmutable que encapsula los parámetros de configuración para un nivel
 * de dificultad de la Inteligencia Artificial en Pong Evolved.
 *
 * <p>Esta clase representa un <b>Value Object</b> que contiene dos parámetros fundamentales
 * que determinan el comportamiento de la IA:</p>
 *
 * <ul>
 *   <li><b>Retraso de Reacción:</b> Tiempo en segundos que tarda la IA en procesar
 *       y reaccionar a los cambios en la trayectoria de la pelota.</li>
 *   <li><b>Amplitud de Error:</b> Desviación máxima en píxeles que la IA puede cometer
 *       al predecir el punto de impacto de la pelota.</li>
 * </ul>
 *
 * <p><b>Características de diseño:</b></p>
 * <ul>
 *   <li><b>Inmutabilidad:</b> Todos los campos son {@code final} y no hay setters</li>
 *   <li><b>Thread-safety:</b> Seguro para uso concurrente gracias a la inmutabilidad</li>
 *   <li><b>Validación:</b> Los parámetros se validan en el constructor</li>
 *   <li><b>Programación funcional:</b> Uso de Vavr para creación segura con {@code Try}</li>
 * </ul>
 *
 * <p><b>Ejemplo de uso:</b></p>
 * <pre>
 * // Creación directa
 * ConfiguracionDificultad config = ConfiguracionDificultad.crear(0.25, 15.0);
 *
 * // Creación funcional con manejo de errores
 * Try<ConfiguracionDificultad> tryConfig = ConfiguracionDificultad.crearSeguro(0.25, 15.0);
 * tryConfig.forEach(config -> System.out.println(config));
 * </pre>
 *
 * @author Equipo Polimorfo
 * @version 2.0
 * @see DificultadIA
 * @see RepositorioConfiguracionesIA
 * @see FabricaIA
 */
public final class ConfiguracionDificultad {

    private final double retrasoReaccion;
    private final double amplitudError;

    /**
     * Valor mínimo válido para el retraso de reacción en segundos.
     */
    public static final double RETRASO_MINIMO = 0.01;

    /**
     * Valor máximo válido para el retraso de reacción en segundos.
     */
    public static final double RETRASO_MAXIMO = 2.0;

    /**
     * Valor mínimo válido para la amplitud de error en píxeles.
     */
    public static final double ERROR_MINIMO = 0.0;

    /**
     * Valor máximo válido para la amplitud de error en píxeles.
     */
    public static final double ERROR_MAXIMO = 40.0;

    /**
     * Construye una nueva configuración de dificultad con los parámetros especificados.
     *
     * @param retrasoReaccion tiempo en segundos que tarda la IA en reaccionar.
     *                        Debe estar en el rango [{@link #RETRASO_MINIMO}, {@link #RETRASO_MAXIMO}].
     * @param amplitudError   desviación máxima en píxeles que la IA puede cometer.
     *                        Debe estar en el rango [{@link #ERROR_MINIMO}, {@link #ERROR_MAXIMO}].
     * @throws IllegalArgumentException si algún parámetro está fuera de su rango válido
     */
    private ConfiguracionDificultad(final double retrasoReaccion, final double amplitudError) {
        validarRetrasoReaccion(retrasoReaccion);
        validarAmplitudError(amplitudError);

        this.retrasoReaccion = retrasoReaccion;
        this.amplitudError = amplitudError;
    }

    /**
     * Método factory para crear una configuración de dificultad.
     *
     * <p>Este método valida los parámetros y crea la instancia, lanzando una excepción
     * si la validación falla.</p>
     *
     * @param retrasoReaccion tiempo de reacción en segundos. Rango válido: [0.01, 2.0]
     * @param amplitudError   amplitud del error en píxeles. Rango válido: [0.0, 40.0]
     * @return una nueva instancia de {@code ConfiguracionDificultad}
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public static ConfiguracionDificultad crear(final double retrasoReaccion, final double amplitudError) {
        return new ConfiguracionDificultad(retrasoReaccion, amplitudError);
    }

    /**
     * Método factory seguro que encapsula la creación en un contenedor {@code Try} de Vavr.
     *
     * <p>Este método permite un estilo de programación funcional donde los errores
     * se manejan mediante composición en lugar de excepciones.</p>
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * Try<ConfiguracionDificultad> resultado = ConfiguracionDificultad.crearSeguro(0.5, 20.0);
     *
     * resultado
     *     .onSuccess(config -> System.out.println("Configuración creada: " + config))
     *     .onFailure(error -> System.err.println("Error: " + error.getMessage()));
     * </pre>
     *
     * @param retrasoReaccion tiempo de reacción en segundos
     * @param amplitudError   amplitud del error en píxeles
     * @return un {@code Try} que contiene la configuración si la validación fue exitosa,
     *         o una excepción si los parámetros son inválidos
     */
    public static Try<ConfiguracionDificultad> crearSeguro(
            final double retrasoReaccion,
            final double amplitudError) {
        return Try.of(() -> crear(retrasoReaccion, amplitudError));
    }

    /**
     * Valida que el retraso de reacción esté en el rango válido.
     *
     * @param retrasoReaccion el valor a validar
     * @throws IllegalArgumentException si el valor está fuera del rango válido
     */
    private static void validarRetrasoReaccion(final double retrasoReaccion) {
        if (retrasoReaccion < RETRASO_MINIMO || retrasoReaccion > RETRASO_MAXIMO) {
            throw new IllegalArgumentException(
                    String.format("El retraso de reacción debe estar en el rango [%.2f, %.2f] segundos. Valor recibido: %.3f",
                            RETRASO_MINIMO, RETRASO_MAXIMO, retrasoReaccion));
        }
    }

    /**
     * Valida que la amplitud de error esté en el rango válido.
     *
     * @param amplitudError el valor a validar
     * @throws IllegalArgumentException si el valor está fuera del rango válido
     */
    private static void validarAmplitudError(final double amplitudError) {
        if (amplitudError < ERROR_MINIMO || amplitudError > ERROR_MAXIMO) {
            throw new IllegalArgumentException(
                    String.format("La amplitud de error debe estar en el rango [%.1f, %.1f] píxeles. Valor recibido: %.2f",
                            ERROR_MINIMO, ERROR_MAXIMO, amplitudError));
        }
    }

    /**
     * Obtiene el tiempo de reacción configurado.
     *
     * @return el retraso de reacción en segundos
     */
    public double obtenerRetrasoReaccion() {
        return retrasoReaccion;
    }

    /**
     * Obtiene la amplitud de error configurada.
     *
     * @return la amplitud de error en píxeles
     */
    public double obtenerAmplitudError() {
        return amplitudError;
    }

    /**
     * Calcula un índice de dificultad normalizado basado en los parámetros.
     *
     * <p>Este método combina ambos parámetros en un único valor numérico que representa
     * la dificultad general. Valores más altos indican mayor dificultad.</p>
     *
     * <p>La fórmula es:</p>
     * <pre>
     * índice = (1 - retrasoNormalizado) × 0.5 + (1 - errorNormalizado) × 0.5
     * </pre>
     *
     * <p>Donde:</p>
     * <ul>
     *   <li>{@code retrasoNormalizado = (retraso - MIN) / (MAX - MIN)}</li>
     *   <li>{@code errorNormalizado = (error - MIN) / (MAX - MIN)}</li>
     * </ul>
     *
     * @return un valor entre 0.0 (más fácil) y 1.0 (más difícil)
     */
    public double calcularIndiceDificultad() {
        final double retrasoNormalizado =
                (retrasoReaccion - RETRASO_MINIMO) / (RETRASO_MAXIMO - RETRASO_MINIMO);
        final double errorNormalizado =
                (amplitudError - ERROR_MINIMO) / (ERROR_MAXIMO - ERROR_MINIMO);

        return (1.0 - retrasoNormalizado) * 0.5 + (1.0 - errorNormalizado) * 0.5;
    }

    /**
     * Compara esta configuración con otra para determinar si son iguales.
     *
     * <p>Dos configuraciones son iguales si tienen los mismos valores de
     * retraso de reacción y amplitud de error.</p>
     *
     * @param obj el objeto a comparar
     * @return {@code true} si las configuraciones son iguales, {@code false} en caso contrario
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ConfiguracionDificultad that = (ConfiguracionDificultad) obj;
        return Double.compare(that.retrasoReaccion, retrasoReaccion) == 0 &&
               Double.compare(that.amplitudError, amplitudError) == 0;
    }

    /**
     * Calcula el código hash de esta configuración.
     *
     * @return el código hash basado en los parámetros de configuración
     */
    @Override
    public int hashCode() {
        return Objects.hash(retrasoReaccion, amplitudError);
    }

    /**
     * Retorna una representación en cadena de texto de esta configuración.
     *
     * @return una cadena descriptiva con los parámetros de configuración
     */
    @Override
    public String toString() {
        return String.format("ConfiguracionDificultad[retraso=%.3fs, error=%.1fpx, índice=%.2f]",
                retrasoReaccion, amplitudError, calcularIndiceDificultad());
    }
}
