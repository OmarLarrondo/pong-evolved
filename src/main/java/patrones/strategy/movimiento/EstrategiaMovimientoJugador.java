package patrones.strategy.movimiento;

import mvc.modelo.entidades.paleta.Paleta;
import mvc.modelo.entidades.pelota.Pelota;
import patrones.adapter.AdaptadorEntrada;
import mvc.modelo.enums.Direccion;

/**
 * Estrategia de movimiento para un jugador humano.
 * 
 * <p>Esta estrategia traduce directamente las entradas del usuario
 * (por medio del {@link AdaptadorEntrada}) en movimientos de la paleta.
 * No realiza predicciones ni cálculos automáticos: solo responde
 * a las teclas o botones presionados.</p>
 */
public class EstrategiaMovimientoJugador implements EstrategiaMovimiento {

    private AdaptadorEntrada adaptadorEntrada;

    /**
     * Crea una nueva estrategia de movimiento controlada por el usuario.
     *
     * @param adaptadorEntrada la fuente de entrada del usuario (teclado, mouse, etc.)
     */
    public EstrategiaMovimientoJugador(AdaptadorEntrada adaptadorEntrada) {
        this.adaptadorEntrada = adaptadorEntrada;
    }

    /**
     * Calcula la dirección de movimiento de la paleta según la entrada del usuario.
     *
     * @param paleta la paleta controlada por el jugador
     * @param pelota la pelota del juego (no usada en esta estrategia)
     * @param tiempoDelta el tiempo transcurrido desde el último frame
     * @return la dirección en la que debe moverse la paleta
     */
    @Override
    public Direccion calcularMovimiento(Paleta paleta, Pelota pelota, double tiempoDelta) {
        if (adaptadorEntrada.arribaPresionado()) {
            return Direccion.ARRIBA;
        } else if (adaptadorEntrada.abajoPresionado()) {
            return Direccion.ABAJO;
        } else {
            return Direccion.NINGUNA;
        }
    }
}
