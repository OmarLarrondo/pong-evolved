package persistencia.repositorio;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import persistencia.conexion.ConexionSQLite;
import persistencia.dto.BloqueDTO;
import persistencia.dto.NivelDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Repositorio funcional para operaciones de persistencia de niveles y bloques.
 * Utiliza Vavr para manejo de errores y composición.
 * Todas las operaciones retornan Try para manejo explícito de errores.
 */
public final class RepositorioNiveles {

    private final ConexionSQLite conexion;

    /**
     * Construye un nuevo repositorio de niveles.
     *
     * @param conexion gestor de conexiones a la base de datos
     */
    public RepositorioNiveles(ConexionSQLite conexion) {
        this.conexion = conexion;
    }

    /**
     * Guarda un nivel completo con sus bloques en una transacción atómica.
     * Si ya existe un nivel con el mismo ID, lo actualiza.
     *
     * @param nivel datos del nivel a guardar
     * @param bloques lista de bloques del nivel
     * @return Try con el ID del nivel guardado, o una excepción si falla
     */
    public Try<String> guardar(NivelDTO nivel, List<BloqueDTO> bloques) {
        return conexion.ejecutarEnTransaccion(conn -> {
            existePorId(nivel.id())
                .map(existe -> existe ? actualizarNivel(nivel) : insertarNivel(nivel))
                .flatMap(unused -> eliminarBloquesPorNivel(nivel.id()))
                .flatMap(unused -> insertarBloques(bloques))
                .get();
            return nivel.id();
        });
    }

