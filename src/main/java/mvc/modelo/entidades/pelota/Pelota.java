package mvc.modelo.entidades.pelota;

import io.vavr.control.Option;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import mvc.modelo.entidades.ObjetoJuego;
import mvc.modelo.entidades.paleta.Paleta;
import mvc.modelo.enums.LadoHorizontal;

/**
 * Representa la pelota del juego Pong.
 *
 * <p>Esta clase encapsula el comportamiento y estado de una pelota,
 * incluyendo su posicion, velocidad, direccion y estado de activacion.
 * La pelota puede moverse, colisionar con objetos y cambiar su direccion.</p>
 *
 * <p><b>Características principales:</b></p>
 * <ul>
 *   <li>Movimiento basado en fisica vectorial</li>
 *   <li>Sistema de velocidad con limite maximo</li>
 *   <li>Capacidad de guardar y restaurar estado (patrón Memento)</li>
 *   <li>Control de activacion/desactivacion</li>
 * </ul>
 *
 * @author Equipo-polimorfo
 * @version 2.0
 * @see ConfigPelota
 */
public class Pelota extends ObjetoJuego {

    private static final double PI = Math.PI;

    private int centroEnX;
    private int centroEnY;
    private int radio;
    private int velocidad;
    private int velocidadMaxima;
    private double anguloDireccional;

    private ConfigPelota estadoOriginal;

    private boolean activo;

    private Option<Paleta> ultimaPaletaQueGolpeo;

    /**
     * Constructor principal de la pelota.
     *
     * @param centroEnX coordenada X del centro
     * @param centroEnY coordenada Y del centro
     * @param radio radio de la pelota
     * @param velocidadInicial velocidad inicial
     * @param velocidadMaxima velocidad maxima permitida
     * @param anguloDireccional angulo de direccion en radianes
     * @throws IndexOutOfBoundsException si los valores no son validos
     */
    public Pelota(
        int centroEnX,
        int centroEnY,
        int radio,
        int velocidadInicial,
        int velocidadMaxima,
        double anguloDireccional
    ) {
        super(centroEnX - radio, centroEnY - radio, 2.0 * radio, 2.0 * radio);

        ConfigPelota validacion = new ConfigPelota(
            centroEnX, centroEnY, radio,
            velocidadInicial, velocidadMaxima, anguloDireccional
        );

        this.centroEnX = validacion.centroEnX();
        this.centroEnY = validacion.centroEnY();
        this.radio = validacion.radio();
        this.velocidad = validacion.velocidad();
        this.velocidadMaxima = validacion.velocidadMaxima();
        this.anguloDireccional = validacion.anguloDireccional();

        this.estadoOriginal = validacion;
        this.activo = true;
        this.ultimaPaletaQueGolpeo = Option.none();
    }

    /**
     * Constructor con estado original especificado.
     *
     * @param centroEnX coordenada X del centro
     * @param centroEnY coordenada Y del centro
     * @param radio radio de la pelota
     * @param velocidadInicial velocidad inicial
     * @param velocidadMaxima velocidad maxima permitida
     * @param anguloDireccional angulo de direccion en radianes
     * @param estadoOriginal estado original para restauracion
     * @throws IndexOutOfBoundsException si los valores no son validos
     */
    public Pelota(
        int centroEnX,
        int centroEnY,
        int radio,
        int velocidadInicial,
        int velocidadMaxima,
        double anguloDireccional,
        ConfigPelota estadoOriginal
    ) {
        this(centroEnX, centroEnY, radio, velocidadInicial, velocidadMaxima, anguloDireccional);
        this.estadoOriginal = estadoOriginal != null ? estadoOriginal : this.estadoOriginal;
    }

    /**
     * Genera un angulo aleatorio con respecto al eje x, con una desviacion uniforme
     * de entre -pi/8 y pi/8, con respecto al sentido indicado.
     * Sirve para dar dinamismo al comienzo de una ronda, siendo su direccion no siempre igual.
     *
     * @param sentido sentido horizontal inicial
     */
    public void inicializaDireccionLateral(LadoHorizontal sentido){
        if(sentido == LadoHorizontal.DERECHA)
            this.anguloDireccional = ( ((PI/4) * Math.random()) + (7*PI/8) ) % (2*PI);
        else
            this.anguloDireccional = ((PI/4) * Math.random()) - (PI*3/8);
    }

    /**
     * Invierte el sentido horizontal de la pelota cambiando su angulo de direccion.
     */
    public void alternarSentidoHorizontal() {
        this.anguloDireccional = PI - this.anguloDireccional;
    }

