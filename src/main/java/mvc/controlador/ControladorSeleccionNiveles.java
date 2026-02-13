package mvc.controlador;

import io.vavr.control.Option;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import patrones.builder.ConstructorMapa;
import patrones.builder.DirectorNiveles;
import mvc.modelo.entidades.Nivel;
import org.kordamp.ikonli.javafx.FontIcon;
import mvc.vista.GestorEscenas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controlador del panel de seleccion de niveles.
 * Gestiona la carga dinamica de niveles prearmados y personalizados,
 * y permite la seleccion de nivel para iniciar el juego.
 * Implementa el componente Controlador del patron MVC.
 * Extiende de ControladorBase para heredar funcionalidad comun de atajos de teclado.
 */
public class ControladorSeleccionNiveles extends ControladorBase {

    private static final int COLUMNAS_GRID = 2;
    private static final double ANCHO_TARJETA = 280.0;
    private static final double ALTO_TARJETA = 150.0;
    private static final double DURACION_ANIMACION_MS = 300.0;
    private static final double DELAY_CASCADA_MS = 80.0;

    @FXML
    private GridPane gridNiveles;

    @FXML
    private Button botonRegresar;

    @FXML
    private Button botonJugar;

    @FXML
    private Label labelDificultad;

    private List<Nivel> nivelesDisponibles;
    private Nivel nivelSeleccionado;
    private Button botonSeleccionado;
    private Integer dificultadIA;
    private boolean esContraIA;
    private persistencia.ServicioPersistencia servicioPersistencia;

    /**
     * Constructor por defecto requerido por FXML.
     */
    public ControladorSeleccionNiveles() {
        this.nivelesDisponibles = new ArrayList<>();
        this.nivelSeleccionado = null;
        this.botonSeleccionado = null;
        this.dificultadIA = null;
        this.esContraIA = false;
        this.servicioPersistencia = new persistencia.ServicioPersistencia();
    }

    /**
     * Inicializa el controlador despues de cargar el FXML.
     */
    @FXML
    @Override
    public void initialize() {
        configurarAtajosTecladoPantallaCompleta(gridNiveles);
        cargarNivelesPrearmados();
        cargarNivelesPersonalizados();
        generarTarjetasNiveles();
    }

    /**
     * Carga los niveles prearmados desde el DirectorNiveles.
     */
    private void cargarNivelesPrearmados() {
        DirectorNiveles director = crearDirectorNiveles();
        agregarNivelesPrearmados(director);
    }

    /**
     * Crea una instancia del DirectorNiveles con su constructor.
     *
     * @return DirectorNiveles configurado
     */
    private DirectorNiveles crearDirectorNiveles() {
        ConstructorMapa constructor = new ConstructorMapa();
        return new DirectorNiveles(constructor);
    }

    /**
     * Agrega los niveles prearmados a la lista de niveles disponibles.
     *
     * @param director DirectorNiveles que construye los niveles
     */
    private void agregarNivelesPrearmados(DirectorNiveles director) {
        nivelesDisponibles.add(director.construirNivelFacil());
        nivelesDisponibles.add(director.construirNivelMedio());
        nivelesDisponibles.add(director.construirNivelDificil());
    }

    /**
     * Carga los niveles personalizados desde la base de datos.
     * Los niveles cargados se agregan a la lista de niveles disponibles.
     */
    private void cargarNivelesPersonalizados() {
        servicioPersistencia.cargarNivelesPersonalizados()
            .onSuccess(niveles -> nivelesDisponibles.addAll(niveles))
            .onFailure(error -> {
                System.err.println("Error al cargar niveles personalizados: " + error.getMessage());
                error.printStackTrace();
            });
    }

    /**
     * Genera las tarjetas de categorias de dificultad en el GridPane.
     */
    private void generarTarjetasNiveles() {
        limpiarGrid();

        final VBox tarjetaFacil = crearTarjetaDificultad("Nivel Fácil", 1, 1);
        final VBox tarjetaMedio = crearTarjetaDificultad("Nivel Medio", 2, 2);
        final VBox tarjetaDificil = crearTarjetaDificultad("Nivel Difícil", 3, 3);
        final VBox tarjetaNuevo = crearTarjetaNuevoNivel();
        
        gridNiveles.add(tarjetaFacil, 0, 0);
        gridNiveles.add(tarjetaMedio, 1, 0);
        gridNiveles.add(tarjetaDificil, 0, 1);
        gridNiveles.add(tarjetaNuevo, 1, 1);
    }

