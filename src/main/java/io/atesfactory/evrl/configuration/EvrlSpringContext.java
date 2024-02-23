package io.atesfactory.evrl.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

public class EvrlSpringContext {
    private final ApplicationContext applicationContext;
    private final Environment environment;

    public EvrlSpringContext(ApplicationContext applicationContext, Environment environment) {
        this.applicationContext = applicationContext;
        this.environment = environment;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Environment getEnvironment() {
        return environment;
    }

}
