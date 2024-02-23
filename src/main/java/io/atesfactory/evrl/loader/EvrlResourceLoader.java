package io.atesfactory.evrl.loader;

import io.atesfactory.evrl.parser.Config;
import io.atesfactory.evrl.configuration.EvrlSpringContext;
import io.atesfactory.evrl.parser.Parser;
import io.atesfactory.evrl.resourcefactory.ResourceFactoryContext;
import io.atesfactory.evrl.resourcefactory.ResourceFactoryRegistry;
import io.atesfactory.evrl.transformer.Transformer;
import io.atesfactory.evrl.transformer.TransformerConfig;
import io.atesfactory.evrl.transformer.TransformerContext;
import io.atesfactory.evrl.transformer.TransformerRegistry;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.Serializable;

public class EvrlResourceLoader implements ResourceLoader{
    private final EvrlSpringContext evrlSpringContext;
    private final ResourceLoader delegate;

    public EvrlResourceLoader(EvrlSpringContext evrlSpringContext, ResourceLoader delegate) {
        this.evrlSpringContext = evrlSpringContext;
        this.delegate = delegate;
    }

    @Override
    public Resource getResource(String location) {
        Config config = new Parser().parse(location);
        LoaderConfig loaderConfig = config.getLoaderConfig();

        Serializable content = LoaderRegistry.get(loaderConfig.getLoaderName()).load(new LoaderContext(evrlSpringContext), loaderConfig.getLoadable());

        content = transform(evrlSpringContext, config, content);

        return ResourceFactoryRegistry.get(content.getClass()).create(new ResourceFactoryContext(evrlSpringContext), content);
    }

    private Serializable transform(EvrlSpringContext evrlSpringContext, Config config, Serializable initialContent) {
        Serializable content = initialContent;
        Class<? extends Serializable> inType = content.getClass();

        for (TransformerConfig transformerConfig : config.getTransformerConfigs()) {
            Transformer<? extends Serializable, ? extends Serializable> transformer =
                    TransformerRegistry.get(transformerConfig.getTransformerName(), inType);

            content = transformer.transform(new TransformerContext(evrlSpringContext), transformerConfig, transformer.getInType().cast(content));
            inType = transformer.getOutType();
        }

        return content;
    }

    @Override
    public ClassLoader getClassLoader() {
        return delegate.getClassLoader();
    }
}
