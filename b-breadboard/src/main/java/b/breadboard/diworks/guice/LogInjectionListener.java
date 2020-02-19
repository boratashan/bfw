package b.breadboard.diworks.guice;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;

public class LogInjectionListener implements TypeListener, ProvisionListener {
    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {

    }

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        Class<?> clazz = type.getRawType();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == Logger.class &&
                        field.isAnnotationPresent(InjectLogger.class)) {
                    encounter.register(new Log4JMembersInjector<I>(field));
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
