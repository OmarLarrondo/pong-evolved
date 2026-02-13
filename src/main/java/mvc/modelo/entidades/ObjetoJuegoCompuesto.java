package mvc.modelo.entidades;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * Implementacion del patron Composite para manejar grupos de objetos del juego.
 * Permite tratar colecciones de objetos de manera uniforme con objetos individuales,
 * facilitando la gestion de estructuras jerarquicas en el juego.
 */
public class ObjetoJuegoCompuesto extends ObjetoJuego{

    /**
     * Lista de objetos hijo contenidos en este objeto compuesto.
     */
    private List<ObjetoJuego> hijos;

    /**
     * Construye un nuevo objeto compuesto del juego.
     *
     * @param x posicion horizontal inicial
     * @param y posicion vertical inicial
     * @param ancho ancho del objeto
     * @param alto alto del objeto
     */
    public ObjetoJuegoCompuesto(double x, double y, double ancho, double alto) {
        super(x, y, ancho, alto);
        this.hijos = new ArrayList<>();

    }

    /**
     * Agrega un objeto hijo a este objeto compuesto.
     *
     * @param objeto el objeto a agregar
     * @throws IllegalArgumentException si el objeto es nulo
     */
    public void agregar(ObjetoJuego objeto){
        if(objeto == null){
            throw new IllegalArgumentException("No es posible agregar eso");
        }
        hijos.add(objeto);
    }
    
    /**
     * Elimina un objeto hijo de este objeto compuesto.
     *
     * @param objeto el objeto a eliminar
     * @throws IllegalArgumentException si el objeto es nulo
     */
    public void  eliminar(ObjetoJuego objeto){
        if(objeto == null){
            throw new IllegalArgumentException("Objeto invalido a eliminar");
        }
        hijos.remove(objeto);
    }
    
    /**
     * Obtiene la lista de objetos hijo contenidos en este objeto compuesto.
     *
     * @return lista de objetos hijo, o una lista vacia si no hay hijos
     */
    public List<ObjetoJuego> obtenerHijos(){
        if(hijos == null){
            return new ArrayList<ObjetoJuego>();
        }
        return hijos;
    }

    /**
     * Actualiza todos los objetos hijo contenidos en este objeto compuesto.
     *
     * @param deltaTime tiempo transcurrido desde la ultima actualizacion en segundos
     * @throws IllegalArgumentException si el tiempo es menor o igual a cero
     * @throws IllegalStateException si la lista de hijos no ha sido inicializada
     */
    @Override
    public void actualizar(double deltaTime) {
        if(deltaTime <= 0){
            throw new IllegalArgumentException("El tiempo debe ser positivo");
        }
        if(hijos == null){
            throw new IllegalStateException("La lista de hijos no ha sido inicializada.");
        }
        for (ObjetoJuego objetoJuego : hijos) {
            objetoJuego.actualizar(deltaTime);
        }
    }

    /**
     * Calcula el rectangulo delimitador que engloba todos los objetos hijo.
     * Si no hay hijos, retorna los limites propios del objeto compuesto.
     *
     * @return rectangulo delimitador que contiene todos los objetos hijo
     */
    @Override
    public Rectangle2D obtenerLimites() {
        if (hijos == null || hijos.isEmpty()) {
            return new Rectangle2D(obtenerX(), obtenerY(), obtenerAncho(), obtenerAlto());
        }
	
        Rectangle2D limites = hijos.get(0).obtenerLimites();

        double minX = limites.getMinX();
        double minY = limites.getMinY();
        double maxX = limites.getMaxX();
        double maxY = limites.getMaxY();

        for (int i = 1; i < hijos.size(); i++) {
            Rectangle2D r = hijos.get(i).obtenerLimites();
            minX = Math.min(minX, r.getMinX());
            minY = Math.min(minY, r.getMinY());
            maxX = Math.max(maxX, r.getMaxX());
            maxY = Math.max(maxY, r.getMaxY());
        }

        return new Rectangle2D(minX, minY, maxX - minX, maxY - minY);
    }


    /**
     * Dibuja todos los objetos hijo contenidos en este objeto compuesto.
     *
     * @param gc contexto grafico donde se dibujaran los objetos
     * @throws IllegalArgumentException si el contexto grafico es nulo
     * @throws IllegalStateException si la lista de hijos no ha sido inicializada
     */
    @Override
    public void dibujar(GraphicsContext gc) {
        if(gc == null){
            throw new IllegalArgumentException("El contexto grafico no puede ser nulo");
        }
        if(hijos == null){
            throw new IllegalStateException("La lista de hijos no ha sido inicializada.");
        }
        for (ObjetoJuego objetoJuego : hijos) {
            objetoJuego.dibujar(gc);
        }
    }
}
