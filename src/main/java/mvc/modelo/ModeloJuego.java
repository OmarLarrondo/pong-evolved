package mvc.modelo;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;

import mvc.modelo.entidades.Nivel;
import mvc.modelo.entidades.Bloque;
import mvc.modelo.entidades.paleta.Paleta;
import mvc.modelo.entidades.pelota.Pelota;
import mvc.modelo.items.Item;
import mvc.modelo.enums.ModoJuego;
import mvc.modelo.enums.Direccion;
import patrones.singleton.GestorPrototiposPaleta;
import patrones.observer.ObservadorJuego;
import patrones.memento.MementoPaletas;
import patrones.strategy.colision.EstrategiaColision;
import patrones.strategy.colision.EstrategiaColisionPelotaPared;
import patrones.strategy.colision.EstrategiaColisionPelotaPaleta;
import patrones.strategy.colision.EstrategiaColisionPelotaBloque;
import patrones.strategy.colision.GestorColisiones;
import patrones.factory.ia.ServicioIA;

/**
 * Modelo principal del juego que contiene el estado completo
 * de la partida actual.
 * <p>
 * Esta clase actúa como el componente Modelo en el patrón MVC,
 * coordinando todas las entidades del juego (pelota, paletas, bloques, items)
 * y gestionando la lógica del game loop. También implementa el patrón Observable
 * para notificar cambios de estado a los observadores registrados.
 * </p>
 *
 * @author Equipo-polimorfo
 * @version 1.0
 */
public class ModeloJuego {

    private Pelota pelota;
    private List<Pelota> pelotas;
    private Paleta jugador1;
    private Paleta jugador2;
    private List<Bloque> bloques;
    private List<Item> items;
    private Nivel nivel;
    private int puntaje1;
    private int puntaje2;
    private ModoJuego modoJuego;
    private ModoJuego modoActual;
    private List<ObservadorJuego> observadores;
    private Option<MementoPaletas> mementoGuardado;
    private Option<GestorColisiones> gestorColisiones;
    private Option<ServicioIA> servicioIA;
    private double tiempoTranscurrido;
    private static final double DURACION_PARTIDA = 300.0;
    private boolean juegoActivo;

    /**
     * Construye un nuevo modelo de juego con estado inicial vacío.
     * <p>
     * Inicializa todas las colecciones como listas inmutables vacías
     * y establece los valores predeterminados para los atributos del juego.
     * </p>
     */
    public ModeloJuego() {
        this.bloques = List.empty();
        this.items = List.empty();
        this.pelotas = List.empty();
        this.observadores = List.empty();
        this.mementoGuardado = Option.none();
        this.gestorColisiones = Option.none();
        this.servicioIA = Option.none();
        this.puntaje1 = 0;
        this.puntaje2 = 0;
        this.tiempoTranscurrido = 0.0;
        this.juegoActivo = true;
    }

    /**
     * Actualiza el estado del juego basándose en el tiempo transcurrido.
     * <p>
     * Este método implementa el game loop principal, actualizando todas las
     * entidades del juego, verificando colisiones, actualizando items activos,
     * controlando la IA si está configurada, y verificando condiciones de victoria/derrota.
     * </p>
     *
     * @param tiempoDelta el tiempo transcurrido desde la última actualización en segundos
     */
    public void actualizar(double tiempoDelta) {
        Try.run(() -> {
            if (!juegoActivo) {
                return;
            }

            tiempoTranscurrido = actualizarTiempo(tiempoTranscurrido, tiempoDelta);

            Option.of(pelota).forEach(p -> p.actualizar(tiempoDelta));
            Option.of(jugador1).forEach(j -> j.actualizar(tiempoDelta));
            Option.of(jugador2).forEach(j -> j.actualizar(tiempoDelta));

            aplicarMovimientoIA(tiempoDelta);

            bloques = actualizarBloques(bloques, tiempoDelta);
            items = actualizarItems(items, tiempoDelta);

            gestorColisiones.forEach(gc -> gc.verificarTodasColisiones(this));

            verificarCondicionesFinales();
        });
    }

