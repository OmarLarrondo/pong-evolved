package mvc.vista;

import io.vavr.control.Option;
import javafx.scene.Scene;
import javafx.stage.Stage;
import patrones.singleton.ConfiguracionGlobal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Gestor de escenas de la aplicación.
 * Administra la navegación entre las diferentes vistas del juego.
 * Implementa un sistema de caché de escenas para mejorar el rendimiento.
 * Mantiene sincronizado el estado de pantalla completa usando ConfiguracionGlobal.
 */
public class GestorEscenas {

    private static final String ESCENA_MENU = "menu";
    private static final String ESCENA_JUEGO = "juego";
    private static final String ESCENA_EDITOR = "editor";
    private static final String ESCENA_SELECCION_DIFICULTAD = "seleccion-dificultad";
    private static final String ESCENA_SELECCION_NIVELES = "seleccion-niveles";

    private final Stage escenarioPrincipal;
    private final Map<String, Scene> escenas;
    private final Map<String, Runnable> callbacksPreMostrar;
    private final ConfiguracionGlobal configuracionGlobal;
    private String escenaActual;
    private Consumer<Integer> configuradorDificultad;

    /**
     * Constructor del gestor de escenas.
     *
     * @param escenarioPrincipal Stage principal de la aplicación
     */
    public GestorEscenas(Stage escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
        this.escenas = new HashMap<>();
        this.callbacksPreMostrar = new HashMap<>();
        this.configuracionGlobal = ConfiguracionGlobal.obtenerInstancia();
        this.escenaActual = null;
    }

    /**
     * Cambia a una escena específica por su nombre.
     * Ejecuta el callback pre-mostrar si existe.
     *
     * @param nombreEscena Nombre de la escena a mostrar
     */
    public void cambiarEscena(String nombreEscena) {
        ejecutarCallbackPreMostrar(nombreEscena);
        obtenerEscena(nombreEscena)
                .ifPresentOrElse(
                        this::establecerEscena,
                        () -> System.err.println("Escena no encontrada: " + nombreEscena)
                );
    }

    /**
     * Registra una nueva escena en el gestor.
     *
     * @param nombre Nombre identificador de la escena
     * @param escena Scene a registrar
     */
    public void registrarEscena(String nombre, Scene escena) {
        Optional.ofNullable(escena)
                .ifPresent(e -> escenas.put(nombre, e));
    }

    /**
     * Registra un callback que se ejecutara antes de mostrar una escena.
     *
     * @param nombreEscena Nombre de la escena
     * @param callback Runnable a ejecutar antes de mostrar la escena
     */
    public void registrarCallbackPreMostrar(String nombreEscena, Runnable callback) {
        Optional.ofNullable(callback)
                .ifPresent(c -> callbacksPreMostrar.put(nombreEscena, c));
    }

    /**
     * Ejecuta el callback pre-mostrar de una escena si existe.
     *
     * @param nombreEscena Nombre de la escena
     */
    private void ejecutarCallbackPreMostrar(String nombreEscena) {
        Optional.ofNullable(callbacksPreMostrar.get(nombreEscena))
                .ifPresent(Runnable::run);
    }

    /**
     * Obtiene una escena del caché por su nombre.
     *
     * @param nombre Nombre de la escena
     * @return Optional con la escena, o Optional.empty() si no existe
     */
    private Optional<Scene> obtenerEscena(String nombre) {
        return Optional.ofNullable(escenas.get(nombre));
    }

    /**
     * Establece una escena como la actual en el escenario principal.
     * Preserva el estado de pantalla completa después del cambio.
     *
     * @param escena Scene a establecer
     */
    private void establecerEscena(Scene escena) {
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();
        preservarEstadoPantallaCompleta();
    }

    /**
     * Preserva el estado de pantalla completa aplicando la configuración global
     * al escenario principal después de cambiar de escena.
     */
    private void preservarEstadoPantallaCompleta() {
        boolean estadoDeseado = obtenerEstadoPantallaCompletaGlobal();
        aplicarPantallaCompletaAlEscenario(estadoDeseado);
    }

    /**
     * Obtiene el estado de pantalla completa desde la configuración global.
     *
     * @return true si la pantalla completa está activa, false en caso contrario
     */
    private boolean obtenerEstadoPantallaCompletaGlobal() {
        return configuracionGlobal.isPantallaCompleta();
    }

    /**
     * Aplica el estado de pantalla completa al escenario principal.
     *
     * @param activar true para activar pantalla completa, false para desactivarla
     */
    private void aplicarPantallaCompletaAlEscenario(boolean activar) {
        escenarioPrincipal.setFullScreen(activar);
    }

    /**
     * Obtiene la escena actualmente mostrada.
     *
     * @return Scene actual
     */
    public Scene obtenerEscenaActual() {
        return Optional.ofNullable(escenaActual)
                .flatMap(this::obtenerEscena)
                .orElse(null);
    }

    /**
     * Muestra el menú principal.
     */
    public void mostrarMenu() {
        cambiarEscena(ESCENA_MENU);
        escenaActual = ESCENA_MENU;
    }

