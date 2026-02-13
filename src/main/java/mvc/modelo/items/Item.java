package mvc.modelo.items;

import mvc.modelo.entidades.ObjetoJuego;

/**
 * Define el contrato para los power-ups del juego tipo Arkanoid.
 *
 * Esta interfaz implementa el patrón Strategy, permitiendo que diferentes
 * efectos temporales (items) sean aplicados de manera intercambiable a los
 * objetos del juego. Cada implementación concreta representa una estrategia
 * específica de modificación del comportamiento del juego.
 *
 * Los items pueden aplicarse a paletas, pelotas u otros objetos del juego,
 * modificando temporalmente sus propiedades o comportamiento durante una
 * duración específica.
 */
public interface Item {
    /**
     * Aplica el efecto del item al objeto especificado.
     *
     * Este método activa el efecto temporal del item, modificando las
     * propiedades o el comportamiento del objeto de juego. Si el item
     * ya está activo, la aplicación puede ser ignorada dependiendo de
     * la implementación concreta.
     *
     * @param objeto el objeto de juego al que se aplicará el efecto
     * @throws IllegalArgumentException si el item no puede ser aplicado
     *         al tipo de objeto especificado
     */
    public void aplicar(ObjetoJuego objeto);

    /**
     * Obtiene la duración total del efecto del item en segundos.
     *
     * @return la duración en segundos que el efecto permanecerá activo
     */
    public double obtenerDuracion();

    /**
     * Verifica si el efecto del item está actualmente activo.
     *
     * @return true si el item está aplicado y su efecto está activo,
     *         false en caso contrario
     */
    public boolean estaActivo();

    /**
     * Desactiva el efecto del item, revirtiendo los cambios aplicados.
     *
     * Este método restaura el estado original del objeto de juego,
     * eliminando las modificaciones introducidas por el item. Debe
     * ser llamado cuando expira la duración del efecto.
     *
     * @param objeto el objeto de juego del cual se removerá el efecto
     * @throws IllegalArgumentException si el objeto no es compatible
     *         con este tipo de item
     */
    public void desactivar(ObjetoJuego objeto);

    /**
     * Actualiza el estado temporal del item según el tiempo transcurrido.
     *
     * Este método gestiona el temporizador del efecto, decrementando el
     * tiempo restante y desactivando automáticamente el item cuando expira
     * su duración. Debe ser invocado en cada frame del game loop.
     *
     * @param deltaTiempo tiempo transcurrido desde la última actualización
     *                    en segundos
     * @param objeto el objeto de juego cuyo efecto será desactivado si
     *               expira el tiempo
     */
    void actualizar(double deltaTiempo, ObjetoJuego objeto);

    /**
     * Obtiene la posición X del item en el campo de juego.
     * Necesario para el renderizado visual del item.
     *
     * @return la coordenada X actual del item
     */
    double obtenerX();

    /**
     * Obtiene la posición Y del item en el campo de juego.
     * Necesario para el renderizado visual del item.
     *
     * @return la coordenada Y actual del item
     */
    double obtenerY();

    /**
     * Obtiene el ancho del item para renderizado.
     *
     * @return el ancho del item en píxeles
     */
    double obtenerAncho();

    /**
     * Obtiene el alto del item para renderizado.
     *
     * @return el alto del item en píxeles
     */
    double obtenerAlto();
}