    /**
     * Aplica el movimiento de la IA a la paleta del jugador 2 si hay servicio configurado
     * y el modo de juego actual es CONTRA_IA.
     * Utiliza programacion funcional pura con Vavr Option para composicion segura.
     *
     * @param tiempoDelta el tiempo transcurrido desde la última actualización en segundos
     */
    private void aplicarMovimientoIA(double tiempoDelta) {
        if (modoActual == ModoJuego.CONTRA_IA) {
            servicioIA
                    .flatMap(servicio ->
                            Option.of(jugador2)
                                    .flatMap(paleta -> Option.of(pelota).map(p -> {
                                        Direccion movimiento = servicio.calcularSiguienteMovimiento(
                                                paleta, p, tiempoDelta);
                                        paleta.moverEnDireccion(movimiento, tiempoDelta);
                                        return movimiento;
                                    }))
                    );
        }
    }

    /**
     * Actualiza el tiempo transcurrido y verifica el límite de la partida.
     *
     * @param tiempoActual el tiempo actual transcurrido
     * @param delta el incremento de tiempo
     * @return el nuevo tiempo transcurrido
     */
    private double actualizarTiempo(double tiempoActual, double delta) {
        double nuevoTiempo = tiempoActual + delta;

        if (nuevoTiempo >= DURACION_PARTIDA) {
            finalizarPorTiempo();
        }

        return nuevoTiempo;
    }

    /**
     * Actualiza todos los bloques del juego de forma funcional.
     * <p>
     * Nota: Aunque la lista es inmutable, los objetos Bloque internos
     * son mutados por actualizar(). Esta limitacion existe debido a que
     * las entidades del juego fueron disenadas con estado mutable por
     * otros miembros del equipo.
     * </p>
     *
     * @param bloquesActuales la lista actual de bloques
     * @param tiempoDelta el tiempo transcurrido
     * @return una nueva lista filtrada con bloques activos
     */
    private List<Bloque> actualizarBloques(List<Bloque> bloquesActuales, double tiempoDelta) {
        return bloquesActuales
            .filter(Bloque::estaActivo)
            .peek(b -> b.actualizar(tiempoDelta));
    }

    /**
     * Actualiza todos los items activos del juego de forma funcional.
     * <p>
     * Nota: Similar a actualizarBloques, los objetos Item son mutados
     * internamente debido al diseno mutable de las entidades del juego.
     * </p>
     *
     * @param itemsActuales la lista actual de items
     * @param tiempoDelta el tiempo transcurrido
     * @return una nueva lista filtrada con items activos
     */
    private List<Item> actualizarItems(List<Item> itemsActuales, double tiempoDelta) {
        return itemsActuales
            .filter(Item::estaActivo)
            .peek(i -> i.actualizar(tiempoDelta, null));
    }

    /**
     * Verifica las condiciones finales del juego (victoria o derrota).
     */
    private void verificarCondicionesFinales() {
        if (todosBloquesDestruidos()) {
            juegoActivo = false;
            notificarCompletarNivel();
        }
    }

    /**
     * Verifica si todos los bloques han sido destruidos.
     *
     * @return true si no quedan bloques activos, false en caso contrario
     */
    private boolean todosBloquesDestruidos() {
        return bloques.filter(b -> b.estaActivo()).isEmpty();
    }

    /**
     * Finaliza el juego cuando se agota el tiempo de la partida.
     */
    private void finalizarPorTiempo() {
        juegoActivo = false;
        int ganador = determinarGanador();
        notificarTerminarJuego(ganador);
    }

    /**
     * Determina el ganador basándose en los puntajes.
     *
     * @return 1 si gana el jugador 1, 2 si gana el jugador 2, 0 si empate
     */
    private int determinarGanador() {
        if (puntaje1 > puntaje2) {
            return 1;
        } else if (puntaje2 > puntaje1) {
            return 2;
        }
        return 0;
    }

    /**
     * Reinicia el estado del juego a sus valores iniciales.
     * <p>
     * Restablece las posiciones de las entidades, los puntajes,
     * el tiempo transcurrido y reactiva el juego.
     * </p>
     */
    public void reiniciar() {
        Try.run(() -> {
            Option.of(pelota).forEach(Pelota::reiniciar);
            Option.of(jugador1).forEach(Paleta::restaurarEstado);
            Option.of(jugador2).forEach(Paleta::restaurarEstado);

            bloques = reiniciarBloques();
            items = List.empty();
            tiempoTranscurrido = 0.0;
            juegoActivo = true;
        });
    }

