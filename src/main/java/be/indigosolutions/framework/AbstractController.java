package be.indigosolutions.framework;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.*;
import java.util.*;

/**
 * Abstract superclass for building a hierarchical controller structure (HMVC).
 * <p>
 * HMVC works with a tree of triads, these triads are a Model (usually several
 * JavaBeans and their binding models for the UI), a View (usually several Swing
 * UI components), and a Controller. This is a basic implementation of a controller
 * that has a pointer to a parent controller (can be null if its the root of the
 * tree) and a collection of subcontrollers (can be empty, usually isn't empty).
 * <p>
 * This needs to be subclassed to be used; if subclassed directly for an application,
 * use an appropriate constructor.
 * <p>
 * The hierarchy of controller supports propagation of action execution and
 * propagation of events.
 * <p>
 * If a controllers view is a <tt>Frame</tt>, you should also register it as a
 * <tt>WindowListener</tt>, so that it can properly clean up its state when the
 * window is closed.
 *
 * @author Christophe Hertigers
 * @version $Id: $
 * @created 21-jul-2008
 */
public abstract class AbstractController implements ActionListener, WindowListener {

    private static final Logger logger = LogManager.getLogger(AbstractController.class);

    private Container view;
    private AbstractController parentController;
    private java.util.List<AbstractController> subControllers = new ArrayList<AbstractController>();
    private Map<String, DefaultAction> actions = new HashMap<String, DefaultAction>();
    private Map<Class, java.util.List<DefaultEventListener>> eventListeners =
            new HashMap<Class, java.util.List<DefaultEventListener>>();

    /**
     * Subclass wants to control own view and is root controller.
     */
    public AbstractController() {}

    /**
     * Subclass wants to control own view and is a subcontroller.
     *
     * @param parentController the parent controller
     */
    public AbstractController(AbstractController parentController) {
        this(null, parentController);
    }

    /**
     * Subclass is completely dependend on the given view and is a subcontroller.
     * @param view the view
     * @param parentController the parent controller
     */
    public AbstractController(Container view, AbstractController parentController) {
        this.view = view;

        // Check if this is a subcontroller or a root controller
        if (parentController != null) {
            this.parentController = parentController;
            parentController.getSubControllers().add(this);
        }
    }


    public Container getView() {
        return view;
    }

    public AbstractController getParentController() {
        return parentController;
    }

    public java.util.List<AbstractController> getSubControllers() {
        return subControllers;
    }


    /**
     * Close controller and all children, detach it from parent controller.
     */
    public void dispose() {
        logger.debug("Disposing controller " + getClass().getName());
        for (Iterator<AbstractController> iterator = getSubControllers().iterator(); iterator.hasNext();) {
            AbstractController subController = iterator.next();
            subController.dispose();
            iterator.remove();
        }
    }

    /**
     * Register an action that can be executed by this controller.
     *
     * @param source The source component, this method sets action command and registers the controller as listener.
     * @param action An actual action implementation.
     */
    public void registerAction(AbstractButton source, DefaultAction action) {
        source.setActionCommand(action.getActionCommand());
        source.addActionListener(this);
        this.actions.put(action.getActionCommand(), action);
    }

    /**
     * Register an action that can be executed by this controller.
     *
     * @param source The source component, this method sets action command and registers the controller as listener.
     * @param action An actual action implementation.
     */
    public void registerAction(JComboBox source, DefaultAction action) {
        source.setActionCommand(action.getActionCommand());
        source.addActionListener(this);
        this.actions.put(action.getActionCommand(), action);
    }

    /**
     * Register an action that can be executed by this controller.
     *
     * @param source The source component, this method sets action command and registers the controller as listener.
     * @param action An actual action implementation.
     */
    public void registerAction(JMenuItem source, DefaultAction action) {
        source.setActionCommand(action.getActionCommand());
        source.addActionListener(this);
        this.actions.put(action.getActionCommand(), action);
    }

