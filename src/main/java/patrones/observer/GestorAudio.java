package patrones.observer;

import io.vavr.Lazy;
import io.vavr.control.Option;
import io.vavr.control.Try;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import util.CargadorRecursos;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestor centralizado de audio del juego implementado como Singleton.
 * Administra la musica de fondo y efectos de sonido.
 *
 * <p>Esta clase proporciona control sobre la reproduccion de audio, volumen
 * y silenciado, desacoplando los componentes del juego de las APIs de JavaFX Media.</p>
 *
 * <p>Patron de diseno: Singleton con inicializacion lazy thread-safe.</p>
 *
 * @author Equipo-Polimorfo
 * @version 2.0
 */
public final class GestorAudio {

    private static final Lazy<GestorAudio> INSTANCIA = Lazy.of(GestorAudio::new);
    private static final double VOLUMEN_PREDETERMINADO = 0.4;
    private static final String RUTA_MUSICA_FONDO = "audio/musica/fondo.wav";

    private final Map<String, AudioClip> efectosSonido;
    private Option<MediaPlayer> musicaFondo;
    private double volumen;
    private boolean silenciado;

    /**
     * Constructor privado para patron Singleton.
     * Inicializa las estructuras de datos internas.
     */
    private GestorAudio() {
        this.efectosSonido = new HashMap<>();
        this.musicaFondo = Option.none();
        this.volumen = VOLUMEN_PREDETERMINADO;
        this.silenciado = false;
    }

    /**
     * Obtiene la instancia unica del gestor de audio.
     * Utiliza inicializacion lazy thread-safe mediante Vavr.
     *
     * @return Instancia singleton del gestor de audio
     */
    public static GestorAudio obtenerInstancia() {
        return INSTANCIA.get();
    }

    /**
     * Inicializa la musica de fondo del juego cargando el archivo MP3
     * y configurando el reproductor para bucle infinito.
     *
     * <p>Esta operacion es idempotente: si la musica ya esta inicializada,
     * no realiza ninguna accion.</p>
     *
     * @return Option con el MediaPlayer si la inicializacion fue exitosa,
     *         Option.none() si fallo o ya estaba inicializado
     */
    public Option<MediaPlayer> inicializarMusicaFondo() {
        return musicaFondo.isEmpty()
                ? cargarYConfigurarMusica()
                : musicaFondo;
    }

    /**
     * Carga el archivo de musica y configura el MediaPlayer con bucle infinito.
     *
     * @return Option con el MediaPlayer configurado, o Option.none() si falla
     */
    private Option<MediaPlayer> cargarYConfigurarMusica() {
        return Option.ofOptional(CargadorRecursos.cargarAudio(RUTA_MUSICA_FONDO))
                .flatMap(this::crearReproductorConConfiguracion)
                .peek(reproductor -> musicaFondo = Option.of(reproductor));
    }

    /**
     * Crea un MediaPlayer con la configuracion apropiada para musica de fondo.
     * Maneja excepciones de MediaPlayer con Try de Vavr.
     *
     * @param media Objeto Media con el archivo de audio cargado
     * @return Option con MediaPlayer configurado, o Option.none() si falla la creacion
     */
    private Option<MediaPlayer> crearReproductorConConfiguracion(Media media) {
        return Try.of(() -> {
            MediaPlayer reproductor = new MediaPlayer(media);
            reproductor.setCycleCount(MediaPlayer.INDEFINITE);
            reproductor.setAutoPlay(false);
            reproductor.setVolume(volumen);
            reproductor.setMute(silenciado);
            return reproductor;
        }).onFailure(error -> System.err.println("Error al crear MediaPlayer: " + error.getMessage()))
          .toOption();
    }

    /**
     * Reproduce la musica de fondo si esta inicializada y no se esta reproduciendo.
     * Inicia automaticamente si no esta sonando.
     *
     * @return true si se inicio la reproduccion exitosamente, false en caso contrario
     */
    public boolean reproducir() {
        return musicaFondo
                .filter(this::noEstaReproduciendo)
                .map(this::iniciarReproduccion)
                .getOrElse(false);
    }

