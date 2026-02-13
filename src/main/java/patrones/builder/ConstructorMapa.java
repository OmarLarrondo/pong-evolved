package patrones.builder;

import java.util.ArrayList;
import java.util.List;

import mvc.modelo.entidades.Bloque;
import mvc.modelo.entidades.Nivel;

/**
 * Implementación concreta de {@link BuilderNivel} que permite construir niveles del juego Pong.
 * 
 * <p>Define cómo se crea un mapa (nivel), incluyendo su nombre, dificultad y los bloques
 * que lo componen. Utiliza el patrón <b>Builder</b> para proporcionar una forma flexible
 * de construir un {@link Nivel} paso a paso.</p>
 * 
 * <p>Cada bloque puede tener diferentes propiedades dependiendo de su {@link TipoBloque},
 * como su resistencia o comportamiento ante las colisiones.</p>
 * 
 * @author Equipo-Polimorfo
 * @version 1.0
 */
public class ConstructorMapa implements BuilderNivel {
    /** Nivel que se está construyendo. */
    private Nivel nivel;

    /** Lista de bloques que conforman el nivel. */
    private List<Bloque> bloques;

    /** {@inheritDoc} */
    @Override
    public ConstructorMapa reiniciar() {
        this.nivel = new Nivel();
        this.nivel.setNombre("Nivel sin nombre");
        this.nivel.setDificultad(1);
        this.bloques = new ArrayList<>();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ConstructorMapa establecerNombre(String nombre) {
        this.nivel.setNombre(nombre);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ConstructorMapa establecerDificultad(int dificultad) {
        this.nivel.setDificultad(dificultad);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>Dependiendo del tipo de bloque indicado, se asignan sus valores de resistencia
     * y propiedades predeterminadas.</p>
     */
    @Override
    public ConstructorMapa agregarBloque(double x, double y, TipoBloque tipo) {
        Bloque bloque;

        switch (tipo) {
            case DESTRUCTIBLE:
                bloque = new Bloque(x, y, 50, 20, 1, tipo);
                break;
            case INDESTRUCTIBLE:
                bloque = new Bloque(x, y, 50, 20, 999, tipo);
                break;
            case BONUS:
                bloque = new Bloque(x, y, 50, 20, 1, tipo);
                break;
            case MULTI_GOLPE:
                bloque = new Bloque(x, y, 50, 20, 3, tipo);
                break;
            default:
                bloque = new Bloque(x, y, 50, 20, 1, TipoBloque.DESTRUCTIBLE);
                break;
        }

        bloques.add(bloque);
        return this;
    }

    /**
     * Agrega un bloque destructible que puede eliminarse con un solo impacto.
     * 
     * @param x posición en el eje X donde se agregará el bloque.
     * @param y posición en el eje Y donde se agregará el bloque.
     * @return la misma instancia de {@link ConstructorMapa} con el bloque agregado.
     */
    public ConstructorMapa agregarBloqueDestructible(double x, double y) {
        return agregarBloque(x, y, TipoBloque.DESTRUCTIBLE);
    }

    /**
     * Agrega un bloque indestructible, es decir, que no puede romperse.
     * 
     * @param x posición en el eje X donde se agregará el bloque.
     * @param y posición en el eje Y donde se agregará el bloque.
     * @return la misma instancia de {@link ConstructorMapa} con el bloque agregado.
     */
    public ConstructorMapa agregarBloqueIndestructible(double x, double y) {
        return agregarBloque(x, y, TipoBloque.INDESTRUCTIBLE);
    }

    /**
     * Agrega un bloque de tipo bonus, el cual otorgará una recompensa o item
     * especial al ser destruido.
     * 
     * @param x posición en el eje X donde se agregará el bloque.
     * @param y posición en el eje Y donde se agregará el bloque.
     * @return la misma instancia de {@link ConstructorMapa} con el bloque agregado.
     */
    public ConstructorMapa agregarBloqueBonus(double x, double y) {
        return agregarBloque(x, y, TipoBloque.BONUS);
    }

    /**
     * Agrega un patrón de bloques predefinido.
     * 
     * <p>Este método añade una fila de bloques destructibles a partir de la posición indicada.
     * Se puede usar para generar estructuras o formaciones básicas rápidamente.</p>
     * 
     * @param x posición inicial en el eje X.
     * @param y posición inicial en el eje Y.
     * @return la misma instancia de {@link ConstructorMapa} con el patrón agregado.
     */
    public ConstructorMapa agregarPatronBloques(double x, double y) {
        for (int i = 0; i < 5; i++) {
            agregarBloqueDestructible(x + i * 55, y);
        }
        return this;
    }

    /**
     * Construye el objeto {@link Nivel} con los bloques y propiedades configuradas.
     * @return una nueva instancia de {@link Nivel} completamente construida.
     */
    @Override
    public Nivel construir() {
        this.nivel.setBloques(bloques);
        return this.nivel;
    }
}
