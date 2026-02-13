package mvc.controlador;

import io.vavr.control.Option;
import io.vavr.control.Try;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mvc.modelo.entidades.Nivel;
import persistencia.ServicioPersistencia;

/**
 * Controlador para el dialogo de carga de niveles personalizados.
 * Permite al usuario seleccionar un nivel guardado previamente para editarlo.
 * Implementa un patron de dialogo modal que retorna un resultado opcional.
 */
public class ControladorDialogoCargarNivel {

    @FXML
    private ListView<Nivel> listaNiveles;

    @FXML
    private Button botonCargar;

    @FXML
    private Button botonCancelar;

    @FXML
    private Button botonEliminar;

    private final ServicioPersistencia servicioPersistencia;
    private Option<Nivel> nivelSeleccionado;

    /**
     * Constructor del controlador que inicializa el servicio de persistencia.
     */
    public ControladorDialogoCargarNivel() {
        this.servicioPersistencia = new ServicioPersistencia();
        this.nivelSeleccionado = Option.none();
    }

    /**
     * Inicializa el controlador despues de que se hayan inyectado todos los componentes FXML.
     * Configura la celda personalizada del ListView y carga los niveles disponibles.
     */
    @FXML
    public void initialize() {
        configurarCeldaPersonalizada();
        cargarNiveles();
        configurarBotonCargar();
    }

    /**
     * Configura la fabrica de celdas del ListView para mostrar informacion detallada de cada nivel.
     * Cada celda muestra el nombre, creador, dificultad y cantidad de bloques del nivel.
     */
    private void configurarCeldaPersonalizada() {
        listaNiveles.setCellFactory(lista -> new ListCell<Nivel>() {
            @Override
            protected void updateItem(Nivel nivel, boolean vacio) {
                super.updateItem(nivel, vacio);

                Option.of(nivel)
                    .filter(n -> !vacio)
                    .peek(n -> configurarCeldaConNivel(this, n))
                    .onEmpty(() -> limpiarCelda(this));
            }
        });
    }

    /**
     * Configura una celda con la informacion de un nivel.
     *
     * @param celda La celda a configurar
     * @param nivel El nivel cuyos datos se mostraran
     */
    private void configurarCeldaConNivel(ListCell<Nivel> celda, Nivel nivel) {
        String estrellas = generarEstrellasDificultad(nivel.getDificultad());
        String creador = Option.of(nivel.getCreador())
            .filter(c -> !c.isEmpty())
            .getOrElse("Anonimo");

        int cantidadBloques = nivel.getBloques().size();

        VBox contenido = crearContenidoCelda(
            nivel.getNombre(),
            creador,
            estrellas,
            cantidadBloques
        );

        celda.setGraphic(contenido);
        celda.setText(null);
        celda.setStyle("-fx-padding: 8px;");
    }

    /**
     * Limpia el contenido de una celda vacia.
     *
     * @param celda La celda a limpiar
     */
    private void limpiarCelda(ListCell<Nivel> celda) {
        celda.setGraphic(null);
        celda.setText(null);
    }

    /**
     * Crea el contenido visual de una celda con la informacion del nivel.
     *
     * @param nombre Nombre del nivel
     * @param creador Creador del nivel
     * @param estrellas Representacion visual de la dificultad
     * @param cantidadBloques Numero de bloques en el nivel
     * @return VBox con el contenido formateado
     */
    private VBox crearContenidoCelda(String nombre, String creador, String estrellas, int cantidadBloques) {
        Text textoNombre = new Text(nombre);
        textoNombre.getStyleClass().add("texto-nivel-titulo");

        Text textoInfo = new Text(String.format(
            "Creador: %s | Dificultad: %s | Bloques: %d",
            creador,
            estrellas,
            cantidadBloques
        ));
        textoInfo.getStyleClass().add("texto-nivel-detalle");

        VBox contenedor = new VBox(3, textoNombre, textoInfo);
        contenedor.setStyle("-fx-padding: 2px;");

        return contenedor;
    }

