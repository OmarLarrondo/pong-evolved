package mvc.modelo.items;

import mvc.modelo.entidades.ObjetoJuego;
import mvc.modelo.entidades.paleta.Paleta;

/**
 * Item que modifica temporalmente el tamaño de la paleta.
 *
 * Este power-up redimensiona la paleta multiplicando sus dimensiones actuales
 * por un factor específico. Puede usarse para agrandar la paleta (multiplicador
 * mayor a 1.0) facilitando el juego, o para reducirla (multiplicador menor a 1.0)
 * aumentando la dificultad.
 *
 * Las dimensiones originales son guardadas antes de aplicar el cambio, permitiendo
 * una restauración exacta al finalizar el efecto. Solo puede aplicarse a objetos
 * de tipo Paleta.
 */
public class ItemRedimensionarPaleta implements Item {
    /**
     * Factor por el cual se multiplicarán las dimensiones de la paleta.
     * Valores mayores a 1.0 agrandan la paleta, menores la reducen.
     */
    private double multiplicadorTamanio;

    /**
     * Duración total del efecto en segundos.
     */
    private double duracion;

    /**
     * Indica si el efecto está actualmente activo.
     */
    private boolean activo;

    /**
     * Tiempo restante en segundos antes de que expire el efecto.
     */
    private double tiempoRestante;

    /**
     * Ancho original de la paleta antes de aplicar el efecto.
     * Usado para restaurar el tamaño al desactivar.
     */
    private double anchoOriginal;

    /**
     * Alto original de la paleta antes de aplicar el efecto.
     * Usado para restaurar el tamaño al desactivar.
     */
    private double altoOriginal;

    /**
     * Paleta objetivo a redimensionar.
     */
    private Paleta paletaObjetivo;

    /**
     * Construye un nuevo item de redimensionamiento de paleta.
     *
     * @param multiplicadorTamanio factor de escala para las dimensiones,
     *                             valores mayores a 1.0 agrandan, menores reducen
     * @param duracion tiempo en segundos que durará el efecto
     * @throws IllegalArgumentException si el multiplicador no es positivo
     *                                  o si la duración es negativa
     */
    public ItemRedimensionarPaleta(double multiplicadorTamanio, double duracion) {
        if(multiplicadorTamanio <= 0) throw new IllegalArgumentException("El multiplicador debe ser mayor a 0");
        if(duracion < 0) throw new IllegalArgumentException("La duracion no puede ser negativa");

        this.multiplicadorTamanio = multiplicadorTamanio;
        this.duracion = duracion;
        this.activo = false;
        this.tiempoRestante = duracion;
        this.paletaObjetivo = null;
    }

    /**
     * Aplica el efecto de redimensionamiento a la paleta especificada.
     *
     * Guarda las dimensiones originales y luego multiplica tanto el ancho como
     * el alto de la paleta por el factor configurado. Si el item ya está activo,
     * ignora la aplicación para evitar modificaciones acumulativas.
     *
     * @param objeto la Paleta cuyas dimensiones serán modificadas
     * @throws IllegalArgumentException si el objeto no es una Paleta
     */
    @Override
    public void aplicar(ObjetoJuego objeto) {
        if(activo == true) return;

        if(!(objeto instanceof mvc.modelo.entidades.paleta.Paleta p)) throw new IllegalArgumentException("Solo se puede aplicar a paletas.");

        anchoOriginal = p.obtenerAncho();
        altoOriginal = p.obtenerAlto();
        double nuevoAncho = anchoOriginal * multiplicadorTamanio;
        double nuevoAlto = altoOriginal * multiplicadorTamanio;
        p.setAlto(nuevoAlto);
        p.setAncho(nuevoAncho);
        activo = true;
        tiempoRestante = duracion;
        paletaObjetivo = p;

    }

    /**
     * Obtiene la duración configurada del efecto.
     *
     * @return duración total en segundos
     */
    @Override
    public double obtenerDuracion() {
        return duracion;

    }

    /**
     * Verifica si el efecto de redimensionamiento está actualmente activo.
     *
     * @return true si el efecto está aplicado, false en caso contrario
     */
    @Override
    public boolean estaActivo() {
        return activo;
    }

    /**
     * Desactiva el efecto y restaura las dimensiones originales de la paleta.
     *
     * Revierte el ancho y alto de la paleta a los valores guardados antes
     * de aplicar el efecto, retornando la paleta a su tamaño original.
     *
     * @param objeto la Paleta cuyas dimensiones serán restauradas
     * @throws IllegalArgumentException si el objeto no es una Paleta
     */
    @Override
    public void desactivar(ObjetoJuego objeto) {
        if (!(objeto instanceof mvc.modelo.entidades.paleta.Paleta) && paletaObjetivo != null) {
            objeto = paletaObjetivo;
        }

        if(!(objeto instanceof mvc.modelo.entidades.paleta.Paleta p)) throw new IllegalArgumentException("Solo se puede aplicar a paletas.");
        p.setAlto(altoOriginal);
        p.setAncho(anchoOriginal);
        activo = false;
        tiempoRestante = duracion;
        paletaObjetivo = null;
    }

    /**
     * Actualiza el estado del item según el tiempo transcurrido.
     *
     * Decrementa el tiempo restante del efecto. Cuando el tiempo llega a cero
     * o menos, desactiva automáticamente el item y restaura el tamaño original.
     * Este método debe ser invocado en cada frame del juego.
     *
     * @param deltaTiempo tiempo transcurrido desde la última actualización en segundos
     * @param objeto la paleta cuyo tamaño será restaurado si expira el tiempo
     */
    @Override
    public void actualizar(double deltaTiempo, ObjetoJuego objeto) {
        if(!activo) return;

        tiempoRestante = tiempoRestante - deltaTiempo;
        if(tiempoRestante <= 0){
            desactivar(paletaObjetivo != null ? paletaObjetivo : objeto);
        }
    }

    /**
     * Obtiene la posición X del item para renderizado.
     * Los items de redimensionamiento no tienen posición física, retorna 0.0 por defecto.
     *
     * @return 0.0 (sin posición física)
     */
    @Override
    public double obtenerX() {
        return 0.0;
    }

    /**
     * Obtiene la posición Y del item para renderizado.
     * Los items de redimensionamiento no tienen posición física, retorna 0.0 por defecto.
     *
     * @return 0.0 (sin posición física)
     */
    @Override
    public double obtenerY() {
        return 0.0;
    }

    /**
     * Obtiene el ancho del item para renderizado.
     *
     * @return 20.0 píxeles
     */
    @Override
    public double obtenerAncho() {
        return 20.0;
    }

    /**
     * Obtiene el alto del item para renderizado.
     *
     * @return 20.0 píxeles
     */
    @Override
    public double obtenerAlto() {
        return 20.0;
    }
}
