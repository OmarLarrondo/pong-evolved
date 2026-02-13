package mvc.controlador;

import io.vavr.control.Option;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import util.CargadorRecursos;
import util.GestorVideos;
import mvc.vista.GestorEscenas;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Controlador del menú principal del juego.
 * Gestiona los eventos de los botones y la reproducción de videos preview.
 * Implementa el componente Controlador del patrón MVC.
 * Extiende de ControladorBase para heredar funcionalidad común de atajos de teclado.
 */
public class ControladorMenu extends ControladorBase {

    private static final String DIRECTORIO_VIDEOS = "videos/";
    private static final String DIRECTORIO_IMAGENES = "imagenes/";
    private static final String VIDEO_IDLE = "preview-idle.mp4";
    private static final String VIDEO_UN_JUGADOR = "preview-1jugador.mp4";
    private static final String VIDEO_DOS_JUGADORES = "preview-2jugadores.mp4";
    private static final String VIDEO_CONSTRUCTOR = "preview-constructor.mp4";
    private static final String IMAGEN_IDLE = "preview-idle.png";
    private static final String IMAGEN_UN_JUGADOR = "preview-1jugador.png";
    private static final String IMAGEN_DOS_JUGADORES = "preview-2jugadores.png";
    private static final String IMAGEN_CONSTRUCTOR = "preview-constructor.png";

    @FXML
    private Button botonUnJugador;

    @FXML
    private Button botonDosJugadores;

    @FXML
    private Button botonModoConstructor;

    @FXML
    private Button botonPantallaCompleta;

    /** Boton para alternar el mute del audio. */
    @FXML
    private Button botonMute;

    @FXML
    private MediaView mediaView;

    @FXML
    private ImageView imageView;

    private Map<String, MediaPlayer> reproductores;
    private Map<String, Image> imagenes;
    private MediaPlayer reproductorActual;
    private String imagenActual;
    private boolean usarVideos;

    /**
     * Constructor por defecto requerido por FXML.
     */
    public ControladorMenu() {
        this.reproductores = new HashMap<>();
        this.imagenes = new HashMap<>();
        this.usarVideos = true;
    }

    /**
     * Inicializa el controlador después de cargar el FXML.
     * Intenta cargar videos, si fallan usa imágenes de fallback.
     */
    @FXML
    @Override
    public void initialize() {
        cargarTodosLosVideos();
        determinarModoVisualizacion();
        iniciarVisualizacionInicial();
        configurarAtajosTecladoPantallaCompleta(mediaView);
        configurarBindingsResponsivos();
    }

    /**
     * Configura bindings responsivos para MediaView e ImageView.
     * Escala los elementos proporcionalmente con el tamaño de la ventana.
     */
    private void configurarBindingsResponsivos() {
        mediaView.sceneProperty().addListener((observable, oldScene, newScene) ->
            Optional.ofNullable(newScene)
                    .ifPresent(this::aplicarBindingsEscalado)
        );
    }

    /**
     * Aplica los bindings de escalado a las vistas de media e imagen.
     *
     * @param escena Scene de referencia para los bindings
     */
    private void aplicarBindingsEscalado(javafx.scene.Scene escena) {
        vincularDimensionesMediaView(escena);
        vincularDimensionesImageView(escena);
    }

    /**
     * Vincula las dimensiones del MediaView con la escena.
     * Usa un factor de escala del 40% del ancho y 45% del alto de la escena.
     *
     * @param escena Scene de referencia
     */
    private void vincularDimensionesMediaView(javafx.scene.Scene escena) {
        mediaView.fitWidthProperty().bind(
            escena.widthProperty().multiply(0.40)
        );
        mediaView.fitHeightProperty().bind(
            escena.heightProperty().multiply(0.45)
        );
    }

    /**
     * Vincula las dimensiones del ImageView con la escena.
     * Usa un factor de escala del 40% del ancho y 45% del alto de la escena.
     *
     * @param escena Scene de referencia
     */
    private void vincularDimensionesImageView(javafx.scene.Scene escena) {
        imageView.fitWidthProperty().bind(
            escena.widthProperty().multiply(0.40)
        );
        imageView.fitHeightProperty().bind(
            escena.heightProperty().multiply(0.45)
        );
    }

    /**
     * Carga todos los videos necesarios para el menú.
     * Videos que fallen al cargar serán omitidos del mapa.
     */
    private void cargarTodosLosVideos() {
        reproductores = new HashMap<>();
        obtenerNombresVideos().forEach(nombreVideo -> {
            MediaPlayer player = crearReproductorParaVideo(nombreVideo);
            if (player != null) {
                reproductores.put(nombreVideo, player);
            }
        });
    }

