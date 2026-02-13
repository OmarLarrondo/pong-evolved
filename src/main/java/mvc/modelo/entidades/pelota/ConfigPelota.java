package mvc.modelo.entidades.pelota;

import mvc.modelo.enums.LadoHorizontal;
import mvc.modelo.enums.LadoVertical;

/**
 * Representa la configuracion inmutable de una pelota en el juego Pong.
 *
 * <p>Esta clase es un objeto de valor (Value Object) que almacena el estado
 * completo de una pelota, incluyendo posicion, radio, velocidad y direccion.
 * Al ser un record inmutable, es thread-safe y puede ser compartido de forma
 * segura entre componentes.</p>
 *
 * <p><b>Características principales:</b></p>
 * <ul>
 *   <li>Inmutabilidad completa (thread-safe)</li>
 *   <li>Gestion de posicion y dimensiones</li>
 *   <li>Control de velocidad y direccion</li>
 *   <li>Calculos de velocidad vectorial</li>
 * </ul>
 *
 * @param centroEnX coordenada X del centro de la pelota
 * @param centroEnY coordenada Y del centro de la pelota
 * @param radio radio de la pelota
 * @param velocidad velocidad actual de la pelota
 * @param velocidadMaxima velocidad maxima permitida
 * @param anguloDireccional angulo de direccion en radianes [0, 2π)
 *
 * @author Equipo-polimorfo
 * @version 2.0
 */
public record ConfigPelota(
    int centroEnX,
    int centroEnY,
    int radio,
    int velocidad,
    int velocidadMaxima,
    double anguloDireccional
) {
    /**
     * Constante PI para calculos trigonometricos.
     */
    public static final double PI = Math.PI;

    /**
     * Constructor compacto que valida y normaliza los parametros.
     *
     * @throws IndexOutOfBoundsException si algun valor numerico no es positivo
     *         o si las relaciones entre valores son invalidas
     */
    public ConfigPelota {
        if (centroEnX <= 0) {
            throw new IndexOutOfBoundsException("Valor de posicion horizontal no positivo no es valido.");
        }
        if (centroEnY <= 0) {
            throw new IndexOutOfBoundsException("Valor de posicion vertical no positivo no es valido.");
        }
        if (radio <= 0) {
            throw new IndexOutOfBoundsException("Valor de radio no positivo no es valido.");
        }
        if (velocidad <= 0) {
            throw new IndexOutOfBoundsException("Valor de velocidad no positivo no es valido.");
        }
        if (velocidadMaxima <= 0) {
            throw new IndexOutOfBoundsException("Valor de velocidad maxima no positivo no es valido.");
        }

        if (velocidad > velocidadMaxima) {
            throw new IndexOutOfBoundsException("La velocidad inicial no puede exceder la maxima.");
        }
        if (anguloDireccional < 0 || anguloDireccional >= 2 * PI) {
            throw new IndexOutOfBoundsException(
                "El angulo direccional debe estar en el rango [0, 2*PI)."
            );
        }

        anguloDireccional = anguloDireccional % (2 * PI);
        if (anguloDireccional < 0) {
            anguloDireccional += 2 * PI;
        }
    }

    /**
     * Obtiene la velocidad actual de la pelota como double.
     *
     * @return la velocidad actual
     */
    public double obtenerVelocidad() {
        return (double) this.velocidad;
    }

    /**
     * Obtiene la componente X de la velocidad.
     *
     * @return la velocidad en el eje X
     */
    public double obtenerVelocidadX() {
        return this.velocidad * Math.cos(this.anguloDireccional);
    }

    /**
     * Obtiene la componente Y de la velocidad.
     *
     * @return la velocidad en el eje Y
     */
    public double obtenerVelocidadY() {
        return this.velocidad * -Math.sin(this.anguloDireccional);
    }

    /**
     * Obtiene el sentido horizontal de movimiento de la pelota.
     *
     * @return DERECHA si se mueve hacia la derecha, IZQUIERDA en caso contrario
     */
    public LadoHorizontal obtenerSentidoHorizontal() {
        double velocidadX = this.obtenerVelocidadX();
        return velocidadX > 0
            ? LadoHorizontal.DERECHA
            : LadoHorizontal.IZQUIERDA;
    }

    /**
     * Obtiene el sentido vertical de movimiento de la pelota.
     *
     * @return ARRIBA si se mueve hacia arriba, ABAJO en caso contrario
     */
    public LadoVertical obtenerSentidoVertical() {
        double velocidadY = this.obtenerVelocidadY();
        return velocidadY < 0
            ? LadoVertical.ARRIBA
            : LadoVertical.ABAJO;
    }

    /**
     * Crea una copia de esta configuracion de pelota.
     * Dado que el record es inmutable, simplemente devuelve la misma instancia.
     *
     * @return esta misma instancia (los records inmutables no necesitan clonacion profunda)
     */
    public ConfigPelota clonar() {
        return this;
    }
}
