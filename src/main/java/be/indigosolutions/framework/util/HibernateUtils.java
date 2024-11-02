package be.indigosolutions.framework.util;

import org.hibernate.SessionFactory;

import javax.persistence.Persistence;

/**
 * The only purpose of this class is to provide a SessionFactory.
 * 
 * @author Christophe Hertigers
 * @version $Id: $
 * @created 21-jul-2008
 */
public class HibernateUtils {

    private static final SessionFactory webSessionFactory;

    static {
        try {
            // Create the SessionFactory from a JPA configuration (persistence.xml)
            webSessionFactory = (SessionFactory) Persistence.createEntityManagerFactory("Avivaria");
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Avivaria SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return webSessionFactory;
    }
}
