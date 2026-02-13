package patrones.factory.ia;

import io.vavr.control.Option;
import io.vavr.control.Try;
import patrones.strategy.movimiento.EstrategiaMovimiento;
import patrones.strategy.movimiento.EstrategiaMovimientoIA;

/**
 * Fábrica (Factory) para crear instancias de estrategias de Inteligencia Artificial
 * con diferentes niveles de dificultad en Pong Evolved.
 *
 * <p>Esta clase implementa el <b>patrón de diseño Factory</b>, encapsulando la lógica
 * de creación de estrategias de IA y proporcionando una interfaz simple para obtener
 * estrategias configuradas según el nivel de dificultad deseado.</p>
 *
 * <p><b>Responsabilidades:</b></p>
 * <ul>
 *   <li>Crear estrategias de IA configuradas con los parámetros apropiados</li>
 *   <li>Abstraer la complejidad de configuración de cada nivel</li>
 *   <li>Proporcionar métodos de conveniencia para niveles específicos</li>
 *   <li>Manejar errores de creación de forma funcional con {@code Try} de Vavr</li>
 * </ul>
 *
 * <p><b>Características de diseño:</b></p>
 * <ul>
 *   <li><b>Singleton:</b> Instancia única compartida</li>
 *   <li><b>Inmutabilidad:</b> No mantiene estado mutable</li>
 *   <li><b>Thread-safety:</b> Seguro para uso concurrente</li>
 *   <li><b>Programación funcional:</b> Uso extensivo de Vavr</li>
 * </ul>
 *
 * <p><b>Ejemplo de uso:</b></p>
 * <pre>
 * FabricaIA fabrica = FabricaIA.obtenerInstancia();
 *
 * // Crear IA de nivel 7
 * Option<EstrategiaMovimiento> estrategia = fabrica.crearIA(DificultadIA.NIVEL_7);
 * estrategia.forEach(e -> paleta.establecerEstrategia(e));
 *
 * // Crear IA por número de nivel (del selector de UI)
 * int nivelSeleccionado = 5;
 * Option<EstrategiaMovimiento> estrategiaPorNumero = fabrica.crearIAPorNumero(nivelSeleccionado);
 * </pre>
 *
 * @author Equipo Polimorfo
 * @version 2.0
 * @see DificultadIA
 * @see ConfiguracionDificultad
 * @see EstrategiaMovimientoIA
 * @see RepositorioConfiguracionesIA
 */
public final class FabricaIA {

    /**
     * Instancia única de la fábrica (Singleton).
     */
    private static final FabricaIA INSTANCIA = new FabricaIA();

    /**
     * Constructor privado para implementar el patrón Singleton.
     *
     * <p>La única instancia de esta clase se crea estáticamente y se obtiene
     * mediante {@link #obtenerInstancia()}.</p>
     */
    private FabricaIA() {
        // Constructor privado para Singleton
    }

    /**
     * Obtiene la instancia única de la fábrica de IA (Singleton).
     *
     * @return la instancia única de {@code FabricaIA}
     */
    public static FabricaIA obtenerInstancia() {
        return INSTANCIA;
    }

