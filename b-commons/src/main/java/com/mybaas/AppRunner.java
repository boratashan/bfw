package com.mybaas;

import com.mybaas.commons.BaseVerticle;
import com.mybaas.commons.exceptions.ApplicationInitializationException;
import com.mybaas.commons.config.ApplicationConfigConstants;
import com.mybaas.commons.config.ApplicationConfigManager;
import com.mybaas.utils.ConsoleUtils;
import com.mybaas.utils.FileAndFolderUtils;
import com.mybaas.utils.ResourceUtils;
import com.sun.tools.javac.util.List;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;


/*
How to run

[0m[34m +-+-+-+-+-+-+-+-+-+
 |C|o|l|l|e|c|t|o|r|
 +-+-+-+-+-+-+-+-+-+
[0m[0m2020-03-24 19:10:30,303 main ERROR NoSql contains an invalid element or attribute "MongoDb3"
2020-03-24 19:10:30,306 main ERROR NoSQL provider not specified for appender [databaseAppender].
2020-03-24 19:10:30,307 main ERROR Null object returned for NoSql in Appenders.
19:10:30.413 [main] INFO  com.mybaas.AppRunner - Starting application...
19:10:30.416 [main] INFO  com.mybaas.AppRunner - Commandline arguments :
[0m==Verticle runner [Bt]==
[35m19:10:30.418 [main] INFO  com.mybaas.AppRunner - Printing help
usage: Application [-config <arg>] [-help] [-options <arg>] [-run] [-stop]
 -config <arg>    Location of config file. Config file is being used to
                  configure vert.x, e.g. ./config.json
 -help            Print information and command line usage
 -options <arg>   Location of options file, e.g. ./options.json
 -run             Starts application
 -stop            Stops application
[0m19:10:30.420 [main] INFO  com.mybaas.AppRunner - Exit Code : 0





 */
public class AppRunner {
    private static final String DEFAULT_OPTIONS_FILE_NAME = "options.json";
    private static final String DEFAULT_VERTXCONFIG_FILE_NAME = "vertxconfig.json";
    private static final String PARAM_RUN = "run";
    private static final String PARAM_STOP = "stop";
    private static final String PARAM_OPTIONS_FILE = "opts";
    private static final String PARAM_PLATFORM_CONFIG_FILE = "pconf";
    private static final String PARAM_LOGCONF_FILE = "logconf";
    private static final String PARAM_HELP = "help";
    private static Logger logger;

    static {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
    }

    private Options options;


    private Set<Class<? extends BaseVerticle>> verticlesToRunSet;


    private AppRunner() {
        this.verticlesToRunSet = new LinkedHashSet<>();
    }

    public static AppRunner initInstance() {
        AppRunner appRunner = new AppRunner();
        appRunner.setupCommandLine();
        return appRunner;
    }

    @SafeVarargs
    public final AppRunner setVerticlesToRun(Class<? extends BaseVerticle>... verticles) {
        Objects.requireNonNull(verticles);
        verticlesToRunSet.clear();
        for (Class verticle : verticles) {
            verticlesToRunSet.add(verticle);
        }
        return this;
    }


    private void loadConfig(CommandLine cmdLine) throws ApplicationInitializationException {
        logger.info("Loading configuration...");
        try {
            String pconfigLoc = cmdLine.getOptionValue(PARAM_PLATFORM_CONFIG_FILE, StringUtils.EMPTY);
            String optionsLoc = cmdLine.getOptionValue(PARAM_OPTIONS_FILE, StringUtils.EMPTY);
            String platform;
            String options;
            if (pconfigLoc.isEmpty()) {
                logger.debug("Platform config is not specified, embedded config (if exists) will be located!");
                platform = ResourceUtils.getResourceAsString(ApplicationConfigConstants.CONFIG_EMBEDDED_PLATFORM);
            } else {
                platform = FileAndFolderUtils.readFileAsString(new File(pconfigLoc));
            }

            if (optionsLoc.isEmpty()) {
                logger.debug("Options file is not specified, embedded config (if exists) will be located!");
                options = ResourceUtils.getResourceAsString(ApplicationConfigConstants.CONFIG_EMBEDDED_OPTIONS);
            }
            else {
                options = FileAndFolderUtils.readFileAsString(new File(optionsLoc));
            }
            ApplicationConfigManager.init(platform, options);
        } catch (IOException e) {
            throw new ApplicationInitializationException("Can not load application configuration.", e);
        }
        logger.info("Loading configuration [DONE]");
    }

    private void setupCommandLine() {
        options = new Options();
        options
                .addOption(PARAM_RUN, false, "Starts application")
                .addOption(PARAM_STOP, false, "Stops application")
                .addOption(PARAM_OPTIONS_FILE, true, "Location of options file, e.g. ./options.json")
                .addOption(PARAM_PLATFORM_CONFIG_FILE, true, "Location of platform config file which provides necessary configuration " +
                        "for using infrastructure services, such as nosql connection, clustering etc... ./pconf.json")
                .addOption(PARAM_LOGCONF_FILE, true, "Location of log configuration file, e.g.  ./log4j.xml")
                .addOption(PARAM_HELP, false, "Print information and command line usage");
    }

    public void Dispatch(String[] args) throws Exception {
        if (this.verticlesToRunSet.size() == 0) {
            throw new Exception("Verticles to run must be specified!");
        }
        run(args);
    }


