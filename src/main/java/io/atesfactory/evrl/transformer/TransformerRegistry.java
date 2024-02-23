package io.atesfactory.evrl.transformer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformerRegistry {
    private static final Map<String, List<Transformer<?, ?>>> defaultRegistry = new HashMap<>();
    private static final Map<String, List<Transformer<?, ?>>> registry = new HashMap<>();

    static {
        defaultRegistry.put("base64", Arrays.asList(new Base64Transformer(), new Base64ByteArrayTransformer()));
        defaultRegistry.put("file", Collections.singletonList(new FileTransformer()));
        defaultRegistry.put("text", Collections.singletonList(new TextTransformer()));
        registry.putAll(defaultRegistry);
    }

    private TransformerRegistry() {
    }

    public static void register(String name, List<Transformer<?, ?>> transformers) {
        registry.put(name, transformers);
    }

    public static Transformer<?, ?> get(String name, Class<?> inType) {
        List<Transformer<?, ?>> transformers = registry.get(name.toLowerCase());
        if (transformers != null) {
            for (Transformer<?, ?> transformer : transformers) {
                if (transformer.getInType().isAssignableFrom(inType)) {
                    return transformer;
                }
            }
        }
        throw new TransformerException("Could not find transformer for name: " + name + " with input type: " + inType);
    }

    public static Map<String, List<Transformer<?, ?>>> getAll() {
        return new HashMap<>(registry);
    }

    public static void reset() {
        registry.clear();
        registry.putAll(defaultRegistry);
    }
}
