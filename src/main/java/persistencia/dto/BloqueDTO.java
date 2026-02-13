package persistencia.dto;

/**
 * Data Transfer Object inmutable para representar un bloque en la base de datos.
 * Utiliza un record de Java para garantizar inmutabilidad total.
 *
 * @param id identificador único del bloque (null para bloques nuevos)
 * @param nivelId identificador del nivel al que pertenece este bloque
 * @param posicionX coordenada X del bloque en el canvas
 * @param posicionY coordenada Y del bloque en el canvas
 * @param ancho ancho del bloque en píxeles
 * @param alto alto del bloque en píxeles
 * @param resistencia resistencia del bloque (golpes necesarios para destruirlo)
 * @param tipoBloque tipo del bloque (DESTRUCTIBLE, INDESTRUCTIBLE, BONUS, MULTI_GOLPE)
 */
public record BloqueDTO(
    Integer id,
    String nivelId,
    double posicionX,
    double posicionY,
    double ancho,
    double alto,
    int resistencia,
    String tipoBloque
) {

    /**
     * Constructor compacto que valida los datos del bloque.
     * Lanza IllegalArgumentException si los datos son inválidos.
     */
    public BloqueDTO {
        if (nivelId == null || nivelId.isBlank()) {
            throw new IllegalArgumentException("El ID del nivel no puede ser nulo o vacío");
        }
        if (posicionX < 0) {
            throw new IllegalArgumentException("La posición X no puede ser negativa");
        }
        if (posicionY < 0) {
            throw new IllegalArgumentException("La posición Y no puede ser negativa");
        }
        if (ancho <= 0) {
            throw new IllegalArgumentException("El ancho debe ser positivo");
        }
        if (alto <= 0) {
            throw new IllegalArgumentException("El alto debe ser positivo");
        }
        if (resistencia < 0) {
            throw new IllegalArgumentException("La resistencia no puede ser negativa");
        }
        if (tipoBloque == null || tipoBloque.isBlank()) {
            throw new IllegalArgumentException("El tipo de bloque no puede ser nulo o vacío");
        }
    }

    /**
     * Crea un nuevo BloqueDTO sin ID (para inserción en base de datos).
     * El ID será asignado automáticamente por la base de datos.
     *
     * @param nivelId identificador del nivel
     * @param posicionX coordenada X
     * @param posicionY coordenada Y
     * @param ancho ancho del bloque
     * @param alto alto del bloque
     * @param resistencia resistencia del bloque
     * @param tipoBloque tipo del bloque
     * @return un nuevo BloqueDTO sin ID
     */
    public static BloqueDTO crearNuevo(
        String nivelId,
        double posicionX,
        double posicionY,
        double ancho,
        double alto,
        int resistencia,
        String tipoBloque
    ) {
        return new BloqueDTO(null, nivelId, posicionX, posicionY, ancho, alto, resistencia, tipoBloque);
    }

    /**
     * Crea una copia de este BloqueDTO con un nuevo ID de nivel.
     * Útil para clonar bloques entre diferentes niveles.
     *
     * @param nuevoNivelId el nuevo ID de nivel
     * @return un nuevo BloqueDTO con el nivel actualizado
     */
    public BloqueDTO conNuevoNivel(String nuevoNivelId) {
        return new BloqueDTO(null, nuevoNivelId, posicionX, posicionY, ancho, alto, resistencia, tipoBloque);
    }
}
