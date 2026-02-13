package patrones.builder;

import mvc.modelo.entidades.Nivel;

/**
 * {@code DirectorNiveles} actúa como el <b>Director</b> en el patrón de diseño Builder.
 * 
 * <p>Su función principal es orquestar el proceso de construcción de diferentes niveles del juego Pong,
 * utilizando un objeto {@link ConstructorMapa} (el <b>Builder</b> concreto) para crear niveles 
 * preconfigurados con distintas dificultades.</p>
 * 
 * <p>De esta forma, el director encapsula la lógica de cómo se combinan los pasos del
 * constructor para producir configuraciones específicas, simplificando la creación de niveles
 * y manteniendo el código cliente libre de detalles de construcción.</p>
 * 
 * @author Equipo-Polimorfo
 * @version 1.0
 * @see ConstructorMapa
 * @see BuilderNivel
 * @see Nivel
 */
public class DirectorNiveles {
    /** Constructor concreto que se usará para construir los niveles. */
    private ConstructorMapa constructor;

    /**
     * Crea un nuevo director de niveles que usará el constructor indicado.
     * 
     * @param constructor instancia de {@link ConstructorMapa} que realizará la construcción de los niveles.
     */
    public DirectorNiveles(ConstructorMapa constructor) {
        this.constructor = constructor;
    }

    /**
     * Construye un nivel de dificultad fácil.
     * 
     * <p>Este nivel contiene un patrón simple de bloques destructibles
     * con baja dificultad y sin obstáculos adicionales.</p>
     * 
     * @return un objeto {@link Nivel} configurado como “Nivel Fácil”.
     */
    public Nivel construirNivelFacil() {
        return constructor.reiniciar()
                        .establecerNombre("Nivel Fácil")
                        .establecerDificultad(1)
                        .agregarPatronBloques(50, 50)
                        .construir();
    }

    /**
     * Construye un nivel de dificultad media.
     * 
     * <p>Incluye bloques indestructibles como obstáculos y un patrón adicional
     * de bloques destructibles para aumentar la complejidad.</p>
     * 
     * @return un objeto {@link Nivel} configurado como “Nivel Medio”.
     */
    public Nivel construirNivelMedio() {
        return constructor.reiniciar()
                        .establecerNombre("Nivel Medio")
                        .establecerDificultad(2)
                        .agregarBloqueIndestructible(50, 25)
                        .agregarPatronBloques(50, 100)
                        .construir();
    }

    /**
     * Construye un nivel de dificultad alta.
     * 
     * <p>Este nivel incluye más obstáculos indestructibles y un patrón de bloques
     * que requiere mayor precisión por parte del jugador, ofreciendo un reto mayor a el.</p>
     * 
     * @return un objeto {@link Nivel} configurado como “Nivel Difícil”.
     */
    public Nivel construirNivelDificil() {
        return constructor.reiniciar()
                        .establecerNombre("Nivel Difícil")
                        .establecerDificultad(3)
                        .agregarBloqueIndestructible(100, 50)
                        .agregarPatronBloques(50, 100)
                        .construir();
    }
}
