package be.indigosolutions.framework;

import java.util.HashMap;
import java.util.Map;

/**
 * User: christophe
 * Date: 09/11/13
 */
public class ControllerRegistry {

    private Map<Class, AbstractController> controllerInstances;
    private static ControllerRegistry INSTANCE;

    private ControllerRegistry() {
        controllerInstances = new HashMap<>();
    }

    public static ControllerRegistry getInstance() {
        if (INSTANCE == null) INSTANCE = new ControllerRegistry();
        return INSTANCE;
    }

    public void register(AbstractController controller) {
        controllerInstances.putIfAbsent(controller.getClass(), controller);
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractController> T get(Class<T> controllerClass) {
        return (T) controllerInstances.get(controllerClass);
    }

    public void unregister(Class controllerClass) {
        controllerInstances.remove(controllerClass);
    }

    public void unregister(AbstractController controller) {
        controllerInstances.remove(controller.getClass());
    }
}
