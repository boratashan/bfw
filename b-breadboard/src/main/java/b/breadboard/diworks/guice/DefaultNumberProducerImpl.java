package b.breadboard.diworks.guice;

import com.google.inject.Inject;

public class DefaultNumberProducerImpl implements NumberProducer {

    @Inject
    RandomNumberProducer randomNumberProducer;

    @Override
    public int getRandomInt() {
        return randomNumberProducer.nextint();
    }
}