    /**
     * Limpia el contenido del GridPane.
     */
    private void limpiarGrid() {
        gridNiveles.getChildren().clear();
    }

    /**
     * Crea una tarjeta visual para una categoria de dificultad.
     *
     * @param nombre nombre de la categoria
     * @param dificultadVisual dificultad visual para mostrar estrellas
     * @param dificultadFiltro dificultad usada para filtrar en la base de datos
     * @return VBox con la tarjeta de la categoria
     */
    private VBox crearTarjetaDificultad(final String nombre, final int dificultadVisual, final int dificultadFiltro) {
        final VBox tarjeta = new VBox(15);
        configurarEstiloTarjeta(tarjeta);

        final Label nombreLabel = new Label(nombre);
        nombreLabel.getStyleClass().add("texto-titulo");
        nombreLabel.setWrapText(true);
        nombreLabel.setMaxWidth(ANCHO_TARJETA - 40);
        nombreLabel.setAlignment(Pos.CENTER);

        final String estrellas = generarEstrellasDificultad(dificultadVisual);
        final Label dificultadLabel = new Label(String.format("Dificultad: %s", estrellas));
        dificultadLabel.getStyleClass().add("texto-info");

        final Button botonSeleccionar = new Button("Seleccionar");
        botonSeleccionar.getStyleClass().add("boton-secundario");
        botonSeleccionar.setOnAction(event -> abrirDialogoDificultad(dificultadFiltro));

        tarjeta.getChildren().addAll(nombreLabel, dificultadLabel, botonSeleccionar);

        return tarjeta;
    }

    /**
     * Genera una representacion visual de la dificultad usando estrellas.
     *
     * @param dificultad nivel de dificultad (1-10)
     * @return cadena con estrellas representando la dificultad
     */
    private String generarEstrellasDificultad(final int dificultad) {
        return io.vavr.collection.List.range(0, dificultad)
            .map(i -> "★")
            .mkString("");
    }

    /**
     * Abre el dialogo modal de seleccion de niveles por dificultad.
     *
     * @param dificultad nivel de dificultad a filtrar (1-10)
     */
    private void abrirDialogoDificultad(final int dificultad) {
        io.vavr.control.Try.of(() -> {
            final javafx.fxml.FXMLLoader loader = util.CargadorRecursos.cargarFXMLLoader("fxml/dialogo-seleccion-por-dificultad.fxml");
            final javafx.scene.layout.BorderPane root = loader.load();
            final ControladorDialogoSeleccionPorDificultad controlador = loader.getController();
            
            controlador.establecerDificultad(dificultad);
            
            final javafx.stage.Stage dialogo = new javafx.stage.Stage();
            dialogo.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogo.initOwner(gestorEscenas.obtenerEscenarioPrincipal());
            dialogo.setTitle("Seleccionar Nivel");
            
            final javafx.scene.Scene escena = new javafx.scene.Scene(root);
            util.CargadorRecursos.obtenerRutaCSS("css/estilo-retro.css")
                .ifPresent(css -> escena.getStylesheets().add(css));
            
            dialogo.setScene(escena);
            dialogo.showAndWait();
            
            return controlador.obtenerNivelSeleccionado();
        })
        .peek(nivelOpcional -> nivelOpcional.peek(this::establecerNivelSeleccionado))
        .onFailure(error -> {
            System.err.println("Error al abrir dialogo de dificultad: " + error.getMessage());
            error.printStackTrace();
        });
    }

    /**
     * Establece el nivel seleccionado y muestra el boton de jugar.
     *
     * @param nivel nivel seleccionado
     */
    private void establecerNivelSeleccionado(final Nivel nivel) {
        nivelSeleccionado = nivel;
        botonJugar.setVisible(true);
    }

