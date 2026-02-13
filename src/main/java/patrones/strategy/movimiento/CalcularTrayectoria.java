package patrones.strategy.movimiento;

import io.vavr.control.Option;
import mvc.modelo.entidades.pelota.Pelota;

/**
 * Calculador de trayectoria que predice la posición de impacto de la pelota.
 *
 * <p>Esta clase implementa un algoritmo de predicción de trayectoria que calcula
 * dónde impactará la pelota en una coordenada X específica, considerando los rebotes
 * múltiples en los límites superior e inferior del campo de juego.</p>
 *
 * <p><b>Algoritmo de predicción:</b></p>
 * <ol>
 *   <li>Calcula el tiempo que tardará la pelota en alcanzar la coordenada X objetivo</li>
 *   <li>Determina el desplazamiento vertical en ese tiempo</li>
 *   <li>Aplica lógica de reflexión para simular rebotes en los límites del campo</li>
 *   <li>Retorna la posición Y final donde impactará la pelota</li>
 * </ol>
 *
 *
 * @author Equipo Polimorfo
 * @version 2.0
 * @see Pelota
 * @see EstrategiaMovimientoIA
 */
public final class CalcularTrayectoria {

    private final double limiteSuperior;
    private final double limiteInferior;
    private final double alturaCampo;

    /**
     * Construye un calculador de trayectoria con los límites especificados del campo.
     *
     * @param limiteSuperior coordenada Y del límite superior del campo
     * @param limiteInferior coordenada Y del límite inferior del campo
     * @throws IllegalArgumentException si {@code limiteInferior} es menor o igual a {@code limiteSuperior}
     */
    private CalcularTrayectoria(final double limiteSuperior, final double limiteInferior) {
        if (limiteInferior <= limiteSuperior) {
            throw new IllegalArgumentException(
                    String.format("El límite inferior (%.2f) debe ser mayor que el límite superior (%.2f)",
                            limiteInferior, limiteSuperior));
        }
        this.limiteSuperior = limiteSuperior;
        this.limiteInferior = limiteInferior;
        this.alturaCampo = limiteInferior - limiteSuperior;
    }

    /**
     * Constructor por defecto que utiliza límites estándar del campo de juego.
     *
     * <p>Los valores por defecto son:</p>
     * <ul>
     *   <li>Límite superior: 0.0</li>
     *   <li>Límite inferior: 600.0</li>
     * </ul>
     *
     * <p><b>NOTA:</b> Este constructor se mantiene por compatibilidad con código existente.
     * Se recomienda usar {@link #crear(double, double)} para especificar límites explícitos.</p>
     */
    public CalcularTrayectoria() {
        this(0.0, 600.0);
    }

    /**
     * Método factory para crear un calculador de trayectoria con límites personalizados.
     *
     * @param limiteSuperior coordenada Y del límite superior del campo
     * @param limiteInferior coordenada Y del límite inferior del campo
     * @return una nueva instancia de {@code CalcularTrayectoria}
     * @throws IllegalArgumentException si los límites son inválidos
     */
    public static CalcularTrayectoria crear(final double limiteSuperior, final double limiteInferior) {
        return new CalcularTrayectoria(limiteSuperior, limiteInferior);
    }

    /**
     * Predice la coordenada Y donde la pelota impactará con la línea vertical en {@code xObjetivo}.
     *
     * <p>Este método implementa un algoritmo de predicción que considera:</p>
     * <ul>
     *   <li>La posición y velocidad actual de la pelota</li>
     *   <li>El tiempo necesario para alcanzar la coordenada X objetivo</li>
     *   <li>Rebotes múltiples en los límites superior e inferior del campo</li>
     * </ul>
     *
     * <p><b>Casos especiales:</b></p>
     * <ul>
     *   <li>Si la velocidad X es 0 (pelota no se mueve horizontalmente), retorna la posición Y actual</li>
     *   <li>Si el tiempo calculado es negativo (pelota se aleja), retorna la posición Y actual</li>
     * </ul>
     *
     * <p>El algoritmo de reflexión utiliza un patrón de espejo periódico:
     * <pre>
     * [superior → inferior → superior → inferior → ...]
     * </pre>
     *
     * @param pelota    la pelota cuya trayectoria se desea predecir. No debe ser {@code null}.
     * @param xObjetivo la coordenada X donde se desea predecir el impacto (típicamente la X de la paleta)
     * @return la coordenada Y predicha donde la pelota impactará en {@code xObjetivo}
     * @throws NullPointerException si {@code pelota} es {@code null}
     */
    public double predecirPosicionImpacto(final Pelota pelota, final double xObjetivo) {
        final double posX = pelota.obtenerX();
        final double posY = pelota.obtenerY();
        final double velX = pelota.obtenerVelocidadX();
        final double velY = pelota.obtenerVelocidadY();

        return Option.of(velX)
                .filter(vx -> vx != 0.0)
                .map(vx -> calcularTiempo(posX, xObjetivo, vx))
                .filter(tiempo -> tiempo >= 0.0)
                .map(tiempo -> calcularPosicionConRebotes(posY, velY, tiempo))
                .getOrElse(posY);
    }