    /**
     * Reinicia los valores del juego sin modificar las entidades existentes.
     * <p>
     * Este metodo resetea bloques, items, puntajes y tiempo transcurrido,
     * pero mantiene intactas las entidades del juego (pelota y paletas)
     * que fueron previamente inicializadas.
     * </p>
     * <p>
     * Diseñado para usarse despues de crear nuevas entidades con
     * {@link #inicializarEntidadesJuego(double, double)}, permitiendo
     * resetear el estado del juego sin corromper las entidades recien creadas.
     * </p>
     * 
     * @return un Option que contiene true si el reinicio fue exitoso,
     *         o none si ocurrio un error
     */
    public Option<Boolean> reiniciarValoresJuego() {
        return Try.of(() -> {
            bloques = reiniciarBloques();
            items = List.empty();
            puntaje1 = 0;
            puntaje2 = 0;
            tiempoTranscurrido = 0.0;
            juegoActivo = true;
            return true;
        }).toOption();
    }

    public Option<Boolean> inicializarEntidadesJuego(final double anchoCanvas, final double altoCanvas) {
        if (anchoCanvas <= 0 || altoCanvas <= 0) {
            System.err.println("ERROR: Dimensiones inválidas para inicializar entidades: " + anchoCanvas + "x" + altoCanvas);
            return Option.none();
        }

        return Try.of(() -> {
            final Pelota pelotaNueva = new Pelota(
                (int) (anchoCanvas / 2),
                (int) (altoCanvas / 2),
                10,
                300,
                500,
                Math.toRadians(45)
            );

            final Paleta paletaJugador1 = new Paleta(
                50.0,
                altoCanvas / 2 - 50,
                20.0,
                100.0
            );

            final Paleta paletaJugador2 = new Paleta(
                anchoCanvas - 70.0,
                altoCanvas / 2 - 50,
                20.0,
                100.0
            );

            inicializarPelota(pelotaNueva);
            inicializarPaletas(paletaJugador1, paletaJugador2);

            final EstrategiaColision estrategiaPared = new EstrategiaColisionPelotaPared(anchoCanvas, altoCanvas);
            final EstrategiaColision estrategiaPaleta = new EstrategiaColisionPelotaPaleta();
            final EstrategiaColision estrategiaBloque = new EstrategiaColisionPelotaBloque();

            final Map<String, EstrategiaColision> estrategias = HashMap.of(
                "pelota-pared", estrategiaPared,
                "pelota-paleta", estrategiaPaleta,
                "pelota-bloque", estrategiaBloque
            );

            final GestorColisiones gestor = new GestorColisiones(estrategias.toJavaMap());
            establecerGestorColisiones(gestor);

            return true;
        })
        .onFailure(e -> {
            System.err.println("ERROR: Fallo al inicializar entidades del juego:");
            e.printStackTrace();
        })
        .toOption();
    }

    /**
     * Reinicia los bloques del nivel actual de forma funcional.
     *
     * @return una nueva lista con los bloques reiniciados
     */
    private List<Bloque> reiniciarBloques() {
        return Option.of(nivel)
            .map(n -> List.ofAll(n.obtenerBloques()))
            .getOrElse(List.empty());
    }

    /**
     * Agrega un bloque al juego de forma inmutable.
     *
     * @param bloque el bloque a agregar
     */
    public void agregarBloque(Bloque bloque) {
        bloques = Option.of(bloque)
            .map(b -> bloques.append(b))
            .getOrElse(bloques);
    }

    /**
     * Elimina un bloque del juego de forma inmutable.
     *
     * @param bloque el bloque a eliminar
     */
    public void eliminarBloque(Bloque bloque) {
        bloques = bloques.remove(bloque);
    }

    /**
     * Genera y agrega un nuevo item al juego.
     * <p>
     * Notifica a los observadores sobre la generación del item.
     * </p>
     *
     * @param item el item a generar
     */
    public void generarItem(Item item) {
        items = Option.of(item)
            .map(i -> {
                notificarGenerarItem(i);
                return items.append(i);
            })
            .getOrElse(items);
    }

