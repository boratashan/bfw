package b.breadboard.diworks.guice;

import java.util.Random;

public class RandomNumberProducerImpl implements RandomNumberProducer {
    Random random = new Random();
    @Override
    public int nextint() {
        return random.nextInt();
    }
}
