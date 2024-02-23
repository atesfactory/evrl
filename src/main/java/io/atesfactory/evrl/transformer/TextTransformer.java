package io.atesfactory.evrl.transformer;

import java.io.Serializable;
import java.nio.charset.Charset;

public class TextTransformer implements Transformer<String, byte[]> {
    @Override
    public byte[] transform(TransformerContext transformerContext, TransformerConfig transformerConfig, Serializable input) {
        Charset charset = Charset.forName(!transformerConfig.getConfig().isEmpty() ? transformerConfig.getConfig().get(0) : "UTF-8");
        return ((String) input).getBytes(charset);
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
