package patrones.factory.ia;

import io.vavr.control.Option;
import mvc.modelo.enums.Direccion;
import mvc.modelo.entidades.paleta.Paleta;
import mvc.modelo.entidades.pelota.Pelota;
import patrones.strategy.movimiento.EstrategiaMovimiento;

import java.util.Objects;

/**
 * Servicio que gestiona la Inteligencia Artificial del juego Pong Evolved.
 *
 * <p>Esta clase actúa como un <b>Facade</b> que simplifica la interacción con el
 * sistema de IA, proporcionando una interfaz de alto nivel para:</p>
 * <ul>
 *   <li>Configurar el nivel de dificultad de la IA</li>
 *   <li>Obtener la estrategia de movimiento actual</li>
 *   <li>Calcular el siguiente movimiento de la paleta controlada por IA</li>
 *   <li>Cambiar dinámicamente la dificultad durante el juego</li>
 * </ul>
 *
 * <p><b>Características de diseño:</b></p>
 * <ul>
 *   <li><b>Encapsulación:</b> Oculta la complejidad de FabricaIA y EstrategiaMovimiento</li>
 *   <li><b>Mutabilidad controlada:</b> Permite cambiar la dificultad, pero de forma segura</li>
 *   <li><b>Lazy initialization:</b> La estrategia solo se crea cuando se necesita</li>
 *   <li><b>Programación funcional:</b> Uso de Option de Vavr para manejar ausencia de valores</li>
 * </ul>
 *
 * <p><b>Ciclo de vida típico:</b></p>
 * <pre>
 * // 1. Crear servicio
 * ServicioIA servicio = new ServicioIA();
 *
 * // 2. Establecer dificultad (ej: nivel seleccionado por el usuario)
 * servicio.establecerDificultad(DificultadIA.NIVEL_7);
 *
 * // 3. En cada frame del juego:
 * Direccion movimiento = servicio.calcularSiguienteMovimiento(paleta, pelota, delta);
 * paleta.mover(movimiento);
 *
 * // 4. Opcionalmente, cambiar dificultad en medio del juego
 * servicio.cambiarDificultad(DificultadIA.NIVEL_10);
 * </pre>
 *
 * @author Equipo Polimorfo
 * @version 2.0
 * @see DificultadIA
 * @see FabricaIA
 * @see EstrategiaMovimiento
 */
public class ServicioIA {

    private final FabricaIA fabricaIA;
    private DificultadIA dificultadActual;
    private EstrategiaMovimiento estrategiaActual;

    /**
     * Construye un nuevo servicio de IA sin dificultad establecida.
     *
     * <p>La estrategia de movimiento se creará cuando se llame a
     * {@link #establecerDificultad(DificultadIA)} por primera vez.</p>
     */
    public ServicioIA() {
        this.fabricaIA = FabricaIA.obtenerInstancia();
        this.dificultadActual = null;
        this.estrategiaActual = null;
    }

    /**
     * Construye un nuevo servicio de IA con una dificultad inicial especificada.
     *
     * <p>La estrategia de movimiento se crea inmediatamente con el nivel especificado.</p>
     *
     * @param dificultadInicial el nivel de dificultad inicial de la IA. No debe ser {@code null}.
     * @throws NullPointerException     si {@code dificultadInicial} es {@code null}
     * @throws IllegalArgumentException si no se puede crear la estrategia para el nivel especificado
     */
    public ServicioIA(final DificultadIA dificultadInicial) {
        this();
        establecerDificultad(Objects.requireNonNull(dificultadInicial,
                "La dificultad inicial no puede ser null"));
    }

