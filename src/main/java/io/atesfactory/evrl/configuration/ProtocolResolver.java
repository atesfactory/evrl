package io.atesfactory.evrl.configuration;

import io.atesfactory.evrl.loader.EvrlResourceLoader;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class ProtocolResolver implements ResourceLoaderAware, org.springframework.core.io.ProtocolResolver {

    private final EvrlSpringContext evrlSpringContext;

    public ProtocolResolver(EvrlSpringContext evrlSpringContext) {
        this.evrlSpringContext = evrlSpringContext;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        if(DefaultResourceLoader.class.isAssignableFrom(resourceLoader.getClass())) {
            ((DefaultResourceLoader) resourceLoader).addProtocolResolver(this);
        }
    }

    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        if(location.startsWith(ProtocolRegistrar.PROTOCOL_PREFIX)) {
            ResourceLoader loader = new EvrlResourceLoader(evrlSpringContext, resourceLoader);

            return loader.getResource(location);
        }
        return null;
    }
}
