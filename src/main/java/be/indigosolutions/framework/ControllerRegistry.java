package be.indigosolutions.framework;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * User: christophe
 * Date: 09/11/13
 */
@Component
public class ControllerRegistry implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @SuppressWarnings("NullableProblems")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ControllerRegistry.applicationContext = applicationContext;
    }
    public static <T extends AbstractController> T get(Class<T> controllerClass) {
        return applicationContext.getBean(controllerClass);
    }
}
