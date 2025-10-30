package util;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles all logic between fxml files and their controllers.
 */
public class ControllerInjection {

    private static final Logger logger = LoggerFactory.getLogger(ControllerInjection.class);

    /**
     * A map of all Controllers that can be injected, and the suppliers responsible for loading them.
     **/
    private static final Map<Class<?>, Supplier<Object>> injectionMethods = new HashMap<>();

    /**
     * Loads a given fxml file with one constructor specified.
     * This method can be used if the fxml file only has exactly one controller (that essentially means
     * there's no fx:include with its own fx:controller present in this file).
     * @param location location of the fxml file
     * @param controllerClass class of the controller which is set in the fxml file
     * @param controllerConstructor method which will be used to instantiate the controller
     * @return the object returned from {@link FXMLLoader#load()}
     * @throws IOException if an error occurred while reading
     */
    public static Parent load(String location, Class<?> controllerClass, Supplier<Object> controllerConstructor) throws IOException {
        return load(location, Map.of(controllerClass, controllerConstructor));
    }

    /**
     * Loads a given fxml file with constructors specified.
     * @param location location of the fxml file
     * @param controllerConstructors map where the key is the class of the controller which is set in the fxml file and
     *                               the value is the method which will be used to instantiate the controller.
     *                               Meant to be used with {@link Map#of()}.
     * @return the object returned from {@link FXMLLoader#load()}
     * @throws IOException if an error occurred while reading
     */
    public static Parent load(String location, Map<Class<?>, Supplier<Object>> controllerConstructors) throws IOException {
        Map<Class<?>, Supplier<Object>> localAndGlobalCombined = Stream.of(injectionMethods, controllerConstructors) //order of variable matters!
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (globalSupplier, localSupplier) -> localSupplier, //if local supplier is provided while a global supplier already exists, prefer local supplier
                        HashMap::new
                ));
        FXMLLoader loader = getLoader(location, localAndGlobalCombined);
        return loader.load();
    }

    /**
     * Loads a given fxml file which either has no controller specified or purposely uses the default (empty) constructor.
     * @param location location of the fxml file
     * @return the object returned from {@link FXMLLoader#load()}
     * @throws IOException if an error occurred while reading
     */
    public static Parent load(String location) throws IOException {
        return load(location, Map.of());
    }

    private static FXMLLoader getLoader(String location, Map<Class<?>, Supplier<Object>> controllerConstructors) {
        return new FXMLLoader(
                ControllerInjection.class.getResource(location),
                null,
                new JavaFXBuilderFactory(),
                fxmlControllerClass -> controllerConstructors.getOrDefault(fxmlControllerClass, () -> loadControllerWithDefaultConstructor(fxmlControllerClass)).get());
    }

    private static Object loadControllerWithDefaultConstructor(Class<?> controller) {

        logger.info("Loading default constructor for controller {}. Maybe you missed to specify a constructor? ", controller);
        try {
            return controller.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Adds this controller with the construction method to a global "register".
     * Anytime any fxml file is loaded where this controller is required, the construction method will be used.
     * @param controllerClass class of the controller which is set in the fxml file
     * @param controllerConstructor method which will be used to instantiate the controller
     */
    public static void addGlobalInjection(Class<?> controllerClass, Supplier<Object> controllerConstructor) {
        injectionMethods.put(controllerClass, controllerConstructor);
    }

    /**
     * Removes this controller from the "global register".
     * @param controllerClass class of the controller which is set in the fxml file
     */
    public static void removeGlobalInjection(Class<?> controllerClass) {
        injectionMethods.remove(controllerClass);
    }
}
