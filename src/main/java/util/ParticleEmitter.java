package util;

import io.vavr.collection.List;
import io.vavr.control.Try;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Emisor de partículas para efectos visuales en el juego.
 * Genera y renderiza sistemas de partículas para eventos del juego
 * como colisiones, destrucción de bloques, celebraciones y otros efectos visuales.
 * Proporciona una implementación funcional para la creación y manipulación de partículas.
 */
public final class ParticleEmitter {

    private static final Random RANDOM = new Random();
    private static final double VELOCIDAD_PARTICULA_MIN = 50.0;
    private static final double VELOCIDAD_PARTICULA_MAX = 150.0;
    private static final double VIDA_PARTICULA_DEFECTO = 1.0;
    private static final double GRAVEDAD = 200.0;

    /**
     * Constructor privado para prevenir instanciación de esta clase de utilidad.
     */
    private ParticleEmitter() {
        throw new AssertionError("Clase utilitaria no instanciable");
    }

    /**
     * Clase que representa una partícula individual inmutable dentro de un sistema de partículas.
     * Cada partícula tiene propiedades como posición, velocidad, vida y color que evolucionan con el tiempo.
     */
    public static final class Particula {
        private final double x;
        private final double y;
        private final double velocidadX;
        private final double velocidadY;
        private final double vida;
        private final double vidaMaxima;
        private final Color color;
        private final double tamanio;

        /**
         * Constructor privado para crear una nueva partícula con los parámetros especificados.
         *
         * @param x           Posición horizontal inicial de la partícula
         * @param y           Posición vertical inicial de la partícula
         * @param velocidadX  Velocidad horizontal de la partícula
         * @param velocidadY  Velocidad vertical de la partícula
         * @param vida        Vida restante actual de la partícula
         * @param vidaMaxima  Vida máxima con la que fue creada la partícula
         * @param color       Color de la partícula para renderizado
         * @param tamanio     Tamaño de la partícula para renderizado
         */
        private Particula(final double x, final double y, final double velocidadX, final double velocidadY,
                          final double vida, final double vidaMaxima, final Color color, final double tamanio) {
            this.x = x;
            this.y = y;
            this.velocidadX = velocidadX;
            this.velocidadY = velocidadY;
            this.vida = vida;
            this.vidaMaxima = vidaMaxima;
            this.color = color;
            this.tamanio = tamanio;
        }

        /**
         * Actualiza el estado de la partícula según el tiempo transcurrido.
         * Aplica física básica como gravedad y actualiza posición y vida.
         *
         * @param delta Tiempo transcurrido en segundos desde la última actualización
         * @return Nueva instancia de Particula con el estado actualizado
         */
        public Particula actualizar(final double delta) {
            final double nuevaVida = vida - delta;
            final double nuevaX = x + velocidadX * delta;
            final double nuevaY = y + velocidadY * delta + GRAVEDAD * delta * delta * 0.5;
            final double nuevaVelocidadY = velocidadY + GRAVEDAD * delta;

            return new Particula(nuevaX, nuevaY, velocidadX, nuevaVelocidadY,
                    nuevaVida, vidaMaxima, color, tamanio);
        }

        /**
         * Verifica si la partícula sigue estando activa (tiene vida restante).
         *
         * @return true si la partícula aún tiene vida (vida > 0), false en caso contrario
         */
        public boolean estaViva() {
            return vida > 0;
        }

        /**
         * Renderiza la partícula en el contexto gráfico proporcionado.
         * Dibuja un círculo que representa la partícula con su color y opacidad actual.
         *
         * @param gc Contexto gráfico donde se dibujará la partícula
         */
        public void renderizar(final GraphicsContext gc) {
            final double opacidad = Math.max(0.0, Math.min(1.0, vida / vidaMaxima));
            final Color colorConOpacidad = Color.color(
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    opacidad
            );

            gc.setFill(colorConOpacidad);
            gc.fillOval(x - tamanio / 2, y - tamanio / 2, tamanio, tamanio);
        }
    }

    /**
     * Clase que representa un sistema de partículas inmutable que gestiona una colección
     * de partículas individuales para efectos visuales en el juego.
     */
    public static final class SistemaParticulas {
        private final List<Particula> particulas;

        /**
         * Constructor privado para crear un sistema de partículas con una lista específica de partículas.
         *
         * @param particulas Lista inmutable de partículas que componen el sistema
         */
        private SistemaParticulas(final List<Particula> particulas) {
            this.particulas = particulas;
        }

        /**
         * Crea un nuevo sistema de partículas vacío, sin partículas iniciales.
         *
         * @return Nuevo sistema de partículas vacío
         */
        public static SistemaParticulas vacio() {
            return new SistemaParticulas(List.empty());
        }

