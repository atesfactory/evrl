package io.atesfactory.evrl.resourcefactory;

import org.springframework.core.io.Resource;

@FunctionalInterface
public interface ResourceFactory {
    Resource create(ResourceFactoryContext resourceFactoryContext, Object content);
}