    /**
     * Establece el nivel de dificultad de la IA y crea la estrategia correspondiente.
     *
     * <p>Este método:</p>
     * <ol>
     *   <li>Valida que la dificultad no sea {@code null}</li>
     *   <li>Utiliza la fábrica para crear una nueva estrategia de movimiento</li>
     *   <li>Actualiza la dificultad y estrategia actuales</li>
     *   <li>Retorna la estrategia creada envuelta en un {@code Option}</li>
     * </ol>
     *
     * <p>Si ya existe una estrategia previa, será reemplazada por la nueva.</p>
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * ServicioIA servicio = new ServicioIA();
     *
     * Option<EstrategiaMovimiento> estrategia =
     *     servicio.establecerDificultad(DificultadIA.NIVEL_5);
     *
     * estrategia
     *     .peek(e -> System.out.println("Estrategia establecida: " + e))
     *     .onEmpty(() -> System.err.println("No se pudo crear la estrategia"));
     * </pre>
     *
     * @param dificultad el nivel de dificultad a establecer. No debe ser {@code null}.
     * @return un {@code Option} que contiene la estrategia creada si fue exitoso,
     *         o {@code Option.none()} si no se pudo crear
     * @throws NullPointerException si {@code dificultad} es {@code null}
     */
    public Option<EstrategiaMovimiento> establecerDificultad(final DificultadIA dificultad) {
        Objects.requireNonNull(dificultad, "La dificultad no puede ser null");

        return fabricaIA.crearIA(dificultad)
                .peek(estrategia -> {
                    this.dificultadActual = dificultad;
                    this.estrategiaActual = estrategia;
                });
    }

    /**
     * Establece el nivel de dificultad de la IA usando un número de nivel (1-10).
     *
     * <p>Este método es un atajo conveniente que convierte el número a {@code DificultadIA}
     * y luego establece la dificultad. Es útil cuando se trabaja con la selección
     * de nivel del usuario desde la interfaz gráfica.</p>
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * // Usuario selecciona nivel 7 desde la UI
     * int nivelSeleccionado = controladorDificultad.obtenerNivelSeleccionado().get();
     *
     * ServicioIA servicio = new ServicioIA();
     * Option<EstrategiaMovimiento> estrategia =
     *     servicio.establecerDificultadPorNumero(nivelSeleccionado);
     * </pre>
     *
     * @param numeroNivel el número de nivel (1-10)
     * @return un {@code Option} que contiene la estrategia si el número es válido,
     *         o {@code Option.none()} si el número es inválido
     */
    public Option<EstrategiaMovimiento> establecerDificultadPorNumero(final int numeroNivel) {
        return DificultadIA.desdeNumeroNivel(numeroNivel)
                .flatMap(this::establecerDificultad);
    }

    /**
     * Obtiene la estrategia de movimiento actualmente configurada.
     *
     * <p>Este método utiliza {@code Option} de Vavr para manejar de forma segura
     * el caso donde aún no se ha establecido ninguna dificultad.</p>
     *
     * @return un {@code Option} que contiene la estrategia actual si existe,
     *         o {@code Option.none()} si no se ha establecido ninguna dificultad
     */
    public Option<EstrategiaMovimiento> obtenerEstrategiaMovimiento() {
        return Option.of(estrategiaActual);
    }

    /**
     * Obtiene la dificultad actualmente configurada.
     *
     * @return un {@code Option} que contiene la dificultad actual si existe,
     *         o {@code Option.none()} si no se ha establecido ninguna dificultad
     */
    public Option<DificultadIA> obtenerDificultadActual() {
        return Option.of(dificultadActual);
    }