    /**
     * Inserta un nuevo nivel en la base de datos.
     * Convierte la fecha de creación a epoch en milisegundos para compatibilidad con SQLite.
     *
     * @param nivel datos del nivel a insertar
     * @return Try con void si la inserción es exitosa
     */
    private Try<Void> insertarNivel(NivelDTO nivel) {
        return conexion.obtenerConexion().flatMap(conn ->
            Try.withResources(() -> conn).of(c -> {
                String sql = """
                    INSERT INTO niveles (id, nombre, creador, dificultad, es_personalizado, fecha_creacion)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;
                try (PreparedStatement stmt = c.prepareStatement(sql)) {
                    stmt.setString(1, nivel.id());
                    stmt.setString(2, nivel.nombre());
                    stmt.setString(3, nivel.creador());
                    stmt.setInt(4, nivel.dificultad());
                    stmt.setInt(5, nivel.esPersonalizado() ? 1 : 0);
                    
                    long epochMilis = nivel.fechaCreacion()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();
                    stmt.setLong(6, epochMilis);
                    
                    stmt.executeUpdate();
                    return null;
                }
            })
        );
    }

    /**
     * Actualiza un nivel existente en la base de datos.
     * Convierte la fecha de creación a epoch en milisegundos para compatibilidad con SQLite.
     *
     * @param nivel datos del nivel a actualizar
     * @return Try con void si la actualización es exitosa
     */
    private Try<Void> actualizarNivel(NivelDTO nivel) {
        return conexion.obtenerConexion().flatMap(conn ->
            Try.withResources(() -> conn).of(c -> {
                String sql = """
                    UPDATE niveles
                    SET nombre = ?, creador = ?, dificultad = ?, es_personalizado = ?, fecha_creacion = ?
                    WHERE id = ?
                    """;
                try (PreparedStatement stmt = c.prepareStatement(sql)) {
                    stmt.setString(1, nivel.nombre());
                    stmt.setString(2, nivel.creador());
                    stmt.setInt(3, nivel.dificultad());
                    stmt.setInt(4, nivel.esPersonalizado() ? 1 : 0);
                    
                    long epochMilis = nivel.fechaCreacion()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();
                    stmt.setLong(5, epochMilis);
                    
                    stmt.setString(6, nivel.id());
                    stmt.executeUpdate();
                    return null;
                }
            })
        );
    }

    /**
     * Inserta una lista de bloques en la base de datos.
     *
     * @param bloques lista de bloques a insertar
     * @return Try con void si la inserción es exitosa
     */
    private Try<Void> insertarBloques(List<BloqueDTO> bloques) {
        return conexion.obtenerConexion().flatMap(conn ->
            Try.withResources(() -> conn).of(c -> {
                String sql = """
                    INSERT INTO bloques (nivel_id, posicion_x, posicion_y, ancho, alto, resistencia, tipo_bloque)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;
                try (PreparedStatement stmt = c.prepareStatement(sql)) {
                    for (BloqueDTO bloque : bloques) {
                        stmt.setString(1, bloque.nivelId());
                        stmt.setDouble(2, bloque.posicionX());
                        stmt.setDouble(3, bloque.posicionY());
                        stmt.setDouble(4, bloque.ancho());
                        stmt.setDouble(5, bloque.alto());
                        stmt.setInt(6, bloque.resistencia());
                        stmt.setString(7, bloque.tipoBloque());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                    return null;
                }
            })
        );
    }

    /**
     * Lista todos los niveles almacenados en la base de datos.
     *
     * @return Try con la lista de todos los niveles
     */
    public Try<List<NivelDTO>> listarTodos() {
        return conexion.obtenerConexion().flatMap(conn ->
            Try.withResources(() -> conn).of(c -> {
                String sql = "SELECT * FROM niveles ORDER BY fecha_creacion DESC";
                try (PreparedStatement stmt = c.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    return construirListaNiveles(rs);
                }
            })
        );
    }

    /**
     * Lista solo los niveles personalizados creados por usuarios.
     *
     * @return Try con la lista de niveles personalizados
     */
    public Try<List<NivelDTO>> listarPersonalizados() {
        return conexion.obtenerConexion().flatMap(conn ->
            Try.withResources(() -> conn).of(c -> {
                String sql = "SELECT * FROM niveles WHERE es_personalizado = 1 ORDER BY fecha_creacion DESC";
                try (PreparedStatement stmt = c.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    return construirListaNiveles(rs);
                }
            })
        );
    }

    /**
     * Lista los niveles filtrados por dificultad especificada.
     *
     * @param dificultad nivel de dificultad a filtrar (1-10)
     * @return Try con la lista de niveles de la dificultad especificada
     */
    public Try<List<NivelDTO>> listarPorDificultad(final int dificultad) {
        return conexion.obtenerConexion().flatMap(conn ->
            Try.withResources(() -> conn).of(c -> {
                final String sql = "SELECT * FROM niveles WHERE dificultad = ? ORDER BY fecha_creacion DESC";
                try (PreparedStatement stmt = c.prepareStatement(sql)) {
                    stmt.setInt(1, dificultad);
                    try (ResultSet rs = stmt.executeQuery()) {
                        return construirListaNiveles(rs);
                    }
                }
            })
        );
    }

    /**
     * Busca un nivel por su ID.
     *
     * @param id identificador del nivel
     * @return Try con Option del nivel (Some si existe, None si no existe)
     */
    public Try<Option<NivelDTO>> buscarPorId(String id) {
        return conexion.obtenerConexion().flatMap(conn ->
            Try.withResources(() -> conn).of(c -> {
                String sql = "SELECT * FROM niveles WHERE id = ?";
                try (PreparedStatement stmt = c.prepareStatement(sql)) {
                    stmt.setString(1, id);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return Option.some(construirNivel(rs));
                        }
                        return Option.none();
                    }
                }
            })
        );
    }

    /**
     * Verifica si existe un nivel con el ID especificado.
     *
     * @param id identificador del nivel
     * @return Try con true si existe, false si no existe
     */
    public Try<Boolean> existePorId(String id) {
        return buscarPorId(id).map(Option::isDefined);
    }

    /**
     * Obtiene todos los bloques de un nivel específico.
     *
     * @param nivelId identificador del nivel
     * @return Try con la lista de bloques del nivel
     */
    public Try<List<BloqueDTO>> obtenerBloques(String nivelId) {
        return conexion.obtenerConexion().flatMap(conn ->
            Try.withResources(() -> conn).of(c -> {
                String sql = "SELECT * FROM bloques WHERE nivel_id = ? ORDER BY id";
                try (PreparedStatement stmt = c.prepareStatement(sql)) {
                    stmt.setString(1, nivelId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        return construirListaBloques(rs);
                    }
                }
            })
        );
    }

    /**
     * Elimina un nivel y todos sus bloques de la base de datos.
     * La eliminación en cascada es manejada por la base de datos.
     *
     * @param id identificador del nivel a eliminar
     * @return Try con void si la eliminación es exitosa
     */
    public Try<Void> eliminar(String id) {
        return conexion.obtenerConexion().flatMap(conn ->
            Try.withResources(() -> conn).of(c -> {
                String sql = "DELETE FROM niveles WHERE id = ?";
                try (PreparedStatement stmt = c.prepareStatement(sql)) {
                    stmt.setString(1, id);
                    stmt.executeUpdate();
                    return null;
                }
            })
        );
    }

    /**
     * Elimina todos los bloques de un nivel específico.
     *
     * @param nivelId identificador del nivel
     * @return Try con void si la eliminación es exitosa
     */
    private Try<Void> eliminarBloquesPorNivel(String nivelId) {
        return conexion.obtenerConexion().flatMap(conn ->
            Try.withResources(() -> conn).of(c -> {
                String sql = "DELETE FROM bloques WHERE nivel_id = ?";
                try (PreparedStatement stmt = c.prepareStatement(sql)) {
                    stmt.setString(1, nivelId);
                    stmt.executeUpdate();
                    return null;
                }
            })
        );
    }

    /**
     * Construye una lista inmutable de niveles desde un ResultSet.
     *
     * @param rs ResultSet con datos de niveles
     * @return lista inmutable de NivelDTO
     */
    private List<NivelDTO> construirListaNiveles(ResultSet rs) throws Exception {
        List<NivelDTO> niveles = List.empty();
        while (rs.next()) {
            niveles = niveles.append(construirNivel(rs));
        }
        return niveles;
    }

    /**
     * Construye una lista inmutable de bloques desde un ResultSet.
     *
     * @param rs ResultSet con datos de bloques
     * @return lista inmutable de BloqueDTO
     */
    private List<BloqueDTO> construirListaBloques(ResultSet rs) throws Exception {
        List<BloqueDTO> bloques = List.empty();
        while (rs.next()) {
            bloques = bloques.append(construirBloque(rs));
        }
        return bloques;
    }

    /**
     * Construye un NivelDTO desde la fila actual del ResultSet.
     * Convierte epoch en milisegundos a LocalDateTime usando transformación funcional pura.
     *
     * @param rs ResultSet posicionado en una fila de nivel
     * @return NivelDTO construido
     */
    private NivelDTO construirNivel(ResultSet rs) throws Exception {
        java.time.LocalDateTime fechaCreacion = io.vavr.control.Try.of(() -> rs.getLong("fecha_creacion"))
            .toOption()
            .filter(ms -> ms > 0)
            .map(ms -> java.time.LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(ms),
                java.time.ZoneId.systemDefault()
            ))
            .getOrElse(java.time.LocalDateTime::now);

        return new NivelDTO(
            rs.getString("id"),
            rs.getString("nombre"),
            rs.getString("creador"),
            rs.getInt("dificultad"),
            rs.getInt("es_personalizado") == 1,
            fechaCreacion
        );
    }

    /**
     * Construye un BloqueDTO desde la fila actual del ResultSet.
     *
     * @param rs ResultSet posicionado en una fila de bloque
     * @return BloqueDTO construido
     */
    private BloqueDTO construirBloque(ResultSet rs) throws Exception {
        return new BloqueDTO(
            rs.getInt("id"),
            rs.getString("nivel_id"),
            rs.getDouble("posicion_x"),
            rs.getDouble("posicion_y"),
            rs.getDouble("ancho"),
            rs.getDouble("alto"),
            rs.getInt("resistencia"),
            rs.getString("tipo_bloque")
        );
    }
}
