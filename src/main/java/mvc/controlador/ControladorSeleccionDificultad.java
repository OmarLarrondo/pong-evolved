package mvc.controlador;


import io.vavr.control.Option;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import mvc.vista.GestorEscenas;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Controlador del panel de seleccion de dificultad de IA.
 * Gestiona el menu radial interactivo con animaciones y seleccion de nivel.
 * Implementa el componente Controlador del patron MVC.
 * Extiende de ControladorBase para heredar funcionalidad común de atajos de teclado.
 */
public class ControladorSeleccionDificultad extends ControladorBase {

    private static final int CANTIDAD_NIVELES = 10;
    private static final double RADIO_BASE = 220.0;
    private static final double DURACION_ANIMACION_MS = 600.0;
    private static final double DELAY_CASCADA_MS = 40.0;
    private static final double ANGULO_INICIAL_GRADOS = -90.0;
    private static final double MARGEN_SEGURIDAD = 80.0;

    @FXML
    private Pane panelRadial;

    @FXML
    private Button botonCentral;

    @FXML
    private Button botonNivel1;

    @FXML
    private Button botonNivel2;

    @FXML
    private Button botonNivel3;

    @FXML
    private Button botonNivel4;

    @FXML
    private Button botonNivel5;

    @FXML
    private Button botonNivel6;

    @FXML
    private Button botonNivel7;

    @FXML
    private Button botonNivel8;

    @FXML
    private Button botonNivel9;

    @FXML
    private Button botonNivel10;

    @FXML
    private Button botonRegresar;

    @FXML
    private Button botonContinuar;

    private boolean menuDesplegado;
    private Integer nivelSeleccionado;
    private List<Button> botonesNivel;
    private double radioActual;

    /**
     * Constructor por defecto requerido por FXML.
     */
    public ControladorSeleccionDificultad() {
        this.menuDesplegado = false;
        this.nivelSeleccionado = null;
        this.radioActual = RADIO_BASE;
    }

    /**
     * Inicializa el controlador despues de cargar el FXML.
     */
    @FXML
    @Override
    public void initialize() {
        inicializarListaBotones();
        configurarAtajosTecladoPantallaCompleta(panelRadial);
        configurarBindingsResponsivos();
        posicionarBotonCentral();
    }

    /**
     * Inicializa la lista de botones de nivel en orden.
     */
    private void inicializarListaBotones() {
        botonesNivel = Arrays.asList(
                botonNivel1, botonNivel2, botonNivel3, botonNivel4, botonNivel5,
                botonNivel6, botonNivel7, botonNivel8, botonNivel9, botonNivel10
        );
    }

    /**
     * Configura los bindings responsivos para recalcular posiciones al redimensionar.
     */
    private void configurarBindingsResponsivos() {
        panelRadial.widthProperty().addListener((obs, oldVal, newVal) ->
                recalcularPosicionesResponsivas()
        );
        panelRadial.heightProperty().addListener((obs, oldVal, newVal) ->
                recalcularPosicionesResponsivas()
        );
    }

    /**
     * Recalcula todas las posiciones de los botones al cambiar el tamano del panel.
     */
    private void recalcularPosicionesResponsivas() {
        calcularRadioResponsivo();
        posicionarBotonCentral();
        if (menuDesplegado) {
            posicionarBotonesRadiales();
        }
    }

    /**
     * Calcula el radio responsivo basado en el tamano minimo del panel.
     * Aplica un margen de seguridad para evitar que los botones se corten.
     */
    private void calcularRadioResponsivo() {
        double anchoPanel = panelRadial.getWidth();
        double altoPanel = panelRadial.getHeight();
        double dimensionMinima = Math.min(anchoPanel, altoPanel);
        double dimensionDisponible = Math.max(dimensionMinima - MARGEN_SEGURIDAD, 0);
        radioActual = dimensionDisponible > 0 ? dimensionDisponible * 0.38 : RADIO_BASE * 0.85;
    }

    /**
     * Posiciona el boton central en el centro del panel.
     */
    private void posicionarBotonCentral() {
        double centroX = calcularCentroX();
        double centroY = calcularCentroY();
        aplicarPosicionCentral(botonCentral, centroX, centroY);
    }

