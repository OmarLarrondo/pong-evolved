package mvc.modelo.items;

import mvc.modelo.entidades.ObjetoJuego;
import mvc.modelo.entidades.paleta.Paleta;
import mvc.modelo.entidades.pelota.Pelota;

/**
 * Item que modifica temporalmente la velocidad de paletas o pelotas.
 *
 * Este power-up multiplica la velocidad del objeto objetivo por un factor
 * específico durante una duración determinada. Al expirar el efecto, la
 * velocidad original es restaurada automáticamente.
 *
 * Este item puede aplicarse únicamente a objetos de tipo Paleta o Pelota,
 * ya que son los únicos que poseen atributos de velocidad modificables.
 *
 * La implementación guarda el estado original inline para permitir la
 * restauración correcta al finalizar el efecto.
 */
public class ItemAumentoVelocidad  implements Item{
    /**
     * Factor por el cual se multiplica la velocidad original.
     */
    private double multiplicadorVelocidad;

    /**
     * Duración total del efecto en segundos.
     */
    private double duracion;

    /**
     * Indica si el efecto está actualmente aplicado.
     */
    private boolean activo;

    /**
     * Tiempo restante en segundos antes de que expire el efecto.
     */
    private double tiempoRestante;

    /**
     * Objeto objetivo al que se le aplica el efecto.
     */
    private ObjetoJuego objetivo;

    /**
     * Construye un nuevo item de aumento de velocidad.
     *
     * @param multiplicadorVelocidad factor por el cual se multiplicará la velocidad,
     *                               valores mayores a 1.0 aumentan la velocidad,
     *                               valores menores la reducen
     * @param duracion tiempo en segundos que durará el efecto activo
     * @param activo estado inicial del item, normalmente false hasta que se aplique
     * @param tiempoRestante tiempo restante inicial, normalmente igual a duracion
     */
    public ItemAumentoVelocidad(double multiplicadorVelocidad, double duracion, boolean activo, double tiempoRestante) {
        this.multiplicadorVelocidad = multiplicadorVelocidad;
        this.duracion = duracion;
        this.activo = activo;
        this.tiempoRestante = tiempoRestante;
        this.objetivo = null;
    }

    /**
     * Aplica el efecto de modificación de velocidad al objeto especificado.
     *
     * Multiplica la velocidad actual del objeto por el factor configurado.
     * Si el item ya está activo, ignora la aplicación para evitar
     * multiplicaciones acumulativas. El objeto debe ser una instancia de
     * Paleta o Pelota.
     *
     * Tras aplicar el cambio, guarda la velocidad original en el propio
     * objeto para permitir su restauración posterior mediante el método
     * restaurarEstado() del objeto.
     *
     * @param objeto la Paleta o Pelota cuya velocidad será modificada
     * @throws IllegalArgumentException si el objeto no es una Paleta ni una Pelota
     */
    @Override
    public void aplicar(ObjetoJuego objeto) {
        if(activo) return;

        if(objeto instanceof mvc.modelo.entidades.paleta.Paleta paleta){
            double velocidadOriginal = paleta.obtenerVelocidad();
            paleta.establecerVelocidad(velocidadOriginal*multiplicadorVelocidad);
            paleta.establecerActivo(true);
            activo = true;
            objetivo = objeto;
        }
        else if(objeto instanceof mvc.modelo.entidades.pelota.Pelota pelota){
            double velocidadOriginal = pelota.obtenerVelocidad();
            pelota.establecerVelocidadGeneral(velocidadOriginal*multiplicadorVelocidad);
            pelota.establecerActivo(true);
            activo = true;
            objetivo = objeto;

        }else{
            throw new IllegalArgumentException("el objeto debe ser una paleta o una pelota");
        }
        tiempoRestante = duracion;


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
     * Verifica si el efecto de velocidad está actualmente activo.
     *
     * @return true si el efecto está aplicado, false en caso contrario
     */
    @Override
    public boolean estaActivo() {
        return activo;
    }

    /**
     * Actualiza el estado del item según el tiempo transcurrido.
     *
     * Decrementa el tiempo restante del efecto. Cuando el tiempo llega a cero
     * o menos, desactiva automáticamente el item y restaura la velocidad original.
     * Este método debe ser invocado en cada frame del juego.
     *
     * @param deltaTiempo tiempo transcurrido desde la última actualización en segundos
     * @param objeto el objeto cuyo estado será restaurado si expira el tiempo
     */
    @Override
    public void actualizar(double deltaTiempo, ObjetoJuego objeto){
        if(!activo) return;

        tiempoRestante -= deltaTiempo;
        if(tiempoRestante<= 0){
            desactivar(objetivo != null ? objetivo : objeto);
        }
    }

    /**
     * Desactiva el efecto y restaura la velocidad original del objeto.
     *
     * Invoca el método restaurarEstado() del objeto para revertir la
     * modificación de velocidad, retornando el objeto a su estado previo
     * a la aplicación del item.
     *
     * @param objeto la Paleta o Pelota cuya velocidad será restaurada
     * @throws IllegalArgumentException si el objeto no es una Paleta ni una Pelota
     */
    @Override
    public void desactivar(ObjetoJuego objeto) {
        if (objeto == null && objetivo != null) {
            objeto = objetivo;
        }
        if(objeto instanceof mvc.modelo.entidades.paleta.Paleta){
            ((mvc.modelo.entidades.paleta.Paleta)objeto).restaurarEstado();
            ((mvc.modelo.entidades.paleta.Paleta)objeto).establecerActivo(false);
            activo = false;
        }
        else if(objeto instanceof mvc.modelo.entidades.pelota.Pelota){
            ((mvc.modelo.entidades.pelota.Pelota)objeto).restaurarEstado();
            ((mvc.modelo.entidades.pelota.Pelota)objeto).establecerActivo(false);
            activo = false;
        }else{
            throw new IllegalArgumentException("el objeto debe ser una paleta o una pelota");
        }
        activo = false;
        tiempoRestante = duracion;
        objetivo = null;

    }

    /**
     * Obtiene la posición X del item para renderizado.
     * Los items de aumento de velocidad no tienen posición física, retorna 0.0 por defecto.
     *
     * @return 0.0 (sin posición física)
     */
    @Override
    public double obtenerX() {
        return 0.0;
    }

    /**
     * Obtiene la posición Y del item para renderizado.
     * Los items de aumento de velocidad no tienen posición física, retorna 0.0 por defecto.
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
