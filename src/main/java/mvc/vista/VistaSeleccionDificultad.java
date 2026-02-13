package mvc.vista;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;
import mvc.controlador.ControladorSeleccionDificultad;
import util.CargadorRecursos;

import java.util.Optional;

/**
 * Vista del panel de seleccion de dificultad de IA para el modo un jugador.
 * Gestiona la carga y configuracion de la interfaz con menu radial y estetica retro.
 * Implementa el componente Vista del patron MVC.
 */
public class VistaSeleccionDificultad {

    private static final String RUTA_FXML = "fxml/seleccion-dificultad.fxml";
    private static final String RUTA_CSS = "css/estilo-retro.css";
    private static final double DURACION_FADE_IN_MS = 500.0;

    private final ControladorSeleccionDificultad controlador;
    private final Scene escena;

    /**
     * Constructor que inicializa la vista de seleccion de dificultad.
     *
     * @param controlador Controlador asociado a esta vista
     */
    public VistaSeleccionDificultad(ControladorSeleccionDificultad controlador) {
        this.controlador = controlador;
        this.escena = crearEscena();
    }

    /**
     * Crea y configura la escena de seleccion de dificultad con su contenido FXML y estilos CSS.
     *
     * @return Scene configurada con el panel de seleccion
     */
    private Scene crearEscena() {
        return cargarContenido()
                .map(raiz -> construirEscena(raiz))
                .orElseThrow(() -> new RuntimeException(
                        "No se pudo cargar el contenido de seleccion de dificultad desde " + RUTA_FXML
                ));
    }

    /**
     * Carga el contenido FXML del panel con el controlador especificado.
     *
     * @return Optional con el Parent raiz del FXML, o Optional.empty() si falla
     */
    private Optional<Parent> cargarContenido() {
        return CargadorRecursos.cargarFXMLConControlador(RUTA_FXML, controlador);
    }

    /**
     * Construye la escena a partir del contenido raiz y aplica los estilos.
     * La escena se crea sin dimensiones fijas para permitir responsividad.
     *
     * @param raiz Contenedor raiz del FXML
     * @return Scene configurada
     */
    private Scene construirEscena(Parent raiz) {
        Scene escena = new Scene(raiz);
        aplicarEstilos(escena);
        aplicarAnimacionInicial(raiz);
        return escena;
    }

    /**
     * Aplica los estilos CSS a la escena.
     *
     * @param escena Scene a la que aplicar los estilos
     */
    private void aplicarEstilos(Scene escena) {
        CargadorRecursos.obtenerRutaCSS(RUTA_CSS)
                .ifPresent(css -> escena.getStylesheets().add(css));
    }

    /**
     * Aplica una animacion fade-in inicial a la vista.
     *
     * @param raiz Nodo raiz al que aplicar la animacion
     */
    private void aplicarAnimacionInicial(Parent raiz) {
        raiz.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(DURACION_FADE_IN_MS), raiz);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    /**
     * Obtiene la escena del panel de seleccion.
     *
     * @return Scene configurada
     */
    public Scene obtenerEscena() {
        return escena;
    }

    /**
     * Obtiene el controlador asociado a esta vista.
     *
     * @return ControladorSeleccionDificultad asociado
     */
    public ControladorSeleccionDificultad obtenerControlador() {
        return controlador;
    }

    /**
     * Refresca la vista reiniciando el estado del menu radial.
     */
    public void refrescar() {
        controlador.reiniciarEstado();
    }

    /**
     * Limpia los recursos de la vista al ser destruida.
     */
    public void limpiar() {
        controlador.liberarRecursos();
    }
}
