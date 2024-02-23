package io.atesfactory.evrl

import java.io.Serializable
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationContext
import org.springframework.context.ResourceLoaderAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.ProtocolResolver
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader

const val PROTOCOL_PREFIX = "evrl://"

@Configuration
@ConditionalOnProperty("io.atesfactory.evrl.enabled", havingValue = "true", matchIfMissing = true)
class EvrlConfiguration {

    @Bean
    fun envResourceLoaderProtocolRegistrar(applicationContext: ApplicationContext, environment: Environment): ProtocolRegistrar {
        return ProtocolRegistrar(EvrlSpringContext(applicationContext, environment))
    }
}

data class EvrlSpringContext(val applicationContext: ApplicationContext, val environment: Environment)

class ProtocolRegistrar(private val evrlSpringContext: EvrlSpringContext) : BeanFactoryPostProcessor {

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val resourceLoader = evrlSpringContext.applicationContext as DefaultResourceLoader
        resourceLoader.addProtocolResolver(EvrlProtocolResolver(evrlSpringContext))
    }
}

private fun isSupportedResourceProtocol(location: String) = location.startsWith(PROTOCOL_PREFIX, ignoreCase = true)

class EvrlProtocolResolver(val evrlSpringContext: EvrlSpringContext) : ResourceLoaderAware, ProtocolResolver {
    override fun setResourceLoader(resourceLoader: ResourceLoader) {
        if (DefaultResourceLoader::class.java.isAssignableFrom(resourceLoader.javaClass)) {
            (resourceLoader as DefaultResourceLoader).addProtocolResolver(this)
        }
    }

    override fun resolve(location: String, resourceLoader: ResourceLoader): Resource? {
        if (isSupportedResourceProtocol(location)) {
            val loader = EvrlResourceLoader(evrlSpringContext, resourceLoader)

            return loader.getResource(location)
        }
        return null
    }
}

class EvrlResourceLoader(val evrlSpringContext: EvrlSpringContext, private val delegate: ResourceLoader) : ResourceLoader {

    override fun getResource(location: String): Resource {
        val evrlConfig = Parser().parse(location)

        val loaderConfig = evrlConfig.loaderConfig

        var content: Serializable = LoaderRegistry.get(loaderConfig.loaderName).load(LoaderContext(evrlSpringContext), loaderConfig.loadable)

        content = transform(evrlSpringContext, evrlConfig, content)

        return ResourceFactoryRegistry.get(content::class.java).create(ResourceFactoryContext(evrlSpringContext), content)
    }

    private fun transform(evrlSpringContext: EvrlSpringContext, evrlConfig: Config, initialContent: Serializable): Serializable {
        var content = initialContent
        var inType = content::class.java

        for (transformerConfig in evrlConfig.transformerConfigs) {
            val transformer = TransformerRegistry.get(transformerConfig.transformerName, inType)

            content = transformer.transform(TransformerContext(evrlSpringContext),transformerConfig, transformer.getInType().cast(content))
            inType = transformer.getOutType()
        }
        return content
    }

    override fun getClassLoader(): ClassLoader {
        return delegate.classLoader!!
    }
}
