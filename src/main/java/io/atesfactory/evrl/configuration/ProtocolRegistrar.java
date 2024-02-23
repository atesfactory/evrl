package io.atesfactory.evrl.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.DefaultResourceLoader;

public class ProtocolRegistrar implements BeanFactoryPostProcessor {
    private final EvrlSpringContext evrlSpringContext;
    public static final String PROTOCOL_PREFIX = "evrl://";

    public ProtocolRegistrar(EvrlSpringContext evrlSpringContext) {
        this.evrlSpringContext = evrlSpringContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        DefaultResourceLoader defaultResourceLoader = (DefaultResourceLoader)this.evrlSpringContext.getApplicationContext();

        defaultResourceLoader.addProtocolResolver(new ProtocolResolver(evrlSpringContext));
    }
}