    /**
     * Calcula el tiempo necesario para que la pelota alcance la coordenada X objetivo.
     *
     * <p>Fórmula: {@code tiempo = (xObjetivo - posX) / velX}</p>
     *
     * @param posX      posición X actual de la pelota
     * @param xObjetivo coordenada X objetivo
     * @param velX      velocidad X de la pelota
     * @return el tiempo en segundos necesario para alcanzar el objetivo
     */
    private double calcularTiempo(final double posX, final double xObjetivo, final double velX) {
        return (xObjetivo - posX) / velX;
    }

    /**
     * Calcula la posición Y final considerando rebotes en los límites del campo.
     *
     * <p>Este método implementa un algoritmo de reflexión periódica que simula
     * rebotes ilimitados de la pelota entre los límites superior e inferior.</p>
     *
     * @param posY   posición Y inicial de la pelota
     * @param velY   velocidad Y de la pelota
     * @param tiempo tiempo transcurrido hasta el impacto
     * @return la posición Y final después de considerar todos los rebotes
     */
    private double calcularPosicionConRebotes(final double posY, final double velY, final double tiempo) {
        final double desplazamientoY = velY * tiempo;
        final double yTentativa = posY + desplazamientoY;

        return aplicarReflexion(yTentativa);
    }

    /**
     * Aplica la lógica de reflexión para mapear una posición Y tentativa
     * al rango válido del campo, considerando rebotes ilimitados.
     *
     * <p>El algoritmo utiliza aritmética modular para simular un patrón de espejo periódico:</p>
     * <pre>
     * Rango total = 2 × altura del campo
     * Offset = (yTentativa - limiteSuperior) mod (2 × altura)
     *
     * Si offset ≤ altura:
     *     yFinal = limiteSuperior + offset  (bajando)
     * Si offset > altura:
     *     yFinal = limiteInferior - (offset - altura)  (rebotando hacia arriba)
     * </pre>
     *
     * @param yTentativa la posición Y tentativa sin considerar límites
     * @return la posición Y final mapeada al rango válido del campo
     */
    private double aplicarReflexion(final double yTentativa) {
        final double rango = 2.0 * alturaCampo;
        double offset = (yTentativa - limiteSuperior) % rango;

        if (offset < 0.0) {
            offset += rango;
        }

        return offset <= alturaCampo
                ? limiteSuperior + offset
                : limiteInferior - (offset - alturaCampo);
    }

    /**
     * Obtiene el límite superior del campo configurado.
     *
     * @return la coordenada Y del límite superior
     */
    public double obtenerLimiteSuperior() {
        return limiteSuperior;
    }

    /**
     * Obtiene el límite inferior del campo configurado.
     *
     * @return la coordenada Y del límite inferior
     */
    public double obtenerLimiteInferior() {
        return limiteInferior;
    }

    /**
     * Obtiene la altura total del campo de juego.
     *
     * @return la distancia entre el límite inferior y superior
     */
    public double obtenerAlturaCampo() {
        return alturaCampo;
    }

    /**
     * Retorna una representación en cadena de texto de este calculador.
     *
     * @return una cadena descriptiva con los límites del campo
     */
    @Override
    public String toString() {
        return String.format("CalcularTrayectoria[superior=%.1f, inferior=%.1f, altura=%.1f]",
                limiteSuperior, limiteInferior, alturaCampo);
    }
}
