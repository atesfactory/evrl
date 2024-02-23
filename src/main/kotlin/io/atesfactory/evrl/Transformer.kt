package io.atesfactory.evrl

import java.io.File
import java.io.Serializable
import java.nio.charset.Charset
import java.util.*

interface Transformer<I, O> {
    fun transform(transformerContext: TransformerContext, transformerConfig: TransformerConfig, i: Serializable): O
    fun getInType() : Class<I>
    fun getOutType() : Class<O>
}

data class TransformerContext(val evrlSpringContext: EvrlSpringContext)

class TransformerRegistry private constructor(){
    companion object {
        @JvmStatic
        private val defaultRegistry = mutableMapOf(
            "base64" to listOf(Base64Transformer(), Base64ByteArrayTransformer()),
            "file" to listOf(FileTransformer()),
            "text" to listOf(TextTransformer())
        )

        private val registry = mutableMapOf<String, List<Transformer<out Serializable, out Serializable>>>()

        init {
            registry.putAll(defaultRegistry)
        }

        @JvmStatic
        fun register(name: String, transformers: List<Transformer<out Serializable, out Serializable>>) {
            registry[name] = transformers
        }

        @JvmStatic
        fun get(name: String, inType: Class<out Any>): Transformer<out Serializable, out Serializable> {
            return registry[name.lowercase()]?.find { it.getInType().isAssignableFrom(inType) } ?: throw TransformerException("Could not find transformer for name: $name with input type: $inType")
        }

        @JvmStatic
        fun getAll(): MutableMap<String, List<Transformer<out Serializable, out Serializable>>> {
            return registry
        }

        @JvmStatic
        fun reset() {
            registry.clear()
            registry.putAll(defaultRegistry)
        }


    }
}

class Base64Transformer: Transformer<String, ByteArray> {
    override fun transform(transformerContext: TransformerContext, transformerConfig: TransformerConfig, i: Serializable): ByteArray {
        return Base64ByteArrayTransformer().transform(transformerContext, transformerConfig, (i as String).toByteArray())
    }

    override fun getInType(): Class<String> {
        return String::class.java
    }

    override fun getOutType(): Class<ByteArray> {
        return ByteArray::class.java
    }
}

class Base64ByteArrayTransformer: Transformer<ByteArray, ByteArray> {
    override fun transform(transformerContext: TransformerContext, transformerConfig: TransformerConfig, i: Serializable): ByteArray {
        val decoderConfig = transformerConfig.config.elementAtOrNull(0)
        val decoder = when(decoderConfig) {
            "mime" -> Base64.getMimeDecoder()
            "url" -> Base64.getUrlDecoder()
            else -> Base64.getDecoder()
        }
        return decoder.decode(i as ByteArray)
    }

    override fun getInType(): Class<ByteArray> {
        return ByteArray::class.java
    }

    override fun getOutType(): Class<ByteArray> {
        return ByteArray::class.java
    }
}

class FileTransformer: Transformer<ByteArray, File> {
    override fun transform(transformerContext: TransformerContext, transformerConfig: TransformerConfig, i: Serializable): File {
        val file = if(transformerConfig.config.isEmpty()) {
            File.createTempFile("evrl_", "")
        } else {
            File(transformerConfig.config.first())
        }


        if(!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        file.writeBytes(i as ByteArray)

        return file
    }

    override fun getInType(): Class<ByteArray> {
        return ByteArray::class.java
    }

    override fun getOutType(): Class<File> {
        return File::class.java
    }
}

class TextTransformer: Transformer<String, ByteArray> {
    override fun transform(transformerContext: TransformerContext, transformerConfig: TransformerConfig, i: Serializable): ByteArray {
        return (i as String).toByteArray(Charset.forName(transformerConfig.config.elementAtOrNull(0) ?: "UTF-8"))
    }

    override fun getInType(): Class<String> {
        return String::class.java
    }

    override fun getOutType(): Class<ByteArray> {
        return ByteArray::class.java
    }
}

class TransformerException(message: String) : RuntimeException(message)