    /**
     * Verifica si el reproductor no esta en estado de reproduccion.
     *
     * @param reproductor MediaPlayer a verificar
     * @return true si no esta reproduciendo, false en caso contrario
     */
    private boolean noEstaReproduciendo(MediaPlayer reproductor) {
        return reproductor.getStatus() != MediaPlayer.Status.PLAYING;
    }

    /**
     * Inicia la reproduccion del MediaPlayer de forma segura.
     *
     * @param reproductor MediaPlayer a iniciar
     * @return true si se inicio correctamente, false si hubo error
     */
    private boolean iniciarReproduccion(MediaPlayer reproductor) {
        return Try.run(reproductor::play)
                .map(v -> true)
                .getOrElse(false);
    }

    /**
     * Reproduce un efecto de sonido identificado por su nombre.
     * Si el efecto no existe en el mapa, no realiza ninguna accion.
     *
     * @param nombreSonido Identificador del efecto de sonido a reproducir
     */
    public void reproducirSonido(String nombreSonido) {
        Option.of(efectosSonido.get(nombreSonido))
                .peek(AudioClip::play);
    }

    /**
     * Inicia la reproduccion de la musica de fondo.
     * Si ya esta reproduciendose, no reinicia la pista.
     */
    public void reproducirMusicaFondo() {
        reproducir();
    }

    /**
     * Detiene completamente la musica de fondo si esta sonando.
     */
    public void detenerMusicaFondo() {
        musicaFondo.peek(MediaPlayer::stop);
    }

    /**
     * Establece el volumen general del audio del juego.
     * Afecta tanto a la musica de fondo como a futuros efectos de sonido.
     *
     * @param nuevoVolumen Volumen en el rango [0.0, 1.0]
     */
    public void establecerVolumen(double nuevoVolumen) {
        this.volumen = Math.max(0.0, Math.min(1.0, nuevoVolumen));
        musicaFondo.peek(reproductor -> reproductor.setVolume(this.volumen));
    }

    /**
     * Silencia todo el audio del juego manteniendo el volumen configurado.
     * Permite restaurar el sonido posteriormente con el mismo volumen.
     */
    public void silenciar() {
        this.silenciado = true;
        musicaFondo.peek(reproductor -> reproductor.setMute(true));
    }

    /**
     * Reactiva el sonido del juego despues de haber sido silenciado.
     * Restaura el volumen previamente configurado.
     */
    public void activarSonido() {
        this.silenciado = false;
        musicaFondo.peek(reproductor -> reproductor.setMute(false));
    }

    /**
     * Alterna entre los estados de silenciado y sonido activo.
     * Metodo de conveniencia para controles de mute/unmute.
     */
    public void alternarMute() {
        if (silenciado) {
            activarSonido();
        } else {
            silenciar();
        }
    }

    /**
     * Verifica si el audio esta actualmente silenciado.
     *
     * @return true si el audio esta silenciado, false en caso contrario
     */
    public boolean estaSilenciado() {
        return silenciado;
    }

    /**
     * Libera todos los recursos de audio nativos del sistema.
     * Debe llamarse al cerrar la aplicacion para evitar memory leaks.
     */
    public void liberarRecursos() {
        musicaFondo.peek(MediaPlayer::dispose);
        musicaFondo = Option.none();
        efectosSonido.clear();
    }

    /**
     * Registra un efecto de sonido en el mapa de efectos disponibles.
     *
     * @param nombre Identificador unico del efecto de sonido
     * @param efecto AudioClip con el efecto de sonido cargado
     */
    public void registrarEfectoSonido(String nombre, AudioClip efecto) {
        efectosSonido.put(nombre, efecto);
    }

    /**
     * Obtiene el volumen actual del audio.
     *
     * @return Volumen en el rango [0.0, 1.0]
     */
    public double obtenerVolumen() {
        return volumen;
    }
}
