package patrones.builder;

import mvc.modelo.entidades.Nivel;

/**
 * La interfaz {@code BuilderNivel} define el contrato que deben seguir
 * todos los constructores concretos de niveles dentro del juego Pong.
 *
 * <p>Aplica el patrón de diseño <strong>Builder</strong>, permitiendo construir
 * objetos de tipo {@link Nivel} paso a paso. Este enfoque facilita la creación
 * de niveles con diferentes configuraciones, tipos de bloques y grados de dificultad,
 * sin depender de una única forma de construcción.</p>
 *
 * <p>Cada implementación de esta interfaz debe mantener un estado interno del
 * nivel que se está construyendo, y retornar el propio objeto {@code BuilderNivel}
 * en cada método (patrón de diseño <em>fluent interface</em>), permitiendo encadenar
 * llamadas de manera legible y ordenada.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * BuilderNivel builder = new BuilderNivelConcreto();
 * Nivel nivel = builder
 *      .reiniciar()
 *      .establecerNombre("Nivel 1")
 *      .establecerDificultad(2)
 *      .agregarBloque(100, 200, TipoBloque.DESTRUCTIBLE)
 *      .construir();
 * }</pre>
 *
 * @see TipoBloque
 * @see patrones.factory.niveles.Nivel
 * @author Equipo-Polimorfo
 * @version 1.0
 */
public interface BuilderNivel {

    /**
     * Reinicia el estado interno del constructor, dejando el builder
     * preparado para comenzar la construcción de un nuevo nivel.
     *
     * @return esta misma instancia de {@code BuilderNivel}, para permitir
     *         el encadenamiento de métodos.
     */
    public BuilderNivel reiniciar();

    /**
     * Establece el nombre identificador del nivel.
     *
     * @param nombre nombre que se asignará al nivel.
     * @return esta misma instancia de {@code BuilderNivel}.
     */
    public BuilderNivel establecerNombre(String nombre);

    /**
     * Define el nivel de dificultad del nivel actual.
     * @param dificultad entero que representa la dificultad
     *                   (por ejemplo, (1,2,3) = fácil, (4,5,6,7) = medio, (8,9,10) = difícil).
     * @return esta misma instancia de {@code BuilderNivel}.
     */
    public BuilderNivel establecerDificultad(int dificultad);

    /**
     * Agrega un bloque al nivel en la posición indicada y del tipo especificado.
     *
     * @param x    coordenada X donde se colocará el bloque.
     * @param y    coordenada Y donde se colocará el bloque.
     * @param tipo tipo de bloque a crear (ver {@link TipoBloque}).
     * @return esta misma instancia de {@code BuilderNivel}.
     */
    public BuilderNivel agregarBloque(double x, double y, TipoBloque tipo);

    /**
     * Finaliza el proceso de construcción y devuelve una instancia completa
     * del nivel configurado.
     *
     * @return una nueva instancia de {@link Nivel} construida con los parámetros definidos.
     */
    public Nivel construir();
}