    /**
     * Obtiene la lista de nombres de todos los videos a cargar.
     *
     * @return Lista con los nombres de los archivos de video
     */
    private List<String> obtenerNombresVideos() {
        return Arrays.asList(
                VIDEO_IDLE,
                VIDEO_UN_JUGADOR,
                VIDEO_DOS_JUGADORES,
                VIDEO_CONSTRUCTOR
        );
    }

    /**
     * Crea un MediaPlayer para un video específico.
     *
     * @param nombreVideo Nombre del archivo de video
     * @return MediaPlayer configurado
     */
    private MediaPlayer crearReproductorParaVideo(String nombreVideo) {
        return CargadorRecursos.cargarVideo(DIRECTORIO_VIDEOS + nombreVideo)
                .map(GestorVideos::crearReproductor)
                .orElseGet(this::crearReproductorVacio);
    }

    /**
     * Crea un reproductor vacío como fallback.
     *
     * @return MediaPlayer vacío
     */
    private MediaPlayer crearReproductorVacio() {
        return null;
    }

    /**
     * Inicia la reproducción del video idle por defecto.
     */
    private void iniciarVideoIdle() {
        Optional.ofNullable(reproductores.get(VIDEO_IDLE))
                .ifPresent(reproductor -> {
                    reproductorActual = reproductor;
                    GestorVideos.reproducirVideo(reproductor, mediaView);
                });
    }

    /**
     * Determina si se usarán videos o imágenes de fallback.
     * Si no hay reproductores disponibles, carga las imágenes.
     */
    private void determinarModoVisualizacion() {
        usarVideos = !reproductores.isEmpty();

        if (!usarVideos) {
            System.out.println("Videos no disponibles. Usando imágenes de fallback.");
            cargarTodasLasImagenes();
        }

        configurarVistas();
    }

    /**
     * Carga todas las imágenes de fallback necesarias para el menú.
     */
    private void cargarTodasLasImagenes() {
        imagenes = new HashMap<>();
        obtenerNombresImagenes().forEach((clave, rutaImagen) ->
            CargadorRecursos.cargarImagen(DIRECTORIO_IMAGENES + rutaImagen)
                    .ifPresent(imagen -> imagenes.put(clave, imagen))
        );
    }

    /**
     * Obtiene el mapeo de claves a nombres de archivos de imágenes.
     *
     * @return Map con claves de video mapeadas a nombres de imágenes
     */
    private Map<String, String> obtenerNombresImagenes() {
        return Map.of(
                VIDEO_IDLE, IMAGEN_IDLE,
                VIDEO_UN_JUGADOR, IMAGEN_UN_JUGADOR,
                VIDEO_DOS_JUGADORES, IMAGEN_DOS_JUGADORES,
                VIDEO_CONSTRUCTOR, IMAGEN_CONSTRUCTOR
        );
    }

    /**
     * Configura la visibilidad de las vistas según el modo activo.
     */
    private void configurarVistas() {
        mediaView.setVisible(usarVideos);
        imageView.setVisible(!usarVideos);
    }

    /**
     * Inicia la visualización inicial (video o imagen idle).
     */
    private void iniciarVisualizacionInicial() {
        if (usarVideos) {
            iniciarVideoIdle();
        } else {
            mostrarImagen(VIDEO_IDLE);
        }
    }

    /**
     * Muestra una imagen en el ImageView.
     *
     * @param claveImagen Clave de la imagen a mostrar
     */
    private void mostrarImagen(String claveImagen) {
        Optional.ofNullable(imagenes.get(claveImagen))
                .ifPresent(imagen -> {
                    imageView.setImage(imagen);
                    imagenActual = claveImagen;
                });
    }

    /**
     * Maneja el evento de clic en el botón "1 jugador".
     *
     * @param evento Evento de acción
     */
    @FXML
    private void accionUnJugador(ActionEvent evento) {
        System.out.println("Navegando a seleccion de dificultad...");
        Optional.ofNullable(gestorEscenas)
                .ifPresent(gestor -> gestor.mostrarSeleccionDificultad());
    }

