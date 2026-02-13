package mvc.vista;

import io.vavr.control.Try;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import util.CargadorRecursos;

import java.util.Optional;

/**
 * Vista principal del juego que muestra el campo de juego, paletas,
 * pelota y bloques.
 * Implementa responsividad mediante bindings con las dimensiones de la escena.
 *
 * @author Equipo-polimorfo
 * @version 1.0
 */
public class VistaJuego {

    private static final String FUENTE_RETRO = "Press Start 2P";
    private static final double TAMANIO_FUENTE_PUNTAJE = 24.0;
    private static final double TAMANIO_FUENTE_INFO = 12.0;
    private static final double PADDING_HUD = 20.0;

    private StackPane contenedor;
    private Pane panelJuego;
    private Canvas canvas;
    private GraphicsContext gc;
    private DoubleProperty anchoProperty;
    private DoubleProperty altoProperty;

    private Label labelPuntaje1;
    private Label labelPuntaje2;
    private Label labelTiempo;
    private Label labelInfo;
    private HBox hudSuperior;
    private VBox hudIzquierdo;
    private VBox hudDerecho;

    /**
     * Constructor que inicializa la vista del juego con responsividad.
     */
    public VistaJuego() {
        inicializarPropiedades();
        cargarFuentes();
        inicializarCanvas();
        inicializarHUD();
        inicializarContenedores();
        configurarResponsividad();
    }

    /**
     * Inicializa las propiedades observables para dimensiones.
     */
    private void inicializarPropiedades() {
        this.anchoProperty = new SimpleDoubleProperty(800.0);
        this.altoProperty = new SimpleDoubleProperty(600.0);
    }

    /**
     * Carga las fuentes necesarias para la vista.
     */
    private void cargarFuentes() {
        Try.run(() -> CargadorRecursos.cargarFuente("fuentes/PressStart2P.ttf", 12.0))
                .onFailure(e -> System.err.println("Error cargando fuente: " + e.getMessage()));
    }

    /**
     * Inicializa el canvas para renderizado del juego.
     */
    private void inicializarCanvas() {
        this.canvas = new Canvas(800, 600);
        this.gc = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(anchoProperty);
        canvas.heightProperty().bind(altoProperty);
    }

    /**
     * Inicializa el HUD (Heads-Up Display) con puntajes, tiempo e información.
     */
    private void inicializarHUD() {
        crearLabels();
        crearPanelesHUD();
        aplicarEstilosHUD();
    }

    /**
     * Crea los labels del HUD.
     */
    private void crearLabels() {
        labelPuntaje1 = crearLabel("0", TAMANIO_FUENTE_PUNTAJE);
        labelPuntaje2 = crearLabel("0", TAMANIO_FUENTE_PUNTAJE);
        labelTiempo = crearLabel("3:00", TAMANIO_FUENTE_INFO);
        labelInfo = crearLabel("ESPACIO: Iniciar | ALT: Pausa", TAMANIO_FUENTE_INFO);
    }

    /**
     * Crea un label con estilo retro.
     *
     * @param texto      Texto inicial del label
     * @param tamanioFuente Tamaño de la fuente
     * @return Label configurado
     */
    private Label crearLabel(final String texto, final double tamanioFuente) {
        final Label label = new Label(texto);
        label.setFont(Font.font(FUENTE_RETRO, tamanioFuente));
        label.setTextFill(Color.WHITE);
        label.setTextAlignment(TextAlignment.CENTER);
        return label;
    }

    /**
     * Crea los paneles del HUD.
     */
    private void crearPanelesHUD() {
        hudSuperior = new HBox(PADDING_HUD);
        hudSuperior.setAlignment(Pos.CENTER);
        hudSuperior.getChildren().addAll(labelTiempo);

        hudIzquierdo = new VBox(PADDING_HUD);
        hudIzquierdo.setAlignment(Pos.TOP_LEFT);
        hudIzquierdo.getChildren().add(labelPuntaje1);

        hudDerecho = new VBox(PADDING_HUD);
        hudDerecho.setAlignment(Pos.TOP_RIGHT);
        hudDerecho.getChildren().add(labelPuntaje2);
    }

