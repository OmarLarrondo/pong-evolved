package persistencia.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object inmutable para representar un nivel en la base de datos.
 * Utiliza un record de Java para garantizar inmutabilidad total y reducir boilerplate.
 *
 * @param id identificador único del nivel
 * @param nombre nombre descriptivo del nivel
 * @param creador nombre del creador del nivel (null para niveles predefinidos)
 * @param dificultad nivel de dificultad (1=fácil, 2=medio, 3=difícil)
 * @param esPersonalizado indica si el nivel fue creado por un usuario
 * @param fechaCreacion fecha y hora de creación del nivel
 */
public record NivelDTO(
    String id,
    String nombre,
    String creador,
    int dificultad,
    boolean esPersonalizado,
    LocalDateTime fechaCreacion
) {

    /**
     * Constructor compacto que valida los datos del nivel.
     * Lanza IllegalArgumentException si los datos son inválidos.
     */
    public NivelDTO {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID del nivel no puede ser nulo o vacío");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del nivel no puede ser nulo o vacío");
        }
        if (dificultad < 1 || dificultad > 3) {
            throw new IllegalArgumentException("La dificultad debe estar entre 1 y 3");
        }
        if (fechaCreacion == null) {
            throw new IllegalArgumentException("La fecha de creación no puede ser nula");
        }
    }

    /**
     * Crea un nuevo NivelDTO para un nivel personalizado creado por un usuario.
     *
     * @param id identificador único del nivel
     * @param nombre nombre descriptivo del nivel
     * @param creador nombre del creador
     * @param dificultad nivel de dificultad
     * @return un nuevo NivelDTO con esPersonalizado=true y fecha actual
     */
    public static NivelDTO crearPersonalizado(String id, String nombre, String creador, int dificultad) {
        return new NivelDTO(id, nombre, creador, dificultad, true, LocalDateTime.now());
    }

    /**
     * Crea un nuevo NivelDTO para un nivel predefinido del juego.
     *
     * @param id identificador único del nivel
     * @param nombre nombre descriptivo del nivel
     * @param dificultad nivel de dificultad
     * @return un nuevo NivelDTO con esPersonalizado=false y fecha actual
     */
    public static NivelDTO crearPredefinido(String id, String nombre, int dificultad) {
        return new NivelDTO(id, nombre, null, dificultad, false, LocalDateTime.now());
    }
}
