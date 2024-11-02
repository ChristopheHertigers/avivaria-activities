package be.indigosolutions.framework;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * An action without database transaction demarcation.
 *
 * @author Christophe Hertigers
 * @version $Id: $
 * @created 21-jul-2008
 */
public abstract class DefaultAction extends AbstractAction implements Runnable {

    protected Thread thread;
    protected String actionCommand;

    public DefaultAction() {
    }

    protected DefaultAction(String actionCommand) {
        super();
        this.actionCommand = actionCommand;
    }

    public void executeInController(AbstractController controller, ActionEvent event) {
        actionPerformed(event);
    }

    public void run() {
        // Default implementation, overwrite to use.
    }

    public String getActionCommand() {
        return actionCommand;
    }
}