    /**
     * Crea una estrategia de IA configurada según el nivel de dificultad especificado.
     *
     * <p>Este es el método principal de la fábrica. Utiliza programación funcional
     * con {@code Option} de Vavr para manejar de forma segura el caso donde no
     * existe configuración para el nivel especificado.</p>
     *
     * <p><b>Flujo de creación:</b></p>
     * <ol>
     *   <li>Obtiene la configuración del repositorio para el nivel especificado</li>
     *   <li>Extrae los parámetros de retraso de reacción y amplitud de error</li>
     *   <li>Crea una nueva instancia de {@code EstrategiaMovimientoIA} con esos parámetros</li>
     *   <li>Retorna la estrategia envuelta en un {@code Option}</li>
     * </ol>
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * FabricaIA fabrica = FabricaIA.obtenerInstancia();
     *
     * Option<EstrategiaMovimiento> estrategia = fabrica.crearIA(DificultadIA.NIVEL_8);
     *
     * estrategia
     *     .peek(e -> System.out.println("IA creada: " + e))
     *     .onEmpty(() -> System.err.println("No se pudo crear la IA"));
     * </pre>
     *
     * @param dificultad el nivel de dificultad de la IA a crear. No debe ser {@code null}.
     * @return un {@code Option} que contiene la estrategia de IA si se creó exitosamente,
     *         o {@code Option.none()} si no existe configuración para ese nivel
     * @throws NullPointerException si {@code dificultad} es {@code null}
     */
    public Option<EstrategiaMovimiento> crearIA(final DificultadIA dificultad) {
        return RepositorioConfiguracionesIA.obtenerConfiguracion(dificultad)
                .map(config -> EstrategiaMovimientoIA.crearDirecto(
                        config.obtenerRetrasoReaccion(),
                        config.obtenerAmplitudError()));
    }

    /**
     * Crea una estrategia de IA por número de nivel (1-10).
     *
     * <p>Este método es un atajo conveniente que combina la conversión de número
     * a {@code DificultadIA} y la creación de la estrategia en una sola operación.
     * Es especialmente útil cuando se trabaja con la selección de nivel del usuario
     * desde la interfaz gráfica.</p>
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * // Usuario selecciona nivel 7 desde la UI
     * int nivelSeleccionado = controlador.obtenerNivelSeleccionado();
     *
     * FabricaIA.obtenerInstancia()
     *     .crearIAPorNumero(nivelSeleccionado)
     *     .forEach(estrategia -> paleta.establecerEstrategia(estrategia));
     * </pre>
     *
     * @param numeroNivel el número de nivel (1-10)
     * @return un {@code Option} que contiene la estrategia si el número es válido,
     *         o {@code Option.none()} si el número es inválido o no tiene configuración
     */
    public Option<EstrategiaMovimiento> crearIAPorNumero(final int numeroNivel) {
        return DificultadIA.desdeNumeroNivel(numeroNivel)
                .flatMap(this::crearIA);
    }

    /**
     * Crea una estrategia de IA de manera segura, encapsulando posibles errores en un {@code Try}.
     *
     * <p>Este método permite un manejo de errores completamente funcional, capturando
     * cualquier excepción que pueda ocurrir durante la creación y permitiendo composición
     * funcional posterior.</p>
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * Try<EstrategiaMovimiento> resultado = fabrica.crearIaSeguro(DificultadIA.NIVEL_10);
     *
     * resultado
     *     .onSuccess(estrategia -> System.out.println("IA creada: " + estrategia))
     *     .onFailure(error -> System.err.println("Error: " + error.getMessage()));
     * </pre>
     *
     * @param dificultad el nivel de dificultad de la IA a crear
     * @return un {@code Try} que contiene la estrategia si se creó exitosamente,
     *         o una excepción si ocurrió algún error
     */
    public Try<EstrategiaMovimiento> crearIaSeguro(final DificultadIA dificultad) {
        return Try.of(() -> crearIA(dificultad)
                .getOrElseThrow(() -> new IllegalStateException(
                        "No se pudo crear IA para el nivel: " + dificultad)));
    }

    /**
     * Crea una estrategia de IA con el nivel más fácil (Nivel 1 - Principiante).
     *
     * <p>Método de conveniencia para crear rápidamente una IA de nivel principiante
     * sin necesidad de especificar el enum completo.</p>
     *
     * <p><b>Configuración del nivel 1:</b></p>
     * <ul>
     *   <li>Retraso de reacción: 0.800 segundos (muy lento)</li>
     *   <li>Amplitud de error: 35.0 píxeles (muy impreciso)</li>
     * </ul>
     *
     * @return un {@code Option} que contiene la estrategia de IA fácil
     */
    public Option<EstrategiaMovimiento> crearIAFacil() {
        return crearIA(DificultadIA.NIVEL_1);
    }

