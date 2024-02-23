package io.atesfactory.evrl.loader;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LoaderRegistry {
    private static final Map<String, Loader<? extends Serializable>> defaultLoaders = new HashMap<>();
    private static final Map<String, Loader<? extends Serializable>> loaders = new HashMap<>();

    static {
        defaultLoaders.put("content", new ContentLoader());
        defaultLoaders.put("file", new FileLoader());
        defaultLoaders.put("prop", new PropertyLoader());
        defaultLoaders.put("env", new PropertyLoader());
        loaders.putAll(defaultLoaders);
    }

    private LoaderRegistry() {}

    public static Loader<? extends Serializable> get(String name) {
        if (!loaders.containsKey(name)) {
            throw new LoaderException("Could not find loader for " + name);
        }
        return loaders.get(name);
    }

    public static void register(String name, Loader<? extends Serializable> loader) {
        loaders.put(name, loader);
    }

    public static Map<String, Loader<? extends Serializable>> getAll() {
        return new HashMap<>(loaders);
    }

    public static void reset() {
        loaders.clear();
        loaders.putAll(defaultLoaders);
    }
}
