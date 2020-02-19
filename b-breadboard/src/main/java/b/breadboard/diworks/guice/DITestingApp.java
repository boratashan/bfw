package b.breadboard.diworks.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;

import javax.inject.Inject;


public class DITestingApp {

    public void run() {
        //Injector injector = Guice.createInjector(new MainModule());
        Injector injector =Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SampleProducer.class).to(DefaultSampleProducerImpl.class).in(Singleton.class);
                bind(NumberProducer.class).to(DefaultNumberProducerImpl.class).in(Singleton.class);
                bind(RandomNumberProducer.class).to(RandomNumberProducerImpl.class);

                bindListener(Matchers.any(), new LogInjectionListener());
            }
        });
        SampleConsumer sampleConsumer = injector.getInstance(SampleConsumer.class);
        sampleConsumer.run();
    }


}
