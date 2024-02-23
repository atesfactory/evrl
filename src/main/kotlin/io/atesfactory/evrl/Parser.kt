package io.atesfactory.evrl

data class Config(val transformerConfigs: ArrayList<TransformerConfig>, val loaderConfig: LoaderConfig)
data class TransformerConfig(val transformerName: String, val config: List<String> = emptyList())
data class LoaderConfig(val loaderName: String, val loadable: String)

class Parser {
    fun parse(location: String) : Config {
        val matchResult = Regex("evrl://(.*)(?=/[A-Za-z0-9]+:)/(.*)").find(location)
        val groups = matchResult?.groupValues ?: throw ParserException("Could not extract transformers and loader")
        val transformers = parseTransformers(groups[1])
        val loader = parseLoaderConfig(groups[2])

        return Config(arrayListOf(*transformers), loader)
    }

    private fun parseLoaderConfig(contentString: String): LoaderConfig {
        val loaderName = contentString.substringBefore(":")
        val loadable = contentString.removePrefix("$loaderName:")

        return LoaderConfig(loaderName, loadable)
    }

    private fun parseTransformers(transformerString: String?) : Array<TransformerConfig> {
        transformerString ?: return emptyArray()

        val transformers = ArrayList<TransformerConfig>()
        for (transformerName in transformerString.split("|")) {
            if(transformerName.contains(":")) {
                val transformerParts = transformerName.split(":")
                transformers.add(
                    TransformerConfig(transformerParts[0], transformerParts[1].split(","))
                )
                continue
            }

            if(transformerName.trim() != "") {
                transformers.add(TransformerConfig(transformerName))
            }
        }

        return transformers.toTypedArray()
    }
}

class ParserException(message: String) : RuntimeException(message)
