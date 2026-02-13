package patrones.factory.ia;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;

/**
 * Repositorio inmutable que almacena las configuraciones predefinidas para los 10 niveles
 * de dificultad de la Inteligencia Artificial en Pong Evolved.
 *
 * <p>Este repositorio actúa como un <b>registro centralizado</b> de todas las configuraciones
 * de IA, proporcionando acceso funcional y type-safe a los parámetros de cada nivel.</p>
 *
 * <p><b>Tabla de configuraciones por nivel:</b></p>
 * <table border="1">
 *   <tr>
 *     <th>Nivel</th>
 *     <th>Nombre</th>
 *     <th>Retraso (s)</th>
 *     <th>Error (px)</th>
 *     <th>Descripción</th>
 *   </tr>
 *   <tr><td>1</td><td>Principiante</td><td>0.800</td><td>35.0</td><td>Muy lento, muy impreciso</td></tr>
 *   <tr><td>2</td><td>Novato</td><td>0.650</td><td>30.0</td><td>Lento, bastante impreciso</td></tr>
 *   <tr><td>3</td><td>Aprendiz</td><td>0.500</td><td>25.0</td><td>Lento, impreciso</td></tr>
 *   <tr><td>4</td><td>Básico</td><td>0.350</td><td>20.0</td><td>Reacción moderada, algo impreciso</td></tr>
 *   <tr><td>5</td><td>Intermedio</td><td>0.250</td><td>15.0</td><td>Reacción decente, precisión media</td></tr>
 *   <tr><td>6</td><td>Competente</td><td>0.180</td><td>12.0</td><td>Reacción rápida, bastante preciso</td></tr>
 *   <tr><td>7</td><td>Avanzado</td><td>0.120</td><td>8.0</td><td>Muy rápido, preciso</td></tr>
 *   <tr><td>8</td><td>Experto</td><td>0.080</td><td>5.0</td><td>Extremadamente rápido, muy preciso</td></tr>
 *   <tr><td>9</td><td>Maestro</td><td>0.050</td><td>3.0</td><td>Casi instantáneo, casi perfecto</td></tr>
 *   <tr><td>10</td><td>Leyenda</td><td>0.020</td><td>1.0</td><td>Reacción instantánea, precisión casi perfecta</td></tr>
 * </table>
 *
 * <p><b>Características de diseño:</b></p>
 * <ul>
 *   <li><b>Inmutabilidad total:</b> Usa {@code Map} inmutable de Vavr</li>
 *   <li><b>Thread-safety:</b> Seguro para acceso concurrente</li>
 *   <li><b>Singleton:</b> Instancia única compartida</li>
 *   <li><b>Programación funcional:</b> Acceso mediante {@code Option}</li>
 * </ul>
 *
 * <p><b>Ejemplo de uso:</b></p>
 * <pre>
 * // Obtener configuración de nivel 5
 * Option<ConfiguracionDificultad> config =
 *     RepositorioConfiguracionesIA.obtenerConfiguracion(DificultadIA.NIVEL_5);
 *
 * config.forEach(c -> System.out.println(
 *     "Retraso: " + c.obtenerRetrasoReaccion() + "s, " +
 *     "Error: " + c.obtenerAmplitudError() + "px"));
 * </pre>
 *
 * @author Equipo Polimorfo
 * @version 2.0
 * @see DificultadIA
 * @see ConfiguracionDificultad
 * @see FabricaIA
 */
public final class RepositorioConfiguracionesIA {

    /**
     * Mapa inmutable que asocia cada nivel de dificultad con su configuración.
     */
    private static final Map<DificultadIA, ConfiguracionDificultad> CONFIGURACIONES =
            inicializarConfiguraciones();

    /**
     * Constructor privado para prevenir instanciación.
     *
     * <p>Esta clase solo proporciona métodos estáticos y no debe ser instanciada.</p>
     */
    private RepositorioConfiguracionesIA() {
        throw new UnsupportedOperationException(
                "RepositorioConfiguracionesIA es una clase de utilidad y no debe ser instanciada");
    }