    /**
     * Aplica estilos a los paneles del HUD.
     */
    private void aplicarEstilosHUD() {
        hudSuperior.setStyle("-fx-padding: " + PADDING_HUD + ";");
        hudIzquierdo.setStyle("-fx-padding: " + PADDING_HUD + ";");
        hudDerecho.setStyle("-fx-padding: " + PADDING_HUD + ";");
    }

    /**
     * Inicializa los contenedores de la vista.
     */
    private void inicializarContenedores() {
        this.panelJuego = new Pane(canvas);

        final BorderPane layoutPrincipal = new BorderPane();
        layoutPrincipal.setCenter(panelJuego);
        layoutPrincipal.setTop(hudSuperior);
        layoutPrincipal.setLeft(hudIzquierdo);
        layoutPrincipal.setRight(hudDerecho);
        layoutPrincipal.setBottom(crearPanelInferior());

        this.contenedor = new StackPane(layoutPrincipal);
        aplicarEstilosContenedor();
    }

    /**
     * Crea el panel inferior con información.
     *
     * @return HBox con información inferior
     */
    private HBox crearPanelInferior() {
        final HBox panelInferior = new HBox();
        panelInferior.setAlignment(Pos.CENTER);
        panelInferior.setStyle("-fx-padding: " + PADDING_HUD + ";");
        panelInferior.getChildren().add(labelInfo);
        return panelInferior;
    }

    /**
     * Aplica estilos al contenedor principal.
     */
    private void aplicarEstilosContenedor() {
        contenedor.setStyle("-fx-background-color: black;");
    }

    /**
     * Configura la responsividad del panel de juego.
     * Vincula las dimensiones del panel con las propiedades observables.
     */
    private void configurarResponsividad() {
        vincularDimensionesPanel();
    }

    /**
     * Vincula las dimensiones del panel de juego con las propiedades.
     */
    private void vincularDimensionesPanel() {
        panelJuego.prefWidthProperty().bind(anchoProperty);
        panelJuego.prefHeightProperty().bind(altoProperty);
        panelJuego.minWidthProperty().bind(anchoProperty);
        panelJuego.minHeightProperty().bind(altoProperty);
        panelJuego.maxWidthProperty().bind(anchoProperty);
        panelJuego.maxHeightProperty().bind(altoProperty);
    }

    /**
     * Vincula las dimensiones de la vista con una escena.
     *
     * @param escena Scene a la cual vincular las dimensiones
     */
    public void vincularConEscena(Scene escena) {
        aplicarVinculoEscena(escena);
    }

    /**
     * Aplica el vínculo de dimensiones con la escena.
     *
     * @param escena Scene fuente de las dimensiones
     */
    private void aplicarVinculoEscena(Scene escena) {
        Optional.ofNullable(escena)
                .ifPresent(this::vincularPropiedades);
    }

    /**
     * Vincula las propiedades de ancho y alto con la escena.
     *
     * @param escena Scene a vincular
     */
    private void vincularPropiedades(Scene escena) {
        anchoProperty.bind(escena.widthProperty());
        altoProperty.bind(escena.heightProperty());
    }

    /**
     * Obtiene el contenedor principal de la vista.
     *
     * @return StackPane contenedor
     */
    public StackPane obtenerContenedor() {
        return contenedor;
    }

    /**
     * Obtiene el panel de juego interno.
     *
     * @return Pane del juego
     */
    public Pane obtenerPanelJuego() {
        return panelJuego;
    }

    /**
     * Obtiene el ancho actual del juego.
     *
     * @return Ancho en píxeles
     */
    public double obtenerAncho() {
        return anchoProperty.get();
    }

    /**
     * Obtiene el alto actual del juego.
     *
     * @return Alto en píxeles
     */
    public double obtenerAlto() {
        return altoProperty.get();
    }

