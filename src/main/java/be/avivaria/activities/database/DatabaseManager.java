package be.avivaria.activities.database;

import be.indigosolutions.framework.util.SystemConfiguration;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class DatabaseManager {

    public static void initDatabase() throws SQLException {
        String userHome = System.getProperty("user.home");
        String dbDir = userHome + File.separator + ".avivaria";
        System.setProperty("derby.system.home", dbDir);
        String dbURL = "jdbc:derby:activities";

        DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
        try (Connection dbConnection = DriverManager.getConnection(dbURL + ";create=true")) {

            String version = getVersion(dbConnection);
            ScriptRunner scriptRunner = new ScriptRunner();
            if (version == null) {
                System.out.println("INITIALIZING DATABASE");
                createSchema(dbConnection, scriptRunner);
                bootstrapInitialData(dbConnection, scriptRunner);
                version = getVersion(dbConnection);
            }

            System.out.println("CURRENT VERSION :: " + Version.parse(version));
            System.out.println("APP VERSION :: " + getAppVersion());
            final String dbVersion = version;

            getAvailableUpgrades().entrySet().forEach(u -> {
                try {
                    if (Version.parse(dbVersion).isOlder(u.getKey())) {
                        System.out.println("UPGRADE TO VERSION " + u.getKey());
                        runScript(dbConnection, scriptRunner, u.getValue());
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static Version getAppVersion() {
        return Version.parse(SystemConfiguration.AppVersion.getValue().replace("-SNAPSHOT",""));
    }

    private static void createSchema(Connection c, ScriptRunner scriptRunner) throws SQLException {
        runScript(c, scriptRunner, "initial_schema.sql");
    }

    private static void bootstrapInitialData(Connection c, ScriptRunner scriptRunner) throws SQLException {
        runScript(c, scriptRunner, "initial_data.sql");
    }

    private static void runScript(Connection c, ScriptRunner scriptRunner, String scriptName) throws SQLException {
        InputStream inputStream = DatabaseManager.class.getResourceAsStream(scriptName);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")))) {
            scriptRunner.runScript(c, bufferedReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getVersion(Connection c) throws SQLException {
        try (Statement statement = c.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT version FROM version");
            if (rs.next()) {
                return rs.getString("version");
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    static Map<Version, String> getAvailableUpgrades() {
        TreeMap<Version, String> availableUpgrades = new TreeMap<>();
        try {
            for (String script : getUpgradeScripts()) {
                String version = script.replace("upgrade-", "").replace(".sql", "");
                availableUpgrades.put(Version.parse(version), script);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        return availableUpgrades;
    }

    static List<String> getUpgradeScripts() throws URISyntaxException, IOException {
        return getScripts().stream().filter(s -> s.startsWith("upgrade-")).collect(Collectors.toList());
    }

    final static String SCRIPTS_DIR = "be/avivaria/activities/database/";

    static List<String> getScripts() throws URISyntaxException, IOException {
        URL dirURL = DatabaseManager.class.getClassLoader().getResource(SCRIPTS_DIR);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
        /* A file path: easy enough */
            return Arrays.asList(new File(dirURL.toURI()).list((dir, name) -> name.endsWith(".sql")));
        }

        if (dirURL == null) {
        /*
         * In case of a jar file, we can't actually find a directory.
         * Have to assume the same jar as clazz.
         */
            String me = DatabaseManager.class.getName().replace(".", "/") + ".class";
            dirURL = DatabaseManager.class.getClassLoader().getResource(me);
        }

        if (dirURL.getProtocol().equals("jar")) {
            /* A JAR path */
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            Set<String> result = new HashSet<>(); //avoid duplicates in case it is a subdirectory
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(SCRIPTS_DIR)) { //filter according to the path
                    String entry = name.substring(SCRIPTS_DIR.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory name
                        entry = entry.substring(0, checkSubdir);
                    }
                    if (entry.endsWith(".sql")) {
                        result.add(entry);
                    }
                }
            }
            return new ArrayList<>(result);
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }

    static class Version implements Comparable<Version> {
        final int major;
        final int minor;

        Version(int major, int minor) {
            this.major = major;
            this.minor = minor;
        }

        boolean isOlder(Version o) {
            return this.compareTo(o) < 0;
        }

        @Override
        public int compareTo(Version o) {
            int compareMajor = this.major - o.major;
            if (compareMajor != 0) return compareMajor;
            return this.minor - o.minor;
        }

        @Override
        public String toString() {
            return major + "." + minor;
        }

        static Version parse(String versionString) {
            String[] parts = versionString.split("\\.");
            return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }
}
