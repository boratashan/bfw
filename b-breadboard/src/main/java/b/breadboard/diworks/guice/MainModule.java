package b.breadboard.diworks.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scope;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;

public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
            bind(SampleProducer.class).to(DefaultSampleProducerImpl.class).in(Singleton.class);
            bind(NumberProducer.class).to(DefaultNumberProducerImpl.class).in(Singleton.class);
            bind(RandomNumberProducer.class).to(RandomNumberProducerImpl.class);

            bindListener(Matchers.any(), new LogInjectionListener());
    }
}
