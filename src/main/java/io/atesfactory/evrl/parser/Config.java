package io.atesfactory.evrl.parser;

import io.atesfactory.evrl.loader.LoaderConfig;
import io.atesfactory.evrl.transformer.TransformerConfig;

import java.util.ArrayList;

public class Config {
    private final ArrayList<TransformerConfig> transformerConfigs;
    private final LoaderConfig loaderConfig;

    public Config(ArrayList<TransformerConfig> transformerConfigs, LoaderConfig loaderConfig) {
        this.transformerConfigs = new ArrayList<>(transformerConfigs);
        this.loaderConfig = loaderConfig;
    }

    public ArrayList<TransformerConfig> getTransformerConfigs() {
        return new ArrayList<>(transformerConfigs);
    }

    public LoaderConfig getLoaderConfig() {
        return loaderConfig;
    }
}
