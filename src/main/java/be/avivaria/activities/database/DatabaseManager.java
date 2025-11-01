package be.avivaria.activities.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.hibernate.Session;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Component
public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    final static String SCRIPTS_DIR = "be/avivaria/activities/database/";

    @PersistenceContext
    private EntityManager em;

    private final String appVersion;

    @Autowired
    public DatabaseManager(@Value("${app.version}") String version) {
        this.appVersion = version;
    }

    private Version getAppVersion() {
        return Version.parse(appVersion.replace("-SNAPSHOT", ""));
    }

    private void createSchema(Session session, ScriptRunner scriptRunner) {
        runScript(session, scriptRunner, "initial_schema.sql");
    }

    private void bootstrapInitialData(Session session, ScriptRunner scriptRunner) {
        runScript(session, scriptRunner, "initial_data.sql");
    }

    private void runScript(Session session, ScriptRunner scriptRunner, String scriptName) {
        InputStream inputStream = DatabaseManager.class.getResourceAsStream(scriptName);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            scriptRunner.runScript(session, bufferedReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getVersion(Session session) {
        return session.doReturningWork(c -> {
            try (Statement statement = c.createStatement()) {
                ResultSet rs = statement.executeQuery("SELECT version FROM version");
                if (rs.next()) {
                    return rs.getString("version");
                }
            } catch (SQLException e) {
                return null;
            }
            return null;
        });
    }

    Map<Version, String> getAvailableUpgrades() {
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

    List<String> getUpgradeScripts() throws URISyntaxException, IOException {
        return Collections.emptyList(); //todo
        //return getScripts().stream().filter(s -> s.startsWith("upgrade-")).collect(Collectors.toList());
    }

    List<String> getScripts() throws URISyntaxException, IOException {
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
            Set<String> result = new HashSet<>(); //avoid duplicates in case it is a subdirectory
            try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
                Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
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
            }
            return new ArrayList<>(result);
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }


    public void initDatabase() {
        logger.info(" init database ");
        Session session = em.unwrap(Session.class);

        String version = getVersion(session);
        ScriptRunner scriptRunner = new ScriptRunner();
        if (version == null) {
            System.out.println("INITIALIZING DATABASE");
            createSchema(session, scriptRunner);
            bootstrapInitialData(session, scriptRunner);
            version = getVersion(session);
        }

        System.out.println("CURRENT VERSION :: " + Version.parse(version));
        System.out.println("APP VERSION :: " + getAppVersion());
        final String dbVersion = version;

        getAvailableUpgrades().forEach((key, value) -> {
            if (Version.parse(dbVersion).isOlder(key)) {
                System.out.println("UPGRADE TO VERSION " + key);
                runScript(session, scriptRunner, value);
            }
        });
    }

    record Version(int major, int minor) implements Comparable<Version> {

        static Version parse(String versionString) {
                String[] parts = versionString.split("\\.");
                return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
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
        }
}