    /**
     * Calcula la coordenada X del centro del panel.
     *
     * @return Coordenada X central
     */
    private double calcularCentroX() {
        return panelRadial.getWidth() / 2.0;
    }

    /**
     * Calcula la coordenada Y del centro del panel.
     *
     * @return Coordenada Y central
     */
    private double calcularCentroY() {
        return panelRadial.getHeight() / 2.0;
    }

    /**
     * Aplica la posicion central a un boton considerando su tamano.
     *
     * @param boton Boton a posicionar
     * @param centroX Coordenada X del centro
     * @param centroY Coordenada Y del centro
     */
    private void aplicarPosicionCentral(Button boton, double centroX, double centroY) {
        double anchoBoton = boton.getWidth() > 0 ? boton.getWidth() : boton.getPrefWidth();
        double altoBoton = boton.getHeight() > 0 ? boton.getHeight() : boton.getPrefHeight();
        boton.setLayoutX(centroX - anchoBoton / 2.0);
        boton.setLayoutY(centroY - altoBoton / 2.0);
    }

    /**
     * Maneja el evento de clic en el boton central para desplegar el menu radial.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void desplegarMenuRadial(ActionEvent evento) {
        if (!menuDesplegado) {
            ejecutarDespliegueRadial();
        }
    }

    /**
     * Ejecuta el despliegue del menu radial con animaciones.
     */
    private void ejecutarDespliegueRadial() {
        menuDesplegado = true;
        ocultarBotonCentral();
        mostrarBotonesNivel();
        posicionarBotonesRadiales();
        animarDespliegueRadial();
    }

