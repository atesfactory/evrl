package io.atesfactory.evrl.resourcefactory;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;

class FileResourceFactory implements ResourceFactory {
    @Override
    public Resource create(ResourceFactoryContext resourceFactoryContext, Object content) {
        return new FileSystemResource((File) content);
    }
}
