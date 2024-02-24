package io.atesfactory.evrl.transformer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class FileTransformer implements Transformer<byte[], File> {
    @Override
    public File transform(TransformerContext transformerContext, TransformerConfig transformerConfig, Serializable input) {
        try {
            return transformWithinTryCatch(transformerConfig, input);
        } catch (Exception e) {
            throw new TransformerException(e);
        }
    }

    private File transformWithinTryCatch(TransformerConfig transformerConfig, Serializable input) throws IOException {
        File file;

        if (transformerConfig.getConfig().isEmpty()) {
            file = File.createTempFile("evrl_", null);
        } else {
            file = new File(transformerConfig.getConfig().get(0));
        }

        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new TransformerException("Could not create dirs in path: " + file.getAbsolutePath());
        }

        java.nio.file.Files.write(file.toPath(), (byte[]) input);

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