    /**
     * Inicializa el mapa inmutable de configuraciones para todos los niveles de dificultad.
     *
     * <p>Este método configura los parámetros de los 10 niveles siguiendo una progresión
     * que equilibra la dificultad:</p>
     *
     * <ul>
     *   <li><b>Retraso de reacción:</b> Disminuye exponencialmente de 0.8s a 0.02s</li>
     *   <li><b>Amplitud de error:</b> Disminuye linealmente de 35px a 1px</li>
     * </ul>
     *
     * <p>Los valores fueron calibrados para que:</p>
     * <ul>
     *   <li>Niveles 1-3: Sean más lentos que un jugador humano promedio (~0.25s)</li>
     *   <li>Niveles 4-6: Sean comparables a un jugador humano</li>
     *   <li>Niveles 7-10: Sean sobrehumanos, requiriendo habilidad excepcional para ganar</li>
     * </ul>
     *
     * @return un {@code Map} inmutable de Vavr con las configuraciones de todos los niveles
     */
    private static Map<DificultadIA, ConfiguracionDificultad> inicializarConfiguraciones() {
        return HashMap.of(
                DificultadIA.NIVEL_1, ConfiguracionDificultad.crear(0.800, 35.0),

                DificultadIA.NIVEL_2, ConfiguracionDificultad.crear(0.650, 30.0),

                DificultadIA.NIVEL_3, ConfiguracionDificultad.crear(0.500, 25.0),

                DificultadIA.NIVEL_4, ConfiguracionDificultad.crear(0.350, 20.0),

                DificultadIA.NIVEL_5, ConfiguracionDificultad.crear(0.250, 15.0),

                DificultadIA.NIVEL_6, ConfiguracionDificultad.crear(0.180, 12.0),

                DificultadIA.NIVEL_7, ConfiguracionDificultad.crear(0.120, 8.0),

                DificultadIA.NIVEL_8, ConfiguracionDificultad.crear(0.080, 5.0),

                DificultadIA.NIVEL_9, ConfiguracionDificultad.crear(0.050, 3.0),

                DificultadIA.NIVEL_10, ConfiguracionDificultad.crear(0.020, 1.0)
        );
    }

    /**
     * Obtiene la configuración asociada a un nivel de dificultad específico.
     *
     * <p>Este método utiliza programación funcional con {@code Option} de Vavr para
     * manejar de forma segura el caso donde el nivel no existe en el repositorio
     * (aunque en la práctica esto no debería ocurrir si el enum está completo).</p>
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * Option<ConfiguracionDificultad> config =
     *     RepositorioConfiguracionesIA.obtenerConfiguracion(DificultadIA.NIVEL_7);
     *
     * config
     *     .peek(c -> System.out.println("Configuración encontrada: " + c))
     *     .onEmpty(() -> System.err.println("Configuración no encontrada"));
     * </pre>
     *
     * @param dificultad el nivel de dificultad cuya configuración se desea obtener.
     *                   No debe ser {@code null}.
     * @return un {@code Option} que contiene la configuración si existe,
     *         o {@code Option.none()} si no se encuentra
     * @throws NullPointerException si {@code dificultad} es {@code null}
     */
    public static Option<ConfiguracionDificultad> obtenerConfiguracion(final DificultadIA dificultad) {
        return CONFIGURACIONES.get(dificultad);
    }

    /**
     * Obtiene la configuración asociada a un nivel de dificultad, lanzando una excepción
     * si la configuración no existe.
     *
     * <p>Este método es útil cuando se prefiere manejar errores de forma imperativa
     * en lugar del estilo funcional con {@code Option}.</p>
     *
     * @param dificultad el nivel de dificultad cuya configuración se desea obtener
     * @return la configuración correspondiente al nivel
     * @throws IllegalArgumentException si no existe configuración para el nivel especificado
     * @throws NullPointerException si {@code dificultad} es {@code null}
     */
    public static ConfiguracionDificultad obtenerConfiguracionDirecto(final DificultadIA dificultad) {
        return obtenerConfiguracion(dificultad)
                .getOrElseThrow(() -> new IllegalArgumentException(
                        "No existe configuración para el nivel: " + dificultad));
    }