        /**
         * Actualiza el estado de todas las partículas en el sistema según el tiempo transcurrido.
         * Elimina las partículas que han expirado su vida útil.
         *
         * @param delta Tiempo transcurrido en segundos desde la última actualización
         * @return Nuevo sistema de partículas con las partículas actualizadas y expiradas filtradas
         */
        public SistemaParticulas actualizar(final double delta) {
            final List<Particula> particulasActualizadas = particulas
                    .map(p -> p.actualizar(delta))
                    .filter(Particula::estaViva);

            return new SistemaParticulas(particulasActualizadas);
        }

        /**
         * Renderiza todas las partículas activas en el sistema en el contexto gráfico proporcionado.
         *
         * @param gc Contexto gráfico donde se dibujarán las partículas
         * @return Try que contiene Unit si la operación de renderizado fue exitosa, o una excepción en caso de error
         */
        public Try<Void> renderizar(final GraphicsContext gc) {
            return Try.run(() -> particulas.forEach(p -> p.renderizar(gc)));
        }

        /**
         * Agrega nuevas partículas al sistema existente, creando un nuevo sistema que contiene
         * todas las partículas originales más las nuevas.
         *
         * @param nuevasParticulas Lista de nuevas partículas a agregar al sistema
         * @return Nuevo sistema de partículas que incluye las partículas originales y las nuevas
         */
        public SistemaParticulas agregar(final List<Particula> nuevasParticulas) {
            return new SistemaParticulas(particulas.appendAll(nuevasParticulas));
        }

        /**
         * Obtiene la cantidad actual de partículas activas en el sistema.
         *
         * @return Número de partículas activas en el sistema
         */
        public int obtenerCantidad() {
            return particulas.size();
        }

        /**
         * Verifica si el sistema de partículas está vacío (no contiene partículas).
         *
         * @return true si el sistema no contiene partículas, false en caso contrario
         */
        public boolean estaVacio() {
            return particulas.isEmpty();
        }
    }

    /**
     * Crea un efecto de explosión en una posición dada con partículas radiales.
     * Las partículas se emiten desde un punto central en todas direcciones.
     *
     * @param x            Posición horizontal del centro de la explosión
     * @param y            Posición vertical del centro de la explosión
     * @param cantidad     Número de partículas a generar para el efecto
     * @param color        Color que tendrán las partículas del efecto
     * @param intensidad   Intensidad de la explosión que afecta la velocidad de las partículas (0.0 a 1.0)
     * @return Lista inmutable de partículas que representan el efecto de explosión
     */
    public static List<Particula> crearExplosion(final double x, final double y, final int cantidad,
                                                  final Color color, final double intensidad) {
        return List.range(0, cantidad)
                .map(i -> {
                    final double angulo = (2.0 * Math.PI * i) / cantidad;
                    final double velocidad = VELOCIDAD_PARTICULA_MIN +
                            (VELOCIDAD_PARTICULA_MAX - VELOCIDAD_PARTICULA_MIN) * intensidad;
                    final double velocidadX = Math.cos(angulo) * velocidad;
                    final double velocidadY = Math.sin(angulo) * velocidad;
                    final double tamanio = 2.0 + RANDOM.nextDouble() * 4.0;
                    final double vida = VIDA_PARTICULA_DEFECTO * (0.5 + RANDOM.nextDouble() * 0.5);

                    return new Particula(x, y, velocidadX, velocidadY, vida, vida, color, tamanio);
                });
    }

    /**
     * Crea un efecto de chispas aleatorias desde una posición central.
     * Las partículas se emiten en direcciones aleatorias con una ligera inclinación hacia arriba.
     *
     * @param x          Posición horizontal del centro del efecto de chispas
     * @param y          Posición vertical del centro del efecto de chispas
     * @param cantidad   Número de chispas a generar
     * @param color      Color que tendrán las chispas
     * @return Lista inmutable de partículas que representan el efecto de chispas
     */
    public static List<Particula> crearChispas(final double x, final double y, final int cantidad,
                                                final Color color) {
        return List.range(0, cantidad)
                .map(i -> {
                    final double angulo = RANDOM.nextDouble() * 2.0 * Math.PI;
                    final double velocidad = VELOCIDAD_PARTICULA_MIN +
                            RANDOM.nextDouble() * (VELOCIDAD_PARTICULA_MAX - VELOCIDAD_PARTICULA_MIN);
                    final double velocidadX = Math.cos(angulo) * velocidad;
                    final double velocidadY = Math.sin(angulo) * velocidad - 50.0;
                    final double tamanio = 1.0 + RANDOM.nextDouble() * 3.0;
                    final double vida = 0.5 + RANDOM.nextDouble() * 0.5;

                    return new Particula(x, y, velocidadX, velocidadY, vida, vida, color, tamanio);
                });
    }