    /**
     * Register an event listener that is being executed when an event is intercepted by this controller.
     *
     * @param eventClass The actual event class this listeners is interested in.
     * @param eventListener The listener implementation.
     */
    public void registerEventListener(Class eventClass, DefaultEventListener eventListener) {
        logger.debug("Registering listener: " + eventListener + " for event type: " + eventClass.getName());
        java.util.List<DefaultEventListener> listenersForEvent = eventListeners.get(eventClass);
        if (listenersForEvent == null) { listenersForEvent = new ArrayList<DefaultEventListener>(); }
        listenersForEvent.add(eventListener);
        eventListeners.put(eventClass, listenersForEvent);
    }

    /**
     * Fire an event and pass it into the hierarchy of controllers.
     * <p>
     * The event is propagated only to the controller instance and its subcontrollers, not upwards in the hierarchy.
     *
     * @param event The event to be propagated.
     */
    public void fireEvent(DefaultEvent event) {
        fireEvent(event, false);
    }

    /**
     * Fire an event and pass it into the hierarchy of controllers.
     * <p>
     * The event is propagated to the controller instance, its subcontrollers, and upwards into the controller
     * hierarchy. This operation effectively propagats the event to every controller in the whole hierarchy.
     *
     * @param event The event to be propagated.
     */
    public void fireEventGlobal(DefaultEvent event) {
        fireEvent(event, true);
    }

    @SuppressWarnings("unchecked")
    private void fireEvent(DefaultEvent event, boolean global) {
        if (!event.alreadyFired(this)) {
            if (eventListeners.get(event.getClass()) != null) {
                for (DefaultEventListener eventListener : eventListeners.get(event.getClass())) {
                    logger.debug("Event: " + event.getClass().getName() + " with listener: " + eventListener.getClass().getName());
                    eventListener.handleEvent(event);
                }
            }
            event.addFiredInController(this);
            logger.debug("Passing event: " + event.getClass().getName() + " DOWN in the controller hierarchy");
            for (AbstractController subController : subControllers) subController.fireEvent(event, global);
        }
        if (getParentController() != null
            && !event.alreadyFired(getParentController())
            && global) {
            logger.debug("Passing event: " + event.getClass().getName() + " UP in the controller hierarchy");
            getParentController().fireEvent(event, global);
        }
    }

    /**
     * Executes an action if it has been registered for this controller, otherwise passes it up the chain.
     * <p>
     * This method extracts the source of the action (an <tt>AbstractButton</tt>) and gets the action
     * command. If the controller has this command registered, the registered action is executed. Otherwise
     * the action is passed upwards in the hierarchy of controllers.
     *
     * @param actionEvent the action event
     */
    public void actionPerformed(ActionEvent actionEvent) {

        try {
            String actionCommand;
            if (actionEvent.getSource() instanceof JComboBox) {
                actionCommand = ((JComboBox)actionEvent.getSource()).getActionCommand();
            } else {
                actionCommand = ((AbstractButton)actionEvent.getSource()).getActionCommand();
            }
            DefaultAction action = actions.get(actionCommand);

            if (action != null) {
                // This controller can handle the action
                try {
                    preActionExecute();
                    action.executeInController(this, actionEvent);
                    postActionExecute();
                } catch (Exception ex) {
                    failedActionExecute();
                    throw new RuntimeException(ex);
                } finally {
                    finalActionExecute();
                }
            } else {
                // Let's try the parent controller in the hierarchy
                if(getParentController() != null) {
                    parentController.actionPerformed(actionEvent);
                } else {
                    throw new RuntimeException("Nobody is responsible for action command: " + actionCommand);
                }
            }
        }
        catch(ClassCastException e) {
            throw new IllegalArgumentException("Action source is not an Abstractbutton: " + actionEvent);
        }
    }

    protected void preActionExecute() {}
    protected void postActionExecute() {}
    protected void failedActionExecute() {}
    protected void finalActionExecute() {}

    // If this controller is responsible for a JFrame, close it and all its children when the
    // window is closed.
    public void windowClosing(WindowEvent windowEvent) { dispose(); }
    public void windowOpened(WindowEvent windowEvent) {}
    public void windowClosed(WindowEvent windowEvent) {}
    public void windowIconified(WindowEvent windowEvent) {}
    public void windowDeiconified(WindowEvent windowEvent) {}
    public void windowActivated(WindowEvent windowEvent) {}
    public void windowDeactivated(WindowEvent windowEvent) {}

}