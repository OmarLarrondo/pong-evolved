package patrones.factory.ia;

import io.vavr.collection.Array;
import io.vavr.control.Option;

/**
 * Enumeración que representa los 10 niveles de dificultad de la Inteligencia Artificial
 * en el juego Pong Evolved.
 *
 * <p>Cada nivel de dificultad determina el comportamiento de la IA mediante parámetros
 * específicos de velocidad de reacción y precisión. Los niveles están ordenados de menor
 * a mayor dificultad:</p>
 *
 * <ul>
 *   <li><b>NIVEL_1 (Principiante):</b> IA muy lenta y muy imprecisa</li>
 *   <li><b>NIVEL_2 (Novato):</b> IA lenta y bastante imprecisa</li>
 *   <li><b>NIVEL_3 (Aprendiz):</b> IA lenta e imprecisa</li>
 *   <li><b>NIVEL_4 (Básico):</b> Reacción moderada, algo imprecisa</li>
 *   <li><b>NIVEL_5 (Intermedio):</b> Reacción decente, precisión media</li>
 *   <li><b>NIVEL_6 (Competente):</b> Reacción rápida, bastante precisa</li>
 *   <li><b>NIVEL_7 (Avanzado):</b> Muy rápida, precisa</li>
 *   <li><b>NIVEL_8 (Experto):</b> Extremadamente rápida, muy precisa</li>
 *   <li><b>NIVEL_9 (Maestro):</b> Casi instantánea, casi perfecta</li>
 *   <li><b>NIVEL_10 (Leyenda):</b> Reacción instantánea, precisión casi perfecta</li>
 * </ul>
 *
 * <p>Esta enumeración proporciona métodos de utilidad basados en programación funcional
 * con Vavr para convertir entre números de nivel (1-10) y valores del enum, así como
 * para obtener descripciones legibles de cada nivel.</p>
 *
 * @author Equipo Polimorfo
 * @version 2.0
 * @see ConfiguracionDificultad
 * @see FabricaIA
 */
public enum DificultadIA {

    /**
     * Nivel 1 - Principiante: IA muy lenta y muy imprecisa.
     */
    NIVEL_1(1, "Principiante"),

    /**
     * Nivel 2 - Novato: IA lenta y bastante imprecisa.
     */
    NIVEL_2(2, "Novato"),

    /**
     * Nivel 3 - Aprendiz: IA lenta e imprecisa.
     */
    NIVEL_3(3, "Aprendiz"),

    /**
     * Nivel 4 - Básico: Reacción moderada, algo imprecisa.
     */
    NIVEL_4(4, "Básico"),

    /**
     * Nivel 5 - Intermedio: Reacción decente, precisión media.
     */
    NIVEL_5(5, "Intermedio"),

    /**
     * Nivel 6 - Competente: Reacción rápida, bastante precisa.
     */
    NIVEL_6(6, "Competente"),

    /**
     * Nivel 7 - Avanzado: Muy rápida, precisa.
     */
    NIVEL_7(7, "Avanzado"),

    /**
     * Nivel 8 - Experto: Extremadamente rápida, muy precisa.
     */
    NIVEL_8(8, "Experto"),

    /**
     * Nivel 9 - Maestro: Casi instantánea, casi perfecta.
     */
    NIVEL_9(9, "Maestro"),

    /**
     * Nivel 10 - Leyenda: Reacción instantánea, precisión casi perfecta.
     */
    NIVEL_10(10, "Leyenda");

    private final int numeroNivel;
    private final String descripcion;

    /**
     * Constructor privado para inicializar cada valor del enum.
     *
     * @param numeroNivel el número de nivel (1-10)
     * @param descripcion la descripción legible del nivel
     */
    DificultadIA(final int numeroNivel, final String descripcion) {
        this.numeroNivel = numeroNivel;
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el número de nivel asociado a esta dificultad.
     *
     * @return un valor entre 1 y 10
     */
    public int obtenerNumeroNivel() {
        return numeroNivel;
    }

    /**
     * Obtiene la descripción legible de este nivel de dificultad.
     *
     * @return la descripción del nivel (ej: "Principiante", "Maestro")
     */
    public String obtenerDescripcion() {
        return descripcion;
    }

    /**
     * Convierte un número de nivel (1-10) a su correspondiente valor del enum.
     *
     * <p>Este método utiliza programación funcional con {@code Option} de Vavr
     * para manejar de forma segura el caso donde el número de nivel es inválido.</p>
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * Option<DificultadIA> dificultad = DificultadIA.desdeNumeroNivel(5);
     * dificultad.forEach(d -> System.out.println(d.obtenerDescripcion())); // "Intermedio"
     * </pre>
     *
     * @param numeroNivel el número de nivel a convertir (1-10)
     * @return un {@code Option} que contiene el valor del enum si el número es válido,
     *         o {@code Option.none()} si el número está fuera del rango [1, 10]
     */
    public static Option<DificultadIA> desdeNumeroNivel(final int numeroNivel) {
        return Array.of(values())
                .find(d -> d.numeroNivel == numeroNivel);
    }

    /**
     * Convierte un número de nivel (1-10) a su correspondiente valor del enum,
     * lanzando una excepción si el número es inválido.
     *
     * <p>Este método es útil cuando se prefiere manejar errores de forma imperativa
     * en lugar del estilo funcional con {@code Option}.</p>
     *
     * @param numeroNivel el número de nivel a convertir (1-10)
     * @return el valor del enum correspondiente
     * @throws IllegalArgumentException si {@code numeroNivel} está fuera del rango [1, 10]
     */
    public static DificultadIA desdeNumeroNivelDirecto(final int numeroNivel) {
        return desdeNumeroNivel(numeroNivel)
                .getOrElseThrow(() -> new IllegalArgumentException(
                        String.format("Número de nivel inválido: %d. Debe estar entre 1 y 10.", numeroNivel)));
    }

    /**
     * Obtiene todos los niveles de dificultad como un arreglo inmutable de Vavr.
     *
     * @return un {@code Array} inmutable con todos los niveles de dificultad
     */
    public static Array<DificultadIA> obtenerTodosLosNiveles() {
        return Array.of(values());
    }

    /**
     * Verifica si un número de nivel es válido (está en el rango [1, 10]).
     *
     * @param numeroNivel el número a verificar
     * @return {@code true} si el número está en el rango válido, {@code false} en caso contrario
     */
    public static boolean esNivelValido(final int numeroNivel) {
        return numeroNivel >= 1 && numeroNivel <= 10;
    }

    /**
     * Retorna una representación en cadena de texto de este nivel de dificultad.
     *
     * @return una cadena con el formato "NIVEL_X (Descripción)"
     */
    @Override
    public String toString() {
        return String.format("%s (%s)", name(), descripcion);
    }
}
