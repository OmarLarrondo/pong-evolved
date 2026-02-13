package util;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import mvc.modelo.entidades.Bloque;
import mvc.modelo.entidades.ObjetoJuego;
import mvc.modelo.entidades.paleta.Paleta;
import mvc.modelo.entidades.pelota.Pelota;
import mvc.modelo.enums.LadoHorizontal;
import mvc.modelo.items.Item;

/**
 * Clase utilitaria para el renderizado de entidades del juego.
 * Implementa el renderizado visual de todas las entidades del juego en un Canvas JavaFX.
 * Proporciona métodos para dibujar pelotas, paletas, bloques, ítems y efectos visuales.
 */
public final class RenderizadorJuego {

    private static final double RADIO_PELOTA_BASE = 5.0;
    private static final int SEGMENTOS_TRAIL = 5;
    private static final double OPACIDAD_TRAIL_BASE = 0.6;

    /**
     * Constructor privado para prevenir instanciación de esta clase de utilidad.
     */
    private RenderizadorJuego() {
        throw new AssertionError("Clase utilitaria no instanciable");
    }

    /**
     * Renderiza una pelota en el contexto gráfico especificado.
     * Dibuja la pelota con un efecto de estela y un brillo para mejorar la visibilidad.
     *
     * @param gc     Contexto gráfico donde se dibujará la pelota
     * @param pelota Instancia de Pelota que se va a renderizar
     * @return Try conteniendo Unit si la operación de renderizado fue exitosa, o una excepción en caso de error
     */
    public static Try<Void> renderizarPelota(final GraphicsContext gc, final Pelota pelota) {
        return Try.run(() -> {
            final double x = pelota.obtenerX();
            final double y = pelota.obtenerY();
            final double radio = calcularRadioPelota(pelota);

            renderizarTrailPelota(gc, x, y, radio, pelota);

            gc.setFill(Color.WHITE);
            gc.fillOval(x - radio, y - radio, radio * 2, radio * 2);

            gc.setFill(Color.color(1.0, 1.0, 1.0, 0.5));
            gc.fillOval(x - radio * 0.5, y - radio * 0.5, radio, radio);
        });
    }