    /**
     * Invierte el sentido vertical de la pelota cambiando su angulo de direccion.
     */
    public void alternarSentidoVertical() {
        this.anguloDireccional = -this.anguloDireccional;
    }

    /**
     * Actualiza el estado base de la pelota.
     *
     * @param configuracion nueva configuracion original
     * @throws NullPointerException si la configuracion es nula.
     */
    public void establecerEstadoOriginal(ConfigPelota configuracion)
        throws NullPointerException {
        if(configuracion == null){
            throw new NullPointerException("Configuracion nula no es valida.");
        }
        this.estadoOriginal = configuracion;
    }

    /**
     * Devuelve el estado original de la pelota.
     *
     * @return configuracion original de la pelota
     */
    public ConfigPelota obtenerEstadoOriginal(){
        return this.estadoOriginal;
    }

    /**
     * Configura la pelota a un estado definido.
     *
     * @param configuracion estado de pelota
     * @throws NullPointerException si la configuracion es nula
     */
    public void configurar(ConfigPelota configuracion) throws NullPointerException {
        if(configuracion == null){
            throw new NullPointerException("Configuracion nula no es valida.");
        }
        this.centroEnX = configuracion.centroEnX();
        this.centroEnY = configuracion.centroEnY();
        this.radio = configuracion.radio();
        this.velocidad = configuracion.velocidad();
        this.velocidadMaxima = configuracion.velocidadMaxima();
        this.anguloDireccional = configuracion.anguloDireccional();
    }

    /**
     * Restaura la pelota a su estado original.
     */
    public void restaurarEstado() {
        this.configurar(this.estadoOriginal);
    }

    /**
     * Actualiza la posicion de la pelota basado en el tiempo transcurrido.
     *
     * @param deltaTime tiempo transcurrido desde la ultima actualizacion
     */
    @Override
    public void actualizar(double deltaTime) {
        if (!activo) return;

        double velocidadX = Math.cos(anguloDireccional) * velocidad;
        double velocidadY = -Math.sin(anguloDireccional) * velocidad;

        this.centroEnX += (int)(velocidadX * deltaTime);
        this.centroEnY += (int)(velocidadY * deltaTime);
    }

    /**
     * Devuelve un cuadrado bidimensional con las dimensiones de la pelota.
     *
     * @return rectangulo con las dimensiones de la pelota
     */
    @Override
    public Rectangle2D obtenerLimites() {
        return new Rectangle2D(
            this.centroEnX - this.radio,
            this.centroEnY - this.radio,
            2 * this.radio,
            2 * this.radio
        );
    }

    /**
     * Dibuja la pelota en el contexto grafico.
     *
     * @param gc contexto grafico donde se dibuja la pelota
     */
    @Override
    public void dibujar(GraphicsContext gc) {
        if (!activo) return;

        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillOval(
            this.centroEnX - this.radio,
            this.centroEnY - this.radio,
            2 * this.radio,
            2 * this.radio
        );
    }

    /**
     * Crea una copia de la pelota.
     *
     * @return copia de la pelota
     */
    public Pelota clonar() {
        Pelota copia = new Pelota(
            this.centroEnX,
            this.centroEnY,
            this.radio,
            this.velocidad,
            this.velocidadMaxima,
            this.anguloDireccional,
            this.estadoOriginal
        );
        copia.activo = this.activo;
        copia.ultimaPaletaQueGolpeo = this.ultimaPaletaQueGolpeo;
        return copia;
    }

    // ========== METODOS DE ACCESO (GETTERS) ==========

    /**
     * Obtiene la coordenada X del centro.
     *
     * @return coordenada X del centro
     */
    public int obtenerCentroEnX() {
        return this.centroEnX;
    }

    /**
     * Obtiene la coordenada Y del centro.
     *
     * @return coordenada Y del centro
     */
    public int obtenerCentroEnY() {
        return this.centroEnY;
    }

    /**
     * Obtiene el radio de la pelota.
     *
     * @return radio
     */
    public int obtenerRadio() {
        return this.radio;
    }

    /**
     * Obtiene la velocidad actual de la pelota.
     *
     * @return velocidad actual
     */
    public double obtenerVelocidad() {
        return (double)this.velocidad;
    }

    /**
     * Obtiene la velocidad maxima.
     *
     * @return velocidad maxima
     */
    public int obtenerVelocidadMaxima() {
        return this.velocidadMaxima;
    }

