package mvc.modelo.entidades;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * Clase base abstracta para todos los objetos del juego
 * (pelota, paleta, bloques, etc.).
 *
 * @author Equipo-polimorfo
 * @version 1.0
 */
public abstract class ObjetoJuego {

    protected double x;
    protected double y;
    protected double ancho;
    protected double alto;
    protected boolean activo;

    /**
     * Construye un nuevo objeto del juego con las dimensiones y posición especificadas.
     * El objeto se inicializa en estado activo por defecto.
     *
     * @param x posición horizontal inicial del objeto en pixeles
     * @param y posición vertical inicial del objeto en pixeles
     * @param ancho ancho del objeto en pixeles
     * @param alto alto del objeto en pixeles
     */
    public ObjetoJuego(double x, double y, double ancho, double alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.activo = true;
    }

    /**
     * Obtiene la posición horizontal actual del objeto.
     *
     * @return la coordenada x del objeto en pixeles
     */
    public double obtenerX() {
        return x;
    }

    /**
     * Obtiene la posición vertical actual del objeto.
     *
     * @return la coordenada y del objeto en pixeles
     */
    public double obtenerY() {
        return y;
    }

    /**
     * Obtiene el ancho del objeto.
     *
     * @return el ancho del objeto en pixeles
     */
    public double obtenerAncho() {
        return ancho;
    }

    /**
     * Obtiene el alto del objeto.
     *
     * @return el alto del objeto en pixeles
     */
    public double obtenerAlto() {
        return alto;
    }

    /**
     * Verifica si el objeto está activo en el juego.
     * Los objetos inactivos suelen ser ignorados en la lógica del juego.
     *
     * @return {@code true} si el objeto está activo, {@code false} en caso contrario
     */
    public boolean estaActivo() {
        return activo;
    }

    /**
     * Establece el estado de actividad del objeto.
     * Los objetos pueden ser desactivados cuando son destruidos o eliminados del juego.
     *
     * @param activo {@code true} para activar el objeto, {@code false} para desactivarlo
     */
    public void establecerActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Actualiza el estado del objeto basándose en el tiempo transcurrido.
     * Este método debe implementarse en las subclases para definir el comportamiento
     * específico de actualización (movimiento, física, etc.).
     *
     * @param deltaTime tiempo transcurrido desde la última actualización en segundos
     */
    public abstract void actualizar(double deltaTime);

    /**
     * Obtiene el rectángulo delimitador del objeto para detección de colisiones.
     * El rectángulo representa el área ocupada por el objeto en el espacio del juego.
     *
     * @return un {@code Rectangle2D} que representa los límites del objeto
     */
    public abstract Rectangle2D obtenerLimites();

    /**
     * Renderiza el objeto en el canvas del juego.
     * Este método debe implementarse en las subclases para definir la representación
     * visual específica del objeto.
     *
     * @param gc el contexto gráfico de JavaFX donde se dibujará el objeto
     */
    public abstract void dibujar(GraphicsContext gc);
}
