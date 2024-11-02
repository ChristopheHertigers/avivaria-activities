package be.indigosolutions.framework.util;

import be.indigosolutions.framework.exception.MissingArgumentException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @author Christophe Hertigers
 * @version $Id: $
 * @created 15-aug-2008
 */
public enum SystemConfiguration {
    AppVersion("app.version");

    private final String propertyName;

    SystemConfiguration(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getValue() {
        return SystemConfigurationManager.getProperty(this.propertyName);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public int getInteger() {
        try {
            return Integer.parseInt(getValue());
        } catch (NumberFormatException e) {
            throw new RuntimeException("System Property '" + propertyName + "' should have an integer value (was:" + getValue() + ")");
        }
    }

    public List<String> getList() {
        return getList(",");
    }

    public List<String> getList(String separator) {
        return CollectionUtils.split(getValue(), separator);
    }

    public boolean isSet() {
        return StringUtils.isNotBlank(getValue());
    }

    public boolean isActive() {
        return Boolean.valueOf(getValue());
    }

    public String toString() {
        return new StringBuilder("[").append(propertyName)
                .append("=").append(getValue()).toString();
    }

    /**
     * Utility class to get access to system properties.
     * <p/>
     * <b>Note:</b> Do not use this class directly, instead use {@link SystemConfiguration}
     *
     * @author Christophe Hertigers
     * @created 15-aug-2008
     */
    private static class SystemConfigurationManager {
        private static final String SYSTEM_PROP_FILE = "system.properties";
        private static final Properties properties;

        static {
            properties = new Properties();
            try {
                properties.load(SystemConfiguration.class.getClassLoader().getResourceAsStream(SYSTEM_PROP_FILE));
            } catch (IOException e) {
                throw new RuntimeException("Unable to load " + SYSTEM_PROP_FILE, e);
            }
        }

        /**
         * Retrieve a property value.
         *
         * @param name the name of the property.
         * @return the property value or <code>null</code>.
         */
        public static String getProperty(SystemConfiguration name) {
            return getProperty(name.getPropertyName());
        }

        /**
         * Retrieve a property value.
         *
         * @param name the name of the property.
         * @return the property value or <code>null</code>.
         */
        public static String getProperty(String name) {
            if (StringUtils.isBlank(name)) {
                throw new MissingArgumentException("name");
            }
            return properties.getProperty(name);
        }
    }

}
