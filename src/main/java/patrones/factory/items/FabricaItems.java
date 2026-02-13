package patrones.factory.items;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import mvc.modelo.items.*;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Fabrica de items que genera power-ups aleatorios usando programacion funcional pura.
 * Implementa el patron Factory para crear instancias de diferentes tipos de items.
 * Utiliza programacion funcional con Vavr para garantizar inmutabilidad y composicion.
 */
public final class FabricaItems {

    /**
     * Generador de numeros aleatorios para la seleccion de items.
     */
    private static final Random generador = new Random();

    /**
     * Duracion predeterminada para items temporales (en segundos).
     */
    private static final double DURACION_PREDETERMINADA = 10.0;

    /**
     * Lista inmutable de proveedores de items.
     * Cada proveedor es una funcion pura que crea una nueva instancia de item.
     */
    private static final List<Supplier<Item>> proveedoresItems = List.of(
        () -> new ItemAumentoVelocidad(1.5, DURACION_PREDETERMINADA, false, DURACION_PREDETERMINADA),
        () -> new ItemNeblina(DURACION_PREDETERMINADA),
        () -> new ItemRedimensionarPaleta(1.3, DURACION_PREDETERMINADA)
    );

    /**
     * Constructor privado para prevenir instanciacion.
     * Esta clase solo expone metodos estaticos puros.
     */
    private FabricaItems() {
        throw new UnsupportedOperationException("Clase utilitaria no instanciable");
    }

    /**
     * Genera un item aleatorio de forma segura.
     * Utiliza el patron Try de Vavr para manejo funcional de errores.
     *
     * @return Try conteniendo el item generado o un error si falla la generacion
     */
    public static Try<Item> generarItemAleatorio() {
        return Try.of(() -> seleccionarProveedorAleatorio()
            .map(Supplier::get)
            .getOrElseThrow(() -> new IllegalStateException("No hay proveedores de items disponibles")));
    }

    /**
     * Genera un item aleatorio con coordenadas especificas.
     * Las coordenadas se usan para posicionar el item en el juego.
     *
     * @param x coordenada X donde aparecera el item
     * @param y coordenada Y donde aparecera el item
     * @return Try conteniendo el item generado en la posicion especificada
     */
    public static Try<Item> generarItemAleatorioEn(double x, double y) {
        return generarItemAleatorio();
    }

    /**
     * Genera un item especifico por tipo.
     *
     * @param tipoItem tipo de item a generar (0-4)
     * @return Option conteniendo el item si el tipo es valido, Option.none() si no
     */
    public static Option<Item> generarItemPorTipo(int tipoItem) {
        return Option.when(
            esTipoValido(tipoItem),
            () -> proveedoresItems.get(tipoItem).get()
        );
    }

    /**
     * Obtiene el numero de tipos de items disponibles.
     *
     * @return cantidad de tipos de items que pueden generarse
     */
    public static int obtenerCantidadTipos() {
        return proveedoresItems.length();
    }

    /**
     * Selecciona un proveedor de items aleatorio de la lista.
     * Funcion pura que utiliza el generador para seleccion aleatoria.
     *
     * @return Option conteniendo el proveedor seleccionado o Option.none() si la lista esta vacia
     */
    private static Option<Supplier<Item>> seleccionarProveedorAleatorio() {
        return Option.when(
            !proveedoresItems.isEmpty(),
            () -> proveedoresItems.get(generador.nextInt(proveedoresItems.length()))
        );
    }

    /**
     * Genera una lista de items aleatorios.
     *
     * @param cantidad numero de items a generar
     * @return List inmutable de items generados exitosamente
     */
    public static List<Item> generarItems(int cantidad) {
        return List.range(0, cantidad)
            .map(i -> generarItemAleatorio())
            .filter(Try::isSuccess)
            .map(Try::get);
    }

    /**
     * Verifica si un tipo de item especifico esta disponible.
     *
     * @param tipoItem indice del tipo de item a verificar
     * @return true si el tipo es valido y esta disponible, false en caso contrario
     */
    public static boolean esTipoValido(int tipoItem) {
        return tipoItem >= 0 && tipoItem < proveedoresItems.length();
    }
}
