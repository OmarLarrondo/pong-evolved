package mvc.controlador;

import io.vavr.control.Option;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mvc.modelo.entidades.Nivel;
import persistencia.ServicioPersistencia;

import java.util.List;

/**
 * Controlador para el dialogo de seleccion de niveles por dificultad.
 * Permite al usuario seleccionar un nivel de una dificultad especifica.
 * Implementa un patron de dialogo modal que retorna un resultado opcional.
 */
public class ControladorDialogoSeleccionPorDificultad {

    private static final int ANCHO_TARJETA = 180;
    private static final int ALTO_TARJETA = 150;
    private static final int COLUMNAS_GRID = 3;

    @FXML
    private GridPane gridNiveles;

    @FXML
    private Label labelTitulo;

    @FXML
    private Label labelSubtitulo;

    @FXML
    private Button botonSeleccionar;

    @FXML
    private Button botonCancelar;

    private final ServicioPersistencia servicioPersistencia;
    private Option<Nivel> nivelSeleccionado;
    private int dificultad;
    private Button botonActivoActual;

    /**
     * Constructor del controlador que inicializa el servicio de persistencia.
     */
    public ControladorDialogoSeleccionPorDificultad() {
        this.servicioPersistencia = new ServicioPersistencia();
        this.nivelSeleccionado = Option.none();
        this.dificultad = 1;
        this.botonActivoActual = null;
    }

    /**
     * Inicializa el controlador despues de que se hayan inyectado todos los componentes FXML.
     */
    @FXML
    public void initialize() {
        // La carga de niveles se realiza cuando se establece la dificultad
    }

    /**
     * Establece la dificultad a filtrar y carga los niveles correspondientes.
     *
     * @param dificultad nivel de dificultad a filtrar (1=Facil, 2=Medio, 3=Dificil)
     */
    public void establecerDificultad(final int dificultad) {
        this.dificultad = dificultad;
        actualizarTitulos();
        cargarNivelesPorDificultad();
    }

    /**
     * Actualiza los titulos del dialogo segun la dificultad seleccionada.
     */
    private void actualizarTitulos() {
        final String nombreDificultad = obtenerNombreDificultad(dificultad);
        labelTitulo.setText(String.format("NIVELES - %s", nombreDificultad.toUpperCase()));
        labelSubtitulo.setText(String.format("Selecciona un nivel de dificultad %s", nombreDificultad.toLowerCase()));
    }

    /**
     * Obtiene el nombre descriptivo de una dificultad.
     *
     * @param dificultad nivel de dificultad (1=Facil, 2=Medio, 3=Dificil)
     * @return nombre descriptivo de la dificultad
     */
    private String obtenerNombreDificultad(final int dificultad) {
        return io.vavr.collection.HashMap.of(
            1, "Fácil",
            2, "Medio",
            3, "Difícil"
        ).get(dificultad).getOrElse("Desconocido");
    }

    /**
     * Carga los niveles de la dificultad especificada desde la base de datos.
     */
    private void cargarNivelesPorDificultad() {
        servicioPersistencia.cargarNivelesPorDificultad(dificultad)
            .peek(niveles -> Platform.runLater(() -> generarTarjetasNiveles(niveles)))
            .peek(niveles -> {
                if (niveles.isEmpty()) {
                    Platform.runLater(this::mostrarMensajeSinNiveles);
                }
            })
            .onFailure(error -> Platform.runLater(() ->
                mostrarError("Error al cargar niveles", error.getMessage())
            ));
    }

    /**
     * Genera las tarjetas de niveles en el GridPane.
     *
     * @param niveles lista de niveles a mostrar
     */
    private void generarTarjetasNiveles(final List<Nivel> niveles) {
        limpiarGrid();
        int fila = 0;
        int columna = 0;

        for (int i = 0; i < niveles.size(); i++) {
            final Nivel nivel = niveles.get(i);
            final VBox tarjeta = crearTarjetaNivel(nivel);
            gridNiveles.add(tarjeta, columna, fila);

            columna++;
            if (columna >= COLUMNAS_GRID) {
                columna = 0;
                fila++;
            }
        }
    }

    /**
     * Limpia el GridPane de niveles.
     */
    private void limpiarGrid() {
        gridNiveles.getChildren().clear();
    }

    /**
     * Crea una tarjeta visual para un nivel.
     *
     * @param nivel nivel a representar
     * @return VBox con la tarjeta del nivel
     */
    private VBox crearTarjetaNivel(final Nivel nivel) {
        final VBox tarjeta = new VBox(15);
        configurarEstiloTarjeta(tarjeta);

        final Label nombreLabel = crearLabelNombre(nivel);
        final Label dificultadLabel = crearLabelDificultad(nivel);
        final Button botonSeleccionarNivel = crearBotonSeleccionarNivel(nivel, tarjeta);

        tarjeta.getChildren().addAll(nombreLabel, dificultadLabel, botonSeleccionarNivel);

        return tarjeta;
    }

