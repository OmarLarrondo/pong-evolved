package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.text.Font;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Clase utilitaria funcional para cargar recursos de la aplicación.
 * Proporciona métodos puros para cargar fuentes, archivos FXML, CSS y videos.
 * Todos los métodos retornan Optional para manejo funcional de errores.
 *
 * @author Equipo-Polimorfo
 */
public final class CargadorRecursos {

    private CargadorRecursos() {
        throw new UnsupportedOperationException("Clase utilitaria no instanciable");
    }

    /**
     * Carga una fuente desde un archivo en el directorio de recursos.
     *
     * @param rutaRelativa Ruta relativa al directorio de recursos (ej: "fuentes/PressStart2P.ttf")
     * @param tamano Tamaño de la fuente
     * @return Optional con la fuente cargada, o Optional.empty() si falla
     */
    public static Optional<Font> cargarFuente(String rutaRelativa, double tamano) {
        return obtenerURL(rutaRelativa)
                .map(url -> url.toExternalForm())
                .map(urlExterna -> Font.loadFont(urlExterna, tamano));
    }

    /**
     * Carga un archivo FXML y retorna su contenedor raíz.
     *
     * @param rutaRelativa Ruta relativa al directorio de recursos (ej: "fxml/menu.fxml")
     * @return Optional con el Parent cargado, o Optional.empty() si falla
     */
    public static Optional<Parent> cargarFXML(String rutaRelativa) {
        return obtenerURL(rutaRelativa)
                .flatMap(url -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(url);
                        return Optional.of(loader.load());
                    } catch (Exception e) {
                        System.err.println("Error al cargar FXML: " + rutaRelativa);
                        e.printStackTrace();
                        return Optional.empty();
                    }
                });
    }

    /**
     * Carga un archivo FXML y retorna el FXMLLoader para acceder al controlador.
     * Este método permite obtener el controlador asociado al FXML después de la carga.
     *
     * @param rutaRelativa Ruta relativa al directorio de recursos (ej: "fxml/juego.fxml")
     * @return FXMLLoader configurado con la URL del FXML, o null si falla
     */
    public static FXMLLoader cargarFXMLLoader(String rutaRelativa) {
        return obtenerURL(rutaRelativa)
                .map(url -> new FXMLLoader(url))
                .orElse(null);
    }

    /**
     * Carga un archivo FXML con un controlador específico.
     *
     * @param rutaRelativa Ruta relativa al directorio de recursos
     * @param controlador Instancia del controlador a usar
     * @return Optional con el Parent cargado, o Optional.empty() si falla
     */
    public static Optional<Parent> cargarFXMLConControlador(String rutaRelativa, Object controlador) {
        return obtenerURL(rutaRelativa)
                .flatMap(url -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(url);
                        loader.setController(controlador);
                        return Optional.of(loader.load());
                    } catch (Exception e) {
                        System.err.println("Error al cargar FXML con controlador: " + rutaRelativa);
                        e.printStackTrace();
                        return Optional.empty();
                    }
                });
    }

    /**
     * Obtiene la URL de un archivo CSS para aplicarlo a una escena.
     *
     * @param rutaRelativa Ruta relativa al directorio de recursos (ej: "css/estilo-retro.css")
     * @return Optional con la URL del CSS como String, o Optional.empty() si falla
     */
    public static Optional<String> obtenerRutaCSS(String rutaRelativa) {
        return obtenerURL(rutaRelativa)
                .map(URL::toExternalForm);
    }

    /**
     * Carga un video desde el directorio de recursos.
     *
     * @param rutaRelativa Ruta relativa al directorio de recursos (ej: "videos/preview-idle.mp4")
     * @return Optional con el objeto Media, o Optional.empty() si falla
     */
    public static Optional<Media> cargarVideo(String rutaRelativa) {
        return obtenerURL(rutaRelativa)
                .map(URL::toExternalForm)
                .flatMap(urlExterna -> {
                    try {
                        return Optional.of(new Media(urlExterna));
                    } catch (Exception e) {
                        return Optional.empty();
                    }
                });
    }

    /**
     * Carga un archivo de audio desde el directorio de recursos.
     * Utiliza la misma infraestructura que los videos, ya que JavaFX Media
     * soporta tanto audio como video.
     *
     * @param rutaRelativa Ruta relativa al directorio de recursos (ej: "audio/musica/fondo.mp3")
     * @return Optional con el objeto Media, o Optional.empty() si falla
     */
    public static Optional<Media> cargarAudio(String rutaRelativa) {
        return obtenerURL(rutaRelativa)
                .map(URL::toExternalForm)
                .flatMap(urlExterna -> {
                    try {
                        return Optional.of(new Media(urlExterna));
                    } catch (Exception e) {
                        System.err.println("Error al cargar audio: " + rutaRelativa);
                        e.printStackTrace();
                        return Optional.empty();
                    }
                });
    }

    /**
     * Carga una imagen desde el directorio de recursos.
     *
     * @param rutaRelativa Ruta relativa al directorio de recursos (ej: "imagenes/preview-idle.png")
     * @return Optional con el objeto Image, o Optional.empty() si falla
     */
    public static Optional<Image> cargarImagen(String rutaRelativa) {
        return obtenerURL(rutaRelativa)
                .map(url -> {
                    try {
                        return new Image(url.toExternalForm());
                    } catch (Exception e) {
                        System.err.println("Error al cargar imagen: " + rutaRelativa);
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(imagen -> imagen != null);
    }

    /**
     * Carga múltiples videos desde el directorio de recursos y los mapea por nombre.
     *
     * @param nombresVideos Lista de nombres de archivos de video
     * @param directorio Directorio donde se encuentran los videos (ej: "videos/")
     * @return Mapa de nombre de video a objeto Media
     */
    public static Map<String, Media> cargarTodosVideos(List<String> nombresVideos, String directorio) {
        return nombresVideos.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        nombre -> cargarVideo(directorio + nombre)
                                .orElseGet(() -> crearVideoPlaceholder())
                ));
    }

    /**
     * Obtiene la URL de un recurso del classpath.
     *
     * @param rutaRelativa Ruta relativa al directorio de recursos
     * @return Optional con la URL del recurso, o Optional.empty() si no existe
     */
    private static Optional<URL> obtenerURL(String rutaRelativa) {
        URL url = CargadorRecursos.class.getClassLoader().getResource(rutaRelativa);
        if (url == null) {
            url = CargadorRecursos.class.getResource("/" + rutaRelativa);
        }
        return Optional.ofNullable(url);
    }

    /**
     * Crea un video placeholder negro cuando no se encuentra el archivo.
     *
     * @return Media con un video placeholder
     */
    private static Media crearVideoPlaceholder() {
        String rutaTemporal = new File("src/main/resources/videos/placeholder.mp4")
                .toURI()
                .toString();
        return new Media(rutaTemporal);
    }

    /**
     * Verifica si un recurso existe en el classpath.
     *
     * @param rutaRelativa Ruta relativa al directorio de recursos
     * @return true si el recurso existe, false en caso contrario
     */
    public static boolean existeRecurso(String rutaRelativa) {
        return obtenerURL(rutaRelativa).isPresent();
    }
}
