package util;

import javafx.animation.FadeTransition;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Clase utilitaria funcional para gestionar reproducción y transiciones de videos.
 * Proporciona métodos puros para crear reproductores, gestionar transiciones
 * y controlar la reproducción de videos en el menú.
 *
 * @author Equipo-Polimorfo
 */
public final class GestorVideos {

    private static final double DURACION_FADE_MS = 300.0;
    private static final double OPACIDAD_MINIMA = 0.0;
    private static final double OPACIDAD_MAXIMA = 1.0;

    private GestorVideos() {
        throw new UnsupportedOperationException("Clase utilitaria no instanciable");
    }

    /**
     * Crea un reproductor de video configurado para reproducción en loop.
     *
     * @param media Objeto Media con el video a reproducir
     * @return MediaPlayer configurado para reproducción continua, o null si falla
     */
    public static MediaPlayer crearReproductor(Media media) {
        try {
            MediaPlayer reproductor = new MediaPlayer(media);
            reproductor.setCycleCount(MediaPlayer.INDEFINITE);
            reproductor.setAutoPlay(false);
            reproductor.setMute(true);
            return reproductor;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Inicia la reproducción de un video en un MediaView.
     *
     * @param reproductor MediaPlayer a reproducir
     * @param mediaView Vista donde se mostrará el video
     */
    public static void reproducirVideo(MediaPlayer reproductor, MediaView mediaView) {
        Optional.ofNullable(reproductor).ifPresent(r -> {
            mediaView.setMediaPlayer(r);
            r.seek(Duration.ZERO);
            r.play();
        });
    }

    /**
     * Detiene la reproducción de un video.
     *
     * @param reproductor MediaPlayer a detener
     */
    public static void detenerVideo(MediaPlayer reproductor) {
        Optional.ofNullable(reproductor).ifPresent(MediaPlayer::stop);
    }

    /**
     * Pausa la reproducción de un video.
     *
     * @param reproductor MediaPlayer a pausar
     */
    public static void pausarVideo(MediaPlayer reproductor) {
        Optional.ofNullable(reproductor).ifPresent(MediaPlayer::pause);
    }

    /**
     * Realiza una transición fade entre dos videos.
     * Primero hace fade out del video actual, luego cambia al nuevo y hace fade in.
     *
     * @param reproductorActual MediaPlayer del video actual (puede ser null)
     * @param reproductorNuevo MediaPlayer del video nuevo
     * @param mediaView Vista donde se mostrarán los videos
     */
    public static void transicionarVideo(
            MediaPlayer reproductorActual,
            MediaPlayer reproductorNuevo,
            MediaView mediaView) {

        if (reproductorActual == null) {
            aplicarFadeIn(mediaView, () -> reproducirVideo(reproductorNuevo, mediaView));
        } else {
            aplicarFadeOut(mediaView, () -> {
                detenerVideo(reproductorActual);
                reproducirVideo(reproductorNuevo, mediaView);
                aplicarFadeIn(mediaView, () -> {});
            });
        }
    }

    /**
     * Aplica una transición fade out a un MediaView.
     *
     * @param mediaView Vista a la que aplicar el efecto
     * @param alTerminar Acción a ejecutar cuando termine la transición
     */
    private static void aplicarFadeOut(MediaView mediaView, Runnable alTerminar) {
        FadeTransition fade = crearTransicionFade(
                mediaView,
                OPACIDAD_MAXIMA,
                OPACIDAD_MINIMA
        );
        fade.setOnFinished(evento -> alTerminar.run());
        fade.play();
    }

    /**
     * Aplica una transición fade in a un MediaView.
     *
     * @param mediaView Vista a la que aplicar el efecto
     * @param alTerminar Acción a ejecutar cuando termine la transición
     */
    private static void aplicarFadeIn(MediaView mediaView, Runnable alTerminar) {
        FadeTransition fade = crearTransicionFade(
                mediaView,
                OPACIDAD_MINIMA,
                OPACIDAD_MAXIMA
        );
        fade.setOnFinished(evento -> alTerminar.run());
        fade.play();
    }

    /**
     * Crea una transición de fade configurada.
     *
     * @param nodo Nodo al que aplicar la transición
     * @param desde Opacidad inicial
     * @param hasta Opacidad final
     * @return FadeTransition configurada
     */
    private static FadeTransition crearTransicionFade(
            javafx.scene.Node nodo,
            double desde,
            double hasta) {

        FadeTransition fade = new FadeTransition(Duration.millis(DURACION_FADE_MS), nodo);
        fade.setFromValue(desde);
        fade.setToValue(hasta);
        return fade;
    }

    /**
     * Libera los recursos de un MediaPlayer.
     *
     * @param reproductor MediaPlayer a liberar
     */
    public static void liberarRecursos(MediaPlayer reproductor) {
        Optional.ofNullable(reproductor).ifPresent(r -> {
            r.stop();
            r.dispose();
        });
    }

    /**
     * Aplica un Consumer a un MediaPlayer si no es null.
     *
     * @param reproductor MediaPlayer sobre el que aplicar la acción
     * @param accion Consumer a aplicar
     */
    public static void aplicarSiExiste(MediaPlayer reproductor, Consumer<MediaPlayer> accion) {
        Optional.ofNullable(reproductor).ifPresent(accion);
    }

    /**
     * Verifica si un MediaPlayer está reproduciendo.
     *
     * @param reproductor MediaPlayer a verificar
     * @return true si está reproduciendo, false en caso contrario
     */
    public static boolean estaReproduciendo(MediaPlayer reproductor) {
        return Optional.ofNullable(reproductor)
                .map(r -> r.getStatus() == MediaPlayer.Status.PLAYING)
                .orElse(false);
    }
}
