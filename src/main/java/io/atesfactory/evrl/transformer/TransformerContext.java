package io.atesfactory.evrl.transformer;

import io.atesfactory.evrl.configuration.EvrlSpringContext;

public class TransformerContext {
    private final EvrlSpringContext evrlSpringContext;

    public TransformerContext(EvrlSpringContext evrlSpringContext) {
        this.evrlSpringContext = evrlSpringContext;
    }

    public EvrlSpringContext getEvrlSpringContext() {
        return evrlSpringContext;
    }
}
