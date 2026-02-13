package mvc.modelo.entidades.paleta;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import mvc.modelo.entidades.ObjetoJuego;
import mvc.modelo.enums.LadoHorizontal;
import patrones.strategy.movimiento.EstrategiaMovimiento;

/**
 * Representa una paleta controlada por el jugador o la IA en el juego Pong.
 *
 * <p>Esta clase encapsula el comportamiento y estado de una paleta, que puede ser
 * controlada por un jugador humano o por inteligencia artificial mediante el patrón
 * Strategy. La paleta puede tener diversos estados y modificadores como
 * velocidad variable y dimensiones personalizables.</p>
 *
 * <p><b>Características principales:</b></p>
 * <ul>
 *   <li>Movimiento vertical con velocidad configurable</li>
 *   <li>Soporte para estrategias de movimiento (jugador o IA)</li>
 *   <li>Capacidad de guardar y restaurar estado (patrón Memento)</li>
 *   <li>Dimensiones y color personalizables</li>
 * </ul>
 *
 * @author Equipo-polimorfo
 * @version 3.1
 * @see EstrategiaMovimiento
 * @see ConfigPaleta
 */
public class Paleta extends ObjetoJuego {

    private int colisionEnX;
    private int centro;
    private int ancho;
    private int anchoLateral;
    private int grosor;
    private int limiteNorte;
    private int limiteSur;
    private int velocidad;
    private LadoHorizontal ladoPantalla;
    private Color colorPrimario;
    private Color colorSecundario;

    private ConfigPaleta estadoOriginal;

    private boolean activo = true;
    private EstrategiaMovimiento estrategiaMovimiento;

    /**
     * Constructor de compatibilidad con API antigua (4 parametros: x, y, ancho, alto).
     *
     * @param x posicion horizontal
     * @param y posicion vertical
     * @param ancho ancho de la paleta
     * @param alto alto de la paleta
     */
    public Paleta(double x, double y, double ancho, double alto) {
        this(
            (int)x,
            (int)(y + alto/2),
            (int)alto,
            (int)ancho,
            300,
            (int)(alto/2),
            (int)(600 - alto/2),
            x < 400 ? LadoHorizontal.IZQUIERDA : LadoHorizontal.DERECHA,
            Color.WHITE,
            Color.RED
        );
    }

    /**
     * Constructor principal de la paleta.
     *
     * @param colisionEnX posicion horizontal de colision
     * @param centro posicion vertical del centro
     * @param ancho altura vertical de la paleta
     * @param grosor ancho horizontal de la paleta
     * @param velocidad velocidad de movimiento
     * @param limNor limite superior
     * @param limSur limite inferior
     * @param lado lado de la pantalla
     * @param colorPrimario color principal
     * @param colorSecundario color secundario
     * @throws IndexOutOfBoundsException si los valores no son validos
     * @throws NullPointerException si los parametros de referencia son nulos
     */
    public Paleta(
        int colisionEnX,
        int centro,
        int ancho,
        int grosor,
        int velocidad,
        int limNor,
        int limSur,
        LadoHorizontal lado,
        Color colorPrimario,
        Color colorSecundario
    ) throws IndexOutOfBoundsException, NullPointerException {
        super(
            lado == LadoHorizontal.IZQUIERDA ? colisionEnX - grosor : colisionEnX,
            centro - (ancho % 2 == 1 ? ancho - 1 : ancho) / 2,
            grosor,
            ancho % 2 == 1 ? ancho - 1 : ancho
        );

        ConfigPaleta validacion = new ConfigPaleta(
            colisionEnX, centro, ancho, grosor, velocidad,
            limNor, limSur, lado,
            colorPrimario, colorSecundario
        );

        this.colisionEnX = validacion.colisionEnX();
        this.centro = validacion.centro();
        this.ancho = validacion.ancho();
        this.anchoLateral = this.ancho / 2;
        this.grosor = validacion.grosor();
        this.velocidad = validacion.velocidad();
        this.limiteNorte = validacion.limiteNorte();
        this.limiteSur = validacion.limiteSur();
        this.ladoPantalla = validacion.ladoPantalla();
        this.colorPrimario = validacion.colorPrimario();
        this.colorSecundario = validacion.colorSecundario();

        this.estadoOriginal = validacion;
    }

