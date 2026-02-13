package mvc.controlador;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import mvc.modelo.entidades.Bloque;
import patrones.builder.ConstructorMapa;
import patrones.builder.TipoBloque;
import mvc.modelo.entidades.Nivel;
import mvc.modelo.entidades.paleta.Paleta;
import mvc.vista.GestorEscenas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador del panel de modo constructor/editor de mapas.
 * Gestiona la interfaz para crear y editar niveles personalizados,
 * permitiendo colocar bloques y paletas en un canvas interactivo.
 * Implementa el componente Controlador del patron MVC.
 * Extiende de ControladorBase para heredar funcionalidad comun de atajos de teclado.
 */
public class ControladorEditor extends ControladorBase {

    private static final double ANCHO_CANVAS_BASE = 800.0;
    private static final double ALTO_CANVAS_BASE = 600.0;
    private static final double TAMANO_BLOQUE_ANCHO = 50.0;
    private static final double TAMANO_BLOQUE_ALTO = 20.0;
    private static final double TAMANO_GRID = 50.0;
    private static final double ANCHO_PALETA = 80.0;
    private static final double ALTO_PALETA = 15.0;
    private static final int MAX_PALETAS = 2;

    @FXML
    private StackPane contenedorCanvas;

    @FXML
    private Canvas canvasMapa;

    @FXML
    private Canvas canvasGrid;

    @FXML
    private TextField campoNombre;

    @FXML
    private TextField campoCreador;

    @FXML
    private Slider deslizadorDificultad;

    @FXML
    private Label etiquetaDificultad;

    @FXML
    private Label valorDificultad;

    @FXML
    private Label etiquetaBloques;

    @FXML
    private Button botonBloqueDestructible;

    @FXML
    private Button botonBloqueIndestructible;

    @FXML
    private Button botonBloqueBonus;

    @FXML
    private Button botonBloqueMulti;

    @FXML
    private Button botonBorrador;

    @FXML
    private Button botonToggleGrid;

    @FXML
    private Button botonCargar;

    @FXML
    private Button botonGuardar;

    @FXML
    private Button botonCancelar;

    private HerramientaEditor herramientaActual;
    private List<ObjetoMapa> objetosEnMapa;
    private Button botonHerramientaSeleccionado;
    private boolean gridVisible;
    private PauseTransition debounceRedibujarMapa;
    private PauseTransition debounceRedibujarGrid;
    private persistencia.ServicioPersistencia servicioPersistencia;

    /**
     * Constructor por defecto requerido por FXML.
     */
    public ControladorEditor() {
        this.herramientaActual = HerramientaEditor.NINGUNA;
        this.objetosEnMapa = new ArrayList<>();
        this.botonHerramientaSeleccionado = null;
        this.gridVisible = true;
        this.servicioPersistencia = new persistencia.ServicioPersistencia();
        inicializarDebouncers();
    }

    /**
     * Inicializa los debouncers para optimizar el redibujado.
     */
    private void inicializarDebouncers() {
        debounceRedibujarMapa = new PauseTransition(Duration.millis(50));
        debounceRedibujarMapa.setOnFinished(event -> redibujarTodo());

        debounceRedibujarGrid = new PauseTransition(Duration.millis(50));
        debounceRedibujarGrid.setOnFinished(event -> dibujarGrid());
    }

    /**
     * Inicializa el controlador despues de cargar el FXML.
     */
    @FXML
    @Override
    public void initialize() {
        configurarAtajosTecladoPantallaCompleta(contenedorCanvas);
        configurarCanvas();
        configurarListeners();
        configurarEventosCanvas();
        dibujarEstadoInicial();
    }

    /**
     * Configura las dimensiones del canvas para responsividad.
     */
    private void configurarCanvas() {
        aplicarBindingsDimensiones();
        configurarContextoGrafico();
    }

    /**
     * Aplica las dimensiones responsivas del canvas usando listeners controlados.
     * No usa bindings para evitar el feedback loop de crecimiento infinito.
     */
    private void aplicarBindingsDimensiones() {
        configurarDimensionesIniciales();
        configurarListenersRedimensionamiento();
    }

    /**
     * Configura las dimensiones iniciales de los canvas.
     */
    private void configurarDimensionesIniciales() {
        canvasMapa.setWidth(ANCHO_CANVAS_BASE);
        canvasMapa.setHeight(ALTO_CANVAS_BASE);
        canvasGrid.setWidth(ANCHO_CANVAS_BASE);
        canvasGrid.setHeight(ALTO_CANVAS_BASE);
    }