    /**
     * Maneja el evento de clic en el boton "2 jugadores".
     * Navega directamente a seleccion de niveles sin configurar dificultad IA.
     * Limpia cualquier dificultad previamente configurada usando programacion funcional.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionDosJugadores(ActionEvent evento) {
        System.out.println("Navegando a seleccion de niveles (2 jugadores)...");
        Optional.ofNullable(gestorEscenas)
                .ifPresent(gestor -> gestor.mostrarSeleccionNivelesConDificultad(Option.none()));
    }

    /**
     * Maneja el evento de clic en el botón "Modo constructor".
     *
     * @param evento Evento de acción
     */
    @FXML
    private void accionModoConstructor(ActionEvent evento) {
        System.out.println("Iniciando modo constructor...");
        Optional.ofNullable(gestorEscenas)
                .ifPresent(gestor -> gestor.mostrarEditor());
    }

    /**
     * Maneja el evento de clic en el botón "Acerca de".
     * Muestra un diálogo modal con información del equipo de desarrollo.
     *
     * @param evento Evento de acción
     */
    @FXML
    private void accionAcercaDe(ActionEvent evento) {
        crearDialogoAcercaDe()
                .ifPresent(Alert::showAndWait);
    }

    /**
     * Crea el diálogo "Acerca de" con información del equipo.
     * Función pura que construye el diálogo sin efectos secundarios.
     *
     * @return Optional con el Alert configurado, o vacío si falla
     */
    private Optional<Alert> crearDialogoAcercaDe() {
        return Optional.of(new Alert(Alert.AlertType.NONE))
                .map(this::configurarDialogoBasico)
                .map(this::aplicarContenidoEquipo)
                .map(this::aplicarBotonCerrar)
                .map(this::aplicarEstilosRetro);
    }

    /**
     * Configura las propiedades básicas del diálogo.
     *
     * @param dialogo Alert a configurar
     * @return Alert configurado
     */
    private Alert configurarDialogoBasico(Alert dialogo) {
        dialogo.setTitle("Acerca de");
        dialogo.setHeaderText(null);
        dialogo.getButtonTypes().clear();
        return dialogo;
    }

    /**
     * Aplica el contenido de información del equipo al diálogo.
     *
     * @param dialogo Alert a modificar
     * @return Alert con contenido aplicado
     */
    private Alert aplicarContenidoEquipo(Alert dialogo) {
        VBox contenido = crearContenidoEquipo();
        dialogo.getDialogPane().setContent(contenido);
        return dialogo;
    }

    /**
     * Crea el contenido visual con información del equipo.
     * Función pura que construye el VBox con título e integrantes.
     *
     * @return VBox con el contenido estructurado
     */
    private VBox crearContenidoEquipo() {
        return configurarContenedor(
                crearSeccionTitulo(),
                crearListaIntegrantes()
        );
    }