    /**
     * Constructor con estado original especificado.
     *
     * @param colisionEnX posicion horizontal de colision
     * @param centro posicion vertical del centro
     * @param ancho altura vertical de la paleta
     * @param grosor ancho horizontal de la paleta
     * @param velocidad velocidad de movimiento
     * @param limNor limite superior
     * @param limSur limite inferior
     * @param lado lado de la pantalla
     * @param colorPrimario color principal
     * @param colorSecundario color secundario
     * @param estadoOriginal estado original para restauracion
     * @throws IndexOutOfBoundsException si los valores no son validos
     * @throws NullPointerException si los parametros de referencia son nulos
     */
    public Paleta(
        int colisionEnX,
        int centro,
        int ancho,
        int grosor,
        int velocidad,
        int limNor,
        int limSur,
        LadoHorizontal lado,
        Color colorPrimario,
        Color colorSecundario,
        ConfigPaleta estadoOriginal
    ) throws IndexOutOfBoundsException, NullPointerException {
        this(colisionEnX, centro, ancho, grosor, velocidad, limNor, limSur,
             lado, colorPrimario, colorSecundario);
        this.estadoOriginal = estadoOriginal != null ? estadoOriginal : this.estadoOriginal;
    }

    /**
     * Mueve la plataforma hacia arriba.
     * Si la paleta llega al limite superior, la posicion del centro no se actualizara.
     *
     * @param deltaTime tiempo transcurrido en segundos
     */
    public void moverArriba(double deltaTime) {
        int desplazamiento = (int)(velocidad * deltaTime);
        if((this.centro - this.anchoLateral) - desplazamiento >= this.limiteNorte){
            this.centro -= desplazamiento;
        }
    }

    /**
     * Mueve la plataforma hacia abajo.
     * Si la paleta llega al limite inferior, la posicion del centro no se actualizara.
     *
     * @param deltaTime tiempo transcurrido en segundos
     */
    public void moverAbajo(double deltaTime) {
        int desplazamiento = (int)(velocidad * deltaTime);
        if((this.centro + this.anchoLateral) + desplazamiento <= this.limiteSur){
            this.centro += desplazamiento;
        }
    }

    /**
     * Mueve la plataforma hacia arriba (version sin deltaTime para compatibilidad).
     */
    public void moverArriba() {
        moverArriba(1.0 / 60.0);
    }

    /**
     * Mueve la plataforma hacia abajo (version sin deltaTime para compatibilidad).
     */
    public void moverAbajo() {
        moverAbajo(1.0 / 60.0);
    }

    /**
     * Actualiza el estado de la paleta basado en el tiempo transcurrido.
     *
     * @param deltaTime tiempo transcurrido desde la ultima actualizacion
     */
    @Override
    public void actualizar(double deltaTime) {
        // La paleta no se actualiza automaticamente, solo responde a comandos de movimiento
    }

    /**
     * Devuelve un rectangulo bidimensional con las dimensiones de la paleta.
     *
     * @return rectangulo con las dimensiones de la paleta
     */
    @Override
    public Rectangle2D obtenerLimites() {
        int puntoInicialX = ladoPantalla == LadoHorizontal.DERECHA
            ? this.colisionEnX
            : this.colisionEnX - this.grosor;

        return new Rectangle2D(
            puntoInicialX,
            this.centro - this.anchoLateral,
            this.grosor,
            this.ancho
        );
    }

    /**
     * Dibuja la paleta en el contexto grafico.
     *
     * @param gc contexto grafico donde se dibuja la paleta
     */
    @Override
    public void dibujar(GraphicsContext gc) {
        int puntoInicialX = ladoPantalla == LadoHorizontal.DERECHA
            ? this.colisionEnX
            : this.colisionEnX - this.grosor;

        gc.setFill(colorPrimario);
        gc.fillRect(
            puntoInicialX,
            this.centro - this.anchoLateral,
            this.grosor,
            this.ancho
        );
    }

    /**
     * Crea una copia profunda de esta paleta.
     *
     * <p>Implementa el patrón Prototype para permitir la clonación de paletas
     * con sus configuraciones actuales.</p>
     *
     * @return una nueva instancia de {@code Paleta} con los mismos valores que esta
     */
    public Paleta clonar(){
        return new Paleta(
            this.colisionEnX,
            this.centro,
            this.ancho,
            this.grosor,
            this.velocidad,
            this.limiteNorte,
            this.limiteSur,
            this.ladoPantalla,
            this.colorPrimario,
            this.colorSecundario,
            this.estadoOriginal
        );
    }

    /**
     * Actualiza el estado base de la paleta.
     *
     * @param configuracion nueva configuracion original
     * @throws NullPointerException si la configuracion es nula.
     */
    public void establecerEstadoOriginal(ConfigPaleta configuracion)
        throws NullPointerException {
        if(configuracion == null){
            throw new NullPointerException("Configuracion nula no es valida.");
        }
        this.estadoOriginal = configuracion;
    }