    /**
     * Renderiza una paleta en el contexto gráfico especificado.
     * Dibuja el cuerpo de la paleta con color específico.
     *
     * @param gc     Contexto gráfico donde se dibujará la paleta
     * @param paleta Instancia de Paleta que se va a renderizar
     * @return Try conteniendo Unit si la operación de renderizado fue exitosa, o una excepción en caso de error
     */
    public static Try<Void> renderizarPaleta(final GraphicsContext gc, final Paleta paleta) {
        return Try.run(() -> {
            final double x = paleta.obtenerX();
            final double y = paleta.obtenerY();
            final double ancho = paleta.obtenerAncho();
            final double alto = paleta.obtenerAlto();

            gc.setFill(paleta.obtenerColor());
            gc.fillRect(x, y, ancho, alto);

            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2.0);
            gc.strokeRect(x, y, ancho, alto);
        });
    }

    /**
     * Renderiza un bloque en el contexto gráfico especificado.
     * Dibuja el bloque con color basado en su resistencia y muestra el número de resistencia si es mayor a 1.
     *
     * @param gc     Contexto gráfico donde se dibujará el bloque
     * @param bloque Instancia de Bloque que se va a renderizar
     * @return Try conteniendo Unit si la operación de renderizado fue exitosa, o una excepción en caso de error
     */
    public static Try<Void> renderizarBloque(final GraphicsContext gc, final Bloque bloque) {
        return Try.run(() -> {
            if (bloque.estaDestruido()) {
                return;
            }

            final double x = bloque.obtenerX();
            final double y = bloque.obtenerY();
            final double ancho = bloque.obtenerAncho();
            final double alto = bloque.obtenerAlto();
            final int resistencia = bloque.obtenerResistencia();

            final Color color = calcularColorBloque(resistencia);

            gc.setFill(color);
            gc.fillRect(x, y, ancho, alto);

            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1.5);
            gc.strokeRect(x, y, ancho, alto);

            if (resistencia > 1) {
                renderizarIndicadorResistencia(gc, x, y, ancho, alto, resistencia);
            }
        });
    }

    /**
     * Renderiza una lista de bloques en el contexto gráfico especificado.
     * Filtra los bloques destruidos y renderiza solo los que están activos.
     *
     * @param gc      Contexto gráfico donde se dibujarán los bloques
     * @param bloques Lista inmutable de bloques que se van a renderizar
     * @return Try conteniendo Unit si la operación de renderizado fue exitosa, o una excepción en caso de error
     */
    public static Try<Void> renderizarBloques(final GraphicsContext gc, final List<Bloque> bloques) {
        return Try.run(() ->
                bloques
                        .filter(bloque -> !bloque.estaDestruido())
                        .forEach(bloque -> renderizarBloque(gc, bloque))
        );
    }

    /**
     * Renderiza una lista de ítems en el contexto gráfico especificado.
     * Filtra los ítems inactivos y renderiza solo los que están activos.
     *
     * @param gc    Contexto gráfico donde se dibujarán los ítems
     * @param items Lista inmutable de ítems que se van a renderizar
     * @return Try conteniendo Unit si la operación de renderizado fue exitosa, o una excepción en caso de error
     */
    public static Try<Void> renderizarItems(final GraphicsContext gc, final List<Item> items) {
        return Try.run(() ->
                items
                        .filter(Item::estaActivo)
                        .forEach(item -> renderizarItem(gc, item))
        );
    }

    /**
     * Renderiza un ítem individual en el contexto gráfico especificado.
     * Dibuja un círculo amarillo con borde dorado y el símbolo "?" en el centro.
     *
     * @param gc   Contexto gráfico donde se dibujará el ítem
     * @param item Instancia de Item que se va a renderizar
     */
    private static void renderizarItem(final GraphicsContext gc, final Item item) {
        final double x = item.obtenerX();
        final double y = item.obtenerY();
        final double ancho = item.obtenerAncho();
        final double alto = item.obtenerAlto();

        gc.setFill(Color.YELLOW);
        gc.fillOval(x, y, ancho, alto);

        gc.setStroke(Color.GOLD);
        gc.setLineWidth(2.0);
        gc.strokeOval(x, y, ancho, alto);

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Press Start 2P", 10));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("?", x + ancho / 2, y + alto / 2 + 3);
    }

    /**
     * Renderiza el efecto de estela de la pelota en el contexto gráfico especificado.
     * Dibuja múltiples versiones descoloridas de la pelota detrás de la posición actual para simular movimiento.
     *
     * @param gc     Contexto gráfico donde se dibujará el efecto de estela
     * @param x      Posición horizontal actual de la pelota
     * @param y      Posición vertical actual de la pelota
     * @param radio  Radio de la pelota para dimensionar la estela
     * @param pelota Instancia de Pelota para obtener la velocidad y calcular la dirección de la estela
     */
    private static void renderizarTrailPelota(final GraphicsContext gc, final double x, final double y,
                                               final double radio, final Pelota pelota) {
        final double velocidadX = pelota.obtenerVelocidadX();
        final double velocidadY = pelota.obtenerVelocidadY();

        for (int i = 1; i <= SEGMENTOS_TRAIL; i++) {
            final double factor = (double) i / SEGMENTOS_TRAIL;
            final double trailX = x - velocidadX * factor * 2;
            final double trailY = y - velocidadY * factor * 2;
            final double opacidad = OPACIDAD_TRAIL_BASE * (1.0 - factor);

            gc.setFill(Color.color(1.0, 1.0, 1.0, opacidad));
            gc.fillOval(trailX - radio * 0.8, trailY - radio * 0.8, radio * 1.6, radio * 1.6);
        }
    }

    /**
     * Renderiza el indicador de resistencia en un bloque en el contexto gráfico especificado.
     * Muestra el número de resistencia restante en el centro del bloque.
     *
     * @param gc          Contexto gráfico donde se dibujará el indicador de resistencia
     * @param x           Posición horizontal del bloque
     * @param y           Posición vertical del bloque
     * @param ancho       Ancho del bloque
     * @param alto        Alto del bloque
     * @param resistencia Valor numérico de la resistencia actual del bloque
     */
    private static void renderizarIndicadorResistencia(final GraphicsContext gc, final double x, final double y,
                                                        final double ancho, final double alto, final int resistencia) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Press Start 2P", 8));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(resistencia), x + ancho / 2, y + alto / 2 + 3);
    }

    /**
     * Calcula el radio de la pelota basado en sus dimensiones.
     * Toma el valor mínimo entre ancho y alto y lo divide por 2.
     *
     * @param pelota Instancia de Pelota de la que se calculará el radio
     * @return El radio calculado de la pelota
     */
    private static double calcularRadioPelota(final Pelota pelota) {
        return Math.min(pelota.obtenerAncho(), pelota.obtenerAlto()) / 2.0;
    }

    /**
     * Calcula el color de un bloque basado en su resistencia.
     * Mapea valores de resistencia a colores específicos para visualización.
     *
     * @param resistencia Valor numérico de la resistencia del bloque
     * @return Color correspondiente basado en la resistencia
     */
    private static Color calcularColorBloque(final int resistencia) {
        return Option.of(resistencia)
                .map(r -> {
                    if (r >= 5) return Color.DARKRED;
                    if (r >= 4) return Color.RED;
                    if (r >= 3) return Color.ORANGE;
                    if (r >= 2) return Color.YELLOW;
                    return Color.LIGHTGREEN;
                })
                .getOrElse(Color.WHITE);
    }

    /**
     * Renderiza el fondo del juego en el contexto gráfico especificado.
     * Dibuja un fondo negro con una línea central punteada para dividir el campo de juego.
     *
     * @param gc     Contexto gráfico donde se dibujará el fondo
     * @param ancho  Ancho del canvas donde se renderiza el juego
     * @param alto   Alto del canvas donde se renderiza el juego
     * @return Try conteniendo Unit si la operación de renderizado fue exitosa, o una excepción en caso de error
     */
    public static Try<Void> renderizarFondo(final GraphicsContext gc, final double ancho, final double alto) {
        return Try.run(() -> {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, ancho, alto);

            gc.setStroke(Color.color(1.0, 1.0, 1.0, 0.3));
            gc.setLineWidth(2.0);
            gc.setLineDashes(10, 10);
            gc.strokeLine(ancho / 2, 0, ancho / 2, alto);
            gc.setLineDashes(null);
        });
    }

    /**
     * Limpia completamente el canvas de renderizado estableciendo un área transparente.
     *
     * @param gc    Contexto gráfico del canvas que se va a limpiar
     * @param ancho Ancho del canvas que se va a limpiar
     * @param alto  Alto del canvas que se va a limpiar
     * @return Try conteniendo Unit si la operación de limpieza fue exitosa, o una excepción en caso de error
     */
    public static Try<Void> limpiarCanvas(final GraphicsContext gc, final double ancho, final double alto) {
        return Try.run(() -> gc.clearRect(0, 0, ancho, alto));
    }

    /**
     * Renderiza un efecto de neblina sobre el canvas para crear atmósfera o efectos visuales.
     * Aplica un filtro de desenfoque GaussianBlur para simular niebla en el juego.
     *
     * @param gc        Contexto gráfico donde se aplicará el efecto de neblina
     * @param ancho     Ancho del canvas donde se aplica el efecto
     * @param alto      Alto del canvas donde se aplica el efecto
     * @param intensidad Intensidad del efecto de neblina (0.0 para ninguno, 1.0 para máximo efecto)
     * @return Try conteniendo Unit si la operación de renderizado fue exitosa, o una excepción en caso de error
     */
    public static Try<Void> renderizarNeblina(final GraphicsContext gc, final double ancho, final double alto,
                                              final double intensidad) {
        return Try.run(() -> {
            gc.setFill(Color.color(0.5, 0.5, 0.5, intensidad * 0.5));
            gc.setEffect(new GaussianBlur(20));
            gc.fillRect(0, 0, ancho, alto);
            gc.setEffect(null);
        });
    }
}