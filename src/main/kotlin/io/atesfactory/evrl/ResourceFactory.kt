package io.atesfactory.evrl

import java.io.ByteArrayInputStream
import java.io.File
import java.io.Serializable
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource

fun interface ResourceFactory{
    fun create(resourceFactoryContext: ResourceFactoryContext, content: Any): Resource
}

data class ResourceFactoryContext(val evrlSpringContext: EvrlSpringContext)

class ResourceFactoryRegistry private constructor(){
    companion object {
        @JvmStatic
        val defaultResourceFactories = mutableMapOf(
            ByteArray::class.java to ByteArrayResourceFactory(),
            File::class.java to FileResourceFactory()
        )

        private val resourceFactories = mutableMapOf<Class<out Serializable>, ResourceFactory>()

        init {
            resourceFactories.putAll(defaultResourceFactories)
        }

        @JvmStatic
        fun get(clazz: Class<out Serializable>): ResourceFactory {
            return resourceFactories[clazz] ?: throw ResourceFactoryException("Could not find loader for $clazz")
        }

        @JvmStatic
        fun register(clazz: Class<out Serializable>, resourceFactory: ResourceFactory) {
            resourceFactories[clazz] = resourceFactory
        }

        @JvmStatic
        fun getAll(): MutableMap<Class<out Serializable>, ResourceFactory> {
            return resourceFactories
        }

        @JvmStatic
        fun reset() {
            resourceFactories.clear()
            resourceFactories.putAll(defaultResourceFactories)
        }
    }
}

class FileResourceFactory: ResourceFactory {
    override fun create(resourceFactoryContext: ResourceFactoryContext, content: Any): Resource {
        return FileSystemResource(content as File)
    }
}

class ByteArrayResourceFactory: ResourceFactory {
    override fun create(resourceFactoryContext: ResourceFactoryContext, content: Any): Resource {
        return InputStreamResource(ByteArrayInputStream(content as ByteArray))
    }
}


class ResourceFactoryException(message: String) : RuntimeException(message)
