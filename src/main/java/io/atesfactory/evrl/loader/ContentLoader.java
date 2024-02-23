package io.atesfactory.evrl.loader;

public class ContentLoader implements Loader<String> {
    @Override
    public String load(LoaderContext loaderContext, String loadable) {
        return loadable;
    }
}
