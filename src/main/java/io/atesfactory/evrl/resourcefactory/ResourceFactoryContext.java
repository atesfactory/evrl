package io.atesfactory.evrl.resourcefactory;

import io.atesfactory.evrl.configuration.EvrlSpringContext;

public class ResourceFactoryContext {
    private final EvrlSpringContext evrlSpringContext;

    public ResourceFactoryContext(EvrlSpringContext evrlSpringContext) {
        this.evrlSpringContext = evrlSpringContext;
    }

    public EvrlSpringContext getEvrlSpringContext() {
        return evrlSpringContext;
    }
}
