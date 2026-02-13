package mvc.controlador;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import mvc.vista.GestorEscenas;

import java.util.Optional;

/**
 * Clase base abstracta para todos los controladores de la aplicación.
 * Proporciona funcionalidad común como la gestión de atajos de teclado
 * para pantalla completa y referencias compartidas a componentes del sistema.
 *
 * Implementa el patrón Template Method para el manejo de atajos de teclado.
 */
public abstract class ControladorBase {

    protected GestorEscenas gestorEscenas;

    /**
     * Establece el gestor de escenas.
     *
     * @param gestorEscenas Gestor para cambiar entre escenas
     */
    public void establecerGestorEscenas(GestorEscenas gestorEscenas) {
        this.gestorEscenas = gestorEscenas;
    }

    /**
     * Método de inicialización de FXML que deben implementar las subclases.
     * Se invoca automáticamente después de cargar el archivo FXML.
     */
    @FXML
    public abstract void initialize();

    /**
     * Configura los atajos de teclado para pantalla completa en un nodo específico.
     * Los atajos son:
     * - P: Alterna entre pantalla completa y modo ventana
     * - ESC: Sale de pantalla completa
     *
     * @param nodo Nodo de JavaFX desde el cual se escucharán los eventos de teclado
     */
    protected void configurarAtajosTecladoPantallaCompleta(Node nodo) {
        aplicarConfiguracionAtajos(nodo);
    }

    /**
     * Aplica la configuración de atajos de teclado al nodo especificado.
     *
     * @param nodo Nodo al cual aplicar los atajos
     */
    private void aplicarConfiguracionAtajos(Node nodo) {
        nodo.sceneProperty().addListener((observable, oldScene, newScene) ->
            Optional.ofNullable(newScene)
                    .ifPresent(escena ->
                        escena.setOnKeyPressed(this::manejarAtajoTecladoPantallaCompleta)
                    )
        );
    }

    /**
     * Maneja los eventos de teclado para los atajos de pantalla completa.
     *
     * @param evento Evento de teclado generado por el usuario
     */
    private void manejarAtajoTecladoPantallaCompleta(KeyEvent evento) {
        procesarTeclaPantallaCompleta(evento.getCode());
    }

    /**
     * Procesa la tecla presionada y ejecuta la acción correspondiente.
     *
     * @param tecla Código de la tecla presionada
     */
    private void procesarTeclaPantallaCompleta(KeyCode tecla) {
        if (tecla == KeyCode.P) {
            ejecutarAlternanciaFullScreen();
        } else if (tecla == KeyCode.ESCAPE) {
            ejecutarSalidaFullScreen();
        }
    }

    /**
     * Ejecuta la alternancia del modo de pantalla completa.
     */
    private void ejecutarAlternanciaFullScreen() {
        Optional.ofNullable(gestorEscenas)
                .ifPresent(GestorEscenas::alternarPantallaCompleta);
    }

    /**
     * Ejecuta la salida del modo de pantalla completa.
     */
    private void ejecutarSalidaFullScreen() {
        Optional.ofNullable(gestorEscenas)
                .ifPresent(GestorEscenas::salirPantallaCompleta);
    }
}
