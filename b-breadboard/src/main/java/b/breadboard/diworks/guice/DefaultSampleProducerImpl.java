package b.breadboard.diworks.guice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class DefaultSampleProducerImpl implements SampleProducer {

    private NumberProducer numberProducer;


    @Override
    public String getNewString() {
        return String.format("This is \"%s\" a random text somehow!", getUniqueIDString());
    }

    @Override
    public String getDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    @Override
    public String getUniqueIDString() {
        return UUID.randomUUID().toString();
    }

    @Override
    public int getRandomInteger() {
        return this.numberProducer.getRandomInt();
    }
}