    /**
     * Crea una tarjeta visual para un nivel.
     *
     * @param nivel Nivel a representar
     * @param indice Indice del nivel para animacion
     * @return VBox con la tarjeta del nivel
     */
    private VBox crearTarjetaNivel(Nivel nivel, int indice) {
        VBox tarjeta = new VBox(15);
        configurarEstiloTarjeta(tarjeta);

        if (nivel.isMapaPersonalizado()) {
            agregarIndicadorPersonalizado(tarjeta, nivel);
        }

        Label nombreLabel = crearLabelNombre(nivel);
        Label dificultadLabel = crearLabelDificultad(nivel);
        Button botonSeleccionar = crearBotonSeleccionar(nivel);

        tarjeta.getChildren().addAll(nombreLabel, dificultadLabel, botonSeleccionar);
        animarTarjeta(tarjeta, indice);

        return tarjeta;
    }

    /**
     * Agrega un indicador visual a tarjetas de niveles personalizados.
     *
     * @param tarjeta tarjeta a la que agregar el indicador
     * @param nivel nivel personalizado
     */
    private void agregarIndicadorPersonalizado(VBox tarjeta, Nivel nivel) {
        FontIcon iconoPersonalizado = new FontIcon("fas-user");
        iconoPersonalizado.setIconSize(20);
        iconoPersonalizado.setStyle("-fx-icon-color: #4CAF50;");

        Label labelCreador = new Label("Por: " + (nivel.getCreador() != null ? nivel.getCreador() : "Desconocido"));
        labelCreador.setStyle("-fx-font-size: 9px; -fx-text-fill: #4CAF50;");

        tarjeta.getChildren().addAll(iconoPersonalizado, labelCreador);
    }

    /**
     * Configura el estilo base de una tarjeta.
     *
     * @param tarjeta VBox a configurar
     */
    private void configurarEstiloTarjeta(VBox tarjeta) {
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPrefSize(ANCHO_TARJETA, ALTO_TARJETA);
        tarjeta.setMaxSize(ANCHO_TARJETA, ALTO_TARJETA);
        tarjeta.setMinSize(ANCHO_TARJETA, ALTO_TARJETA);
        tarjeta.setPadding(new Insets(20));
        tarjeta.getStyleClass().add("tarjeta-nivel");
    }

    /**
     * Crea el label del nombre del nivel.
     *
     * @param nivel Nivel del que obtener el nombre
     * @return Label configurado
     */
    private Label crearLabelNombre(Nivel nivel) {
        Label label = new Label(nivel.getNombre());
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        return label;
    }

    /**
     * Crea el label de la dificultad del nivel.
     *
     * @param nivel Nivel del que obtener la dificultad
     * @return Label configurado
     */
    private Label crearLabelDificultad(Nivel nivel) {
        String estrellas = generarEstrellas(nivel.getDificultad());
        Label label = new Label("Dificultad: " + estrellas);
        label.setStyle("-fx-font-size: 10px; -fx-text-fill: #FFFFFF;");
        return label;
    }

    /**
     * Genera una representacion visual de la dificultad con estrellas.
     *
     * @param dificultad Nivel de dificultad (1-3)
     * @return String con estrellas
     */
    private String generarEstrellas(int dificultad) {
        return "★".repeat(dificultad) + "☆".repeat(3 - dificultad);
    }

    /**
     * Crea el boton de seleccion para un nivel.
     *
     * @param nivel Nivel a seleccionar
     * @return Button configurado
     */
    private Button crearBotonSeleccionar(Nivel nivel) {
        Button boton = new Button("Seleccionar");
        boton.getStyleClass().add("boton-secundario");
        boton.setOnAction(event -> manejarSeleccionNivel(nivel, boton));
        return boton;
    }

    /**
     * Crea la tarjeta para crear un nuevo nivel.
     *
     * @return VBox con la tarjeta de nuevo nivel
     */
    private VBox crearTarjetaNuevoNivel() {
        VBox tarjeta = new VBox(15);
        configurarEstiloTarjeta(tarjeta);

        FontIcon iconoMas = new FontIcon("fas-plus");
        iconoMas.setIconSize(40);
        iconoMas.setStyle("-fx-icon-color: #FFFFFF;");

        Label label = new Label("Crear Nivel");
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: #FFFFFF;");

        Button botonCrear = new Button("Modo Constructor");
        botonCrear.getStyleClass().add("boton-secundario");
        botonCrear.setOnAction(event -> accionModoConstructor());

        tarjeta.getChildren().addAll(iconoMas, label, botonCrear);
        animarTarjeta(tarjeta, nivelesDisponibles.size());

        return tarjeta;
    }