    /**
     * Incrementa el puntaje de un jugador de forma inmutable.
     * <p>
     * Notifica a los observadores sobre el cambio de puntaje.
     * </p>
     *
     * @param jugador el identificador del jugador (1 o 2)
     * @param puntos la cantidad de puntos a incrementar
     */
    public void incrementarPuntaje(int jugador, int puntos) {
        if (jugador == 1) {
            puntaje1 = calcularNuevoPuntaje(puntaje1, puntos);
            notificarCambioPuntaje(1, puntaje1);
        } else if (jugador == 2) {
            puntaje2 = calcularNuevoPuntaje(puntaje2, puntos);
            notificarCambioPuntaje(2, puntaje2);
        }
    }

    /**
     * Calcula un nuevo puntaje de forma pura.
     *
     * @param puntajeActual el puntaje actual
     * @param incremento el incremento a aplicar
     * @return el nuevo puntaje calculado
     */
    private int calcularNuevoPuntaje(int puntajeActual, int incremento) {
        return Math.max(0, puntajeActual + incremento);
    }

    /**
     * Inicializa las paletas desde prototipos registrados.
     * <p>
     * Utiliza el patron Prototype a traves del gestor singleton
     * para crear instancias de las paletas basadas en prototipos
     * predefinidos.
     * </p>
     *
     * @param nombreProto1 el nombre del prototipo para el jugador 1
     * @param nombreProto2 el nombre del prototipo para el jugador 2
     */
    public void inicializarPaletasDesdePrototipos(String nombreProto1, String nombreProto2) {
        GestorPrototiposPaleta gestor = GestorPrototiposPaleta.obtenerInstancia();

        jugador1 = gestor.obtenerPrototipo(nombreProto1)
            .getOrNull();

        jugador2 = gestor.obtenerPrototipo(nombreProto2)
            .getOrNull();
    }

    /**
     * Guarda el estado actual de las paletas usando el patrón Memento.
     * <p>
     * Permite restaurar el estado posteriormente sin romper el encapsulamiento.
     * </p>
     */
    public void guardarEstadoPaletas() {
        mementoGuardado = Option.of(jugador1)
            .flatMap(j1 -> Option.of(jugador2)
                .map(j2 -> new MementoPaletas(j1, j2)));
    }

    /**
     * Restaura el estado de las paletas desde el memento guardado.
     * <p>
     * Utiliza el patrón Memento para restaurar el estado previamente guardado.
     * </p>
     */
    public void restaurarEstadoPaletas() {
        mementoGuardado.forEach(memento -> {
            Option.of(jugador1).forEach(memento::restaurarJugador1);
            Option.of(jugador2).forEach(memento::restaurarJugador2);
        });
    }

    /**
     * Obtiene el modo de juego actual.
     *
     * @return el modo de juego actual
     */
    public ModoJuego obtenerModoActual() {
        return modoActual;
    }

    /**
     * Establece el modo de juego actual.
     *
     * @param modo el nuevo modo de juego
     */
    public void establecerModo(ModoJuego modo) {
        this.modoActual = modo;
    }

    /**
     * Registra un observador para recibir notificaciones de eventos del juego.
     *
     * @param observador el observador a registrar
     */
    public void agregarObservador(ObservadorJuego observador) {
        observadores = Option.of(observador)
            .map(o -> observadores.append(o))
            .getOrElse(observadores);
    }

    /**
     * Elimina un observador de la lista de notificaciones.
     *
     * @param observador el observador a eliminar
     */
    public void eliminarObservador(ObservadorJuego observador) {
        observadores = observadores.remove(observador);
    }

    /**
     * Establece el gestor de colisiones para el juego.
     *
     * @param gestor el gestor de colisiones a utilizar
     */
    public void establecerGestorColisiones(GestorColisiones gestor) {
        this.gestorColisiones = Option.of(gestor);
    }

    /**
     * Establece el nivel actual del juego.
     *
     * @param nivel el nivel a establecer
     */
    public void establecerNivel(Nivel nivel) {
        this.nivel = nivel;
        this.bloques = Option.of(nivel)
            .map(n -> List.ofAll(n.obtenerBloques()))
            .getOrElse(List.empty());
    }

    /**
     * Notifica a todos los observadores sobre un cambio de puntaje.
     *
     * @param jugador el jugador cuyo puntaje cambió
     * @param nuevoPuntaje el nuevo puntaje
     */
    private void notificarCambioPuntaje(int jugador, int nuevoPuntaje) {
        observadores.forEach(o -> o.alcambiarPuntaje(jugador, nuevoPuntaje));
    }

