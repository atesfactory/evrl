package io.atesfactory.evrl.transformer;

import java.io.Serializable;

public interface Transformer<I extends Serializable, O extends Serializable> {
    O transform(TransformerContext transformerContext, TransformerConfig transformerConfig, Serializable i);

    Class<I> getInType();

    Class<O> getOutType();
}
