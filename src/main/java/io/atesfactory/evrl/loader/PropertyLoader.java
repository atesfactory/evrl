package io.atesfactory.evrl.loader;

import org.springframework.core.env.Environment;

public class PropertyLoader implements Loader<String> {
    @Override
    public String load(LoaderContext loaderContext, String loadable) {
        Environment environment = loaderContext.getEvrlSpringContext().getEnvironment();
        String property = environment.getProperty(loadable);
        if (property == null) {
            throw new LoaderException("Could not find property or env named: " + loadable);
        }
        return property;
    }
}
