package com.mybaas;

import com.mybaas.commons.BaseVerticle;
import com.mybaas.utils.ConsoleUtils;
import com.mybaas.utils.ResourceUtils;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import java.io.IOException;
import java.util.function.Consumer;

public class AppRunner {
    private static final String DEFAULT_OPTIONS_FILE_NAME = "options.json";
    private static final String DEFAULT_VERTXCONFIG_FILE_NAME = "vertxconfig.json";

    
    private static final String PARAM_RUN = "run";
    private static final String PARAM_STOP = "stop";

    private static final String PARAM_OPTIONS_FILE = "options";
    private static final String PARAM_VERTXCONFIG_FILE = "config";

    private static final String PARAM_LOGCONF_FILE = "logconfig";
    private static final String PARAM_HELP = "help";


    private static final Logger logger = LogManager.getLogger(AppRunner.class);


    private Options options;

    private Class verticleToRunClazz;
    private VertxOptions vertxOptions = new VertxOptions();
    private DeploymentOptions deploymentOptions = new DeploymentOptions();


    public AppRunner setVerticleToRun(Class<? extends BaseVerticle> verticleClazz) {
        this.verticleToRunClazz = verticleClazz;
        return this;
    }

    public void Dispatch(String[] args) throws Exception {
        if (this.verticleToRunClazz == null) {
            throw new Exception("Verticle object must be specified!");
        }
        setupCommandLine();
        run(args);
    }


    private void loadOptions() throws IOException {
        String options = ResourceUtils.getResourceAsString(DEFAULT_OPTIONS_FILE_NAME);
        if (options != null) {
            deploymentOptions.setConfig(new JsonObject(options));
            logger.info(String.format("Loading deployment options from %s", DEFAULT_OPTIONS_FILE_NAME));
            logger.debug(String.format("Options  content -> %s", options));
        } else {
            logger.info(String.format("%s options file is not found. Deployment options will be empty!", DEFAULT_OPTIONS_FILE_NAME));
        }
    }


    private void loadVertxConfiguration() throws IOException {
        String config = ResourceUtils.getResourceAsString(DEFAULT_VERTXCONFIG_FILE_NAME);
        if (config != null) {
            logger.info(String.format("Loading vertx configuration from %s", DEFAULT_VERTXCONFIG_FILE_NAME));
            logger.debug(String.format("Vertx configuration  content -> %s", config));
            JsonObject json = new JsonObject(config);
            boolean isClustered =  json.getBoolean("clustering.enabled", false);
            vertxOptions.getEventBusOptions().setClustered(isClustered);

        } else {
            logger.info(String.format("%s vertx configuration file is not found. Default configuration will be used!", DEFAULT_VERTXCONFIG_FILE_NAME));
        }
    }

    private void setupAndRun() {
        Consumer<Vertx> runner = vertx -> {
            try {
                vertx.deployVerticle(verticleToRunClazz, deploymentOptions);
            }
            catch (Exception e) {
                throw e;
            }
        };
        if (vertxOptions.getEventBusOptions().isClustered()) {
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
        return  new ZookeeperClusterManager(zkConfig);
    }

    private void run(String[] args) {
        logger.info("Starting application...");
        String arguments = "";
        for (String s : args) {
            arguments.concat(" ").concat(s);
        }
        logger.info("Commandline arguments : ".concat(arguments));
        ConsoleUtils.resetColour();
        try {
            ConsoleUtils.writeLine("==Verticle runner [Bt]==");
            CommandLineParser parser = new DefaultParser();
            try {
                CommandLine cmdLine = parser.parse(options, args);
                String optionsFile = cmdLine.getOptionValue(PARAM_OPTIONS_FILE);
                boolean isPrintHelp = cmdLine.hasOption(PARAM_HELP);
                if (isPrintHelp) {
                    ConsoleUtils.setColor(ConsoleUtils.AnsiColours.PURPLE);
                    logger.info("Printing help");
                    printHelp();
                    ConsoleUtils.resetColour();
                    logger.info("Exit Code : 0");
                    System.exit(0);
                }
                this.loadVertxConfiguration();
                this.loadOptions();
                this.setupAndRun();
                //System.exit(0);
            } catch (ParseException | IOException e) {
                logger.error(e);
                logger.error("Exit Code : 1");
                System.exit(-1);
            }
        } finally {
            ConsoleUtils.resetColour();
        }

    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Application", options, true);
    }

    private void setupCommandLine() {
        options = new Options();
        options
                .addOption(PARAM_RUN, false, "Starts application")
                .addOption(PARAM_STOP, false, "Stops application")
                .addOption(PARAM_OPTIONS_FILE, true, "Location of options file, e.g. ./options.json")
                .addOption(PARAM_VERTXCONFIG_FILE, true, "Location of config file. Config file is being used to configure vert.x, e.g. ./config.json")
                .addOption(PARAM_HELP, false, "Print information and command line usage");
    }
}