    /**
     * Obtiene el angulo direccional.
     *
     * @return angulo direccional en radianes
     */
    public double obtenerAnguloDireccional() {
        return this.anguloDireccional;
    }

    /**
     * Obtiene la componente X de la velocidad.
     *
     * @return velocidad en el eje X
     */
    public double obtenerVelocidadX() {
        return this.velocidad * Math.cos(this.anguloDireccional);
    }

    /**
     * Obtiene la componente Y de la velocidad.
     *
     * @return velocidad en el eje Y
     */
    public double obtenerVelocidadY() {
        return this.velocidad * -Math.sin(this.anguloDireccional);
    }

    // ========== METODOS DE MODIFICACION (SETTERS) ==========

    /**
     * Establece la coordenada X del centro.
     *
     * @param nuevoCentroEnX nueva coordenada X
     * @throws IndexOutOfBoundsException si el valor no es positivo
     */
    public void establecerCentroEnX(int nuevoCentroEnX) throws IndexOutOfBoundsException {
        if(nuevoCentroEnX <= 0) {
            throw new IndexOutOfBoundsException("Valor de posicion horizontal no positivo no es valido.");
        }
        this.centroEnX = nuevoCentroEnX;
    }

    /**
     * Establece la coordenada Y del centro.
     *
     * @param nuevoCentroEnY nueva coordenada Y
     * @throws IndexOutOfBoundsException si el valor no es positivo
     */
    public void establecerCentroEnY(int nuevoCentroEnY) throws IndexOutOfBoundsException {
        if(nuevoCentroEnY <= 0) {
            throw new IndexOutOfBoundsException("Valor de posicion vertical no positivo no es valido.");
        }
        this.centroEnY = nuevoCentroEnY;
    }

    /**
     * Establece la posicion del centro de la pelota.
     *
     * @param nuevoCentroEnX nueva coordenada X del centro
     * @param nuevoCentroEnY nueva coordenada Y del centro
     * @throws IndexOutOfBoundsException si alguno de los valores no es positivo
     */
    public void establecerPosicionCentro(int nuevoCentroEnX, int nuevoCentroEnY)
            throws IndexOutOfBoundsException {
        establecerCentroEnX(nuevoCentroEnX);
        establecerCentroEnY(nuevoCentroEnY);
    }

    /**
     * Establece el radio de la pelota.
     *
     * @param nuevoRadio nuevo radio
     * @throws IndexOutOfBoundsException si el radio no es positivo o es menor que la velocidad maxima
     */
    public void establecerRadio(int nuevoRadio) throws IndexOutOfBoundsException {
        if(nuevoRadio <= 0) {
            throw new IndexOutOfBoundsException("Valor de radio no positivo no es valido.");
        }
        if(nuevoRadio < this.velocidadMaxima) {
            throw new IndexOutOfBoundsException("El radio no puede ser menor que la velocidad maxima.");
        }
        this.radio = nuevoRadio;
    }

    /**
     * Establece la velocidad actual de la pelota.
     *
     * @param nuevaVelocidad nueva velocidad
     * @throws IndexOutOfBoundsException si la velocidad no es positiva o excede la maxima
     */
    public void establecerVelocidadActual(int nuevaVelocidad) throws IndexOutOfBoundsException {
        if(nuevaVelocidad <= 0) {
            throw new IndexOutOfBoundsException("Valor de velocidad no positivo no es valido.");
        }
        if(nuevaVelocidad > this.velocidadMaxima) {
            throw new IndexOutOfBoundsException("El valor excede la velocidad maxima permitida.");
        }
        this.velocidad = nuevaVelocidad;
    }

    /**
     * Establece la velocidad maxima de la pelota.
     *
     * @param nuevaVelocidadMaxima nueva velocidad maxima
     * @throws IndexOutOfBoundsException si la velocidad maxima no es valida
     */
    public void establecerVelocidadMaxima(int nuevaVelocidadMaxima) throws IndexOutOfBoundsException {
        if(nuevaVelocidadMaxima <= 0) {
            throw new IndexOutOfBoundsException("Valor de velocidad maxima no positivo no es valido.");
        }
        if(nuevaVelocidadMaxima > this.radio) {
            throw new IndexOutOfBoundsException(
                "El valor excede el radio, esto podria generar problemas de colision."
            );
        }
        if(nuevaVelocidadMaxima < this.velocidad) {
            throw new IndexOutOfBoundsException(
                "La velocidad maxima no puede ser menor que la velocidad actual."
            );
        }
        this.velocidadMaxima = nuevaVelocidadMaxima;
    }

