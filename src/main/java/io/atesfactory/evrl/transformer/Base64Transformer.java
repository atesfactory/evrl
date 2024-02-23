package io.atesfactory.evrl.transformer;

import java.io.Serializable;

public class Base64Transformer implements Transformer<String, byte[]> {
    @Override
    public byte[] transform(TransformerContext transformerContext, TransformerConfig transformerConfig, Serializable i) {
        return new Base64ByteArrayTransformer().transform(transformerContext, transformerConfig, ((String)i).getBytes());
    }

    @Override
    public Class<String> getInType() {
        return String.class;
    }

    @Override
    public Class<byte[]> getOutType() {
        return byte[].class;
    }
}
