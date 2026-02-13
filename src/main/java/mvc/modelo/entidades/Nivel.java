package mvc.modelo.entidades;

import java.util.ArrayList;
import java.util.List;
import mvc.modelo.entidades.Bloque;

/**
 * Representa un {@code Nivel} del juego Pong, con atributos como su identificador,
 * nombre, dificultad, bloques, y metadatos adicionales.
 *
 * <p>Un nivel contiene una colección de objetos {@link Bloque} que definen su
 * estructura y dificultad. Esta clase forma parte del patrón {@code Builder}
 * y es utilizada por el {@link patrones.builder.ConstructorMapa} y 
 * {@link patrones.builder.DirectorNiveles} para crear configuraciones
 * de niveles de manera modular.</p>
 *
 * <p>También puede representar niveles personalizados creados por jugadores.</p>
 *
 * @author Equipo-Polimorfo
 * @version 1.0
 */
public class Nivel {
    /** El identificador unico del nivel. */
    private String id;
    /** El nombre del nivel. */
    private String nombre;
    /** La lista de bloques */
    private List<Bloque> bloques;
    /** La Dificultad del nivel */
    private int dificultad;
    /** Indica si el mapa es personalizado. */
    private boolean mapaPersonalizado;
    /**EL creador del mapa */
    private String creador;

    /**
     * Agrega un bloque a la {@code lista} de bloques.
     *  <p>El bloque no puede ser {@code null}; si lo es, se lanza una excepción.</p>
     *
     * @param bloque bloque a agregar.
     */
    public void  agregarBloque(Bloque bloque){
        if(bloque == null){
            throw new IllegalArgumentException("Error, el bloque no puuede ser null.");
        }
        bloques.add(bloque);
    }

    /**
     * Elimina un bloque de la{@code lista} de bloques
     *  <p>El bloque no puede ser {@code null}; si lo es, se lanza una excepción.</p>
     *
     * @param bloque
     */
    public void  eliminarBloque(Bloque bloque){
        if(bloque == null){
            throw new IllegalArgumentException("Estas eliminando un bloque null.");
        }
        bloques.remove(bloque); 
    }

    /**
     * Devuelve la lista de bloques del nivel.
     *
     * <p>Si la lista está vacía, se retorna una nueva lista vacía
     * (para evitar referencias nulas).</p>
     *
     * @return lista de bloques del nivel.
     */
    public List<Bloque> obtenerBloques(){
        if(bloques.isEmpty()){
            return new ArrayList<>();
        }
        return bloques;
    }

   /**
     * Verifica si el nivel está completado.
     *
     * <p>Este método puede usarse para determinar si todos los bloques
     * destructibles han sido eliminados. Actualmente no implementa
     * lógica funcional.</p>
     *
     * @return {@code false} por defecto.
     */
    public boolean estaCompletado() {
        // Actualmente no implementado, ya que se eliminó la mecánica tipo "Breakout".
        return false;
    }

    /**
     * Reinicia el nivel, restableciendo todos sus atributos a valores por defecto.
     *
     * <p>El identificador, nombre y creador se ponen en {@code null}, la dificultad
     * se reinicia a 0, el mapa se marca como no personalizado y la lista de bloques
     * se limpia.</p>
     */
    public void reiniciar(){
        id = null;
        nombre = null;
        dificultad = 0;
        mapaPersonalizado = false;
        creador = null;

        if(bloques.isEmpty()){
            bloques = new ArrayList<>();
        }else{
            bloques.clear();;
        }        
    }
   // ------------------------
    // Getters y Setters
    // ------------------------

    /**
     * Obtiene el identificador del nivel.
     * @return el ID del nivel.
     */
    public String getId() {
        return id;
    }

    /**
     * Asigna un identificador al nivel.
     * @param id identificador único del nivel.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del nivel.
     * @return el nombre del nivel.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna un nombre al nivel.
     * @param nombre nombre del nivel.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la lista de bloques del nivel.
     * @return lista de bloques.
     */
    public List<Bloque> getBloques() {
        return bloques;
    }

    /**
     * Asigna una lista de bloques al nivel.
     * @param bloques lista de bloques.
     */
    public void setBloques(List<Bloque> bloques) {
        this.bloques = bloques;
    }

    /**
     * Obtiene la dificultad del nivel.
     * @return nivel de dificultad.
     */
    public int getDificultad() {
        return dificultad;
    }

    /**
     * Asigna la dificultad al nivel.
     * @param dificultad nivel de dificultad (1, 2 o 3, por ejemplo).
     */
    public void setDificultad(int dificultad) {
        this.dificultad = dificultad;
    }

    /**
     * Indica si el mapa fue creado por un jugador.
     * @return {@code true} si el mapa es personalizado; {@code false} en caso contrario.
     */
    public boolean isMapaPersonalizado() {
        return mapaPersonalizado;
    }

    /**
     * Define si el mapa es personalizado.
     * @param mapaPersonalizado {@code true} si el mapa fue creado por un jugador.
     */
    public void setMapaPersonalizado(boolean mapaPersonalizado) {
        this.mapaPersonalizado = mapaPersonalizado;
    }

    /**
     * Obtiene el nombre o identificador del creador del nivel.
     * @return el nombre del creador.
     */
    public String getCreador() {
        return creador;
    }

    /**
     * Asigna el nombre del creador del nivel.
     * @param creador nombre o ID del creador.
     */
    public void setCreador(String creador) {
        this.creador = creador;
    }
}