    /**
     * Configura listeners manuales para el redimensionamiento del canvas.
     * Incluye limites maximos y validacion de diferencia minima para evitar
     * redibujados innecesarios y prevenir el loop infinito de crecimiento.
     */
    private void configurarListenersRedimensionamiento() {
        contenedorCanvas.widthProperty().addListener((obs, oldVal, newVal) -> {
            double nuevoAncho = Math.min(newVal.doubleValue() - 40, ANCHO_CANVAS_BASE * 1.5);
            if (Math.abs(canvasMapa.getWidth() - nuevoAncho) > 1) {
                canvasMapa.setWidth(nuevoAncho);
                canvasGrid.setWidth(nuevoAncho);
            }
        });

        contenedorCanvas.heightProperty().addListener((obs, oldVal, newVal) -> {
            double nuevoAlto = Math.min(newVal.doubleValue() - 20, ALTO_CANVAS_BASE * 1.5);
            if (Math.abs(canvasMapa.getHeight() - nuevoAlto) > 1) {
                canvasMapa.setHeight(nuevoAlto);
                canvasGrid.setHeight(nuevoAlto);
            }
        });
    }

    /**
     * Configura el contexto grafico de ambos canvas.
     */
    private void configurarContextoGrafico() {
        configurarContextoCanvasMapa();
        configurarContextoCanvasGrid();
    }

