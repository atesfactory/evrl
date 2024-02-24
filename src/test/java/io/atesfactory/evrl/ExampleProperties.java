package io.atesfactory.evrl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "io.atesfactory.evrl.example3")
public class ExampleProperties {

    private Resource fileBased;
    private Resource inputStreamBased;

    // Constructor is not necessary unless you have specific initialization logic

    public Resource getFileBased() {
        return fileBased;
    }

    public void setFileBased(Resource fileBased) {
        this.fileBased = fileBased;
    }

    public Resource getInputStreamBased() {
        return inputStreamBased;
    }

    public void setInputStreamBased(Resource inputStreamBased) {
        this.inputStreamBased = inputStreamBased;
    }
}