    /**
     * Crea un efecto de confeti de celebración con múltiples colores.
     * Las partículas se emiten hacia arriba con gravedad para simular caída.
     *
     * @param x          Posición horizontal del centro del efecto de confeti
     * @param y          Posición vertical del centro del efecto de confeti
     * @param cantidad   Número de piezas de confeti a generar
     * @return Lista inmutable de partículas que representan el efecto de confeti
     */
    public static List<Particula> crearConfeti(final double x, final double y, final int cantidad) {
        final List<Color> colores = List.of(
                Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE,
                Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK
        );

        return List.range(0, cantidad)
                .map(i -> {
                    final double angulo = RANDOM.nextDouble() * 2.0 * Math.PI;
                    final double velocidad = VELOCIDAD_PARTICULA_MIN +
                            RANDOM.nextDouble() * (VELOCIDAD_PARTICULA_MAX - VELOCIDAD_PARTICULA_MIN);
                    final double velocidadX = Math.cos(angulo) * velocidad * 0.5;
                    final double velocidadY = Math.sin(angulo) * velocidad - 100.0;
                    final double tamanio = 3.0 + RANDOM.nextDouble() * 5.0;
                    final double vida = 1.5 + RANDOM.nextDouble();
                    final Color color = colores.get(RANDOM.nextInt(colores.size()));

                    return new Particula(x, y, velocidadX, velocidadY, vida, vida, color, tamanio);
                });
    }

    /**
     * Crea un efecto de fragmentos cuando un bloque es destruido.
     * Las partículas se generan desde el centro del bloque destruido.
     *
     * @param x          Posición horizontal del bloque destruido
     * @param y          Posición vertical del bloque destruido
     * @param ancho      Ancho del bloque destruido
     * @param alto       Alto del bloque destruido
     * @param color      Color del bloque que se replica en los fragmentos
     * @return Lista inmutable de partículas que representan los fragmentos del bloque destruido
     */
    public static List<Particula> crearFragmentosBloque(final double x, final double y,
                                                         final double ancho, final double alto,
                                                         final Color color) {
        final int cantidad = 8 + RANDOM.nextInt(8);
        final double centroX = x + ancho / 2;
        final double centroY = y + alto / 2;

        return List.range(0, cantidad)
                .map(i -> {
                    final double angulo = (2.0 * Math.PI * i) / cantidad + (RANDOM.nextDouble() - 0.5) * 0.5;
                    final double velocidad = 100.0 + RANDOM.nextDouble() * 100.0;
                    final double velocidadX = Math.cos(angulo) * velocidad;
                    final double velocidadY = Math.sin(angulo) * velocidad - 50.0;
                    final double tamanio = 3.0 + RANDOM.nextDouble() * 4.0;
                    final double vida = 0.8 + RANDOM.nextDouble() * 0.4;

                    return new Particula(centroX, centroY, velocidadX, velocidadY, vida, vida, color, tamanio);
                });
    }

    /**
     * Crea un efecto de impacto cuando ocurre una colisión.
     * Las partículas se emiten en una dirección específica basada en la dirección de impacto.
     *
     * @param x          Posición horizontal del punto de impacto
     * @param y          Posición vertical del punto de impacto
     * @param direccionX Componente horizontal de la dirección del impacto (-1, 0, 1)
     * @param direccionY Componente vertical de la dirección del impacto (-1, 0, 1)
     * @param color      Color de las partículas que representan el efecto de impacto
     * @return Lista inmutable de partículas que representan el efecto de impacto de la colisión
     */
    public static List<Particula> crearImpacto(final double x, final double y,
                                                final double direccionX, final double direccionY,
                                                final Color color) {
        final int cantidad = 5 + RANDOM.nextInt(6);

        return List.range(0, cantidad)
                .map(i -> {
                    final double dispersión = 0.5;
                    final double anguloBase = Math.atan2(direccionY, direccionX);
                    final double angulo = anguloBase + (RANDOM.nextDouble() - 0.5) * Math.PI * dispersión;
                    final double velocidad = 80.0 + RANDOM.nextDouble() * 80.0;
                    final double velocidadX = Math.cos(angulo) * velocidad;
                    final double velocidadY = Math.sin(angulo) * velocidad;
                    final double tamanio = 2.0 + RANDOM.nextDouble() * 3.0;
                    final double vida = 0.3 + RANDOM.nextDouble() * 0.3;

                    return new Particula(x, y, velocidadX, velocidadY, vida, vida, color, tamanio);
                });
    }
}
