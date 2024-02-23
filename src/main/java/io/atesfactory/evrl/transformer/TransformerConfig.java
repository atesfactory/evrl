package io.atesfactory.evrl.transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransformerConfig {
    private final String transformerName;
    private final List<String> config;

    public TransformerConfig(String transformerName, List<String> config) {
        this.transformerName = transformerName;
        this.config = Collections.unmodifiableList(new ArrayList<>(config));
    }

    public TransformerConfig(String transformerName) {
        this(transformerName, Collections.emptyList());
    }

    public String getTransformerName() {
        return transformerName;
    }

    public List<String> getConfig() {
        return config;
    }
}
