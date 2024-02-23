package io.atesfactory.evrl

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "io.atesfactory.evrl.example3")
@Component
data class ExampleProperties(
    var fileBased: Resource? = null,
    var inputStreamBased: Resource? = null,
)
