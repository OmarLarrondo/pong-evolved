package patrones.adapter;

import javafx.scene.input.KeyCode;

import java.lang.annotation.Inherited;
import java.util.HashSet;
import java.util.Set;


/**
 * Implementación concreta de {@link AdaptadorEntrada} que adapta las acciones
 * del jugador realizadas con las teclas(W,S,UP,DOWM) a señales comprensibles por el sistema
 * de control del juego Pong.
 *
 * <p>Esta clase detecta que tecla se presiono y traduce dicha información en 
 * movimientos verticales(arriba o abajo) o en la acción de pausa. De este modo, el controlador del
 * juego puede interpretar el uso del teclado de manera uniforme.</p>
 *
 * <p>Utiliza códigos de tecla de JavaFX ({@link KeyCode}) para registrar y
 * liberar teclas activas según los eventos del sistema.</p>
 *
 * <p>Teclas admitidas por defecto:</p>
 * <ul>
 *   <li><strong>W</strong> o <strong>↑</strong> → mover hacia arriba.</li>
 *   <li><strong>S</strong> o <strong>↓</strong> → mover hacia abajo.</li>
 *   <li><strong>P</strong> o <strong>ESC</strong> → pausar el juego.</li>
 * </ul>
 * 
 * @author Equipo-Polimorfo
 * @version 1.0
 * @see AdaptadorEntrada
 */
public class AdaptadorEntradaTeclado implements AdaptadorEntrada {

    /** Conjunto las teclas que se han estan presionando actualmente.*/
    private Set<KeyCode> teclasPresionadas = new HashSet<>();

    /**
     * Detecta si una tecla está siendo presionada.
     * Agrega la tecla que esta siendo presionada a {@code teclasPresionadas}.
     * @param key  key código de la tecla presionada.
     */
    public void teclaPresionada(KeyCode key) {
        teclasPresionadas.add(key);
    }

    /**
     * Detecta si una tecla fue liberada.
     * Si se liberó, se elimina de {@code teclasPresionadas}.
     * @param key  key código de la tecla presionada.
     */
    public void teclaLiberada(KeyCode key) {
        teclasPresionadas.remove(key);
    }

    /**{@InheritDoc}*/
    @Override
    public boolean arribaPresionado() {
        return teclasPresionadas.contains(KeyCode.W) || teclasPresionadas.contains(KeyCode.UP);
    }

    /**{@inheriDoc}*/
    @Override
    public boolean abajoPresionado() {
        return teclasPresionadas.contains(KeyCode.S) || teclasPresionadas.contains(KeyCode.DOWN);
    }

    /**{@inheritDoc}*/
    @Override
    public boolean pausaPresionado() {
        return teclasPresionadas.contains(KeyCode.P) || teclasPresionadas.contains(KeyCode.ESCAPE);
    }

    /**
     * Verifica si una tecla específica está siendo presionada actualmente.
     *
     * @param key código de la tecla a verificar
     * @return true si la tecla está presionada, false en caso contrario
     */
    public boolean esTeclaPresionada(KeyCode key) {
        return teclasPresionadas.contains(key);
    }
}