    /**
     * Devuelve el estado original de la paleta.
     *
     * @return configuracion original de la paleta
     */
    public ConfigPaleta obtenerEstadoOriginal() {
        return this.estadoOriginal;
    }

    /**
     * Configura la paleta a un estado definido.
     *
     * @param configuracion estado de paleta
     * @throws NullPointerException si la configuracion es nula
     */
    public void configurar(ConfigPaleta configuracion) throws NullPointerException {
        if(configuracion == null){
            throw new NullPointerException("Configuracion nula no es valida.");
        }
        this.colisionEnX = configuracion.colisionEnX();
        this.centro = configuracion.centro();
        this.ancho = configuracion.ancho();
        this.anchoLateral = this.ancho / 2;
        this.grosor = configuracion.grosor();
        this.velocidad = configuracion.velocidad();
        this.limiteNorte = configuracion.limiteNorte();
        this.limiteSur = configuracion.limiteSur();
        this.ladoPantalla = configuracion.ladoPantalla();
        this.colorPrimario = configuracion.colorPrimario();
        this.colorSecundario = configuracion.colorSecundario();
    }

    /**
     * Restaura la paleta a su estado original.
     */
    public void restaurarEstado() {
        this.configurar(this.estadoOriginal);
    }

    /**
     * Establece el ancho de la paleta.
     *
     * @param nuevoAncho nuevo ancho de la paleta
     * @throws IndexOutOfBoundsException si el ancho no es valido
     */
    public void establecerAncho(int nuevoAncho) throws IndexOutOfBoundsException {
        if(nuevoAncho <= 0) {
            throw new IndexOutOfBoundsException("Valor de ancho no positivo no es valido.");
        }
        if(nuevoAncho % 2 == 1)
            nuevoAncho--;

        if(this.centro - nuevoAncho/2 < this.limiteNorte)
            throw new IndexOutOfBoundsException("El nuevo ancho obligaria a la paleta a exceder el limite norte.");
        if(this.centro + nuevoAncho/2 > this.limiteSur)
            throw new IndexOutOfBoundsException("El nuevo ancho obligaria a la paleta a exceder el limite sur.");

        this.ancho = nuevoAncho;
        this.anchoLateral = nuevoAncho/2;
    }

    // ========== METODOS DE ACCESO (GETTERS) ==========

    /**
     * Obtiene la posicion horizontal de colision.
     *
     * @return posicion X de colision
     */
    public int obtenerColisionEnX() {
        return this.colisionEnX;
    }

    /**
     * Obtiene la posicion vertical del centro.
     *
     * @return posicion Y del centro
     */
    public int obtenerCentro() {
        return this.centro;
    }

    /**
     * Obtiene el grosor (ancho horizontal) de la paleta.
     *
     * @return grosor
     */
    public int obtenerGrosor() {
        return this.grosor;
    }

    /**
     * Obtiene la velocidad de movimiento.
     *
     * @return velocidad
     */
    public double obtenerVelocidad() {
        return (double)this.velocidad;
    }

    /**
     * Obtiene el limite norte (superior).
     *
     * @return limite norte
     */
    public int obtenerLimiteNorte() {
        return this.limiteNorte;
    }

    /**
     * Obtiene el limite sur (inferior).
     *
     * @return limite sur
     */
    public int obtenerLimiteSur() {
        return this.limiteSur;
    }

    /**
     * Obtiene el lado de la pantalla.
     *
     * @return lado de la pantalla
     */
    public LadoHorizontal obtenerLadoPantalla() {
        return this.ladoPantalla;
    }

    /**
     * Obtiene el color primario.
     *
     * @return color primario
     */
    public Color obtenerColorPrimario() {
        return this.colorPrimario;
    }

    /**
     * Obtiene el color secundario.
     *
     * @return color secundario
     */
    public Color obtenerColorSecundario() {
        return this.colorSecundario;
    }

    // ========== METODOS DE COMPATIBILIDAD CON API ANTIGUA ==========

    /**
     * Obtiene la posicion X de la paleta.
     *
     * @return posicion en el eje X
     */
    public double obtenerX() {
        return ladoPantalla == LadoHorizontal.DERECHA
            ? this.colisionEnX
            : this.colisionEnX - this.grosor;
    }

    /**
     * Obtiene la posicion Y de la paleta.
     *
     * @return posicion en el eje Y
     */
    public double obtenerY() {
        return this.centro - this.anchoLateral;
    }