    /**
     * Configura el contexto grafico del canvas principal del mapa.
     */
    private void configurarContextoCanvasMapa() {
        GraphicsContext gc = canvasMapa.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvasMapa.getWidth(), canvasMapa.getHeight());
    }

    /**
     * Configura el contexto grafico del canvas del grid.
     */
    private void configurarContextoCanvasGrid() {
        GraphicsContext gc = canvasGrid.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasGrid.getWidth(), canvasGrid.getHeight());
    }

    /**
     * Configura los listeners de los componentes de la interfaz.
     */
    private void configurarListeners() {
        configurarListenerDificultad();
        configurarListenersCanvas();
    }

    /**
     * Configura el listener del deslizador de dificultad.
     */
    private void configurarListenerDificultad() {
        deslizadorDificultad.valueProperty().addListener((obs, oldVal, newVal) ->
            actualizarVisualizacionDificultad(newVal.intValue())
        );
    }

    /**
     * Configura los listeners de los canvas para responsividad.
     */
    private void configurarListenersCanvas() {
        configurarListenersCanvasMapa();
        configurarListenersCanvasGrid();
    }

    /**
     * Configura los listeners del canvas principal del mapa con debouncing.
     * Usa un delay de 50ms para evitar redibujados excesivos durante resize.
     */
    private void configurarListenersCanvasMapa() {
        canvasMapa.widthProperty().addListener((obs, oldVal, newVal) -> {
            debounceRedibujarMapa.playFromStart();
        });
        canvasMapa.heightProperty().addListener((obs, oldVal, newVal) -> {
            debounceRedibujarMapa.playFromStart();
        });
    }

    /**
     * Configura los listeners del canvas del grid con debouncing.
     * Usa un delay de 50ms para evitar redibujados excesivos durante resize.
     */
    private void configurarListenersCanvasGrid() {
        canvasGrid.widthProperty().addListener((obs, oldVal, newVal) -> {
            debounceRedibujarGrid.playFromStart();
        });
        canvasGrid.heightProperty().addListener((obs, oldVal, newVal) -> {
            debounceRedibujarGrid.playFromStart();
        });
    }

    /**
     * Actualiza la visualizacion de la dificultad con estrellas.
     *
     * @param dificultad Nivel de dificultad (1-3)
     */
    private void actualizarVisualizacionDificultad(int dificultad) {
        String estrellas = generarEstrellas(dificultad);
        etiquetaDificultad.setText(estrellas);
        valorDificultad.setText(String.valueOf(dificultad));
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
     * Configura los eventos de clic en el canvas.
     */
    private void configurarEventosCanvas() {
        canvasMapa.setOnMouseClicked(this::manejarClicCanvas);
    }

    /**
     * Maneja el evento de clic en el canvas para colocar o eliminar objetos.
     * Solo procesa si hay una herramienta seleccionada.
     *
     * @param evento Evento del mouse
     */
    private void manejarClicCanvas(MouseEvent evento) {
        if (herramientaActual == HerramientaEditor.NINGUNA) {
            return;
        }

        double x = evento.getX();
        double y = evento.getY();
        double xAjustada = ajustarAGrid(x);
        double yAjustada = ajustarAGrid(y);

        procesarAccionHerramienta(xAjustada, yAjustada);
    }

    /**
     * Ajusta una coordenada al grid mas cercano.
     *
     * @param coordenada Coordenada a ajustar
     * @return Coordenada ajustada al grid
     */
    private double ajustarAGrid(double coordenada) {
        return Math.floor(coordenada / TAMANO_GRID) * TAMANO_GRID;
    }

    /**
     * Procesa la accion de la herramienta actual en las coordenadas especificadas.
     * Solo redibuja si realmente se realizo un cambio.
     *
     * @param x Coordenada X ajustada
     * @param y Coordenada Y ajustada
     */
    private void procesarAccionHerramienta(double x, double y) {
        boolean huboModificacion = false;

        if (herramientaActual == HerramientaEditor.BORRADOR) {
            huboModificacion = eliminarObjetoEnPosicion(x, y);
        } else if (herramientaActual != HerramientaEditor.NINGUNA) {
            huboModificacion = agregarObjetoSiEsValido(x, y);
        }

        if (huboModificacion) {
            redibujarTodo();
        }
    }

    /**
     * Elimina un objeto en la posicion especificada.
     *
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return true si se elimino un objeto, false en caso contrario
     */
    private boolean eliminarObjetoEnPosicion(double x, double y) {
        int tamanoAnterior = objetosEnMapa.size();
        objetosEnMapa = filtrarObjetosNoEnPosicion(x, y);
        boolean huboEliminacion = objetosEnMapa.size() < tamanoAnterior;

        if (huboEliminacion) {
            actualizarEstadisticas();
        }

        return huboEliminacion;
    }

    /**
     * Filtra los objetos que no estan en la posicion especificada.
     *
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return Lista de objetos filtrada
     */
    private List<ObjetoMapa> filtrarObjetosNoEnPosicion(double x, double y) {
        return objetosEnMapa.stream()
                .filter(obj -> !estaEnPosicion(obj, x, y))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Verifica si un objeto esta en la posicion especificada.
     *
     * @param objeto Objeto a verificar
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return true si el objeto esta en la posicion, false en caso contrario
     */
    private boolean estaEnPosicion(ObjetoMapa objeto, double x, double y) {
        return Math.abs(objeto.x - x) < 5 && Math.abs(objeto.y - y) < 5;
    }

    /**
     * Agrega un objeto si es valido en la posicion especificada.
     *
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return true si se agrego el objeto, false en caso contrario
     */
    private boolean agregarObjetoSiEsValido(double x, double y) {
        if (esValidoAgregarObjeto(x, y)) {
            ObjetoMapa nuevoObjeto = crearObjetoDesdeHerramienta(x, y);
            objetosEnMapa.add(nuevoObjeto);
            actualizarEstadisticas();
            return true;
        }
        return false;
    }

    /**
     * Verifica si es valido agregar un objeto en la posicion especificada.
     *
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return true si es valido, false en caso contrario
     */
    private boolean esValidoAgregarObjeto(double x, double y) {
        return !existeObjetoEnPosicion(x, y);
    }

    /**
     * Verifica si existe un objeto en la posicion especificada.
     *
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return true si existe un objeto, false en caso contrario
     */
    private boolean existeObjetoEnPosicion(double x, double y) {
        return objetosEnMapa.stream()
                .anyMatch(obj -> estaEnPosicion(obj, x, y));
    }

    /**
     * Crea un objeto desde la herramienta actual.
     *
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return Objeto creado
     */
    private ObjetoMapa crearObjetoDesdeHerramienta(double x, double y) {
        return switch (herramientaActual) {
            case BLOQUE_DESTRUCTIBLE -> new ObjetoMapa(x, y, TipoObjetoMapa.BLOQUE, TipoBloque.DESTRUCTIBLE);
            case BLOQUE_INDESTRUCTIBLE -> new ObjetoMapa(x, y, TipoObjetoMapa.BLOQUE, TipoBloque.INDESTRUCTIBLE);
            case BLOQUE_BONUS -> new ObjetoMapa(x, y, TipoObjetoMapa.BLOQUE, TipoBloque.BONUS);
            case BLOQUE_MULTI -> new ObjetoMapa(x, y, TipoObjetoMapa.BLOQUE, TipoBloque.MULTI_GOLPE);
            default -> throw new IllegalStateException("Herramienta no soportada: " + herramientaActual);
        };
    }

    /**
     * Actualiza las estadisticas mostradas en el panel de propiedades.
     */
    private void actualizarEstadisticas() {
        long numBloques = contarBloques();
        etiquetaBloques.setText("Bloques: " + numBloques);
    }

    /**
     * Cuenta el numero de bloques en el mapa.
     *
     * @return Numero de bloques
     */
    private long contarBloques() {
        return objetosEnMapa.stream()
                .filter(obj -> obj.tipo == TipoObjetoMapa.BLOQUE)
                .count();
    }

    /**
     * Dibuja el estado inicial del canvas.
     */
    private void dibujarEstadoInicial() {
        limpiarCanvas();
        dibujarGrid();
    }

    /**
     * Redibuja todo el contenido del canvas principal.
     * El grid se maneja en su propia capa y no se redibuja aqui.
     */
    private void redibujarTodo() {
        limpiarCanvas();
        dibujarObjetos();
    }

    /**
     * Limpia el canvas completamente.
     */
    private void limpiarCanvas() {
        GraphicsContext gc = canvasMapa.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvasMapa.getWidth(), canvasMapa.getHeight());
    }

    /**
     * Dibuja el grid en el canvas dedicado.
     */
    private void dibujarGrid() {
        GraphicsContext gc = canvasGrid.getGraphicsContext2D();
        limpiarCanvasGrid(gc);

        if (gridVisible) {
            gc.setStroke(Color.rgb(255, 255, 255, 0.2));
            gc.setLineWidth(1);
            dibujarLineasVerticalesGrid(gc);
            dibujarLineasHorizontalesGrid(gc);
        }
    }

    /**
     * Limpia completamente el canvas del grid.
     *
     * @param gc Contexto grafico del canvas del grid
     */
    private void limpiarCanvasGrid(GraphicsContext gc) {
        gc.clearRect(0, 0, canvasGrid.getWidth(), canvasGrid.getHeight());
    }

    /**
     * Dibuja las lineas verticales del grid usando bucles optimizados.
     *
     * @param gc Contexto grafico del canvas
     */
    private void dibujarLineasVerticalesGrid(GraphicsContext gc) {
        double ancho = canvasGrid.getWidth();
        double alto = canvasGrid.getHeight();

        for (double x = 0; x <= ancho; x += TAMANO_GRID) {
            gc.strokeLine(x, 0, x, alto);
        }
    }

    /**
     * Dibuja las lineas horizontales del grid usando bucles optimizados.
     *
     * @param gc Contexto grafico del canvas
     */
    private void dibujarLineasHorizontalesGrid(GraphicsContext gc) {
        double ancho = canvasGrid.getWidth();
        double alto = canvasGrid.getHeight();

        for (double y = 0; y <= alto; y += TAMANO_GRID) {
            gc.strokeLine(0, y, ancho, y);
        }
    }

    /**
     * Dibuja todos los objetos en el canvas.
     */
    private void dibujarObjetos() {
        GraphicsContext gc = canvasMapa.getGraphicsContext2D();
        dibujarPaletasFijas(gc);
        objetosEnMapa.forEach(obj -> dibujarObjeto(gc, obj));
    }

    /**
     * Dibuja las paletas fijas en posiciones predeterminadas.
     * Las paletas se muestran semi-transparentes para indicar que son automaticas.
     *
     * @param gc Contexto grafico del canvas
     */
    private void dibujarPaletasFijas(GraphicsContext gc) {
        double anchoReal = canvasMapa.getWidth();
        double altoReal = canvasMapa.getHeight();
        double centroY = (altoReal / 2.0) - (ANCHO_PALETA / 2.0);
        double paletaIzqX = 5.0;
        double paletaDerX = anchoReal - ALTO_PALETA - 5.0;

        gc.setGlobalAlpha(0.3);
        gc.setFill(Color.WHITE);
        gc.fillRect(paletaIzqX, centroY, ALTO_PALETA, ANCHO_PALETA);
        gc.fillRect(paletaDerX, centroY, ALTO_PALETA, ANCHO_PALETA);
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);
        gc.strokeRect(paletaIzqX, centroY, ALTO_PALETA, ANCHO_PALETA);
        gc.strokeRect(paletaDerX, centroY, ALTO_PALETA, ANCHO_PALETA);
        gc.setGlobalAlpha(1.0);
    }

    /**
     * Dibuja un objeto individual en el canvas.
     *
     * @param gc Contexto grafico del canvas
     * @param objeto Objeto a dibujar
     */
    private void dibujarObjeto(GraphicsContext gc, ObjetoMapa objeto) {
        if (objeto.tipo == TipoObjetoMapa.BLOQUE) {
            dibujarBloque(gc, objeto);
        } else if (objeto.tipo == TipoObjetoMapa.PALETA) {
            dibujarPaleta(gc, objeto);
        }
    }

    /**
     * Dibuja un bloque en el canvas.
     *
     * @param gc Contexto grafico del canvas
     * @param objeto Objeto bloque a dibujar
     */
    private void dibujarBloque(GraphicsContext gc, ObjetoMapa objeto) {
        Color color = obtenerColorBloque(objeto.tipoBloque);
        gc.setFill(color);
        gc.fillRect(objeto.x, objeto.y, TAMANO_BLOQUE_ANCHO, TAMANO_BLOQUE_ALTO);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(objeto.x, objeto.y, TAMANO_BLOQUE_ANCHO, TAMANO_BLOQUE_ALTO);
    }

    /**
     * Obtiene el color correspondiente a un tipo de bloque.
     *
     * @param tipo Tipo de bloque
     * @return Color del bloque
     */
    private Color obtenerColorBloque(TipoBloque tipo) {
        return switch (tipo) {
            case DESTRUCTIBLE -> Color.WHITE;
            case INDESTRUCTIBLE -> Color.rgb(100, 100, 100);
            case BONUS -> Color.rgb(255, 215, 0);
            case MULTI_GOLPE -> Color.rgb(0, 191, 255);
        };
    }

    /**
     * Dibuja una paleta en el canvas.
     *
     * @param gc Contexto grafico del canvas
     * @param objeto Objeto paleta a dibujar
     */
    private void dibujarPaleta(GraphicsContext gc, ObjetoMapa objeto) {
        gc.setFill(Color.WHITE);
        gc.fillRect(objeto.x, objeto.y, ANCHO_PALETA, ALTO_PALETA);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(objeto.x, objeto.y, ANCHO_PALETA, ALTO_PALETA);
    }

    /**
     * Maneja la seleccion de la herramienta bloque destructible.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionSeleccionarBloqueDestructible(ActionEvent evento) {
        seleccionarHerramienta(HerramientaEditor.BLOQUE_DESTRUCTIBLE, botonBloqueDestructible);
    }

    /**
     * Maneja la seleccion de la herramienta bloque indestructible.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionSeleccionarBloqueIndestructible(ActionEvent evento) {
        seleccionarHerramienta(HerramientaEditor.BLOQUE_INDESTRUCTIBLE, botonBloqueIndestructible);
    }

    /**
     * Maneja la seleccion de la herramienta bloque bonus.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionSeleccionarBloqueBonus(ActionEvent evento) {
        seleccionarHerramienta(HerramientaEditor.BLOQUE_BONUS, botonBloqueBonus);
    }

    /**
     * Maneja la seleccion de la herramienta bloque multi-golpe.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionSeleccionarBloqueMulti(ActionEvent evento) {
        seleccionarHerramienta(HerramientaEditor.BLOQUE_MULTI, botonBloqueMulti);
    }

    /**
     * Maneja la seleccion de la herramienta borrador.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionSeleccionarBorrador(ActionEvent evento) {
        seleccionarHerramienta(HerramientaEditor.BORRADOR, botonBorrador);
    }

    /**
     * Selecciona una herramienta y actualiza el estado visual de los botones.
     *
     * @param herramienta Herramienta a seleccionar
     * @param boton Boton asociado a la herramienta
     */
    private void seleccionarHerramienta(HerramientaEditor herramienta, Button boton) {
        limpiarSeleccionHerramientaPrevia();
        herramientaActual = herramienta;
        botonHerramientaSeleccionado = boton;
        resaltarBotonHerramienta(boton);
    }

    /**
     * Limpia el resaltado de la herramienta previamente seleccionada.
     */
    private void limpiarSeleccionHerramientaPrevia() {
        if (botonHerramientaSeleccionado != null) {
            botonHerramientaSeleccionado.getStyleClass().remove("boton-herramienta-seleccionado");
        }
    }

    /**
     * Resalta el boton de herramienta seleccionado.
     *
     * @param boton Boton a resaltar
     */
    private void resaltarBotonHerramienta(Button boton) {
        boton.getStyleClass().add("boton-herramienta-seleccionado");
    }

    /**
     * Maneja la accion de alternar la visibilidad del grid.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionToggleGrid(ActionEvent evento) {
        gridVisible = !gridVisible;
        dibujarGrid();
    }

    /**
     * Maneja la accion de cargar un nivel existente desde la base de datos.
     * Muestra un dialogo modal para seleccionar un nivel guardado y lo carga en el editor.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionCargarNivel(ActionEvent evento) {
        io.vavr.control.Try.of(() -> util.CargadorRecursos.cargarFXMLLoader("fxml/dialogo-cargar-nivel.fxml"))
            .filter(java.util.Objects::nonNull)
            .flatMap(this::crearYMostrarDialogoCargar)
            .peek(this::cargarNivelEnEditor)
            .onFailure(error -> mostrarErrorValidacion(
                "Error al cargar nivel",
                "No se pudo abrir el dialogo de carga: " + error.getMessage()
            ));
    }

    /**
     * Crea y muestra el dialogo de carga de niveles.
     *
     * @param loader FXMLLoader configurado con el FXML del dialogo
     * @return Try con el nivel seleccionado, o vacio si se cancelo
     */
    private io.vavr.control.Try<Nivel> crearYMostrarDialogoCargar(javafx.fxml.FXMLLoader loader) {
        return io.vavr.control.Try.of(() -> {
            javafx.scene.Parent root = loader.load();
            ControladorDialogoCargarNivel controlador = loader.getController();

            javafx.stage.Stage dialogo = new javafx.stage.Stage();
            dialogo.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogo.initOwner(botonCargar.getScene().getWindow());
            dialogo.setTitle("Cargar Nivel");
            dialogo.setResizable(false);

            javafx.scene.Scene escena = new javafx.scene.Scene(root);
            util.CargadorRecursos.obtenerRutaCSS("css/estilo-retro.css")
                .ifPresent(ruta -> escena.getStylesheets().add(ruta));

            dialogo.setScene(escena);
            dialogo.showAndWait();

            return controlador.obtenerNivelSeleccionado()
                .getOrElseThrow(() -> new RuntimeException("No se selecciono ningun nivel"));
        });
    }

    /**
     * Carga un nivel en el editor, actualizando todos los campos y objetos del mapa.
     *
     * @param nivel Nivel a cargar
     */
    private void cargarNivelEnEditor(Nivel nivel) {
        limpiarEditor();
        actualizarCamposDesdeNivel(nivel);
        convertirYAgregarBloques(nivel);
        redibujarCanvas();
    }

    /**
     * Limpia el estado actual del editor.
     */
    private void limpiarEditor() {
        objetosEnMapa.clear();
    }

    /**
     * Actualiza los campos del formulario con los datos del nivel.
     *
     * @param nivel Nivel cuyos datos se mostraran
     */
    private void actualizarCamposDesdeNivel(Nivel nivel) {
        campoNombre.setText(nivel.getNombre());
        deslizadorDificultad.setValue(nivel.getDificultad());
        io.vavr.control.Option.of(nivel.getCreador())
            .filter(creador -> !creador.isEmpty())
            .peek(campoCreador::setText)
            .onEmpty(() -> campoCreador.setText(""));
    }

    /**
     * Convierte los bloques del nivel a objetos del mapa y los agrega a la lista.
     *
     * @param nivel Nivel con los bloques a convertir
     */
    private void convertirYAgregarBloques(Nivel nivel) {
        io.vavr.collection.List.ofAll(nivel.getBloques())
            .map(this::convertirBloqueAObjetoMapa)
            .forEach(objetosEnMapa::add);

        etiquetaBloques.setText("Bloques: " + objetosEnMapa.size());
    }

    /**
     * Convierte un bloque del dominio a un objeto del mapa del editor.
     *
     * @param bloque Bloque a convertir
     * @return ObjetoMapa equivalente
     */
    private ObjetoMapa convertirBloqueAObjetoMapa(Bloque bloque) {
        return new ObjetoMapa(
            bloque.obtenerX(),
            bloque.obtenerY(),
            TipoObjetoMapa.BLOQUE,
            bloque.obtenerTipo()
        );
    }

    /**
     * Redibuja el canvas completo con los objetos actuales.
     */
    private void redibujarCanvas() {
        limpiarCanvas();
        GraphicsContext gc = canvasMapa.getGraphicsContext2D();
        gc.setFill(Color.web("#1a1a2e"));
        gc.fillRect(0, 0, canvasMapa.getWidth(), canvasMapa.getHeight());
        dibujarPaletasFijas(gc);
        dibujarObjetos();
    }

    /**
     * Maneja la accion de guardar el mapa en la base de datos.
     * Valida los datos del formulario, construye el nivel y lo persiste.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionGuardarMapa(ActionEvent evento) {
        if (!validarDatosGuardado()) {
            return;
        }

        Nivel nivel = construirNivel();
        configurarNivelComoPersonalizado(nivel);

        servicioPersistencia.guardarNivel(nivel)
            .onSuccess(id -> mostrarExitoGuardado(nivel.getNombre()))
            .onFailure(error -> mostrarErrorGuardado(error.getMessage()));
    }

    /**
     * Valida que los datos del formulario sean correctos para guardar.
     *
     * @return true si los datos son validos, false en caso contrario
     */
    private boolean validarDatosGuardado() {
        if (!validarNivel()) {
            return false;
        }

        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarErrorValidacion("Datos Incompletos", "Debe especificar un nombre para el nivel.");
            return false;
        }

        String creador = campoCreador.getText().trim();
        if (creador.isEmpty()) {
            mostrarErrorValidacion("Datos Incompletos", "Debe especificar el nombre del creador.");
            return false;
        }

        return true;
    }

    /**
     * Configura el nivel como personalizado y establece el creador.
     *
     * @param nivel nivel a configurar
     */
    private void configurarNivelComoPersonalizado(Nivel nivel) {
        nivel.setMapaPersonalizado(true);
        nivel.setCreador(campoCreador.getText().trim());
    }

    /**
     * Muestra un mensaje de exito al guardar el nivel.
     *
     * @param nombreNivel nombre del nivel guardado
     */
    private void mostrarExitoGuardado(String nombreNivel) {
        String mensaje = String.format("El nivel '%s' se ha guardado correctamente en la base de datos.", nombreNivel);
        Alert alerta = crearAlerta(Alert.AlertType.INFORMATION, "Nivel Guardado", mensaje);
        configurarPropietarioAlerta(alerta);
        alerta.showAndWait();
    }

    /**
     * Muestra un mensaje de error al intentar guardar el nivel.
     *
     * @param mensajeError mensaje de error detallado
     */
    private void mostrarErrorGuardado(String mensajeError) {
        String mensaje = String.format("Error al guardar el nivel:\n%s", mensajeError);
        Alert alerta = crearAlerta(Alert.AlertType.ERROR, "Error de Guardado", mensaje);
        configurarPropietarioAlerta(alerta);
        alerta.showAndWait();
    }

    /**
     * Muestra un mensaje de funcionalidad proximamente.
     *
     * @param titulo Titulo del mensaje
     * @param contenido Contenido del mensaje
     */
    private void mostrarMensajeProximamente(String titulo, String contenido) {
        Alert alerta = crearAlerta(Alert.AlertType.INFORMATION, titulo, contenido);
        configurarPropietarioAlerta(alerta);
        alerta.showAndWait();
    }

    /**
     * Valida que el nivel tenga los elementos minimos requeridos.
     *
     * @return true si el nivel es valido, false en caso contrario
     */
    private boolean validarNivel() {
        long numBloques = contarBloques();
        if (numBloques < 1) {
            mostrarErrorValidacion("Nivel Inválido", "Debe haber al menos 1 bloque en el mapa.");
            return false;
        }
        return true;
    }

    /**
     * Muestra un mensaje de error de validacion.
     *
     * @param titulo Titulo del error
     * @param contenido Contenido del error
     */
    private void mostrarErrorValidacion(String titulo, String contenido) {
        Alert alerta = crearAlerta(Alert.AlertType.WARNING, titulo, contenido);
        configurarPropietarioAlerta(alerta);
        alerta.showAndWait();
    }

    /**
     * Crea una alerta estilizada con los parámetros especificados.
     * Aplica automáticamente los estilos retro del proyecto.
     *
     * @param tipo Tipo de alerta
     * @param titulo Título
     * @param contenido Contenido
     * @return Alerta configurada con estilos retro
     */
    private Alert crearAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        return aplicarEstilosAAlert(alerta);
    }

    /**
     * Aplica la hoja de estilos retro a un Alert de forma funcional y pura.
     * Utiliza composición funcional con Vavr Option para manejar el DialogPane de manera segura.
     *
     * @param alerta Alert a estilizar
     * @return el mismo Alert con estilos aplicados
     */
    private Alert aplicarEstilosAAlert(Alert alerta) {
        io.vavr.control.Option.of(alerta.getDialogPane())
            .forEach(dialogPane -> {
                dialogPane.getStyleClass().add("dialogo-editor");
                util.CargadorRecursos.obtenerRutaCSS("css/estilo-retro.css")
                    .ifPresent(ruta -> dialogPane.getStylesheets().add(ruta));
            });
        return alerta;
    }

    /**
     * Configura el propietario de la alerta para mantener el contexto.
     *
     * @param alerta Alerta a configurar
     */
    private void configurarPropietarioAlerta(Alert alerta) {
        Optional.ofNullable(contenedorCanvas.getScene())
                .map(javafx.scene.Scene::getWindow)
                .ifPresent(alerta::initOwner);
    }

    /**
     * Construye el nivel usando el patron Builder.
     *
     * @return Nivel construido
     */
    private Nivel construirNivel() {
        ConstructorMapa constructor = new ConstructorMapa();
        constructor.reiniciar()
                  .establecerNombre(obtenerNombreNivel())
                  .establecerDificultad(obtenerDificultadNivel());

        agregarBloquesAlConstructor(constructor);
        return constructor.construir();
    }

    /**
     * Obtiene el nombre del nivel desde el campo de texto.
     *
     * @return Nombre del nivel
     */
    private String obtenerNombreNivel() {
        String nombre = campoNombre.getText().trim();
        return nombre.isEmpty() ? "Nivel sin nombre" : nombre;
    }

    /**
     * Obtiene la dificultad del nivel desde el deslizador.
     *
     * @return Dificultad del nivel (1-3)
     */
    private int obtenerDificultadNivel() {
        return (int) deslizadorDificultad.getValue();
    }

    /**
     * Agrega todos los bloques al constructor.
     *
     * @param constructor Constructor al que agregar los bloques
     */
    private void agregarBloquesAlConstructor(ConstructorMapa constructor) {
        objetosEnMapa.stream()
                .filter(obj -> obj.tipo == TipoObjetoMapa.BLOQUE)
                .forEach(obj -> constructor.agregarBloque(obj.x, obj.y, obj.tipoBloque));
    }

    /**
     * Maneja la accion de cancelar la edicion y volver al menu.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionCancelar(ActionEvent evento) {
        navegarAlMenu();
    }

    /**
     * Navega al menu principal.
     */
    private void navegarAlMenu() {
        Optional.ofNullable(gestorEscenas)
                .ifPresent(GestorEscenas::mostrarMenu);
    }

    /**
     * Reinicia el estado del controlador al estado inicial.
     * Solo redibuja si hay objetos que limpiar o si el grid cambio de visibilidad.
     */
    public void reiniciarEstado() {
        boolean hayObjetos = !objetosEnMapa.isEmpty();
        boolean gridCambioVisibilidad = !gridVisible;

        objetosEnMapa.clear();
        herramientaActual = HerramientaEditor.NINGUNA;
        limpiarSeleccionHerramientaPrevia();
        campoNombre.clear();
        campoCreador.clear();
        deslizadorDificultad.setValue(1);
        gridVisible = true;
        actualizarEstadisticas();

        if (hayObjetos) {
            redibujarTodo();
        }

        if (gridCambioVisibilidad) {
            dibujarGrid();
        }
    }

    /**
     * Libera los recursos del controlador.
     * Detiene los timers de debouncing para evitar fugas de memoria.
     */
    public void liberarRecursos() {
        if (debounceRedibujarMapa != null) {
            debounceRedibujarMapa.stop();
        }
        if (debounceRedibujarGrid != null) {
            debounceRedibujarGrid.stop();
        }
    }

    /**
     * Enumeracion de las herramientas disponibles en el editor.
     */
    private enum HerramientaEditor {
        NINGUNA,
        BLOQUE_DESTRUCTIBLE,
        BLOQUE_INDESTRUCTIBLE,
        BLOQUE_BONUS,
        BLOQUE_MULTI,
        BORRADOR
    }

    /**
     * Enumeracion de los tipos de objetos que se pueden colocar en el mapa.
     */
    private enum TipoObjetoMapa {
        BLOQUE,
        PALETA
    }

    /**
     * Clase interna que representa un objeto colocado en el mapa.
     */
    private static class ObjetoMapa {
        final double x;
        final double y;
        final TipoObjetoMapa tipo;
        final TipoBloque tipoBloque;

        /**
         * Constructor de objeto del mapa.
         *
         * @param x Coordenada X
         * @param y Coordenada Y
         * @param tipo Tipo de objeto (bloque o paleta)
         * @param tipoBloque Tipo especifico de bloque (null si es paleta)
         */
        ObjetoMapa(double x, double y, TipoObjetoMapa tipo, TipoBloque tipoBloque) {
            this.x = x;
            this.y = y;
            this.tipo = tipo;
            this.tipoBloque = tipoBloque;
        }
    }
}
