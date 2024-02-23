package io.atesfactory.evrl.loader;

import io.atesfactory.evrl.configuration.EvrlSpringContext;

public class LoaderContext {
   private final EvrlSpringContext evrlSpringContext;

    LoaderContext(EvrlSpringContext evrlSpringContext) {
        this.evrlSpringContext = evrlSpringContext;
    }

    public EvrlSpringContext getEvrlSpringContext() {
        return evrlSpringContext;
    }
}
