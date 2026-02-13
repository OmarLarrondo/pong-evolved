package persistencia.conexion;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.vavr.control.Try;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Gestor de conexiones a SQLite usando HikariCP para pooling de conexiones.
 * Implementa el patrón Singleton para garantizar una única instancia del pool.
 * Utiliza Vavr para manejo de errores.
 */
public final class ConexionSQLite {

    private static final String RUTA_BASE_DATOS = "data/pong.db";
    private static final String URL_JDBC = "jdbc:sqlite:" + RUTA_BASE_DATOS;
    private static final String DIRECTORIO_DATA = "data";

    private static volatile ConexionSQLite instancia;
    private final HikariDataSource dataSource;

    /**
     * Constructor privado que inicializa el pool de conexiones HikariCP.
     * Asegura que el directorio de datos exista antes de crear el pool.
     */
    private ConexionSQLite() {
        this.dataSource = crearDirectorioData()
            .flatMap(unused -> Try.of(() -> crearConfiguracionHikari()))
            .flatMap(config -> Try.of(() -> new HikariDataSource(config)))
            .getOrElseThrow(causa ->
                new RuntimeException("Error al inicializar pool de conexiones: " + causa.getMessage(), causa)
            );
    }

    /**
     * Crea el directorio de datos si no existe.
     * Operación pura que retorna un Try indicando éxito o fallo.
     *
     * @return Try con el Path del directorio creado o error
     */
    private static Try<Path> crearDirectorioData() {
        return Try.of(() -> {
            Path directorio = Paths.get(DIRECTORIO_DATA);
            return Files.exists(directorio)
                ? directorio
                : Files.createDirectories(directorio);
        });
    }

    /**
     * Crea la configuración de HikariCP de forma pura.
     * Método funcional sin efectos secundarios que retorna una configuración inmutable.
     *
     * @return configuración de HikariCP
     */
    private static HikariConfig crearConfiguracionHikari() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL_JDBC);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return config;
    }

    /**
     * Obtiene la instancia única del gestor de conexiones.
     *
     * @return la instancia única de ConexionSQLite
     */
    public static ConexionSQLite obtenerInstancia() {
        if (instancia == null) {
            synchronized (ConexionSQLite.class) {
                if (instancia == null) {
                    instancia = new ConexionSQLite();
                }
            }
        }
        return instancia;
    }

    /**
     * Obtiene una conexión del pool de manera funcional.
     *
     * @return Try con la conexión si es exitosa, o una excepción si falla
     */
    public Try<Connection> obtenerConexion() {
        return Try.of(dataSource::getConnection);
    }

    /**
     * Inicializa la base de datos creando las tablas si no existen.
     * Operación idempotente que puede ejecutarse múltiples veces de forma segura.
     * El directorio de datos se crea automáticamente en el constructor.
     *
     * @return Try con void si la inicialización es exitosa, o una excepción si falla
     */
    public Try<Void> inicializarBaseDatos() {
        return obtenerConexion().flatMap(conexion ->
            Try.withResources(() -> conexion).of(conn -> {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(crearTablaNiveles());
                    stmt.execute(crearTablaBloques());
                    return null;
                }
            })
        );
    }

    /**
     * Genera el SQL para crear la tabla de niveles.
     *
     * @return sentencia SQL CREATE TABLE para niveles
     */
    private String crearTablaNiveles() {
        return """
            CREATE TABLE IF NOT EXISTS niveles (
                id TEXT PRIMARY KEY,
                nombre TEXT NOT NULL,
                creador TEXT,
                dificultad INTEGER NOT NULL,
                es_personalizado INTEGER NOT NULL,
                fecha_creacion TEXT NOT NULL
            )
            """;
    }

    /**
     * Genera el SQL para crear la tabla de bloques.
     *
     * @return sentencia SQL CREATE TABLE para bloques
     */
    private String crearTablaBloques() {
        return """
            CREATE TABLE IF NOT EXISTS bloques (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nivel_id TEXT NOT NULL,
                posicion_x REAL NOT NULL,
                posicion_y REAL NOT NULL,
                ancho REAL NOT NULL,
                alto REAL NOT NULL,
                resistencia INTEGER NOT NULL,
                tipo_bloque TEXT NOT NULL,
                FOREIGN KEY (nivel_id) REFERENCES niveles(id) ON DELETE CASCADE
            )
            """;
    }

    /**
     * Cierra el pool de conexiones y libera todos los recursos.
     * Debe llamarse al finalizar la aplicación.
     *
     * @return Try con void si el cierre es exitoso, o una excepción si falla
     */
    public Try<Void> cerrarPool() {
        return Try.run(() -> {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }
        });
    }

    /**
     * Verifica si el pool de conexiones está activo.
     *
     * @return true si el pool está activo, false en caso contrario
     */
    public boolean estaActivo() {
        return dataSource != null && !dataSource.isClosed();
    }

    /**
     * Ejecuta una operación de escritura (INSERT, UPDATE, DELETE) en una transacción.
     * La transacción se confirma automáticamente si la operación es exitosa,
     * o se revierte si ocurre una excepción.
     *
     * @param operacion la operación a ejecutar dentro de la transacción
     * @return Try con el resultado de la operación, o una excepción si falla
     */
    public <T> Try<T> ejecutarEnTransaccion(FuncionSQL<Connection, T> operacion) {
        return obtenerConexion().flatMap(conexion ->
            Try.withResources(() -> conexion).of(conn -> {
                conn.setAutoCommit(false);
                try {
                    T resultado = operacion.aplicar(conn);
                    conn.commit();
                    return resultado;
                } catch (Exception e) {
                    conn.rollback();
                    throw e;
                }
            })
        );
    }

    /**
     * Interfaz funcional para operaciones SQL que pueden lanzar excepciones.
     * Permite composición funcional con manejo de errores.
     *
     * @param <T> tipo de entrada
     * @param <R> tipo de retorno
     */
    @FunctionalInterface
    public interface FuncionSQL<T, R> {
        R aplicar(T entrada) throws Exception;
    }
}
