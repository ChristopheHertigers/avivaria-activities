package be.indigosolutions.framework;

/**
 * A listener for a particular event, needs parameterization for typesafe usage of the payload.
 *
 * @author Christophe Hertigers
 * @version $Id: $
 * @created 21-jul-2008
 */
public interface DefaultEventListener<PAYLOAD> {
    public void handleEvent(DefaultEvent<PAYLOAD> event);
}
