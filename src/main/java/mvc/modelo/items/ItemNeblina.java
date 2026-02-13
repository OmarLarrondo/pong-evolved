package mvc.modelo.items;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import mvc.modelo.entidades.ObjetoJuego;
import mvc.modelo.entidades.paleta.Paleta;

/**
 * Item que crea una neblina visual sobre la paleta para dificultar la visibilidad.
 *
 * Este power-up genera un efecto visual temporal de niebla semitransparente
 * que cubre verticalmente el área de la paleta, obstaculizando la visión del
 * jugador. La neblina es un efecto puramente visual que no modifica las
 * propiedades físicas de la paleta.
 *
 * Solo puede aplicarse a objetos de tipo Paleta. El renderizado de la neblina
 * debe ser manejado preferentemente por la Vista, utilizando los getters de
 * este item para obtener el color y estado de activación.
 */
public class ItemNeblina implements Item {
    /**
     * Duración total del efecto en segundos.
     */
    private double duracion;

    /**
     * Indica si el efecto está actualmente activo.
     */
    private boolean activo;

    /**
     * Tiempo restante en segundos antes de que expire el efecto.
     */
    private double tiempoRestante;

    /**
     * Color de la neblina con transparencia.
     * Gris semitransparente (200, 200, 200, 0.50).
     */
    private Color colorNeblina = Color.rgb(200, 200, 200, 0.50);

    /**
     * Paleta objetivo sobre la que se aplica la neblina.
     */
    private Paleta paletaObjetivo;

    /**
     * Construye un nuevo item de neblina.
     *
     * @param duracion tiempo en segundos que durará el efecto visual
     * @throws IllegalArgumentException si la duración es negativa
     */
    public ItemNeblina(double duracion) {
        if(duracion < 0) throw new IllegalArgumentException("La duracion no puede ser negativa");

        this.duracion = duracion;
        this.tiempoRestante = duracion;
        this.activo = false;
        this.paletaObjetivo = null;
    }

    /**
     * Aplica el efecto de neblina a la paleta especificada.
     *
     * Activa el efecto visual sin modificar las propiedades físicas de la paleta.
     * Si el item ya está activo, ignora la aplicación.
     *
     * @param objeto la Paleta sobre la cual se aplicará el efecto visual
     * @throws IllegalArgumentException si el objeto no es una Paleta
     */
    @Override
    public void aplicar(ObjetoJuego objeto) {
        if (activo) return;
        if (!(objeto instanceof mvc.modelo.entidades.paleta.Paleta paleta)) {
            throw new IllegalArgumentException("La neblina solo se puede aplicar a una paleta.");
        }
        activo = true;
        tiempoRestante = duracion;
        paletaObjetivo = paleta;
    }

    /**
     * Obtiene la duración configurada del efecto.
     *
     * @return duración total en segundos
     */
    @Override
    public double obtenerDuracion() {
        return duracion;
    }

    /**
     * Verifica si el efecto de neblina está actualmente activo.
     *
     * @return true si el efecto está aplicado, false en caso contrario
     */
    @Override
    public boolean estaActivo() {
        return activo;
    }

    /**
     * Desactiva el efecto de neblina.
     *
     * Detiene el renderizado del efecto visual y reinicia el temporizador.
     *
     * @param objeto la Paleta de la cual se removerá el efecto visual
     */
    @Override
    public void desactivar(ObjetoJuego objeto) {
        if (!(objeto instanceof mvc.modelo.entidades.paleta.Paleta) && paletaObjetivo == null) {
            return;
        }
        activo = false;
        tiempoRestante = duracion;
        paletaObjetivo = null;
    }

    /**
     * Actualiza el estado del item según el tiempo transcurrido.
     *
     * Decrementa el tiempo restante del efecto. Cuando el tiempo llega a cero
     * o menos, desactiva automáticamente el item.
     * Este método debe ser invocado en cada frame del juego.
     *
     * @param deltaTiempo tiempo transcurrido desde la última actualización en segundos
     * @param objeto la paleta cuyo efecto será desactivado si expira el tiempo
     */
    @Override
    public void actualizar(double deltaTiempo, ObjetoJuego objeto) {
        if (!activo) return;
        tiempoRestante = tiempoRestante - deltaTiempo;
        if (tiempoRestante <= 0) desactivar(paletaObjetivo != null ? paletaObjetivo : objeto);
    }

    /**
     * Obtiene el color de la neblina para renderizado.
     *
     * @return color gris semitransparente de la neblina
     */
    public Color obtenerColorNeblina() {
        return colorNeblina;
    }

    /**
     * Obtiene el ancho predeterminado de la neblina.
     *
     * @return ancho en píxeles, actualmente retorna 100.0
     */
    public double obtenerAnchoNeblina() {
        return 100.0;
    }

    /**
     * Renderiza el efecto de neblina en el canvas.
     *
     * @param gc contexto gráfico donde se dibujará la neblina
     * @param objeto la Paleta sobre la cual se dibujará el efecto
     * @deprecated Este método viola la separación de responsabilidades del patrón MVC.
     *             La Vista debería usar obtenerColorNeblina() y estaActivo() para
     *             manejar el renderizado directamente.
     */
    @Deprecated
    public void render(GraphicsContext gc, ObjetoJuego objeto) {
        if (!activo || !(objeto instanceof mvc.modelo.entidades.paleta.Paleta paleta)) return;

        double x = paleta.obtenerX();
        double ancho = paleta.obtenerAncho();

        gc.setFill(colorNeblina);
        gc.fillRect(x, 0, ancho, gc.getCanvas().getHeight());
    }

    /**
     * Obtiene la posición X del item para renderizado.
     * Los items de neblina no tienen posición física, retorna 0.0 por defecto.
     *
     * @return 0.0 (sin posición física)
     */
    @Override
    public double obtenerX() {
        return 0.0;
    }

    /**
     * Obtiene la posición Y del item para renderizado.
     * Los items de neblina no tienen posición física, retorna 0.0 por defecto.
     *
     * @return 0.0 (sin posición física)
     */
    @Override
    public double obtenerY() {
        return 0.0;
    }

    /**
     * Obtiene el ancho del item para renderizado.
     *
     * @return 20.0 píxeles
     */
    @Override
    public double obtenerAncho() {
        return 20.0;
    }

    /**
     * Obtiene el alto del item para renderizado.
     *
     * @return 20.0 píxeles
     */
    @Override
    public double obtenerAlto() {
        return 20.0;
    }
}