    private void run(String[] args) throws IOException {
        ConsoleUtils.resetColour();
        try {
            CommandLineParser parser = new DefaultParser();
            try {
                CommandLine cmdLine = parser.parse(options, args);
                String optionsFile = cmdLine.getOptionValue(PARAM_OPTIONS_FILE);
                boolean isPrintHelp = cmdLine.hasOption(PARAM_HELP);
                if (isPrintHelp) {
                    /*Print help and exit*/
                    ConsoleUtils.setColor(ConsoleUtils.AnsiColours.RED);
//                    logger.info("Printing help");
                    printHelp();
                    ConsoleUtils.setColor(ConsoleUtils.AnsiColours.BLUE);
                    logger.info("Exit Code : 0");
                    ConsoleUtils.resetColour();
                    System.exit(0);
                }
                this.setLogger(cmdLine);
                ConsoleUtils.resetColour();
                logger.info("Starting application...");
                logger.debug(String.format("Run with arguments %s", Arrays.toString(args)));
                /*Booting and application*/
                printHeading();
                this.loadConfig(cmdLine);
                this.setupAndRun();
                //System.exit(0);
            } catch (ParseException e) {
                logger.error("improper commandline arguments.");
                logger.debug(e);
                logger.info("Exit Code : -1");
                System.exit(-1);
            } catch (ApplicationInitializationException e) {
                logger.error("Application initialization exception");
                logger.debug(e, e.getCause());
                logger.info("Exit Code : -3");
                System.exit(-3);
            }
        } finally {
            ConsoleUtils.resetColour();
        }

    }

    private void setLogger(CommandLine cmdLine) {
        if (cmdLine.hasOption(PARAM_LOGCONF_FILE)) {
            System.setProperty("LOG4J_CONFIGURATION_FILE", cmdLine.getOptionValue(PARAM_LOGCONF_FILE, ""));
        }
         this.logger = LogManager.getLogger(AppRunner.class);
    }



    private void setupAndRun() throws ApplicationInitializationException {

        JsonObject options = ApplicationConfigManager.get().getOptionsConfig();
        JsonObject pOpts = ApplicationConfigManager.get().getPlatformConfig();
        JsonObject vxOpts = pOpts.getJsonObject(ApplicationConfigConstants.CONFIG_TAGNAME_VERTX);
        boolean isClustered =  vxOpts.getBoolean("clustering.enabled", false);
        VertxOptions vertxOptions = new VertxOptions(vxOpts);
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setConfig(options);
        logger.debug(String.format("Options : %s", options.toString()));
        logger.debug(String.format("Platform options : %s", pOpts.toString()));
        logger.debug(String.format("Vertx options : %s", vxOpts.toString()));

        Consumer<Vertx> runner = vertx -> {
            try {
                for (Class v : verticlesToRunSet) {
                    logger.info(String.format("Deploying verticle %s", v.getName()));
                    vertx.deployVerticle(v, deploymentOptions);
                }
            } catch (Exception e) {
                throw e;
            }
        };

        if (vertxOptions.getEventBusOptions().isClustered()) {
            logger.info("Clustered vertx instance is running");
            vertxOptions.setClusterManager(this.configureClusterManager());
            Vertx.clusteredVertx(vertxOptions, res -> {
                if (res.succeeded()) {
                    Vertx vertx = res.result();
                    runner.accept(vertx);
                } else {
                    res.cause().printStackTrace();
                }
            });
        } else {
            logger.info("Standalone vertx instance is running");
            Vertx vertx = Vertx.vertx(vertxOptions);
            runner.accept(vertx);
        }
    }

    private ClusterManager configureClusterManager() {
        JsonObject zkConfig = new JsonObject();
        zkConfig.put("zookeeperHosts", "localhost");
        zkConfig.put("rootPath", "com.b.io.vertx");
        zkConfig.put("retry", new JsonObject()
                .put("initialSleepTime", 3000)
                .put("maxTimes", 3));
        return new ZookeeperClusterManager(zkConfig);
    }


    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Application", options, true);
    }


    private void printHeading() {
        ConsoleUtils.resetColour();
        try {
            String logo = ResourceUtils.getResourceAsString("logo.txt");
            ConsoleUtils.printLogo(logo, ConsoleUtils.AnsiColours.BLUE);
        } catch (IOException e) {
        }
        ConsoleUtils.resetColour();
    }



}


    /*private void loadOptions() throws IOException {
        String options = ResourceUtils.getResourceAsString(DEFAULT_OPTIONS_FILE_NAME);
        if (options != null) {
            deploymentOptions.setConfig(new JsonObject(options));
            logger.info(String.format("Loading deployment options from %s", DEFAULT_OPTIONS_FILE_NAME));
            logger.debug(String.format("Options  content -> %s", options));
        } else {
            logger.info(String.format("%s options file is not found. Deployment options will be empty!", DEFAULT_OPTIONS_FILE_NAME));
        }
    }
*/

  /*  private void loadVertxConfiguration() throws IOException {
        String config = ResourceUtils.getResourceAsString(DEFAULT_VERTXCONFIG_FILE_NAME);
        if (config != null) {
            logger.info(String.format("Loading vertx configuration from %s", DEFAULT_VERTXCONFIG_FILE_NAME));
            logger.debug(String.format("Vertx configuration  content -> %s", config));
            JsonObject json = new JsonObject(config);
            boolean isClustered = json.getBoolean("clustering.enabled", false);
            vertxOptions.getEventBusOptions().setClustered(isClustered);

        } else {
            logger.info(String.format("%s vertx configuration file is not found. Default configuration will be used!", DEFAULT_VERTXCONFIG_FILE_NAME));
        }
    }*/