    /**
     * Notifica a todos los observadores sobre la finalización del juego.
     *
     * @param ganador el jugador ganador
     */
    private void notificarTerminarJuego(int ganador) {
        observadores.forEach(o -> o.alTerminarJuego(ganador));
    }

    /**
     * Notifica a todos los observadores sobre la completación de un nivel.
     */
    private void notificarCompletarNivel() {
        observadores.forEach(ObservadorJuego::alCompletarNivel);
    }

    /**
     * Notifica a todos los observadores sobre la generación de un item.
     *
     * @param item el item generado
     */
    private void notificarGenerarItem(Item item) {
        observadores.forEach(o -> o.alGenrarItem(item));
    }

    /**
     * Obtiene la pelota del juego.
     *
     * @return la pelota actual
     */
    public Pelota obtenerPelota() {
        return pelota;
    }

    /**
     * Obtiene la paleta del jugador 1.
     *
     * @return la paleta del jugador 1
     */
    public Paleta obtenerJugador1() {
        return jugador1;
    }

    /**
     * Obtiene la paleta del jugador 2.
     *
     * @return la paleta del jugador 2
     */
    public Paleta obtenerJugador2() {
        return jugador2;
    }

    /**
     * Obtiene la lista inmutable de bloques del juego.
     *
     * @return la lista de bloques
     */
    public java.util.List<Bloque> obtenerBloques() {
        return bloques.asJava();
    }

    /**
     * Obtiene la lista inmutable de items del juego.
     *
     * @return la lista de items
     */
    public java.util.List<Item> obtenerItems() {
        return items.asJava();
    }

    /**
     * Obtiene el puntaje del jugador 1.
     *
     * @return el puntaje del jugador 1
     */
    public int obtenerPuntaje1() {
        return puntaje1;
    }

    /**
     * Obtiene el puntaje del jugador 2.
     *
     * @return el puntaje del jugador 2
     */
    public int obtenerPuntaje2() {
        return puntaje2;
    }

    /**
     * Obtiene el tiempo transcurrido en la partida.
     *
     * @return el tiempo transcurrido en segundos
     */
    public double obtenerTiempoTranscurrido() {
        return tiempoTranscurrido;
    }

    /**
     * Obtiene la duración total de una partida.
     *
     * @return la duración en segundos
     */
    public double obtenerDuracionPartida() {
        return DURACION_PARTIDA;
    }

    /**
     * Verifica si el juego está activo.
     *
     * @return true si el juego está activo, false en caso contrario
     */
    public boolean estaActivo() {
        return juegoActivo;
    }

    /**
     * Establece el estado activo del juego.
     *
     * @param activo el nuevo estado activo
     */
    public void establecerActivo(boolean activo) {
        this.juegoActivo = activo;
    }

    /**
     * Inicializa la pelota del juego con una instancia proporcionada.
     * <p>
     * Metodo de construccion que debe usarse durante la inicializacion
     * del juego, no para modificacion dinamica durante el gameplay.
     * </p>
     *
     * @param pelota la pelota inicial del juego
     */
    public void inicializarPelota(Pelota pelota) {
        this.pelota = Option.of(pelota).getOrNull();
    }

    /**
     * Inicializa ambas paletas del juego con instancias proporcionadas.
     * <p>
     * Metodo de construccion que debe usarse durante la inicializacion
     * del juego, no para modificacion dinamica durante el gameplay.
     * </p>
     *
     * @param jugador1 la paleta del jugador 1
     * @param jugador2 la paleta del jugador 2
     */
    public void inicializarPaletas(Paleta jugador1, Paleta jugador2) {
        this.jugador1 = Option.of(jugador1).getOrNull();
        this.jugador2 = Option.of(jugador2).getOrNull();
    }

    /**
     * Establece el servicio de IA para controlar la paleta del jugador 2.
     * Utiliza programacion funcional pura con Vavr Option.
     *
     * @param servicio el servicio de IA configurado con una dificultad específica
     */
    public void establecerServicioIA(ServicioIA servicio) {
        this.servicioIA = Option.of(servicio);
    }
}
