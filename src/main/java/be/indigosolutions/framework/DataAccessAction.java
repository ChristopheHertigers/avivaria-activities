package be.indigosolutions.framework;

import org.apache.logging.log4j.LogManager;
import org.hibernate.Session;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionEvent;

import be.indigosolutions.framework.util.HibernateUtils;

/**
 * Overrides executeInController() and adds transaction demarcation.
 *
 * @author Christophe Hertigers
 * @version $Id: $
 * @created 21-jul-2008
 */
public abstract class DataAccessAction extends DefaultAction {

    private static final Logger logger = LogManager.getLogger(DataAccessAction.class);

    public DataAccessAction() {}

    public void actionPerformed(ActionEvent actionEvent) {
        actionPerformed(actionEvent, null,null);//HibernateUtils.getOdbcSessionFactory().getCurrentSession(), HibernateUtils.getWebSessionFactory().getCurrentSession()); //compile
    }

    public void executeInController(AbstractController controller, ActionEvent event) {
        PersistenceController persistenceController;
        if (controller instanceof PersistenceController)
            persistenceController = (PersistenceController) controller;
        else
            throw new IllegalArgumentException(
                "Controller: " + controller.getClass() + " is not a PersistenceController"
            );

        if (preTransaction()) { return; }
        try {
            //log.debug("Beginning database transaction");
            //persistenceController.getWebPersistenceContext().beginTransaction();

            logger.debug("Executing action event");
            actionPerformed(event, null,null);//persistenceController.getOdbcPersistenceContext(), persistenceController.getWebPersistenceContext()); //compile

            //log.debug("Committing database transaction");
            //persistenceController.getWebPersistenceContext().getTransaction().commit();
        } catch (RuntimeException ex) {
            //log.debug("Rolling back database transaction");
            //persistenceController.getWebPersistenceContext().getTransaction().rollback();
            failedTransaction();
            throw ex;
        }
        postTransaction();
    }

    /**
     * Executes after transaction completion, Session is in auto-commit mode.
     *
     * @return Return <tt>true</tt> to abort execution.
     */
    protected boolean preTransaction() { return false; }

    /**
     * Executes before transaction begins, Session is in auto-commit mode.
     */
    protected void postTransaction() {}

    /**
     * Executes when transaction failed, after rollback. Session can't be used anymore.
     */
    protected void failedTransaction() {}

    /**
     * The main execute routine of this action.
     *
     * @param actionEvent the action event
     * @param currentOdbcSession use this session to access the odbc databse
     * @param currentWebSession Use this Session to access the web database.
     */
    public abstract void actionPerformed(ActionEvent actionEvent, Session currentOdbcSession, Session currentWebSession);

}
