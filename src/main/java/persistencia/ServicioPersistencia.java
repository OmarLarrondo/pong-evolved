package persistencia;

import io.vavr.control.Try;
import mvc.modelo.entidades.Bloque;
import patrones.builder.TipoBloque;
import mvc.modelo.entidades.Nivel;
import persistencia.conexion.ConexionSQLite;
import persistencia.dto.BloqueDTO;
import persistencia.dto.NivelDTO;
import persistencia.repositorio.RepositorioNiveles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Servicio de persistencia que proporciona operaciones de alto nivel
 * para guardar y cargar niveles del juego.
 * Actúa como capa de aplicación entre el dominio y la persistencia.
 */
public class ServicioPersistencia {

    private final RepositorioNiveles repositorio;

    /**
     * Construye un nuevo servicio de persistencia con el repositorio predeterminado.
     */
    public ServicioPersistencia() {
        ConexionSQLite conexion = ConexionSQLite.obtenerInstancia();
        this.repositorio = new RepositorioNiveles(conexion);
    }

    /**
     * Construye un servicio de persistencia con un repositorio específico.
     *
     * @param repositorio repositorio de niveles a utilizar
     */
    public ServicioPersistencia(RepositorioNiveles repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Guarda un nivel completo con sus bloques en la base de datos.
     * Genera un ID único si el nivel no tiene uno asignado.
     *
     * @param nivel nivel a guardar
     * @return Try con el ID del nivel guardado, o una excepción si falla
     */
    public Try<String> guardarNivel(Nivel nivel) {
        return Try.of(() -> {
            if (nivel.getId() == null || nivel.getId().isEmpty()) {
                nivel.setId(UUID.randomUUID().toString());
            }

            NivelDTO nivelDTO = convertirNivelADTO(nivel);
            io.vavr.collection.List<BloqueDTO> bloquesDTO = convertirBloquesADTO(nivel);

            return repositorio.guardar(nivelDTO, bloquesDTO)
                .getOrElseThrow(e -> new RuntimeException("Error al guardar nivel: " + e.getMessage(), e));
        });
    }

    /**
     * Carga un nivel completo desde la base de datos.
     *
     * @param id identificador del nivel
     * @return Try con el nivel cargado, o una excepción si falla
     */
    public Try<Nivel> cargarNivel(String id) {
        return repositorio.buscarPorId(id)
            .flatMap(nivelOpt -> nivelOpt
                .map(nivelDTO -> repositorio.obtenerBloques(id)
                    .map(bloquesDTO -> convertirDTOANivel(nivelDTO, bloquesDTO)))
                .getOrElse(Try.failure(new IllegalArgumentException("Nivel no encontrado: " + id)))
            );
    }

    /**
     * Carga todos los niveles personalizados creados por usuarios.
     *
     * @return Try con la lista de niveles personalizados
     */
    public Try<List<Nivel>> cargarNivelesPersonalizados() {
        return repositorio.listarPersonalizados()
            .map(niveles -> niveles
                .map(nivelDTO -> repositorio.obtenerBloques(nivelDTO.id())
                    .map(bloques -> convertirDTOANivel(nivelDTO, bloques))
                    .getOrElseThrow(e -> new RuntimeException("Error al cargar bloques", e)))
                .toJavaList()
            );
    }

    /**
     * Carga los niveles filtrados por dificultad especificada.
     *
     * @param dificultad nivel de dificultad a filtrar (1-10)
     * @return Try con la lista de niveles de la dificultad especificada
     */
    public Try<List<Nivel>> cargarNivelesPorDificultad(final int dificultad) {
        return repositorio.listarPorDificultad(dificultad)
            .map(niveles -> niveles
                .map(nivelDTO -> repositorio.obtenerBloques(nivelDTO.id())
                    .map(bloques -> convertirDTOANivel(nivelDTO, bloques))
                    .getOrElseThrow(e -> new RuntimeException("Error al cargar bloques", e)))
                .toJavaList()
            );
    }

    /**
     * Carga todos los niveles (personalizados y predefinidos).
     *
     * @return Try con la lista de todos los niveles
     */
    public Try<List<Nivel>> cargarTodosLosNiveles() {
        return repositorio.listarTodos()
            .map(niveles -> niveles
                .map(nivelDTO -> repositorio.obtenerBloques(nivelDTO.id())
                    .map(bloques -> convertirDTOANivel(nivelDTO, bloques))
                    .getOrElseThrow(e -> new RuntimeException("Error al cargar bloques", e)))
                .toJavaList()
            );
    }

    /**
     * Elimina un nivel de la base de datos.
     *
     * @param id identificador del nivel a eliminar
     * @return Try con void si la eliminación es exitosa
     */
    public Try<Void> eliminarNivel(String id) {
        return repositorio.eliminar(id);
    }

    /**
     * Verifica si existe un nivel con el ID especificado.
     *
     * @param id identificador del nivel
     * @return Try con true si existe, false si no existe
     */
    public Try<Boolean> existeNivel(String id) {
        return repositorio.existePorId(id);
    }

    /**
     * Convierte un Nivel del dominio a un NivelDTO para persistencia.
     *
     * @param nivel nivel del dominio
     * @return NivelDTO para persistencia
     */
    private NivelDTO convertirNivelADTO(Nivel nivel) {
        return new NivelDTO(
            nivel.getId(),
            nivel.getNombre(),
            nivel.getCreador(),
            nivel.getDificultad(),
            nivel.isMapaPersonalizado(),
            LocalDateTime.now()
        );
    }

    /**
     * Convierte los bloques de un Nivel a una lista de BloqueDTOs.
     *
     * @param nivel nivel con los bloques
     * @return lista inmutable de BloqueDTOs
     */
    private io.vavr.collection.List<BloqueDTO> convertirBloquesADTO(Nivel nivel) {
        return io.vavr.collection.List.ofAll(nivel.getBloques())
            .map(bloque -> BloqueDTO.crearNuevo(
                nivel.getId(),
                bloque.obtenerX(),
                bloque.obtenerY(),
                bloque.obtenerAncho(),
                bloque.obtenerAlto(),
                bloque.obtenerResistencia(),
                bloque.obtenerTipo().name()
            ));
    }

    /**
     * Convierte un NivelDTO y sus bloques a un Nivel del dominio.
     *
     * @param nivelDTO datos del nivel desde persistencia
     * @param bloquesDTO lista de bloques desde persistencia
     * @return Nivel del dominio
     */
    private Nivel convertirDTOANivel(NivelDTO nivelDTO, io.vavr.collection.List<BloqueDTO> bloquesDTO) {
        Nivel nivel = new Nivel();
        nivel.setId(nivelDTO.id());
        nivel.setNombre(nivelDTO.nombre());
        nivel.setCreador(nivelDTO.creador());
        nivel.setDificultad(nivelDTO.dificultad());
        nivel.setMapaPersonalizado(nivelDTO.esPersonalizado());

        List<Bloque> bloques = bloquesDTO
            .map(this::convertirDTOABloque)
            .toJavaList();

        nivel.setBloques(bloques);
        return nivel;
    }

    /**
     * Convierte un BloqueDTO a un Bloque del dominio.
     *
     * @param bloqueDTO datos del bloque desde persistencia
     * @return Bloque del dominio
     */
    private Bloque convertirDTOABloque(BloqueDTO bloqueDTO) {
        TipoBloque tipo = TipoBloque.valueOf(bloqueDTO.tipoBloque());
        return new Bloque(
            bloqueDTO.posicionX(),
            bloqueDTO.posicionY(),
            bloqueDTO.ancho(),
            bloqueDTO.alto(),
            bloqueDTO.resistencia(),
            tipo
        );
    }

}
