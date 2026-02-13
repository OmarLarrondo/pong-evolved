package app;

import io.vavr.control.Try;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import mvc.controlador.ControladorEditor;
import mvc.controlador.ControladorJuego;
import mvc.controlador.ControladorMenu;
import mvc.controlador.ControladorSeleccionDificultad;
import mvc.controlador.ControladorSeleccionNiveles;
import mvc.vista.VistaEditor;
import mvc.vista.VistaMenu;
import mvc.vista.VistaSeleccionDificultad;
import mvc.vista.VistaSeleccionNiveles;
import mvc.vista.GestorEscenas;
import util.CargadorRecursos;

/**
 * Clase principal de la aplicación Pong Evolved.
 * Extiende Application de JavaFX y configura el entorno inicial del juego.
 * Implementa el patrón MVC integrando el modelo, vista y controlador.
 */
public class AplicacionPong extends Application {

    private static final String TITULO_APLICACION = "PONG EVOLVED";
    private static final int ANCHO_MINIMO = 800;
    private static final int ALTO_MINIMO = 600;

    private GestorEscenas gestorEscenas;
    private Stage escenarioPrincipal;
    private ControladorSeleccionDificultad controladorDificultad;
    private ControladorSeleccionNiveles controladorNiveles;
    private ControladorEditor controladorEditor;
    private ControladorJuego controladorJuego;

    /**
     * Método de inicialización ejecutado antes de start().
     * Inicializa el modelo del juego y carga recursos necesarios.
     */
    @Override
    public void init() {
        System.out.println("Inicializando Pong Evolved...");
        inicializarBaseDatos();
    }

    /**
     * Inicializa la base de datos SQLite creando las tablas necesarias.
     * Se ejecuta al inicio de la aplicación de forma síncrona.
     */
    private void inicializarBaseDatos() {
        System.out.println("Inicializando base de datos...");
        persistencia.conexion.ConexionSQLite conexion = persistencia.conexion.ConexionSQLite.obtenerInstancia();

        conexion.inicializarBaseDatos()
            .onSuccess(unused -> System.out.println("Base de datos inicializada correctamente."))
            .onFailure(error -> {
                System.err.println("Error al inicializar la base de datos: " + error.getMessage());
                error.printStackTrace();
            });
    }

    /**
     * Metodo principal de inicio de la aplicacion JavaFX.
     * Configura el Stage principal y muestra el menu inicial.
     *
     * @param escenarioPrincipal Stage principal de la aplicacion
     */
    @Override
    public void start(Stage escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
        configurarEscenario(escenarioPrincipal);
        inicializarGestorEscenas(escenarioPrincipal);
        inicializarVistaMenu();
        inicializarVistaSeleccionDificultad();
        inicializarVistaSeleccionNiveles();
        inicializarVistaEditor();
        inicializarVistaJuego();

        gestorEscenas.mostrarMenu();
        inicializarMusicaFondo();

        System.out.println("Pong Evolved iniciado correctamente.");
    }

    /**
     * Configura las propiedades del Stage principal.
     *
     * @param escenario Stage a configurar
     */
    private void configurarEscenario(Stage escenario) {
        escenario.setTitle(TITULO_APLICACION);
        escenario.setMinWidth(ANCHO_MINIMO);
        escenario.setMinHeight(ALTO_MINIMO);
        escenario.setResizable(true);

        escenario.setOnCloseRequest(evento -> {
            System.out.println("Cerrando aplicación...");
            limpiarRecursos();
        });
    }

    /**
     * Inicializa el gestor de escenas de la aplicación.
     *
     * @param escenario Stage principal
     */
    private void inicializarGestorEscenas(Stage escenario) {
        this.gestorEscenas = new GestorEscenas(escenario);
    }

    /**
     * Inicializa la vista del menú principal y la registra en el gestor.
     */
    private void inicializarVistaMenu() {
        ControladorMenu controladorMenu = new ControladorMenu();
        controladorMenu.establecerGestorEscenas(gestorEscenas);

        VistaMenu vistaMenu = new VistaMenu(controladorMenu);
        gestorEscenas.registrarEscena("menu", vistaMenu.obtenerEscena());
    }

    /**
     * Inicializa la vista de seleccion de dificultad y la registra en el gestor.
     */
    private void inicializarVistaSeleccionDificultad() {
        controladorDificultad = new ControladorSeleccionDificultad();
        controladorDificultad.establecerGestorEscenas(gestorEscenas);

        VistaSeleccionDificultad vista = new VistaSeleccionDificultad(controladorDificultad);
        gestorEscenas.registrarEscena("seleccion-dificultad", vista.obtenerEscena());
        gestorEscenas.registrarCallbackPreMostrar("seleccion-dificultad",
                () -> controladorDificultad.reiniciarEstado());
    }

    /**
     * Inicializa la vista de seleccion de niveles y la registra en el gestor.
     * Configura el consumer funcional para recibir la dificultad desde el panel de seleccion.
     */
    private void inicializarVistaSeleccionNiveles() {
        controladorNiveles = new ControladorSeleccionNiveles();
        controladorNiveles.establecerGestorEscenas(gestorEscenas);

        gestorEscenas.establecerConfiguradorDificultad(
                dificultad -> controladorNiveles.establecerDificultadIA(dificultad)
        );

        VistaSeleccionNiveles vista = new VistaSeleccionNiveles(controladorNiveles);
        gestorEscenas.registrarEscena("seleccion-niveles", vista.obtenerEscena());
        gestorEscenas.registrarCallbackPreMostrar("seleccion-niveles",
                () -> controladorNiveles.reiniciarSeleccionNivel());
        gestorEscenas.registrarCallbackPreMostrar("limpiar-estado-niveles",
                () -> io.vavr.control.Try.run(() -> {
                    controladorNiveles.reiniciarEstadoCompleto();
                    controladorDificultad.reiniciarEstado();
                }).getOrElse(() -> null));
    }