    /**
     * Genera una representacion visual de la dificultad usando estrellas.
     *
     * @param dificultad Nivel de dificultad (1-3)
     * @return Cadena con estrellas representando la dificultad
     */
    private String generarEstrellasDificultad(int dificultad) {
        return io.vavr.collection.List.range(0, dificultad)
            .map(i -> "★")
            .mkString("");
    }

    /**
     * Carga los niveles personalizados desde la base de datos y los muestra en el ListView.
     * Muestra un mensaje de error si la carga falla.
     */
    private void cargarNiveles() {
        servicioPersistencia.cargarNivelesPersonalizados()
            .peek(niveles -> Platform.runLater(() ->
                listaNiveles.getItems().setAll(niveles)
            ))
            .onFailure(error -> Platform.runLater(() ->
                mostrarError("Error al cargar niveles", error.getMessage())
            ));
    }

    /**
     * Configura el estado del boton de carga basandose en la seleccion del usuario.
     * El boton solo se habilita cuando hay un nivel seleccionado.
     */
    private void configurarBotonCargar() {
        botonCargar.setDisable(true);
        listaNiveles.getSelectionModel().selectedItemProperty().addListener(
            (observable, anterior, nuevo) ->
                botonCargar.setDisable(nuevo == null)
        );
    }

    /**
     * Maneja el evento de carga del nivel seleccionado.
     * Cierra el dialogo con el nivel seleccionado.
     */
    @FXML
    private void manejarCargar() {
        Option.of(listaNiveles.getSelectionModel().getSelectedItem())
            .peek(nivel -> {
                nivelSeleccionado = Option.of(nivel);
                cerrarDialogo();
            });
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
     * Maneja el evento de eliminacion del nivel seleccionado.
     * Solicita confirmacion al usuario antes de eliminar el nivel de la base de datos.
     * Si la eliminacion es exitosa, recarga la lista de niveles.
     * 
     * @throws NullPointerException si no hay un nivel seleccionado
     */
    @FXML
    private void manejarEliminar() {
        Option.of(listaNiveles.getSelectionModel().getSelectedItem())
            .peek(nivel -> confirmarYEliminarNivel(nivel))
            .onEmpty(() -> mostrarError("Sin selección", "Por favor, selecciona un nivel para eliminar"));
    }

    /**
     * Solicita confirmacion al usuario y elimina el nivel si se confirma.
     * Recarga la lista de niveles tras una eliminacion exitosa.
     * 
     * @param nivel El nivel a eliminar
     */
    private void confirmarYEliminarNivel(Nivel nivel) {
        crearDialogoConfirmacion(nivel.getNombre())
            .filter(confirmado -> confirmado)
            .peek(__ -> eliminarNivelDeLaBaseDeDatos(nivel.getId()));
    }

    /**
     * Crea un dialogo de confirmacion para eliminar un nivel.
     * 
     * @param nombreNivel El nombre del nivel a eliminar
     * @return Option conteniendo true si el usuario confirma, false en caso contrario
     */
    private Option<Boolean> crearDialogoConfirmacion(String nombreNivel) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar nivel?");
        confirmacion.setContentText(String.format(
            "¿Estás seguro de que deseas eliminar el nivel '%s'?\nEsta acción no se puede deshacer.",
            nombreNivel
        ));
        
        return Option.of(confirmacion.showAndWait())
            .flatMap(resultado -> Option.of(resultado.orElse(null)))
            .map(botonPresionado -> botonPresionado == javafx.scene.control.ButtonType.OK);
    }

    /**
     * Elimina un nivel de la base de datos usando su identificador.
     * Recarga la lista de niveles si la eliminacion es exitosa.
     * Muestra un mensaje de error si la eliminacion falla.
     * 
     * @param idNivel El identificador del nivel a eliminar
     */
    private void eliminarNivelDeLaBaseDeDatos(String idNivel) {
        servicioPersistencia.eliminarNivel(idNivel)
            .peek(__ -> Platform.runLater(this::cargarNiveles))
            .onFailure(error -> Platform.runLater(() ->
                mostrarError("Error al eliminar",
                    String.format("No se pudo eliminar el nivel: %s", error.getMessage()))
            ));
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
     * @param titulo Titulo del mensaje de error
     * @param mensaje Contenido del mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
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