    /**
     * Oculta el boton central con fade out.
     */
    private void ocultarBotonCentral() {
        FadeTransition fade = new FadeTransition(Duration.millis(200), botonCentral);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> botonCentral.setVisible(false));
        fade.play();
    }

    /**
     * Muestra todos los botones de nivel haciendolos visibles.
     */
    private void mostrarBotonesNivel() {
        botonesNivel.forEach(boton -> {
            boton.setVisible(true);
            boton.setOpacity(0.0);
            boton.setScaleX(0.0);
            boton.setScaleY(0.0);
        });
    }

    /**
     * Posiciona todos los botones de nivel en formacion radial.
     */
    private void posicionarBotonesRadiales() {
        double centroX = calcularCentroX();
        double centroY = calcularCentroY();
        double anguloIncremento = 360.0 / CANTIDAD_NIVELES;

        IntStream.range(0, CANTIDAD_NIVELES)
                .forEach(indice -> posicionarBotonEnRadial(
                        botonesNivel.get(indice),
                        indice,
                        centroX,
                        centroY,
                        anguloIncremento
                ));
    }

    /**
     * Posiciona un boton especifico en su posicion radial.
     *
     * @param boton Boton a posicionar
     * @param indice Indice del boton en el circulo
     * @param centroX Coordenada X del centro
     * @param centroY Coordenada Y del centro
     * @param anguloIncremento Angulo entre botones consecutivos
     */
    private void posicionarBotonEnRadial(Button boton, int indice, double centroX,
                                         double centroY, double anguloIncremento) {
        double angulo = calcularAnguloRadial(indice, anguloIncremento);
        double x = calcularCoordenadaRadialX(centroX, angulo);
        double y = calcularCoordenadaRadialY(centroY, angulo);
        aplicarPosicionRadial(boton, x, y);
    }

    /**
     * Calcula el angulo en radianes para un boton en la posicion radial.
     *
     * @param indice Indice del boton
     * @param anguloIncremento Angulo entre botones en grados
     * @return Angulo en radianes
     */
    private double calcularAnguloRadial(int indice, double anguloIncremento) {
        double anguloGrados = ANGULO_INICIAL_GRADOS + (indice * anguloIncremento);
        return Math.toRadians(anguloGrados);
    }

    /**
     * Calcula la coordenada X de un punto en el circulo radial.
     *
     * @param centroX Coordenada X del centro
     * @param anguloRadianes Angulo en radianes
     * @return Coordenada X calculada
     */
    private double calcularCoordenadaRadialX(double centroX, double anguloRadianes) {
        return centroX + radioActual * Math.cos(anguloRadianes);
    }

    /**
     * Calcula la coordenada Y de un punto en el circulo radial.
     *
     * @param centroY Coordenada Y del centro
     * @param anguloRadianes Angulo en radianes
     * @return Coordenada Y calculada
     */
    private double calcularCoordenadaRadialY(double centroY, double anguloRadianes) {
        return centroY + radioActual * Math.sin(anguloRadianes);
    }

    /**
     * Aplica la posicion radial a un boton considerando su tamano.
     *
     * @param boton Boton a posicionar
     * @param x Coordenada X objetivo
     * @param y Coordenada Y objetivo
     */
    private void aplicarPosicionRadial(Button boton, double x, double y) {
        double anchoBoton = boton.getWidth() > 0 ? boton.getWidth() : 60.0;
        double altoBoton = boton.getHeight() > 0 ? boton.getHeight() : 60.0;
        boton.setLayoutX(x - anchoBoton / 2.0);
        boton.setLayoutY(y - altoBoton / 2.0);
    }

    /**
     * Anima el despliegue radial de todos los botones con efecto cascada.
     */
    private void animarDespliegueRadial() {
        IntStream.range(0, CANTIDAD_NIVELES)
                .forEach(indice -> {
                    double delay = indice * DELAY_CASCADA_MS;
                    animarBotonIndividual(botonesNivel.get(indice), delay);
                });
    }

    /**
     * Anima un boton individual con rotacion, fade y escala.
     *
     * @param boton Boton a animar
     * @param delayMs Delay antes de iniciar la animacion en milisegundos
     */
    private void animarBotonIndividual(Button boton, double delayMs) {
        ParallelTransition animacionCompleta = crearAnimacionCompleta(boton);
        animacionCompleta.setDelay(Duration.millis(delayMs));
        animacionCompleta.play();
    }

    /**
     * Crea la animacion completa para un boton (fade + escala + rotacion).
     *
     * @param boton Boton a animar
     * @return ParallelTransition con todas las animaciones
     */
    private ParallelTransition crearAnimacionCompleta(Button boton) {
        FadeTransition fade = crearFadeIn(boton);
        ScaleTransition escala = crearEscalaIn(boton);
        RotateTransition rotacion = crearRotacion(boton);
        return new ParallelTransition(fade, escala, rotacion);
    }

    /**
     * Crea una transicion fade-in para un boton.
     *
     * @param boton Boton a animar
     * @return FadeTransition configurada
     */
    private FadeTransition crearFadeIn(Button boton) {
        FadeTransition fade = new FadeTransition(Duration.millis(DURACION_ANIMACION_MS), boton);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        return fade;
    }

    /**
     * Crea una transicion de escala para un boton.
     *
     * @param boton Boton a animar
     * @return ScaleTransition configurada
     */
    private ScaleTransition crearEscalaIn(Button boton) {
        ScaleTransition escala = new ScaleTransition(Duration.millis(DURACION_ANIMACION_MS), boton);
        escala.setFromX(0.0);
        escala.setFromY(0.0);
        escala.setToX(1.0);
        escala.setToY(1.0);
        return escala;
    }

    /**
     * Crea una transicion de rotacion para un boton.
     *
     * @param boton Boton a animar
     * @return RotateTransition configurada
     */
    private RotateTransition crearRotacion(Button boton) {
        RotateTransition rotacion = new RotateTransition(
                Duration.millis(DURACION_ANIMACION_MS),
                boton
        );
        rotacion.setFromAngle(360.0);
        rotacion.setToAngle(0.0);
        return rotacion;
    }

    /**
     * Maneja la seleccion de un nivel de dificultad.
     *
     * @param evento Evento de accion del boton
     */
    @FXML
    private void seleccionarNivel(ActionEvent evento) {
        Optional.ofNullable(evento.getSource())
                .filter(source -> source instanceof Button)
                .map(source -> (Button) source)
                .ifPresent(this::procesarSeleccionNivel);
    }

    /**
     * Procesa la seleccion de un nivel desde un boton.
     *
     * @param botonSeleccionado Boton que fue clickeado
     */
    private void procesarSeleccionNivel(Button botonSeleccionado) {
        int nivel = extraerNivelDeBoton(botonSeleccionado);
        aplicarSeleccion(nivel, botonSeleccionado);
    }

    /**
     * Extrae el numero de nivel del texto del boton.
     *
     * @param boton Boton con el numero de nivel
     * @return Numero de nivel extraido
     */
    private int extraerNivelDeBoton(Button boton) {
        return Integer.parseInt(boton.getText());
    }

    /**
     * Aplica la seleccion de nivel y actualiza la interfaz.
     *
     * @param nivel Nivel seleccionado
     * @param botonSeleccionado Boton correspondiente al nivel
     */
    private void aplicarSeleccion(int nivel, Button botonSeleccionado) {
        limpiarSeleccionPrevia();
        nivelSeleccionado = nivel;
        resaltarBotonSeleccionado(botonSeleccionado);
        mostrarBotonContinuar();
        System.out.println("Nivel de dificultad seleccionado: " + nivel);
    }

    /**
     * Limpia el resaltado de la seleccion previa si existe.
     */
    private void limpiarSeleccionPrevia() {
        botonesNivel.forEach(this::removerEstiloSeleccionado);
    }

    /**
     * Remueve el estilo de seleccionado de un boton.
     *
     * @param boton Boton a limpiar
     */
    private void removerEstiloSeleccionado(Button boton) {
        boton.getStyleClass().remove("boton-nivel-seleccionado");
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
     * Muestra el boton continuar con animacion fade-in.
     */
    private void mostrarBotonContinuar() {
        botonContinuar.setVisible(true);
        botonContinuar.setOpacity(0.0);
        FadeTransition fade = new FadeTransition(Duration.millis(300), botonContinuar);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    /**
     * Maneja el evento de clic en el boton Continuar.
     * Navega al juego con la dificultad seleccionada.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionContinuar(ActionEvent evento) {
        if (nivelSeleccionado != null) {
            System.out.println("Iniciando juego con dificultad nivel " + nivelSeleccionado);
            navegarAlJuego();
        }
    }

    /**
     * Navega a la pantalla de seleccion de niveles con la dificultad seleccionada.
     * Utiliza programacion funcional pura con Vavr para pasar la dificultad de manera tipo-segura.
     */
    private void navegarAlJuego() {
        Option.ofOptional(Optional.ofNullable(nivelSeleccionado))
                .peek(nivel -> System.out.println("Navegando con dificultad: " + nivel))
                .forEach(nivel ->
                        Optional.ofNullable(gestorEscenas)
                                .ifPresent(gestor ->
                                        gestor.mostrarSeleccionNivelesConDificultad(Option.some(nivel))
                                )
                );
    }

    /**
     * Maneja el evento de clic en el boton Regresar.
     * Vuelve al menu principal.
     *
     * @param evento Evento de accion
     */
    @FXML
    private void accionRegresar(ActionEvent evento) {
        System.out.println("Regresando al menu principal...");
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
     */
    public void reiniciarEstado() {
        menuDesplegado = false;
        nivelSeleccionado = null;
        botonCentral.setVisible(true);
        botonCentral.setOpacity(1.0);
        botonesNivel.forEach(boton -> {
            boton.setVisible(false);
            boton.setOpacity(0.0);
            boton.setScaleX(0.0);
            boton.setScaleY(0.0);
        });
        botonContinuar.setVisible(false);
        limpiarSeleccionPrevia();
    }

    /**
     * Libera los recursos del controlador.
     */
    public void liberarRecursos() {
    }

    /**
     * Obtiene el nivel de dificultad actualmente seleccionado.
     *
     * @return Optional con el nivel seleccionado, o empty si no hay seleccion
     */
    public Optional<Integer> obtenerNivelSeleccionado() {
        return Optional.ofNullable(nivelSeleccionado);
    }
}
