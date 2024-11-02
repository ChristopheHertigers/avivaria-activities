package be.indigosolutions.framework;

import java.util.HashSet;
import java.util.Set;

/**
 * An event with an Object payload.
 * <p>
 * Instantiate an event, put an optional payload into it fire it with the API of a controller.
 *
 * @author Christophe Hertigers
 * @version $Id: $
 * @created 21-jul-2008
 */
public class DefaultEvent<PAYLOAD> {

    PAYLOAD payload;
    Set<AbstractController> firedInControllers = new HashSet<AbstractController>();

    public DefaultEvent() {}

    public DefaultEvent(PAYLOAD payload) {
        this.payload = payload;
    }

    public PAYLOAD getPayload() {
        return payload;
    }

    public void setPayload(PAYLOAD payload) {
        this.payload = payload;
    }

    void addFiredInController(AbstractController seenController) {
        firedInControllers.add(seenController);
    }

    boolean alreadyFired(AbstractController controller) {
        return firedInControllers.contains(controller);
    }
}
