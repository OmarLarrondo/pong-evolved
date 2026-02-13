package patrones.strategy.colision;

import mvc.modelo.entidades.ObjetoJuego;

/**
 * La interfaz {@code EstrategiaColision} define el contrato para las estrategias
 * encargadas de manejar y verificar las colisiones entre dos objetos del juego.
 * 
 * <p>Forma parte del patrón de diseño <b>Strategy</b>, permitiendo que distintas
 * implementaciones definan comportamientos personalizados para detectar y 
 * responder a colisiones sin acoplar la lógica directamente a las clases 
 * de los objetos del juego.</p>
 * 
 * <p>De este modo, se puede cambiar fácilmente la manera en que las colisiones 
 * se calculan o manejan (por ejemplo, colisionPelotaPaleta, bloque, pared, etc... ) 
 * sin modificar las clases que representan los objetos.</p>
 * 
 * @author Equipo-polimorfo
 * @version 1.0
 */
public interface EstrategiaColision {

    /**
     * Maneja la lógica que debe ejecutarse cuando ocurre una colisión entre
     * dos objetos del juego.
     *
     * @param obj1 el primer objeto involucrado en la colisión
     * @param obj2 el segundo objeto involucrado en la colisión
     */
    public void manejarColision(ObjetoJuego obj1, ObjetoJuego obj2);

    /**
     * Verifica si dos objetos del juego han colisionado de acuerdo con la
     * estrategia de detección implementada (por ejemplo, colisión pelotaPaleta, bloque, etc ...).
     *
     * @param obj1 el primer objeto a evaluar
     * @param obj2 el segundo objeto a evaluar
     * @return {@code true} si los objetos colisionan; {@code false} en caso contrario
     */
    public boolean verificarColision(ObjetoJuego obj1, ObjetoJuego obj2);
}
