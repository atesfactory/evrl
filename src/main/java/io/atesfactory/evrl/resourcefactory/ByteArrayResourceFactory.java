package io.atesfactory.evrl.resourcefactory;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;

public class ByteArrayResourceFactory implements ResourceFactory {
    @Override
    public Resource create(ResourceFactoryContext resourceFactoryContext, Object content) {
        return new InputStreamResource(new ByteArrayInputStream((byte[]) content));
    }
}
