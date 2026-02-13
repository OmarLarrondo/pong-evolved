package patrones.strategy.movimiento;

/**
 * Temporizador que controla la frecuencia de reacción de la inteligencia artificial.
 *
 * <p>Esta clase simula el tiempo de reacción humano para hacer que la IA sea más realista
 * y ajustable en dificultad. En lugar de reaccionar instantáneamente a cada cambio en la
 * trayectoria de la pelota, la IA debe esperar un intervalo de tiempo antes de poder
 * calcular un nuevo movimiento.</p>
 *
 * <p><b>Funcionamiento:</b></p>
 * <ol>
 *   <li>El temporizador acumula el tiempo transcurrido en cada frame</li>
 *   <li>Cuando el tiempo acumulado alcanza el intervalo configurado, permite una reacción</li>
 *   <li>Después de cada reacción, el temporizador se reinicia automáticamente</li>
 * </ol>
 *
 * <p><b>Impacto en la dificultad:</b></p>
 * <ul>
 *   <li><b>Intervalos cortos (0.01-0.05s):</b> IA muy reactiva y difícil</li>
 *   <li><b>Intervalos medios (0.1-0.3s):</b> IA moderada</li>
 *   <li><b>Intervalos largos (0.5-2.0s):</b> IA lenta y fácil de vencer</li>
 * </ul>
 *
 * <p><b>Ejemplo de uso:</b></p>
 * <pre>
 * // IA que reacciona cada 0.2 segundos
 * TemporizadorReaccion temporizador = new TemporizadorReaccion(0.2);
 *
 * // En el game loop:
 * temporizador.actualizar(deltaTime);
 * if (temporizador.puedeReaccionar()) {
 *     calcularNuevoMovimiento();
 * }
 * </pre>
 *
 * @author Equipo Polimorfo
 * @version 2.0
 * @see EstrategiaMovimientoIA
 */
public class TemporizadorReaccion {

    /**
     * Tiempo acumulado en segundos desde la última reacción de la IA.
     */
    private double tiempoAcumulado;

    /**
     * Intervalo en segundos que debe transcurrir antes de permitir una nueva reacción.
     * Este valor determina qué tan rápido puede reaccionar la IA a cambios en el juego.
     */
    private double intervaloReaccion;

    /**
     * Construye un temporizador con el intervalo de reacción especificado.
     *
     * <p>El intervalo determina qué tan frecuentemente la IA puede recalcular su movimiento.
     * Valores más pequeños resultan en una IA más reactiva y difícil.</p>
     *
     * @param intervaloReaccion tiempo en segundos entre reacciones permitidas. Debe ser mayor que 0.
     */
    public TemporizadorReaccion(double intervaloReaccion) {
        this.intervaloReaccion = intervaloReaccion;
    }

    /**
     * Actualiza el temporizador con el tiempo transcurrido desde el último frame.
     *
     * <p>Este método debe ser invocado en cada iteración del game loop para acumular
     * el tiempo. Cuando el tiempo acumulado alcance el intervalo configurado,
     * {@link #puedeReaccionar()} retornará {@code true}.</p>
     *
     * @param tiempoDelta tiempo transcurrido desde el último frame en segundos
     */
    public void actualizar(double tiempoDelta){
        tiempoAcumulado += tiempoDelta;
    }

    /**
     * Verifica si la IA puede reaccionar en este momento.
     *
     * <p>Si el tiempo acumulado ha alcanzado o superado el intervalo de reacción,
     * este método retorna {@code true} y reinicia automáticamente el temporizador
     * a cero. Esto permite que la IA reaccione y comience a contar el tiempo
     * hasta la próxima reacción permitida.</p>
     *
     * <p><b>Comportamiento:</b></p>
     * <ul>
     *   <li>Si {@code tiempoAcumulado >= intervaloReaccion}: retorna {@code true} y reinicia el contador</li>
     *   <li>Si {@code tiempoAcumulado < intervaloReaccion}: retorna {@code false} sin cambios</li>
     * </ul>
     *
     * <p><b>Ejemplo de uso en el game loop:</b></p>
     * <pre>
     * temporizador.actualizar(deltaTime);
     * if (temporizador.puedeReaccionar()) {
     *     Direccion nuevaDireccion = calcularMovimientoIA();
     *     paleta.moverEnDireccion(nuevaDireccion, deltaTime);
     * }
     * </pre>
     *
     * @return {@code true} si ha transcurrido suficiente tiempo para permitir una reacción,
     *         {@code false} en caso contrario
     */
    public boolean puedeReaccionar(){
        if(tiempoAcumulado >= intervaloReaccion){
            tiempoAcumulado = 0;
            return true;
        }else {
            return false;
        }
    }

}
