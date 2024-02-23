package io.atesfactory.evrl.loader;

@FunctionalInterface
public interface Loader<O> {
    O load(LoaderContext loaderContext, String loadable);
}