    /**
     * Configura el estilo base de una tarjeta.
     *
     * @param tarjeta VBox a configurar
     */
    private void configurarEstiloTarjeta(final VBox tarjeta) {
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
     * @param nivel nivel del que obtener el nombre
     * @return Label configurado con el nombre
     */
    private Label crearLabelNombre(final Nivel nivel) {
        final Label label = new Label(nivel.getNombre());
        label.getStyleClass().add("texto-titulo");
        label.setWrapText(true);
        label.setMaxWidth(ANCHO_TARJETA - 40);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    /**
     * Crea el label de dificultad del nivel.
     *
     * @param nivel nivel del que obtener la dificultad
     * @return Label configurado con la dificultad
     */
    private Label crearLabelDificultad(final Nivel nivel) {
        final String estrellas = generarEstrellasDificultad(nivel.getDificultad());
        final Label label = new Label(String.format("Dificultad: %s", estrellas));
        label.getStyleClass().add("texto-info");
        return label;
    }

    /**
     * Genera una representacion visual de la dificultad usando estrellas.
     *
     * @param dificultad nivel de dificultad (1=Facil, 2=Medio, 3=Dificil)
     * @return cadena con estrellas representando la dificultad
     */
    private String generarEstrellasDificultad(final int dificultad) {
        return io.vavr.collection.List.range(0, dificultad)
            .map(i -> "★")
            .mkString("");
    }

    /**
     * Crea el boton de seleccion de un nivel especifico.
     *
     * @param nivel nivel a asociar con el boton
     * @param tarjeta tarjeta que contiene el boton
     * @return Button configurado
     */
    private Button crearBotonSeleccionarNivel(final Nivel nivel, final VBox tarjeta) {
        final Button boton = new Button("Seleccionar");
        boton.getStyleClass().add("boton-secundario");
        boton.setOnAction(event -> manejarSeleccionNivel(nivel, boton, tarjeta));
        return boton;
    }

    /**
     * Maneja la seleccion de un nivel por parte del usuario.
     *
     * @param nivel nivel seleccionado
     * @param boton boton que fue presionado
     * @param tarjeta tarjeta que contiene el nivel
     */
    private void manejarSeleccionNivel(final Nivel nivel, final Button boton, final VBox tarjeta) {
        desactivarSeleccionAnterior();

        nivelSeleccionado = Option.of(nivel);
        botonActivoActual = boton;

        tarjeta.getStyleClass().add("tarjeta-seleccionada");
        botonSeleccionar.setVisible(true);
    }

    /**
     * Desactiva la seleccion anterior si existe.
     */
    private void desactivarSeleccionAnterior() {
        Option.of(botonActivoActual)
            .peek(boton -> {
                final VBox tarjetaAnterior = (VBox) boton.getParent();
                tarjetaAnterior.getStyleClass().remove("tarjeta-seleccionada");
            });
    }

    /**
     * Muestra un mensaje cuando no hay niveles disponibles para la dificultad seleccionada.
     */
    private void mostrarMensajeSinNiveles() {
        final Label mensaje = new Label("No hay niveles guardados con esta dificultad");
        mensaje.getStyleClass().add("texto-info");
        mensaje.setWrapText(true);
        mensaje.setMaxWidth(400);
        mensaje.setAlignment(Pos.CENTER);

        final VBox contenedor = new VBox(mensaje);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(40));

        gridNiveles.add(contenedor, 0, 0, COLUMNAS_GRID, 1);
    }

    /**
     * Maneja el evento de seleccion confirmada.
     * Cierra el dialogo con el nivel seleccionado.
     */
    @FXML
    private void manejarSeleccionar() {
        cerrarDialogo();
    }

    /**
     * Maneja el evento de cancelacion.
     * Cierra el dialogo sin seleccionar ningun nivel.
     */
    @FXML
    private void manejarCancelar() {
        nivelSeleccionado = Option.none();
        cerrarDialogo();
    }

    /**
     * Cierra el dialogo obteniendo la ventana desde cualquier control.
     */
    private void cerrarDialogo() {
        Option.of(botonCancelar.getScene())
            .map(escena -> escena.getWindow())
            .filter(ventana -> ventana instanceof Stage)
            .map(ventana -> (Stage) ventana)
            .peek(Stage::close);
    }

    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param titulo titulo del mensaje de error
     * @param mensaje contenido del mensaje de error
     */
    private void mostrarError(final String titulo, final String mensaje) {
        final Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Obtiene el nivel seleccionado por el usuario.
     *
     * @return Option conteniendo el nivel seleccionado, o vacio si se cancelo
     */
    public Option<Nivel> obtenerNivelSeleccionado() {
        return nivelSeleccionado;
    }
}