    /**
     * Obtiene el alto de la paleta.
     *
     * @return alto de la paleta
     */
    public double obtenerAlto() {
        return this.ancho;
    }

    /**
     * Obtiene el ancho de la paleta.
     *
     * @return ancho de la paleta
     */
    @Override
    public double obtenerAncho() {
        return this.grosor;
    }

    /**
     * Verifica si la paleta esta activa.
     *
     * @return true si la paleta esta activa, false en caso contrario
     */
    public boolean estaActivo() {
        return this.activo;
    }

    /**
     * Establece el estado de activacion de la paleta.
     *
     * @param activo nuevo estado de activacion
     */
    public void establecerActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Redimensiona la paleta.
     *
     * @param factor factor de escala
     */
    public void redimensionar(double factor) {
        int nuevoAncho = (int)(this.ancho * factor);
        if (nuevoAncho > 0) {
            establecerAncho(nuevoAncho);
        }
    }

    /**
     * Establece la velocidad de la paleta.
     *
     * @param velocidad nueva velocidad
     */
    public void establecerVelocidad(double velocidad) {
        this.velocidad = (int)velocidad;
    }

    /**
     * Obtiene el color primario de la paleta.
     *
     * @return color primario
     */
    public Color obtenerColor() {
        return this.colorPrimario;
    }

    /**
     * Establece el color primario de la paleta.
     *
     * @param color nuevo color
     */
    public void setColor(Color color) {
        this.colorPrimario = color;
    }

    /**
     * Establece el ancho de la paleta (grosor).
     *
     * @param ancho nuevo ancho
     */
    public void setAncho(double ancho) {
        this.grosor = (int)ancho;
    }

    /**
     * Establece el alto de la paleta.
     *
     * @param alto nuevo alto
     */
    public void setAlto(double alto) {
        establecerAncho((int)alto);
    }

    /**
     * Establece la velocidad de la paleta.
     *
     * @param velocidad nueva velocidad
     */
    public void setVelocidad(double velocidad) {
        this.velocidad = (int)velocidad;
    }

    /**
     * Obtiene la estrategia de movimiento.
     *
     * @return estrategia de movimiento actual
     */
    public EstrategiaMovimiento obtenerEstrategiaMovimiento() {
        return this.estrategiaMovimiento;
    }

    /**
     * Establece la estrategia de movimiento.
     *
     * @param estrategia nueva estrategia
     */
    public void establecerEstrategiaMovimiento(EstrategiaMovimiento estrategia) {
        this.estrategiaMovimiento = estrategia;
    }

    /**
     * Verifica si tiene estrategia de movimiento.
     *
     * @return true si tiene estrategia
     */
    public boolean tieneEstrategiaMovimiento() {
        return this.estrategiaMovimiento != null;
    }

    /**
     * Calcula el movimiento segun la estrategia.
     *
     * @param pelota pelota del juego
     * @param tiempoDelta tiempo transcurrido
     * @return direccion de movimiento
     */
    public mvc.modelo.enums.Direccion calcularMovimiento(
            mvc.modelo.entidades.pelota.Pelota pelota,
            double tiempoDelta) {
        if (estrategiaMovimiento != null) {
            return estrategiaMovimiento.calcularMovimiento(this, pelota, tiempoDelta);
        }
        return mvc.modelo.enums.Direccion.NINGUNA;
    }

    /**
     * Mueve la paleta en una direccion.
     *
     * @param direccion direccion de movimiento
     * @param deltaTime tiempo transcurrido
     */
    public void moverEnDireccion(mvc.modelo.enums.Direccion direccion, double deltaTime) {
        switch (direccion) {
            case ARRIBA:
                moverArriba(deltaTime);
                break;
            case ABAJO:
                moverAbajo(deltaTime);
                break;
            default:
                break;
        }
    }

    /**
     * Guarda el estado actual como estado original.
     */
    public void guardarEstado() {
        this.estadoOriginal = new ConfigPaleta(
            this.colisionEnX,
            this.centro,
            this.ancho,
            this.grosor,
            this.velocidad,
            this.limiteNorte,
            this.limiteSur,
            this.ladoPantalla,
            this.colorPrimario,
            this.colorSecundario
        );
    }

    /**
     * Obtiene el estado original (alias para compatibilidad).
     *
     * @return estado original
     */
    public ConfigPaleta getEstadoOriginal() {
        return obtenerEstadoOriginal();
    }

    /**
     * Establece el estado original (alias para compatibilidad).
     *
     * @param estado nuevo estado original
     */
    public void setEstadoOriginal(ConfigPaleta estado) {
        establecerEstadoOriginal(estado);
    }
}
