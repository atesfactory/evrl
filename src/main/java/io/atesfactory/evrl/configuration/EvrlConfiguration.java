package io.atesfactory.evrl.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@AutoConfiguration
@ConditionalOnProperty(name = "io.atesfactory.evrl.enabled", havingValue = "true", matchIfMissing = true)
public class EvrlConfiguration {
    @Bean
    public ProtocolRegistrar protocolRegistrar(ApplicationContext applicationContext, Environment environment) {
        return new ProtocolRegistrar(new EvrlSpringContext(applicationContext, environment));
    }
}
