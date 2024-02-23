package io.atesfactory.evrl.loader;

public class LoaderConfig {
    private final String loaderName;
    private final String loadable;

    public LoaderConfig(String loaderName, String loadable) {
        this.loaderName = loaderName;
        this.loadable = loadable;
    }

    public String getLoaderName() {
        return loaderName;
    }

    public String getLoadable() {
        return loadable;
    }
}
