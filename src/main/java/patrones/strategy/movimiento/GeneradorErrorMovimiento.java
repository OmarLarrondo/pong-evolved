package patrones.strategy.movimiento;

import java.util.Random;

/**
 * Generador de errores aleatorios para simular imprecisión en el movimiento de la IA.
 *
 * <p>Esta clase se utiliza para hacer que la inteligencia artificial del juego Pong
 * tenga un comportamiento más realista y variable, introduciendo desviaciones aleatorias
 * en sus predicciones de la trayectoria de la pelota.</p>
 *
 * <p><b>Características:</b></p>
 * <ul>
 *   <li>Genera desplazamientos aleatorios dentro de un rango configurable</li>
 *   <li>Los errores pueden ser positivos o negativos</li>
 *   <li>La amplitud del error determina el nivel de imprecisión de la IA</li>
 *   <li>Amplitudes mayores resultan en una IA más fácil de vencer</li>
 * </ul>
 *
 * <p><b>Uso típico:</b></p>
 * <pre>
 * // IA con error de 20 píxeles
 * GeneradorErrorMovimiento generador = new GeneradorErrorMovimiento(20.0);
 * double posicionObjetivo = calcularPuntoImpacto();
 * double posicionConError = posicionObjetivo + generador.generarError();
 * </pre>
 *
 * @author Equipo Polimorfo
 * @version 2.0
 * @see EstrategiaMovimientoIA
 */
public class GeneradorErrorMovimiento {

    /**
     * Amplitud máxima del error generado en píxeles.
     * El error real estará en el rango [-amplitudError, +amplitudError].
     */
    private double amplitudError;

    /**
     * Generador de números aleatorios para crear las desviaciones.
     */
    private final Random random = new Random();

    /**
     * Construye un generador de errores con la amplitud especificada.
     *
     * <p>La amplitud determina qué tan imprecisa será la IA. Valores más altos
     * producen errores más grandes y una IA más fácil de vencer.</p>
     *
     * @param amplitudError amplitud máxima del error en píxeles. Debe estar en el rango [0.0, 40.0].
     * @throws IllegalArgumentException si la amplitud está fuera del rango válido
     */
    public GeneradorErrorMovimiento(double amplitudError) {
        if(amplitudError > 40 || amplitudError<0){
            throw new IllegalArgumentException("La amplitud del error debe estar entre 0.0 y 40.0 píxeles");
        }
        this.amplitudError = amplitudError;
    }

    /**
     * Genera un desplazamiento aleatorio dentro del rango configurado.
     *
     * <p>Este método produce valores aleatorios distribuidos uniformemente en el
     * intervalo [-amplitudError, +amplitudError]. Cada invocación retorna un valor
     * diferente e independiente.</p>
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * GeneradorErrorMovimiento generador = new GeneradorErrorMovimiento(15.0);
     * double error1 = generador.generarError(); // Ej: -8.3
     * double error2 = generador.generarError(); // Ej: +12.7
     * double error3 = generador.generarError(); // Ej: -2.1
     * </pre>
     *
     * @return un desplazamiento aleatorio en píxeles, en el rango [-amplitudError, +amplitudError]
     */
    public double generarError() {
        return (random.nextDouble() * 2 - 1) * amplitudError;
    }

}
