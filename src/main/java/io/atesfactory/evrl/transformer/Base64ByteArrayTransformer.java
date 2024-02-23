package io.atesfactory.evrl.transformer;

import java.io.Serializable;
import java.util.Base64;

public class Base64ByteArrayTransformer implements Transformer<byte[], byte[]> {
    @Override
    public byte[] transform(TransformerContext transformerContext, TransformerConfig transformerConfig, Serializable input) {
        String decoderConfig = !transformerConfig.getConfig().isEmpty() ? transformerConfig.getConfig().get(0) : "";
        Base64.Decoder decoder;
        switch (decoderConfig) {
            case "mime":
                decoder = Base64.getMimeDecoder();
                break;
            case "url":
                decoder = Base64.getUrlDecoder();
                break;
            default:
                decoder = Base64.getDecoder();
                break;
        }
        return decoder.decode((byte[]) input);
    }

    @Override
    public Class<byte[]> getInType() {
        return byte[].class;
    }

    @Override
    public Class<byte[]> getOutType() {
        return byte[].class;
    }
}