    /**
     * Calcula el siguiente movimiento que debe realizar la paleta controlada por la IA.
     *
     * <p>Este método delega el cálculo a la estrategia de movimiento actual.
     * Si no hay estrategia configurada, retorna {@code Direccion.NINGUNA} (sin movimiento).</p>
     *
     * <p><b>Algoritmo:</b></p>
     * <ol>
     *   <li>Verifica si existe una estrategia configurada</li>
     *   <li>Si existe, delega el cálculo a {@code estrategia.calcularMovimiento()}</li>
     *   <li>Si no existe, retorna {@code Direccion.NINGUNA}</li>
     * </ol>
     *
     * <p>Este método debe ser llamado en cada frame del game loop para actualizar
     * el movimiento de la paleta controlada por IA.</p>
     *
     * <p><b>Ejemplo de uso en el game loop:</b></p>
     * <pre>
     * public void actualizar(double delta) {
     *     // Calcular movimiento de la IA
     *     Direccion movimiento = servicioIA.calcularSiguienteMovimiento(
     *         paletaIA, pelota, delta);
     *
     *     // Aplicar movimiento a la paleta
     *     paletaIA.moverEnDireccion(movimiento, delta);
     * }
     * </pre>
     *
     * @param paleta      la paleta controlada por la IA. No debe ser {@code null}.
     * @param pelota      la pelota del juego. No debe ser {@code null}.
     * @param tiempoDelta tiempo transcurrido desde el último frame en segundos. Debe ser >= 0.
     * @return la dirección en la que la paleta debe moverse: {@code ARRIBA}, {@code ABAJO}, o {@code NINGUNA}
     * @throws NullPointerException si {@code paleta} o {@code pelota} son {@code null}
     */
    public Direccion calcularSiguienteMovimiento(
            final Paleta paleta,
            final Pelota pelota,
            final double tiempoDelta) {

        Objects.requireNonNull(paleta, "La paleta no puede ser null");
        Objects.requireNonNull(pelota, "La pelota no puede ser null");

        return obtenerEstrategiaMovimiento()
                .map(estrategia -> estrategia.calcularMovimiento(paleta, pelota, tiempoDelta))
                .getOrElse(Direccion.NINGUNA);
    }

    /**
     * Cambia dinámicamente el nivel de dificultad de la IA durante el juego.
     *
     * <p>Este método es un alias de {@link #establecerDificultad(DificultadIA)}
     * con un nombre más descriptivo para su uso en el contexto de cambio dinámico
     * de dificultad.</p>
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * // Incrementar dificultad después de que el jugador gane un nivel
     * servicio.obtenerDificultadActual()
     *     .map(DificultadIA::obtenerNumeroNivel)
     *     .filter(nivel -> nivel < 10)
     *     .map(nivel -> nivel + 1)
     *     .flatMap(DificultadIA::desdeNumeroNivel)
     *     .forEach(servicio::cambiarDificultad);
     * </pre>
     *
     * @param nuevaDificultad la nueva dificultad a establecer
     * @return un {@code Option} con la estrategia creada
     * @throws NullPointerException si {@code nuevaDificultad} es {@code null}
     */
    public Option<EstrategiaMovimiento> cambiarDificultad(final DificultadIA nuevaDificultad) {
        return establecerDificultad(nuevaDificultad);
    }

    /**
     * Verifica si el servicio tiene una estrategia de IA configurada y lista para usar.
     *
     * @return {@code true} si hay una estrategia configurada, {@code false} en caso contrario
     */
    public boolean estaConfigurado() {
        return estrategiaActual != null;
    }

    /**
     * Reinicia el servicio, eliminando la dificultad y estrategia actuales.
     *
     * <p>Después de llamar a este método, será necesario llamar a
     * {@link #establecerDificultad(DificultadIA)} nuevamente antes de usar el servicio.</p>
     */
    public void reiniciar() {
        this.dificultadActual = null;
        this.estrategiaActual = null;
    }

    /**
     * Obtiene información de la configuración actual de la IA.
     *
     * @return un {@code Option} con la configuración de dificultad actual
     */
    public Option<ConfiguracionDificultad> obtenerConfiguracionActual() {
        return Option.of(dificultadActual)
                .flatMap(RepositorioConfiguracionesIA::obtenerConfiguracion);
    }

    /**
     * Retorna una representación en cadena de texto de este servicio.
     *
     * @return una cadena descriptiva con el estado actual del servicio
     */
    @Override
    public String toString() {
        final String estadoDificultad = Option.of(dificultadActual)
                .map(DificultadIA::toString)
                .getOrElse("No configurada");

        final String estadoEstrategia = estaConfigurado() ? "Activa" : "Inactiva";

        return String.format("ServicioIA[dificultad=%s, estrategia=%s]",
                estadoDificultad, estadoEstrategia);
    }
}