    /**
     * Obtiene la configuración asociada a un número de nivel (1-10).
     *
     * <p>Este método es un atajo que combina la conversión de número a enum
     * y la obtención de la configuración en una sola operación.</p>
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * // Obtener configuración del nivel seleccionado por el usuario
     * int nivelSeleccionado = 7;
     * Option<ConfiguracionDificultad> config =
     *     RepositorioConfiguracionesIA.obtenerConfiguracionPorNumero(nivelSeleccionado);
     * </pre>
     *
     * @param numeroNivel el número de nivel (1-10)
     * @return un {@code Option} que contiene la configuración si el número es válido,
     *         o {@code Option.none()} si el número es inválido
     */
    public static Option<ConfiguracionDificultad> obtenerConfiguracionPorNumero(final int numeroNivel) {
        return DificultadIA.desdeNumeroNivel(numeroNivel)
                .flatMap(RepositorioConfiguracionesIA::obtenerConfiguracion);
    }

    /**
     * Obtiene la configuración asociada a un número de nivel, lanzando una excepción
     * si el número es inválido.
     *
     * @param numeroNivel el número de nivel (1-10)
     * @return la configuración correspondiente al nivel
     * @throws IllegalArgumentException si el número de nivel es inválido o no tiene configuración
     */
    public static ConfiguracionDificultad obtenerConfiguracionPorNumeroDirecto(final int numeroNivel) {
        return obtenerConfiguracionPorNumero(numeroNivel)
                .getOrElseThrow(() -> new IllegalArgumentException(
                        String.format("No existe configuración para el nivel %d. Debe estar entre 1 y 10.",
                                numeroNivel)));
    }

    /**
     * Obtiene el mapa completo de configuraciones como una colección inmutable de Vavr.
     *
     * <p>Útil para operaciones que necesiten iterar sobre todas las configuraciones,
     * como generar reportes o validar la integridad del repositorio.</p>
     *
     * @return un {@code Map} inmutable con todas las configuraciones
     */
    public static Map<DificultadIA, ConfiguracionDificultad> obtenerTodasLasConfiguraciones() {
        return CONFIGURACIONES;
    }

    /**
     * Verifica si existe una configuración para el nivel de dificultad especificado.
     *
     * @param dificultad el nivel de dificultad a verificar
     * @return {@code true} si existe configuración para ese nivel, {@code false} en caso contrario
     * @throws NullPointerException si {@code dificultad} es {@code null}
     */
    public static boolean existeConfiguracion(final DificultadIA dificultad) {
        return CONFIGURACIONES.containsKey(dificultad);
    }

    /**
     * Obtiene la cantidad total de configuraciones almacenadas en el repositorio.
     *
     * <p>En condiciones normales, este método debería retornar 10 (una configuración
     * por cada nivel de dificultad).</p>
     *
     * @return el número total de configuraciones
     */
    public static int obtenerCantidadConfiguraciones() {
        return CONFIGURACIONES.size();
    }

    /**
     * Valida la integridad del repositorio verificando que existan configuraciones
     * para todos los niveles de dificultad.
     *
     * <p>Este método es útil para pruebas y validación durante el desarrollo.</p>
     *
     * @return {@code true} si el repositorio contiene configuraciones para todos los niveles,
     *         {@code false} en caso contrario
     */
    public static boolean validarIntegridad() {
        return DificultadIA.obtenerTodosLosNiveles()
                .forAll(nivel -> CONFIGURACIONES.containsKey(nivel));
    }

    /**
     * Retorna una representación en cadena de texto del repositorio.
     *
     * @return una cadena descriptiva con información del repositorio
     */
    public static String obtenerResumen() {
        final boolean integro = validarIntegridad();
        final int cantidad = obtenerCantidadConfiguraciones();

        return String.format("RepositorioConfiguracionesIA[configuraciones=%d, íntegro=%s]",
                cantidad, integro ? "Sí" : "No");
    }
}