    /**
     * Crea una estrategia de IA con nivel intermedio (Nivel 5 - Intermedio).
     *
     * <p>Método de conveniencia para crear una IA de dificultad media,
     * comparable al tiempo de reacción de un jugador humano promedio.</p>
     *
     * <p><b>Configuración del nivel 5:</b></p>
     * <ul>
     *   <li>Retraso de reacción: 0.250 segundos (comparable a humano)</li>
     *   <li>Amplitud de error: 15.0 píxeles (precisión media)</li>
     * </ul>
     *
     * @return un {@code Option} que contiene la estrategia de IA media
     */
    public Option<EstrategiaMovimiento> crearIAMedio() {
        return crearIA(DificultadIA.NIVEL_5);
    }

    /**
     * Crea una estrategia de IA con el nivel más difícil (Nivel 10 - Leyenda).
     *
     * <p>Método de conveniencia para crear una IA de máxima dificultad,
     * con reacción casi instantánea y precisión casi perfecta.</p>
     *
     * <p><b>Configuración del nivel 10:</b></p>
     * <ul>
     *   <li>Retraso de reacción: 0.020 segundos (casi instantáneo)</li>
     *   <li>Amplitud de error: 1.0 píxel (casi perfecto)</li>
     * </ul>
     *
     * @return un {@code Option} que contiene la estrategia de IA difícil
     */
    public Option<EstrategiaMovimiento> crearIADificil() {
        return crearIA(DificultadIA.NIVEL_10);
    }

    /**
     * Crea una estrategia de IA personalizada con parámetros específicos.
     *
     * <p>Este método permite crear una IA con configuración arbitraria,
     * no necesariamente correspondiente a uno de los 10 niveles predefinidos.
     * Útil para pruebas, ajustes finos, o modos de juego especiales.</p>
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * // Crear IA super difícil personalizada
     * Try<EstrategiaMovimiento> iaPersonalizada =
     *     fabrica.crearIAPersonalizada(0.015, 0.5);
     * </pre>
     *
     * @param retrasoReaccion tiempo de reacción en segundos. Debe ser > 0.
     * @param amplitudError   amplitud del error en píxeles. Debe estar en [0.0, 40.0].
     * @return un {@code Try} que contiene la estrategia si los parámetros son válidos,
     *         o una excepción si son inválidos
     */
    public Try<EstrategiaMovimiento> crearIAPersonalizada(
            final double retrasoReaccion,
            final double amplitudError) {
        return EstrategiaMovimientoIA.crear(retrasoReaccion, amplitudError)
                .map(estrategia -> (EstrategiaMovimiento) estrategia);
    }

    /**
     * Verifica si la fábrica puede crear una estrategia para el nivel especificado.
     *
     * @param dificultad el nivel de dificultad a verificar
     * @return {@code true} si existe configuración para ese nivel, {@code false} en caso contrario
     */
    public boolean puedeCrear(final DificultadIA dificultad) {
        return RepositorioConfiguracionesIA.existeConfiguracion(dificultad);
    }

    /**
     * Obtiene la configuración que se usaría para crear una IA del nivel especificado,
     * sin crear la estrategia.
     *
     * <p>Útil para mostrar información al usuario antes de iniciar el juego.</p>
     *
     * @param dificultad el nivel de dificultad
     * @return un {@code Option} con la configuración del nivel
     */
    public Option<ConfiguracionDificultad> obtenerConfiguracion(final DificultadIA dificultad) {
        return RepositorioConfiguracionesIA.obtenerConfiguracion(dificultad);
    }

    /**
     * Retorna una representación en cadena de texto de esta fábrica.
     *
     * @return una cadena descriptiva
     */
    @Override
    public String toString() {
        return String.format("FabricaIA[niveles=%d, repositorio=%s]",
                DificultadIA.obtenerTodosLosNiveles().size(),
                RepositorioConfiguracionesIA.obtenerResumen());
    }
}