    /**
     * Configura un contenedor VBox con los elementos proporcionados.
     *
     * @param seccionTitulo VBox con la sección de título
     * @param listaIntegrantes VBox con los integrantes
     * @return VBox configurado
     */
    private VBox configurarContenedor(VBox seccionTitulo, VBox listaIntegrantes) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.getChildren().addAll(seccionTitulo, listaIntegrantes);
        return contenedor;
    }

    /**
     * Crea la sección de título completa con "Desarrollado por:" y nombre del equipo.
     *
     * @return VBox con ambos labels
     */
    private VBox crearSeccionTitulo() {
        VBox seccionTitulo = new VBox(10);
        seccionTitulo.setAlignment(Pos.CENTER);
        seccionTitulo.getChildren().addAll(
                crearLabelDesarrolladoPor(),
                crearTituloEquipo()
        );
        return seccionTitulo;
    }

    /**
     * Crea el Label "Desarrollado por:".
     *
     * @return Label configurado
     */
    private Label crearLabelDesarrolladoPor() {
        return crearLabel("Desarrollado por:", "dialogo-texto-integrante");
    }

    /**
     * Crea el Label con el título del equipo.
     *
     * @return Label configurado con el nombre del equipo
     */
    private Label crearTituloEquipo() {
        return crearLabel("Equipo Polimorfo", "dialogo-titulo-equipo");
    }

    /**
     * Crea un VBox con la lista de integrantes del equipo.
     *
     * @return VBox con Labels de cada integrante
     */
    private VBox crearListaIntegrantes() {
        VBox listaIntegrantes = new VBox(10);
        listaIntegrantes.setAlignment(Pos.CENTER);

        obtenerNombresIntegrantes()
                .stream()
                .map(this::crearLabelIntegrante)
                .forEach(listaIntegrantes.getChildren()::add);

        return listaIntegrantes;
    }

    /**
     * Obtiene la lista de nombres de los integrantes del equipo.
     *
     * @return Lista con los nombres completos de los integrantes
     */
    private List<String> obtenerNombresIntegrantes() {
        return Arrays.asList(
                "Juarez Larrondo Omar Alejandro",
                "Soto Mendoza Ismael de Jesus",
                "Vega Navas Saul"
        );
    }

    /**
     * Crea un Label para un integrante del equipo.
     *
     * @param nombre Nombre del integrante
     * @return Label configurado
     */
    private Label crearLabelIntegrante(String nombre) {
        return crearLabel(nombre, "dialogo-texto-integrante");
    }

    /**
     * Crea un Label con texto y clase de estilo.
     * Función pura para construcción de Labels.
     *
     * @param texto Texto del Label
     * @param claseEstilo Clase CSS a aplicar
     * @return Label configurado
     */
    private Label crearLabel(String texto, String claseEstilo) {
        Label label = new Label(texto);
        label.getStyleClass().add(claseEstilo);
        return label;
    }

    /**
     * Aplica el botón de cerrar personalizado al diálogo.
     *
     * @param dialogo Alert a modificar
     * @return Alert con botón aplicado
     */
    private Alert aplicarBotonCerrar(Alert dialogo) {
        ButtonType botonCerrar = new ButtonType("Volver");
        dialogo.getButtonTypes().add(botonCerrar);

        obtenerDialogPane(dialogo)
                .flatMap(this::obtenerBotonCerrar)
                .ifPresent(this::configurarBotonConIcono);

        return dialogo;
    }

    /**
     * Obtiene el DialogPane de un Alert.
     *
     * @param dialogo Alert del que extraer el DialogPane
     * @return Optional con el DialogPane
     */
    private Optional<DialogPane> obtenerDialogPane(Alert dialogo) {
        return Optional.ofNullable(dialogo.getDialogPane());
    }

    /**
     * Obtiene el botón de cerrar del DialogPane.
     *
     * @param dialogPane DialogPane del que extraer el botón
     * @return Optional con el Button
     */
    private Optional<Button> obtenerBotonCerrar(DialogPane dialogPane) {
        return dialogPane.getButtonTypes()
                .stream()
                .findFirst()
                .map(dialogPane::lookupButton)
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node);
    }

    /**
     * Configura un botón con icono y estilos personalizados.
     *
     * @param boton Button a configurar
     */
    private void configurarBotonConIcono(Button boton) {
        aplicarIconoReturnAlBoton(boton);
        aplicarEstilosAlBoton(boton);
    }

    /**
     * Aplica el icono de return al botón.
     *
     * @param boton Button a modificar
     */
    private void aplicarIconoReturnAlBoton(Button boton) {
        FontIcon icono = crearIconoReturn();
        boton.setGraphic(icono);
    }

    /**
     * Crea el icono de return usando Ikonli.
     *
     * @return FontIcon configurado
     */
    private FontIcon crearIconoReturn() {
        FontIcon icono = new FontIcon(FontAwesomeSolid.REPLY);
        icono.getStyleClass().add("icono-return");
        return icono;
    }

    /**
     * Aplica las clases de estilo al botón.
     *
     * @param boton Button a modificar
     */
    private void aplicarEstilosAlBoton(Button boton) {
        boton.getStyleClass().clear();
        boton.getStyleClass().add("boton-cerrar-dialogo");
    }

    /**
     * Aplica los estilos CSS retro al diálogo completo.
     *
     * @param dialogo Alert a estilizar
     * @return Alert estilizado
     */
    private Alert aplicarEstilosRetro(Alert dialogo) {
        obtenerDialogPane(dialogo)
                .ifPresent(this::aplicarEstilosADialogPane);
        return dialogo;
    }

    /**
     * Aplica las clases de estilo CSS al DialogPane.
     *
     * @param dialogPane DialogPane a estilizar
     */
    private void aplicarEstilosADialogPane(DialogPane dialogPane) {
        dialogPane.getStyleClass().add("dialogo-acerca-de");
        aplicarHojaEstilos(dialogPane);
    }

    /**
     * Aplica la hoja de estilos retro al DialogPane.
     *
     * @param dialogPane DialogPane a modificar
     */
    private void aplicarHojaEstilos(DialogPane dialogPane) {
        CargadorRecursos.obtenerRutaCSS("css/estilo-retro.css")
                .map(Object::toString)
                .ifPresent(rutaCss ->
                        dialogPane.getStylesheets().add(rutaCss)
                );
    }

    /**
     * Maneja el evento de clic en el botón "Pantalla Completa".
     * Alterna entre modo ventana y pantalla completa.
     *
     * @param evento Evento de acción
     */
    @FXML
    private void accionPantallaCompleta(ActionEvent evento) {
        Optional.ofNullable(gestorEscenas)
                .ifPresent(GestorEscenas::alternarPantallaCompleta);
    }

    /**
     * Maneja el evento de clic en el boton "Mute".
     * Alterna entre silenciar y activar el audio del juego.
     * Actualiza el icono del boton segun el estado actual.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionAlternarMute(ActionEvent evento) {
        patrones.observer.GestorAudio gestorAudio = patrones.observer.GestorAudio.obtenerInstancia();
        gestorAudio.alternarMute();
        actualizarIconoBotonMute(gestorAudio.estaSilenciado());
    }

    /**
     * Actualiza el icono del boton de mute segun el estado de silenciado.
     *
     * @param silenciado true si el audio esta silenciado, false en caso contrario
     */
    private void actualizarIconoBotonMute(boolean silenciado) {
        Optional.ofNullable(botonMute)
                .ifPresent(boton -> boton.setGraphic(crearIconoMute(silenciado)));
    }

    /**
     * Crea un icono de bocina para el boton de mute.
     * Retorna un icono de bocina normal o con linea de silenciado segun el estado.
     *
     * @param silenciado true si el audio esta silenciado, false en caso contrario
     * @return FontIcon con el icono apropiado
     */
    private FontIcon crearIconoMute(boolean silenciado) {
        FontIcon icono = new FontIcon(silenciado ? FontAwesomeSolid.VOLUME_MUTE : FontAwesomeSolid.VOLUME_UP);
        icono.setIconSize(24);
        return icono;
    }

    /**
     * Maneja el evento hover sobre el botón "1 jugador".
     */
    @FXML
    private void hoverUnJugador() {
        cambiarVideo(VIDEO_UN_JUGADOR);
    }

    /**
     * Maneja el evento hover sobre el botón "2 jugadores".
     */
    @FXML
    private void hoverDosJugadores() {
        cambiarVideo(VIDEO_DOS_JUGADORES);
    }

    /**
     * Maneja el evento hover sobre el botón "Modo constructor".
     */
    @FXML
    private void hoverModoConstructor() {
        cambiarVideo(VIDEO_CONSTRUCTOR);
    }

    /**
     * Maneja el evento de salida del hover de cualquier botón.
     */
    @FXML
    private void salirHover() {
        cambiarVideo(VIDEO_IDLE);
    }

    /**
     * Cambia el video o imagen actual según el modo activo.
     *
     * @param nombreRecurso Nombre del recurso (video o imagen) a mostrar
     */
    private void cambiarVideo(String nombreRecurso) {
        if (usarVideos) {
            cambiarVideoInterno(nombreRecurso);
        } else {
            cambiarImagenInterno(nombreRecurso);
        }
    }

    /**
     * Cambia el video actual por otro con transición fade.
     *
     * @param nombreVideo Nombre del video a reproducir
     */
    private void cambiarVideoInterno(String nombreVideo) {
        Optional.ofNullable(reproductores.get(nombreVideo))
                .filter(nuevoReproductor -> nuevoReproductor != reproductorActual)
                .ifPresent(nuevoReproductor -> {
                    GestorVideos.transicionarVideo(
                            reproductorActual,
                            nuevoReproductor,
                            mediaView
                    );
                    reproductorActual = nuevoReproductor;
                });
    }

    /**
     * Cambia la imagen actual por otra.
     *
     * @param claveImagen Clave de la imagen a mostrar
     */
    private void cambiarImagenInterno(String claveImagen) {
        if (!claveImagen.equals(imagenActual)) {
            mostrarImagen(claveImagen);
        }
    }

    /**
     * Reinicia la visualización al estado inicial (video o imagen idle).
     */
    public void reiniciarVideos() {
        if (usarVideos) {
            Optional.ofNullable(reproductorActual)
                    .ifPresent(GestorVideos::detenerVideo);
            iniciarVideoIdle();
        } else {
            mostrarImagen(VIDEO_IDLE);
        }
    }

    /**
     * Libera los recursos de todos los reproductores.
     */
    public void liberarRecursos() {
        reproductores.values()
                .forEach(GestorVideos::liberarRecursos);
        reproductores.clear();
    }
}