    /**
     * Anima la aparicion de una tarjeta con efecto cascada.
     *
     * @param tarjeta Tarjeta a animar
     * @param indice Indice para calcular delay
     */
    private void animarTarjeta(VBox tarjeta, int indice) {
        tarjeta.setOpacity(0.0);
        tarjeta.setScaleX(0.8);
        tarjeta.setScaleY(0.8);

        double delay = indice * DELAY_CASCADA_MS;

        FadeTransition fade = new FadeTransition(Duration.millis(DURACION_ANIMACION_MS), tarjeta);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setDelay(Duration.millis(delay));

        ScaleTransition escala = new ScaleTransition(Duration.millis(DURACION_ANIMACION_MS), tarjeta);
        escala.setFromX(0.8);
        escala.setFromY(0.8);
        escala.setToX(1.0);
        escala.setToY(1.0);
        escala.setDelay(Duration.millis(delay));

        fade.play();
        escala.play();
    }

    /**
     * Maneja la seleccion de un nivel.
     *
     * @param nivel Nivel seleccionado
     * @param boton Boton que fue clickeado
     */
    private void manejarSeleccionNivel(Nivel nivel, Button boton) {
        limpiarSeleccionPrevia();
        nivelSeleccionado = nivel;
        botonSeleccionado = boton;
        resaltarBotonSeleccionado(boton);
        mostrarBotonJugar();
        System.out.println("Nivel seleccionado: " + nivel.getNombre());
    }

    /**
     * Limpia el resaltado de la seleccion previa si existe.
     */
    private void limpiarSeleccionPrevia() {
        if (botonSeleccionado != null) {
            botonSeleccionado.getStyleClass().remove("boton-nivel-seleccionado");
        }
    }

    /**
     * Resalta el boton seleccionado con estilo especial.
     *
     * @param boton Boton a resaltar
     */
    private void resaltarBotonSeleccionado(Button boton) {
        boton.getStyleClass().add("boton-nivel-seleccionado");
    }

