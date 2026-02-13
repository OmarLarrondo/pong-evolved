package mvc.modelo.entidades.paleta;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import mvc.modelo.enums.LadoHorizontal;

/**
 * Representa la configuracion inmutable de una paleta en el juego Pong.
 *
 * <p>Esta clase es un objeto de valor (Value Object) que almacena el estado
 * completo de una paleta, incluyendo posicion, dimensiones, velocidad
 * y apariencia visual. Al ser un record inmutable, es thread-safe y puede
 * ser compartido de forma segura entre componentes.</p>
 *
 * <p><b>Características principales:</b></p>
 * <ul>
 *   <li>Inmutabilidad completa (thread-safe)</li>
 *   <li>Gestion de posicion y dimensiones de la paleta</li>
 *   <li>Configuracion de limites de movimiento</li>
 *   <li>Personalizacion de colores</li>
 * </ul>
 *
 * @param colisionEnX posicion horizontal de colision de la paleta
 * @param centro posicion vertical del centro de la paleta
 * @param ancho altura vertical de la paleta (siempre par)
 * @param grosor ancho horizontal de la paleta
 * @param velocidad velocidad de movimiento de la paleta
 * @param limiteNorte limite superior de movimiento
 * @param limiteSur limite inferior de movimiento
 * @param ladoPantalla lado de la pantalla donde se ubica la paleta
 * @param colorPrimario color principal de la paleta
 * @param colorSecundario color secundario de la paleta
 *
 * @author Equipo-polimorfo
 * @version 3.0
 */
public record ConfigPaleta(
    int colisionEnX,
    int centro,
    int ancho,
    int grosor,
    int velocidad,
    int limiteNorte,
    int limiteSur,
    LadoHorizontal ladoPantalla,
    Color colorPrimario,
    Color colorSecundario
) {
    /**
     * Constructor compacto que valida y normaliza los parametros.
     *
     * @throws IndexOutOfBoundsException si algun valor numerico no es positivo
     *         o si los limites son invalidos
     * @throws NullPointerException si algun parametro de referencia es nulo
     */
    public ConfigPaleta {
        if (colisionEnX <= 0) {
            throw new IndexOutOfBoundsException("Valor de colision horizontal no positivo no es valido.");
        }
        if (centro <= 0) {
            throw new IndexOutOfBoundsException("Valor de centro vertical no positivo no es valido.");
        }
        if (ancho <= 0) {
            throw new IndexOutOfBoundsException("Valor de ancho no positivo no es valido.");
        }
        if (grosor <= 0) {
            throw new IndexOutOfBoundsException("Valor de grosor no positivo no es valido.");
        }
        if (velocidad <= 0) {
            throw new IndexOutOfBoundsException("Valor de velocidad no positivo no es valido.");
        }
        if (limiteNorte <= 0) {
            throw new IndexOutOfBoundsException("Valor de limite norte no positivo no es valido.");
        }
        if (limiteSur <= 0) {
            throw new IndexOutOfBoundsException("Valor de limite sur no positivo no es valido.");
        }

        if (ancho % 2 == 1) {
            ancho--;
        }

        if (centro - ancho / 2 < limiteNorte) {
            throw new IndexOutOfBoundsException("Cara superior del trampolin excede limite norte.");
        }
        if (centro + ancho / 2 > limiteSur) {
            throw new IndexOutOfBoundsException("Cara inferior del trampolin excede limite sur.");
        }
        if (limiteSur <= limiteNorte) {
            throw new IndexOutOfBoundsException("El limite sur debe estar por debajo del limite norte.");
        }

        if (ladoPantalla == null) {
            throw new NullPointerException("Lado de la pantalla no puede ser nulo.");
        }
        if (colorPrimario == null) {
            throw new NullPointerException("Color primario no puede ser nulo.");
        }
        if (colorSecundario == null) {
            throw new NullPointerException("Color secundario no puede ser nulo.");
        }
    }

    /**
     * Obtiene la mitad del ancho de la paleta.
     *
     * @return la mitad del ancho
     */
    public int obtenerAnchoLateral() {
        return this.ancho / 2;
    }

    /**
     * Obtiene la velocidad como double para compatibilidad.
     *
     * @return la velocidad como double
     */
    public double obtenerVelocidad() {
        return (double) this.velocidad;
    }

    /**
     * Crea una copia de esta configuracion de paleta.
     * Dado que el record es inmutable, simplemente devuelve la misma instancia.
     *
     * @return esta misma instancia (los records inmutables no necesitan clonacion profunda)
     */
    public ConfigPaleta clonar() {
        return this;
    }
}