    /**
     * Muestra la vista del juego.
     */
    public void mostrarJuego() {
        cambiarEscena(ESCENA_JUEGO);
        escenaActual = ESCENA_JUEGO;
    }

    /**
     * Muestra el editor de mapas.
     */
    public void mostrarEditor() {
        cambiarEscena(ESCENA_EDITOR);
        escenaActual = ESCENA_EDITOR;
    }

    /**
     * Muestra el panel de seleccion de dificultad de IA.
     */
    public void mostrarSeleccionDificultad() {
        cambiarEscena(ESCENA_SELECCION_DIFICULTAD);
        escenaActual = ESCENA_SELECCION_DIFICULTAD;
    }

    /**
     * Muestra el panel de seleccion de niveles.
     */
    public void mostrarSeleccionNiveles() {
        cambiarEscena(ESCENA_SELECCION_NIVELES);
        escenaActual = ESCENA_SELECCION_NIVELES;
    }

    /**
     * Muestra el panel de seleccion de niveles con una dificultad de IA preconfigurada.
     * Utiliza programacion funcional pura para establecer la dificultad antes de navegar.
     * Si se pasa Option.none(), limpia completamente el estado del controlador para modo 2 jugadores.
     *
     * @param dificultad Option conteniendo el nivel de dificultad (1-10), o None si es modo 2 jugadores
     */
    public void mostrarSeleccionNivelesConDificultad(Option<Integer> dificultad) {
        dificultad.fold(
            () -> {
                Option.of(callbacksPreMostrar.get("limpiar-estado-niveles"))
                        .peek(Runnable::run);
                return null;
            },
            d -> {
                Option.of(configuradorDificultad)
                        .peek(configurador -> configurador.accept(d));
                return d;
            }
        );

        mostrarSeleccionNiveles();
    }

    /**
     * Establece el configurador funcional de dificultad.
     * Este consumer sera invocado cuando se navegue a seleccion de niveles con dificultad.
     *
     * @param configurador Consumer que recibe el nivel de dificultad y lo aplica al controlador
     */
    public void establecerConfiguradorDificultad(Consumer<Integer> configurador) {
        this.configuradorDificultad = configurador;
    }

    /**
     * Obtiene el Stage principal de la aplicación.
     *
     * @return Stage principal
     */
    public Stage obtenerEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    /**
     * Verifica si una escena está registrada.
     *
     * @param nombre Nombre de la escena
     * @return true si está registrada, false en caso contrario
     */
    public boolean existeEscena(String nombre) {
        return escenas.containsKey(nombre);
    }

    /**
     * Limpia todas las escenas del caché excepto la actual.
     */
    public void limpiarCache() {
        String escenaActualTemp = escenaActual;
        escenas.keySet().stream()
                .filter(nombre -> !nombre.equals(escenaActualTemp))
                .collect(Collectors.toList())
                .forEach(escenas::remove);
    }

    /**
     * Obtiene el nombre de la escena actual.
     *
     * @return Nombre de la escena actual
     */
    public String obtenerNombreEscenaActual() {
        return escenaActual;
    }

    /**
     * Alterna el estado de pantalla completa.
     * Actualiza la configuración global y aplica el cambio al escenario.
     */
    public void alternarPantallaCompleta() {
        ejecutarAlternancia();
    }

    /**
     * Ejecuta la alternancia del estado de pantalla completa.
     */
    private void ejecutarAlternancia() {
        actualizarConfiguracionPantallaCompleta();
        sincronizarPantallaCompletaConEscenario();
    }

    /**
     * Actualiza el estado de pantalla completa en la configuración global.
     */
    private void actualizarConfiguracionPantallaCompleta() {
        configuracionGlobal.alternarPantallaCompleta();
    }

    /**
     * Sincroniza el estado de pantalla completa del escenario con la configuración global.
     */
    private void sincronizarPantallaCompletaConEscenario() {
        boolean estadoActual = obtenerEstadoPantallaCompletaGlobal();
        aplicarPantallaCompletaAlEscenario(estadoActual);
    }

    /**
     * Sale del modo de pantalla completa.
     * Desactiva la pantalla completa tanto en la configuración como en el escenario.
     */
    public void salirPantallaCompleta() {
        ejecutarSalidaPantallaCompleta();
    }

    /**
     * Ejecuta la salida del modo de pantalla completa.
     */
    private void ejecutarSalidaPantallaCompleta() {
        desactivarPantallaCompletaEnConfiguracion();
        desactivarPantallaCompletaEnEscenario();
    }

    /**
     * Desactiva la pantalla completa en la configuración global.
     */
    private void desactivarPantallaCompletaEnConfiguracion() {
        configuracionGlobal.setPantallaCompleta(false);
    }

    /**
     * Desactiva la pantalla completa en el escenario principal.
     */
    private void desactivarPantallaCompletaEnEscenario() {
        aplicarPantallaCompletaAlEscenario(false);
    }
}