    /**
     * Inicializa la vista del editor de mapas y la registra en el gestor.
     */
    private void inicializarVistaEditor() {
        controladorEditor = new ControladorEditor();
        controladorEditor.establecerGestorEscenas(gestorEscenas);

        VistaEditor vista = new VistaEditor(controladorEditor);
        gestorEscenas.registrarEscena("editor", vista.obtenerEscena());
        gestorEscenas.registrarCallbackPreMostrar("editor",
                () -> controladorEditor.reiniciarEstado());
    }

    /**
     * Inicializa la vista del juego principal y la registra en el gestor.
     */
    private void inicializarVistaJuego() {
        Try.run(() -> {
            final FXMLLoader loader = CargadorRecursos.cargarFXMLLoader("fxml/juego.fxml");
            final StackPane root = loader.load();
            controladorJuego = loader.getController();

            if (controladorJuego != null) {
                controladorJuego.establecerGestorEscenas(gestorEscenas);
            }

            final Scene escenaJuego = new Scene(root, ANCHO_MINIMO, ALTO_MINIMO);
            gestorEscenas.registrarEscena("juego", escenaJuego);
            gestorEscenas.registrarCallbackPreMostrar("juego",
                    () -> {
                        if (controladorJuego != null) {
                            configurarYReiniciarJuego();
                        }
                    });
        }).onFailure(e -> {
            System.err.println("Error inicializando vista de juego: " + e.getMessage());
            e.printStackTrace();
        });
    }

    private void configurarYReiniciarJuego() {
        io.vavr.control.Option.of(controladorNiveles)
            .flatMap(ctrl -> io.vavr.control.Option.ofOptional(ctrl.obtenerNivelSeleccionado()))
            .peek(nivel -> controladorJuego.establecerNivel(nivel));

        final io.vavr.control.Option<Integer> dificultadIA =
            io.vavr.control.Option.of(controladorDificultad)
                .flatMap(ctrl -> io.vavr.control.Option.ofOptional(ctrl.obtenerNivelSeleccionado()));

        dificultadIA.peek(dificultad -> controladorJuego.configurarDificultadIA(dificultad));

        final mvc.modelo.enums.ModoJuego modo = dificultadIA
            .map(d -> mvc.modelo.enums.ModoJuego.CONTRA_IA)
            .getOrElse(mvc.modelo.enums.ModoJuego.DOS_JUGADORES);

        controladorJuego.establecerModo(modo);

        controladorJuego.reiniciarEstado();
    }

    /**
     * Inicializa el sistema de musica de fondo del juego.
     * Carga el archivo de audio y comienza la reproduccion automatica en bucle.
     * Si falla la carga, la aplicacion continua sin musica.
     */
    private void inicializarMusicaFondo() {
        try {
            patrones.observer.GestorAudio gestorAudio = patrones.observer.GestorAudio.obtenerInstancia();
            gestorAudio.inicializarMusicaFondo()
                    .peek(reproductor -> {
                        System.out.println("Musica de fondo cargada correctamente.");
                        gestorAudio.reproducir();
                    })
                    .onEmpty(() -> System.err.println("No se pudo cargar la musica de fondo. Continuando sin audio."));
        } catch (Exception e) {
            System.err.println("Error al inicializar la musica de fondo: " + e.getMessage());
            System.err.println("La aplicacion continuara sin musica de fondo.");
        }
    }

    /**
     * Método ejecutado al cerrar la aplicación.
     * Libera recursos y realiza limpieza necesaria.
     */
    @Override
    public void stop() {
        limpiarRecursos();
        System.out.println("Aplicación cerrada.");
    }

    /**
     * Limpia los recursos de la aplicacion incluyendo audio y base de datos.
     */
    private void limpiarRecursos() {
        if (gestorEscenas != null) {
            gestorEscenas.limpiarCache();
        }

        patrones.observer.GestorAudio.obtenerInstancia().liberarRecursos();
        cerrarBaseDatos();
    }

    /**
     * Cierra el pool de conexiones de la base de datos.
     */
    private void cerrarBaseDatos() {
        System.out.println("Cerrando conexión a la base de datos...");
        persistencia.conexion.ConexionSQLite conexion = persistencia.conexion.ConexionSQLite.obtenerInstancia();

        conexion.cerrarPool()
            .onSuccess(unused -> System.out.println("Base de datos cerrada correctamente."))
            .onFailure(error -> {
                System.err.println("Error al cerrar la base de datos: " + error.getMessage());
            });
    }

    /**
     * Alterna el modo de pantalla completa del escenario principal.
     * Implementa programación funcional aplicando la transformación de estado.
     */
    public void alternarPantallaCompleta() {
        aplicarCambioEstadoPantallaCompleta(escenarioPrincipal);
    }

    /**
     * Aplica el cambio de estado de pantalla completa al Stage.
     * Función pura que transforma el estado actual al opuesto.
     *
     * @param stage Stage al que aplicar el cambio
     */
    private void aplicarCambioEstadoPantallaCompleta(Stage stage) {
        stage.setFullScreen(!stage.isFullScreen());
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
     * Método main que lanza la aplicación JavaFX.
     *
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}
