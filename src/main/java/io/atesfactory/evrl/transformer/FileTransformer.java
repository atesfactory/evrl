package io.atesfactory.evrl.transformer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class FileTransformer implements Transformer<byte[], File> {
    @Override
    public File transform(TransformerContext transformerContext, TransformerConfig transformerConfig, Serializable input) {
        File file;
        if (transformerConfig.getConfig().isEmpty()) {
            try {
                file = File.createTempFile("evrl_", null);
            } catch (IOException e) {
                throw new TransformerException(e);
            }
        } else {
            file = new File(transformerConfig.getConfig().get(0));
        }

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            java.nio.file.Files.write(file.toPath(), (byte[]) input);
        } catch (java.io.IOException e) {
            throw new TransformerException(e);
        }

        return file;
    }

    @Override
    public Class<byte[]> getInType() {
        return byte[].class;
    }

    @Override
    public Class<File> getOutType() {
        return File.class;
    }
}
