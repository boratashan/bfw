package b.breadboard.diworks.guice;

import com.mybaas.utils.ConsoleUtils;

import javax.inject.Inject;
import org.apache.logging.log4j.Logger;

public class SampleConsumer {

    @Inject SampleProducer sampleProducer;

    @Inject
    NumberProducer numberProducer;

    @InjectLogger
    Logger logger;

    public SampleConsumer() {
    }


    public void run() {
        ConsoleUtils.setColor(ConsoleUtils.AnsiColours.BLUE);
        try {
            logger.info("This is from injected logger");
            ConsoleUtils.writeLine("DI TEST...");
            ConsoleUtils.writeLine( String.format("Requesting sample text : %s", sampleProducer.getNewString()));
            ConsoleUtils.writeLine( String.format("Requesting random number : %d", numberProducer.getRandomInt()));

            logger.info("This is from injected logger");
        }
        finally {
            ConsoleUtils.resetColour();
        }

    }
}
