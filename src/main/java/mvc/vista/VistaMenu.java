package mvc.vista;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;
import mvc.controlador.ControladorMenu;
import util.CargadorRecursos;

import java.util.Optional;

/**
 * Vista del menú principal del juego Pong Evolved.
 * Gestiona la carga y configuración de la interfaz del menú con estética retro.
 * Implementa el componente Vista del patrón MVC.
 */
public class VistaMenu {

    private static final String RUTA_FXML = "fxml/menu.fxml";
    private static final String RUTA_CSS = "css/estilo-retro.css";
    private static final int ANCHO_VENTANA = 800;
    private static final int ALTO_VENTANA = 600;
    private static final double DURACION_FADE_IN_MS = 500.0;

    private final ControladorMenu controlador;
    private final Scene escena;

    /**
     * Constructor que inicializa la vista del menú.
     *
     * @param controlador Controlador asociado a esta vista
     */
    public VistaMenu(ControladorMenu controlador) {
        this.controlador = controlador;
        this.escena = crearEscena();
    }

    /**
     * Crea y configura la escena del menú con su contenido FXML y estilos CSS.
     *
     * @return Scene configurada con el menú
     */
    private Scene crearEscena() {
        return cargarContenido()
                .map(raiz -> construirEscena(raiz))
                .orElseThrow(() -> new RuntimeException(
                        "No se pudo cargar el contenido del menú desde " + RUTA_FXML
                ));
    }

    /**
     * Carga el contenido FXML del menú con el controlador especificado.
     *
     * @return Optional con el Parent raíz del FXML, o Optional.empty() si falla
     */
    private Optional<Parent> cargarContenido() {
        return CargadorRecursos.cargarFXMLConControlador(RUTA_FXML, controlador);
    }

    /**
     * Construye la escena a partir del contenido raíz y aplica los estilos.
     * La escena se crea sin dimensiones fijas para permitir responsividad.
     *
     * @param raiz Contenedor raíz del FXML
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
     * Aplica una animación fade-in inicial a la vista.
     *
     * @param raiz Nodo raíz al que aplicar la animación
     */
    private void aplicarAnimacionInicial(Parent raiz) {
        raiz.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(DURACION_FADE_IN_MS), raiz);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    /**
     * Obtiene la escena del menú.
     *
     * @return Scene del menú configurada
     */
    public Scene obtenerEscena() {
        return escena;
    }

    /**
     * Obtiene el controlador asociado a esta vista.
     *
     * @return ControladorMenu asociado
     */
    public ControladorMenu obtenerControlador() {
        return controlador;
    }

    /**
     * Refresca la vista reiniciando animaciones y videos.
     */
    public void refrescar() {
        controlador.reiniciarVideos();
    }

    /**
     * Limpia los recursos de la vista al ser destruida.
     */
    public void limpiar() {
        controlador.liberarRecursos();
    }
}