    /**
     * Establece el angulo direccional de la pelota.
     *
     * @param nuevoAnguloDireccional nuevo angulo en radianes
     * @throws IndexOutOfBoundsException si el angulo esta fuera del rango valido
     */
    public void establecerAnguloDireccional(double nuevoAnguloDireccional)
            throws IndexOutOfBoundsException {
        if(nuevoAnguloDireccional < 0 || nuevoAnguloDireccional >= 2*PI) {
            throw new IndexOutOfBoundsException("El angulo direccional debe estar en el rango [0, 2*PI).");
        }
        this.anguloDireccional = nuevoAnguloDireccional;
    }

    // ========== METODOS DE COMPATIBILIDAD CON API ANTIGUA ==========

    /**
     * Establece la componente horizontal de la velocidad.
     * Actualiza el angulo direccional y la magnitud de velocidad.
     *
     * @param velocidadX nueva velocidad en el eje X
     */
    public void establecerVelocidadX(double velocidadX) {
        double velocidadY = obtenerVelocidadY();
        this.velocidad = (int)Math.sqrt(velocidadX * velocidadX + velocidadY * velocidadY);
        this.anguloDireccional = Math.atan2(velocidadY, velocidadX);
    }

    /**
     * Establece la componente vertical de la velocidad.
     * Actualiza el angulo direccional y la magnitud de velocidad.
     *
     * @param velocidadY nueva velocidad en el eje Y
     */
    public void establecerVelocidadY(double velocidadY) {
        double velocidadX = obtenerVelocidadX();
        this.velocidad = (int)Math.sqrt(velocidadX * velocidadX + velocidadY * velocidadY);
        this.anguloDireccional = Math.atan2(velocidadY, velocidadX);
    }

    /**
     * Invierte la componente horizontal de la velocidad.
     */
    public void invertirX() {
        alternarSentidoHorizontal();
    }

    /**
     * Invierte la componente vertical de la velocidad.
     */
    public void invertirY() {
        alternarSentidoVertical();
    }

    /**
     * Reinicia la pelota a su estado inicial.
     */
    public void reiniciar() {
        restaurarEstado();
    }

    /**
     * Establece la velocidad general de la pelota.
     *
     * @param velocidad nueva velocidad
     */
    public void establecerVelocidadGeneral(double velocidad) {
        this.velocidad = (int)velocidad;
    }

    /**
     * Obtiene la posicion X de la pelota.
     *
     * @return posicion en el eje X
     */
    public double obtenerX() {
        return this.centroEnX - this.radio;
    }

    /**
     * Obtiene la posicion Y de la pelota.
     *
     * @return posicion en el eje Y
     */
    public double obtenerY() {
        return this.centroEnY - this.radio;
    }

    /**
     * Obtiene el ancho de la pelota (diametro).
     *
     * @return ancho de la pelota
     */
    public double obtenerAncho() {
        return 2 * this.radio;
    }

    /**
     * Obtiene el alto de la pelota (diametro).
     *
     * @return alto de la pelota
     */
    public double obtenerAlto() {
        return 2 * this.radio;
    }

    /**
     * Verifica si la pelota esta activa.
     *
     * @return true si la pelota esta activa, false en caso contrario
     */
    public boolean estaActivo() {
        return this.activo;
    }

    /**
     * Establece el estado de activacion de la pelota.
     *
     * @param activo nuevo estado de activacion
     */
    public void establecerActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Obtiene la ultima paleta que golpeo esta pelota.
     * <p>
     * Este metodo se utiliza para rastrear cual paleta debe recibir
     * los beneficios de los power-ups cuando se rompe un bloque bonus.
     * </p>
     *
     * @return Option conteniendo la paleta si existe, Option.none() en caso contrario
     */
    public Option<Paleta> obtenerUltimaPaletaQueGolpeo() {
        return this.ultimaPaletaQueGolpeo;
    }

    /**
     * Establece la ultima paleta que golpeo esta pelota.
     * <p>
     * Este metodo debe ser llamado por el sistema de colisiones cada vez
     * que una paleta golpea la pelota, para poder aplicar correctamente
     * los power-ups a la paleta responsable de romper bloques bonus.
     * </p>
     *
     * @param paleta la paleta que acaba de golpear esta pelota
     */
    public void establecerUltimaPaletaQueGolpeo(final Paleta paleta) {
        this.ultimaPaletaQueGolpeo = Option.of(paleta);
    }
}