    /**
     * Obtiene la propiedad de ancho para vinculación.
     *
     * @return DoubleProperty del ancho
     */
    public DoubleProperty anchoProperty() {
        return anchoProperty;
    }

    /**
     * Obtiene la propiedad de alto para vinculación.
     *
     * @return DoubleProperty del alto
     */
    public DoubleProperty altoProperty() {
        return altoProperty;
    }

    /**
     * Obtiene el canvas de renderizado.
     *
     * @return Canvas del juego
     */
    public Canvas obtenerCanvas() {
        return canvas;
    }

    /**
     * Obtiene el contexto gráfico del canvas.
     *
     * @return GraphicsContext para renderizado
     */
    public GraphicsContext obtenerContextoGrafico() {
        return gc;
    }

    /**
     * Actualiza el puntaje mostrado para un jugador.
     *
     * @param jugador Número del jugador (1 o 2)
     * @param nuevoPuntaje Nuevo puntaje a mostrar
     */
    public void actualizarPuntaje(final int jugador, final int nuevoPuntaje) {
        Try.run(() -> {
            final String puntajeFormateado = String.valueOf(nuevoPuntaje);
            if (jugador == 1) {
                labelPuntaje1.setText(puntajeFormateado);
            } else if (jugador == 2) {
                labelPuntaje2.setText(puntajeFormateado);
            }
        }).onFailure(e -> System.err.println("Error actualizando puntaje: " + e.getMessage()));
    }

    /**
     * Actualiza el tiempo mostrado en formato MM:SS.
     *
     * @param segundosRestantes Segundos restantes del juego
     */
    public void actualizarTiempo(final double segundosRestantes) {
        Try.run(() -> {
            final int minutos = (int) segundosRestantes / 60;
            final int segundos = (int) segundosRestantes % 60;
            final String tiempoFormateado = String.format("%d:%02d", minutos, segundos);
            labelTiempo.setText(tiempoFormateado);
        }).onFailure(e -> System.err.println("Error actualizando tiempo: " + e.getMessage()));
    }

    /**
     * Actualiza el mensaje informativo en la parte inferior.
     *
     * @param mensaje Nuevo mensaje a mostrar
     */
    public void actualizarInfo(final String mensaje) {
        Try.run(() -> labelInfo.setText(mensaje))
                .onFailure(e -> System.err.println("Error actualizando info: " + e.getMessage()));
    }

    /**
     * Muestra un mensaje temporal en el centro de la pantalla.
     *
     * @param mensaje Mensaje a mostrar
     */
    public void mostrarMensajeCentral(final String mensaje) {
        mostrarMensajeCentral(mensaje, 2.0);
    }

    /**
     * Muestra un mensaje temporal en el centro de la pantalla con duracion personalizada.
     *
     * @param mensaje Mensaje a mostrar
     * @param duracionSegundos Duracion en segundos que el mensaje permanecera visible
     */
    public void mostrarMensajeCentral(final String mensaje, final double duracionSegundos) {
        Try.run(() -> {
            final Label labelMensaje = crearLabel(mensaje, TAMANIO_FUENTE_INFO * 1.5);
            labelMensaje.setStyle(
                    "-fx-background-color: rgba(0, 0, 0, 0.8);" +
                    "-fx-padding: 20;" +
                    "-fx-background-radius: 10;"
            );

            contenedor.getChildren().add(labelMensaje);
            StackPane.setAlignment(labelMensaje, Pos.CENTER);

            new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(
                            javafx.util.Duration.seconds(duracionSegundos),
                            event -> contenedor.getChildren().remove(labelMensaje)
                    )
            ).play();
        }).onFailure(e -> System.err.println("Error mostrando mensaje: " + e.getMessage()));
    }

    /**
     * Actualiza las vidas mostradas para un jugador.
     * Método mantenido para compatibilidad futura si se agrega sistema de vidas.
     *
     * @param jugador Número del jugador
     * @param nuevasVidas Nuevas vidas a mostrar
     */
    public void actualizarVidas(final int jugador, final int nuevasVidas) {
        // Reservado para implementación futura del sistema de vidas
    }
}
