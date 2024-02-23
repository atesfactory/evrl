package io.atesfactory.evrl.resourcefactory;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ResourceFactoryRegistry {
    private static final Map<Class<? extends Serializable>, ResourceFactory> defaultResourceFactories = new HashMap<>();
    private static final Map<Class<? extends Serializable>, ResourceFactory> resourceFactories = new HashMap<>();

    static {
        defaultResourceFactories.put(byte[].class, new ByteArrayResourceFactory());
        defaultResourceFactories.put(File.class, new FileResourceFactory());
        resourceFactories.putAll(defaultResourceFactories);
    }

    private ResourceFactoryRegistry() {}

    public static ResourceFactory get(Class<? extends Serializable> clazz) {
        if (!resourceFactories.containsKey(clazz)) {
            throw new ResourceFactoryException("Could not find loader for " + clazz);
        }
        return resourceFactories.get(clazz);
    }

    public static void register(Class<? extends Serializable> clazz, ResourceFactory resourceFactory) {
        resourceFactories.put(clazz, resourceFactory);
    }

    public static Map<Class<? extends Serializable>, ResourceFactory> getAll() {
        return new HashMap<>(resourceFactories);
    }

    public static void reset() {
        resourceFactories.clear();
        resourceFactories.putAll(defaultResourceFactories);
    }
}