    /**
     * Muestra el boton Jugar con animacion fade-in.
     */
    private void mostrarBotonJugar() {
        botonJugar.setVisible(true);
        botonJugar.setOpacity(0.0);
        FadeTransition fade = new FadeTransition(Duration.millis(300), botonJugar);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    /**
     * Maneja el evento de clic en el boton Jugar.
     * Navega al juego con el nivel seleccionado.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionJugar(ActionEvent evento) {
        if (nivelSeleccionado != null) {
            System.out.println("Iniciando juego con nivel: " + nivelSeleccionado.getNombre());
            navegarAlJuego();
        }
    }

    /**
     * Navega a la pantalla del juego.
     */
    private void navegarAlJuego() {
        Optional.ofNullable(gestorEscenas)
                .ifPresent(GestorEscenas::mostrarJuego);
    }

    /**
     * Maneja el evento de clic en el boton Modo Constructor.
     */
    private void accionModoConstructor() {
        System.out.println("Navegando al modo constructor...");
        navegarAlEditor();
    }

    /**
     * Navega al modo constructor/editor.
     */
    private void navegarAlEditor() {
        Optional.ofNullable(gestorEscenas)
                .ifPresent(GestorEscenas::mostrarEditor);
    }

    /**
     * Maneja el evento de clic en el boton Regresar.
     * Vuelve al menu principal o seleccion de dificultad.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionRegresar(ActionEvent evento) {
        System.out.println("Regresando...");
        navegarAlMenu();
    }

    /**
     * Navega al menu principal.
     */
    private void navegarAlMenu() {
        Optional.ofNullable(gestorEscenas)
                .ifPresent(gestor -> {
                    if (esContraIA) {
                        gestor.mostrarSeleccionDificultad();
                    } else {
                        gestor.mostrarMenu();
                    }
                });
    }

    /**
     * Reinicia solo la seleccion de nivel sin modificar configuracion de modo/dificultad.
     * Utilizado cuando se muestra la pantalla de seleccion con configuracion preestablecida.
     * Preserva los valores de esContraIA y dificultadIA para mantener el modo de juego.
     */
    public void reiniciarSeleccionNivel() {
        nivelSeleccionado = null;
        botonSeleccionado = null;
        botonJugar.setVisible(false);
        limpiarSeleccionPrevia();
        generarTarjetasNiveles();
        actualizarVisualizacionDificultad();
    }

    /**
     * Reinicia completamente el estado del controlador, incluyendo modo y dificultad.
     * Utilizado cuando se vuelve al menu principal o se cambia de modo de juego.
     * Limpia todos los flags de estado incluyendo esContraIA y dificultadIA.
     */
    public void reiniciarEstadoCompleto() {
        nivelSeleccionado = null;
        botonSeleccionado = null;
        botonJugar.setVisible(false);
        esContraIA = false;
        dificultadIA = null;
        limpiarSeleccionPrevia();
        generarTarjetasNiveles();
        actualizarVisualizacionDificultad();
    }

    /**
     * Metodo legacy que redirige a reiniciarSeleccionNivel para compatibilidad.
     *
     * @deprecated Use reiniciarSeleccionNivel() o reiniciarEstadoCompleto() segun el caso.
     */
    @Deprecated
    public void reiniciarEstado() {
        reiniciarSeleccionNivel();
    }

    /**
     * Libera los recursos del controlador.
     */
    public void liberarRecursos() {
    }

    /**
     * Establece si el juego es contra IA para determinar la navegacion de regreso.
     *
     * @param esContraIA true si es contra IA, false si es 2 jugadores
     */
    public void establecerEsContraIA(boolean esContraIA) {
        this.esContraIA = esContraIA;
    }

    /**
     * Establece el nivel de dificultad de IA seleccionado previamente.
     * Actualiza la visualizacion del label de dificultad de manera funcional.
     *
     * @param dificultad Nivel de dificultad (1-10)
     */
    public void establecerDificultadIA(Integer dificultad) {
        this.dificultadIA = dificultad;
        this.esContraIA = dificultad != null;
        actualizarVisualizacionDificultad();
    }

    /**
     * Actualiza la visualizacion del label de dificultad de manera funcional.
     * Muestra u oculta el label dependiendo de si hay dificultad configurada.
     * Utiliza programacion funcional pura con Vavr Option.
     */
    private void actualizarVisualizacionDificultad() {
        io.vavr.control.Option.of(labelDificultad)
                .peek(this::configurarVisibilidadLabel)
                .peek(this::configurarTextoLabel);
    }

    /**
     * Configura la visibilidad del label segun si hay dificultad.
     *
     * @param label Label a configurar
     */
    private void configurarVisibilidadLabel(Label label) {
        boolean tieneDificultad = dificultadIA != null;
        label.setVisible(tieneDificultad);
        label.setManaged(tieneDificultad);
    }

    /**
     * Configura el texto del label con el nivel de dificultad.
     * Usa programacion funcional pura para manejar presencia/ausencia de dificultad.
     *
     * @param label Label a configurar
     */
    private void configurarTextoLabel(Label label) {
        String texto = io.vavr.control.Option.of(dificultadIA)
                .map(this::construirTextoDificultad)
                .getOrElse("");
        label.setText(texto);
    }

    /**
     * Construye el texto de dificultad con formato retro.
     *
     * @param nivel Nivel de dificultad (1-10)
     * @return Texto formateado
     */
    private String construirTextoDificultad(Integer nivel) {
        return String.format("IA NIVEL: %d", nivel);
    }

    /**
     * Obtiene el nivel actualmente seleccionado.
     *
     * @return Optional con el nivel seleccionado, o empty si no hay seleccion
     */
    public Optional<Nivel> obtenerNivelSeleccionado() {
        return Optional.ofNullable(nivelSeleccionado);
    }

    /**
     * Obtiene la dificultad de IA configurada.
     *
     * @return Optional con la dificultad, o empty si no fue configurada
     */
    public Optional<Integer> obtenerDificultadIA() {
        return Optional.ofNullable(dificultadIA);
    }
}
