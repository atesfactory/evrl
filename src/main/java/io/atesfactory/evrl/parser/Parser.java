package io.atesfactory.evrl.parser;

import io.atesfactory.evrl.loader.LoaderConfig;
import io.atesfactory.evrl.transformer.TransformerConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public Config parse(String location) throws ParserException {
        Pattern pattern = Pattern.compile("evrl://(.*)(?=/[A-Za-z0-9]+:)/(.*)");
        Matcher matcher = pattern.matcher(location);

        if (!matcher.find()) {
            throw new ParserException("Could not extract transformers and loader");
        }

        ArrayList<TransformerConfig> transformers = new ArrayList<>(Arrays.asList(parseTransformers(matcher.group(1))));
        LoaderConfig loader = parseLoaderConfig(matcher.group(2));

        return new Config(transformers, loader);
    }

    private LoaderConfig parseLoaderConfig(String contentString) {
        String loaderName = contentString.substring(0, contentString.indexOf(":"));
        String loadable = contentString.substring(loaderName.length() + 1);

        return new LoaderConfig(loaderName, loadable);
    }

    private TransformerConfig[] parseTransformers(String transformerString) {
        if (transformerString == null || transformerString.isEmpty()) {
            return new TransformerConfig[0];
        }

        List<TransformerConfig> transformers = new ArrayList<>();
        for (String transformerName : transformerString.split("\\|")) {
            if (transformerName.contains(":")) {
                String[] transformerParts = transformerName.split(":");
                transformers.add(new TransformerConfig(transformerParts[0], Arrays.asList(transformerParts[1].split(","))));
                continue;
            }

            if (!transformerName.trim().isEmpty()) {
                transformers.add(new TransformerConfig(transformerName));
            }
        }

        return transformers.toArray(new TransformerConfig[0]);
    }
}
