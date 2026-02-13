package patrones.singleton;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Clase Singleton que gestiona la configuración global de la aplicación.
 * Mantiene estados compartidos entre diferentes vistas y controladores,
 * como el estado de pantalla completa.
 *
 * Utiliza propiedades observables de JavaFX para permitir que los cambios
 * en la configuración se reflejen automáticamente en la interfaz.
 */
public class ConfiguracionGlobal {

    private static ConfiguracionGlobal instancia;

    private final BooleanProperty pantallaCompleta;

    /**
     * Constructor privado que inicializa las propiedades de configuración.
     * Solo puede ser invocado desde dentro de esta clase para garantizar
     * el patrón Singleton.
     */
    private ConfiguracionGlobal() {
        this.pantallaCompleta = new SimpleBooleanProperty(false);
    }

    /**
     * Obtiene la instancia única de ConfiguracionGlobal.
     * Si no existe, la crea (inicialización lazy).
     *
     * @return la instancia única de ConfiguracionGlobal
     */
    public static ConfiguracionGlobal obtenerInstancia() {
        return aplicarObtenerInstancia();
    }

    /**
     * Aplica la lógica de obtención de la instancia única.
     * Crea la instancia si no existe.
     *
     * @return la instancia única de ConfiguracionGlobal
     */
    private static ConfiguracionGlobal aplicarObtenerInstancia() {
        if (instancia == null) {
            instancia = crearNuevaInstancia();
        }
        return instancia;
    }

    /**
     * Crea una nueva instancia de ConfiguracionGlobal.
     *
     * @return una nueva instancia de ConfiguracionGlobal
     */
    private static ConfiguracionGlobal crearNuevaInstancia() {
        return new ConfiguracionGlobal();
    }

    /**
     * Obtiene la propiedad observable de pantalla completa.
     * Permite vincular esta propiedad con componentes de la interfaz
     * para actualizaciones automáticas.
     *
     * @return la propiedad observable de pantalla completa
     */
    public BooleanProperty pantallaCompletaProperty() {
        return pantallaCompleta;
    }

    /**
     * Verifica si el modo de pantalla completa está activo.
     *
     * @return true si la pantalla completa está activa, false en caso contrario
     */
    public boolean isPantallaCompleta() {
        return obtenerEstadoPantallaCompleta();
    }

    /**
     * Obtiene el estado actual de la pantalla completa.
     *
     * @return true si la pantalla completa está activa, false en caso contrario
     */
    private boolean obtenerEstadoPantallaCompleta() {
        return pantallaCompleta.get();
    }

    /**
     * Establece el estado de pantalla completa.
     *
     * @param valor true para activar pantalla completa, false para desactivarla
     */
    public void setPantallaCompleta(boolean valor) {
        aplicarCambioPantallaCompleta(valor);
    }

    /**
     * Aplica el cambio del estado de pantalla completa.
     *
     * @param valor el nuevo estado de pantalla completa
     */
    private void aplicarCambioPantallaCompleta(boolean valor) {
        pantallaCompleta.set(valor);
    }

    /**
     * Alterna el estado de pantalla completa.
     * Si está activa, la desactiva; si está desactivada, la activa.
     */
    public void alternarPantallaCompleta() {
        aplicarAlternancia();
    }

    /**
     * Aplica la alternancia del estado de pantalla completa.
     */
    private void aplicarAlternancia() {
        boolean estadoActual = obtenerEstadoPantallaCompleta();
        boolean nuevoEstado = calcularEstadoAlternado(estadoActual);
        aplicarCambioPantallaCompleta(nuevoEstado);
    }

    /**
     * Calcula el estado alternado de pantalla completa.
     *
     * @param estadoActual el estado actual
     * @return el estado opuesto al actual
     */
    private boolean calcularEstadoAlternado(boolean estadoActual) {
        return !estadoActual;
    }
}
