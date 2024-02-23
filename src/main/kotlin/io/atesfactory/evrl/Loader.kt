package io.atesfactory.evrl

import java.io.Serializable
import org.springframework.core.env.get
import org.springframework.core.io.DefaultResourceLoader

fun interface Loader<O> {
    fun load(loaderContext: LoaderContext, loadable: String) : O
}

data class LoaderContext(val evrlSpringContext: EvrlSpringContext)

class LoaderRegistry private constructor(){
    companion object {
        @JvmStatic
        val defaultLoaders = mutableMapOf(
            "content" to ContentLoader(),
            "file" to FileLoader(),
            "prop" to PropertyLoader(),
            "env" to PropertyLoader()
        )

        private val loaders = mutableMapOf<String, Loader<out Serializable>>()

        init {
            loaders.putAll(defaultLoaders)
        }

        @JvmStatic
        fun get(name: String): Loader<out Serializable> {
            return loaders[name] ?: throw LoaderException("Could not find loader for $name")
        }

        @JvmStatic
        fun register(name: String, loader: Loader<out Serializable>) {
            loaders[name] = loader
        }

        @JvmStatic
        fun getAll(): MutableMap<String, Loader<out Serializable>> {
            return loaders
        }

        @JvmStatic
        fun reset() {
            loaders.clear()
            loaders.putAll(defaultLoaders)
        }
    }
}

class ContentLoader: Loader<String> {
    override fun load(loaderContext: LoaderContext, loadable: String): String {
        return loadable
    }
}

class FileLoader: Loader<ByteArray> {
    override fun load(loaderContext: LoaderContext, loadable: String): ByteArray {
        val resource = (loaderContext.evrlSpringContext.applicationContext as DefaultResourceLoader).getResource(loadable)

        return resource.file.readBytes()
    }
}

class PropertyLoader: Loader<String> {
    override fun load(loaderContext: LoaderContext, loadable: String): String {
        return loaderContext.evrlSpringContext.environment[loadable] ?: throw LoaderException("Could not find property or env named $loadable")
    }
}

class LoaderException(message: String) : RuntimeException(message)
