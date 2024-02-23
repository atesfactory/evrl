package io.atesfactory.evrl.loader;

import org.springframework.core.io.DefaultResourceLoader;

import java.io.FileInputStream;
import java.io.IOException;

public class FileLoader implements Loader<byte[]> {
    @Override
    public byte[] load(LoaderContext loaderContext, String loadable) {
        DefaultResourceLoader resourceLoader = (DefaultResourceLoader) loaderContext.getEvrlSpringContext().getApplicationContext();

        try (FileInputStream fileInputStream = new FileInputStream(resourceLoader.getResource(loadable).getFile())) {
            byte[] fileContent = new byte[fileInputStream.available()];
            fileInputStream.read(fileContent);

            return fileContent;
        } catch (IOException e) {
            throw new LoaderException("Failed to read file", e);
        }
    }
}
