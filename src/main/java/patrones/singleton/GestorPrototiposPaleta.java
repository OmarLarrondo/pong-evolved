package patrones.singleton;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.collection.List;
import java.util.concurrent.atomic.AtomicReference;

import mvc.modelo.entidades.paleta.Paleta;
import javafx.scene.paint.Color;

/**
 * Gestor singleton de prototipos de paletas.
 * <p>
 * Implementa el patron Singleton utilizando enum para garantizar
 * una unica instancia thread-safe. Administra un registro inmutable
 * de prototipos de paletas utilizando estructuras de datos funcionales
 * de Vavr.
 * </p>
 * <p>
 * El uso de enum como Singleton proporciona proteccion contra reflexion,
 * serializacion y garantiza inicializacion perezosa thread-safe sin
 * codigo de sincronizacion adicional.
 * </p>
 *
 * @author Equipo-polimorfo
 * @version 1.1
 */
public enum GestorPrototiposPaleta {
    INSTANCE;

    private final AtomicReference<Map<String, Paleta>> prototipos;

    /**
     * Constructor del enum singleton.
     * <p>
     * Inicializa el registro de prototipos con un mapa inmutable vacio
     * y registra los prototipos predeterminados del sistema.
     * </p>
     */
    GestorPrototiposPaleta() {
        this.prototipos = new AtomicReference<>(HashMap.empty());
        registrarPrototiposDefecto();
    }

    /**
     * Obtiene la instancia unica del gestor.
     *
     * @return la instancia singleton del gestor de prototipos
     */
    public static GestorPrototiposPaleta obtenerInstancia() {
        return INSTANCE;
    }

    /**
     * Registra los prototipos de paletas predeterminados del sistema.
     * <p>
     * Crea cuatro prototipos estandar con configuraciones tipicas:
     * <ul>
     * <li>estandar: configuracion balanceada</li>
     * <li>rapida: optimizada para velocidad</li>
     * <li>defensiva: optimizada para cobertura</li>
     * <li>ofensiva: configuracion de alta velocidad</li>
     * </ul>
     * </p>
     */
    public void registrarPrototiposDefecto() {
        registrarPrototipo("estandar", crearPaletaEstandar());
        registrarPrototipo("rapida", crearPaletaRapida());
        registrarPrototipo("defensiva", crearPaletaDefensiva());
        registrarPrototipo("ofensiva", crearPaletaOfensiva());
    }

    /**
     * Crea una paleta con configuracion estandar balanceada.
     *
     * @return una nueva paleta con valores estandar
     */
    private Paleta crearPaletaEstandar() {
        Paleta paleta = new Paleta(0, 0, 15, 100);
        paleta.setVelocidad(400);
        paleta.setColor(Color.WHITE);
        return paleta;
    }

    /**
     * Crea una paleta con configuracion rapida.
     * <p>
     * Sacrifica tamano por velocidad aumentada.
     * </p>
     *
     * @return una nueva paleta optimizada para velocidad
     */
    private Paleta crearPaletaRapida() {
        Paleta paleta = new Paleta(0, 0, 12, 80);
        paleta.setVelocidad(600);
        paleta.setColor(Color.YELLOW);
        return paleta;
    }

    /**
     * Crea una paleta con configuracion defensiva.
     * <p>
     * Mayor tamano con velocidad reducida para mejor cobertura.
     * </p>
     *
     * @return una nueva paleta optimizada para defensa
     */
    private Paleta crearPaletaDefensiva() {
        Paleta paleta = new Paleta(0, 0, 20, 150);
        paleta.setVelocidad(300);
        paleta.setColor(Color.BLUE);
        return paleta;
    }

    /**
     * Crea una paleta con configuracion ofensiva.
     * <p>
     * Paleta de alta velocidad para un juego agresivo.
     * </p>
     *
     * @return una nueva paleta con capacidades ofensivas
     */
    private Paleta crearPaletaOfensiva() {
        Paleta paleta = new Paleta(0, 0, 15, 100);
        paleta.setVelocidad(450);
        paleta.setColor(Color.RED);
        return paleta;
    }

    /**
     * Registra un nuevo prototipo de paleta de forma funcional.
     * <p>
     * Actualiza el registro de prototipos de manera thread-safe
     * utilizando operaciones atomicas sobre estructuras inmutables.
     * La operacion es no bloqueante y garantiza consistencia.
     * </p>
     *
     * @param nombre el nombre identificador unico del prototipo
     * @param paleta la paleta prototipo a registrar
     */
    public void registrarPrototipo(String nombre, Paleta paleta) {
        prototipos.updateAndGet(mapa -> mapa.put(nombre, paleta));
    }

    /**
     * Obtiene una copia de un prototipo de paleta por nombre.
     * <p>
     * Retorna una copia profunda del prototipo solicitado, manteniendo
     * el prototipo original intacto para futuras clonaciones. Si el
     * prototipo no existe, retorna Option.none().
     * </p>
     *
     * @param nombre el nombre del prototipo a obtener
     * @return un Option conteniendo la copia del prototipo si existe,
     *         o None si no se encuentra el prototipo especificado
     */
    public Option<Paleta> obtenerPrototipo(String nombre) {
        return prototipos.get()
            .get(nombre)
            .map(this::clonarPaleta);
    }

    /**
     * Clona una paleta creando una copia profunda independiente.
     * <p>
     * Crea una nueva instancia con todos los atributos copiados
     * del original, garantizando que modificaciones en el clon
     * no afecten al prototipo original.
     * </p>
     *
     * @param original la paleta a clonar
     * @return una nueva paleta con los mismos atributos
     */
    private Paleta clonarPaleta(Paleta original) {
        Paleta clon = new Paleta(
            original.obtenerX(),
            original.obtenerY(),
            original.obtenerAncho(),
            original.obtenerAlto()
        );

        clon.setVelocidad(original.obtenerVelocidad());

        Option.of(original.obtenerColor())
            .forEach(clon::setColor);

        return clon;
    }

    /**
     * Guarda un prototipo personalizado creado por el usuario.
     * <p>
     * Permite a los usuarios guardar sus configuraciones personalizadas
     * de paletas para uso futuro. Los prototipos de usuario se prefijan
     * automaticamente con "usuario_" para distinguirlos de los predeterminados.
     * </p>
     *
     * @param nombre el nombre para identificar el prototipo personalizado
     * @param paleta la paleta personalizada a guardar como prototipo
     */
    public void guardarPrototipoUsuario(String nombre, Paleta paleta) {
        String nombreUsuario = "usuario_" + nombre;
        registrarPrototipo(nombreUsuario, paleta);
    }

    /**
     * Obtiene la lista ordenada de nombres de prototipos disponibles.
     * <p>
     * Retorna una lista inmutable de Vavr con todos los nombres
     * de prototipos registrados, ordenados alfabeticamente.
     * </p>
     *
     * @return lista inmutable ordenada de nombres de prototipos
     */
    public List<String> listaPrototiposDisponibles() {
        return prototipos.get()
            .keySet()
            .toList()
            .sorted();
    }

    /**
     * Obtiene la lista de prototipos como java.util.List.
     * <p>
     * Metodo de compatibilidad para codigo que requiere
     * colecciones de Java estandar.
     * </p>
     *
     * @return lista de nombres de prototipos como java.util.List
     */
    public java.util.List<String> listaPrototiposDisponiblesJava() {
        return listaPrototiposDisponibles().asJava();
    }
}