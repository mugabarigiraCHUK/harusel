package common

public class HRToolConfig {

    public static final String HRTOOL_CONF_DIR = System.getProperty("hrtool.conf.dir", ".")

    private static final String CONFIGURATION_FILE_NOT_FOUND = "cannot find configuration file: %s"

    private static ConfigObject loadConfig(String path) {
        File configurationFile = new File(path)

        // TODO: Use logging.
        System.out.println("Reading configuation from file: " + configurationFile);

        if (!configurationFile.exists()) {
            throw new FileNotFoundException(String.format(CONFIGURATION_FILE_NOT_FOUND, path))
        }
        return new ConfigSlurper().parse(configurationFile.toURL())
    }

    public static final ConfigObject config = loadConfig("$HRTOOL_CONF_DIR/configuration.groovy");

}
