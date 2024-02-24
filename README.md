# EVRL

**A highly extensible last mile declarative resource loading and transformation library for Spring Boot.**

Resources come in different shapes and forms. It is a crucial task to bring the resources into the correct format for application startup.
Convenient tools are not always available, the environments and the inputs can change. Sometimes only a stream is needed, not a file on a disk.
It happens that staff responsible to provide resources are not capable to.
EVRL enables resource transformation and loading for Spring at bean initialization.

Right now EVRL is only applicable for `org.springframework.core.io.Resource`.
If there is interest in more supported destination types then I will extend it.
It is also possible to use the `@Value` annotation to load a Resource with EVRL.

The library is lightweight, easily extensible and maintainability, and should be easy to comprehend.
Code coverage is 100%.

Jump to the [Examples](#Examples) to see EVRL in action.

## Usage
Below you can see the EVRL usage, available loaders, and transformers.

### Minimum Requirements
* Spring Boot 3.x.x+/2.7.x+
* Java 8
### Install
EVRL is ready to go as soon as the dependency is present.

Maven:
```xml
<dependency>
  <groupId>io.atesfactory</groupId>
  <artifactId>evrl</artifactId>
  <version>1.0.0</version>
</dependency>
```
or Gradle (Kotlin)
```kotlin
implementation("io.atesfactory:evrl:1.0.0")
```
### Configuration
Enable or disable EVRL via Spring property. Default is enabled (true) 
```yaml
  io.atesfactory.evrl.enabled: <true|false>
```

### Syntax
On a highlevel EVRL does the following:
```
<parse evrl expression> --> <load data> --> <transform data> --> <create Resource>
```
```yaml
    evrl://base64/env:MY_ENV # loads env content, base64 decodes it, and returns a org.springframework.core.io.Resource
    evrl://<transformer-chain>/<loader>
```
`<transformer-chain>`Consisting of one or many transformers, chaining multiple transformers with `|`
```yaml
    <transformer> or <transformer>|<transformer>|...
    e.g. text|base64|file
```
`<loaders>` Loaders load data from a source
```yaml
    <loader-prefix>:<value>
    e.g. env:MY_ENV to load the content of a environment variable named 'MY_ENV'
```

### Built-in Loaders
* `env:<environment variable>` -  Loads content of an environment variable
  * e.g. `env:MY_ENV`
* `content:<text content>` - Uses the content after this prefix.
  * e.g. `content:Hello world`
* `prop:<property name>`-  Loads a property
  * e.g. `prop:my.application.yaml.property`
* `file:<path>` - Loads file content
  * e.g. `file:/tmp/keystore.txt`

### Built-in Transformers
* `base64[:decoder]`: Decoding a Base64 String to a byte[]
  * possible optional decoder values are `mime`, `url`, or `default`
  * e.g. `base64` (uses default), `base64:mime`, `base64:url`
* `file[:filepath]`: Creating a java.io.File from a byte[]
  * optional filepath let you control the destination, default is a temp file.
  * all non existing dirs in the filepath are created during the transformation
  * e.g. `file:./mydir/myfile.p12`
* `text[:charset]`: String to byte[] transformer
  *  uses default charset UTF-8 
  *  e.g. `text`, `text:UTF-8`

### Resource Factory

In the end of an EVRL expression you get either a `org.springframework.core.io.InputStreamResource` 
or a `org.springframework.core.io.FileSystemResource`. Both are a sub-class of `org.springframework.core.io.Resource`.

Depending on the last type in the transformer-chain a corresponding resource is returned.

* byte[] on last transformation returns `org.springframework.core.io.InputStreamResource`
* File on last transformation returns `org.springframework.core.io.FileSystemResource`

## Examples
Here are four examples that showcase the EVRL resource loading and transformation.
### Example 1
The following shows how the content of the `KEY_STORE` environment variable is loaded.
Then a transformer gets applied:
* `base64`: decodes text to bytes

In the end an `org.springframework.core.io.Resource` is created.
In this case the Resource is an instance of `org.springframework.core.io.InputStreamResource`
``` yaml
    # application.yaml
    sample-lib: 
        ...
        key-store: evrl://base64/env:KEY_STORE
        trust-store-file: file:///tmp/truststore.p12 
                          # other resource loadings methods are still possible

```

```java
@ConfigurationProperties(prefix="sample-lib")
public class SampleLibProperties {
    private Resource keyStore; // EVRL creates InputStreamResource
    private Resource trustStoreFile;
    ...
} 
```
Depending on the usage of the `org.springframework.core.io.Resource` an InputStream could be enough.

But it is also possible to write transformed data to disk with another transformer: `file` (see Example 2).

### Example 2

``` yaml
    # application.yaml
    sample-lib: 
        ...
        key-store-file: evrl://base64|file/env:KEY_STORE
```

The above shows the usage of two transformers. They are chained from left to right.
First the `KEY_STORE` environment variable is loaded.
Then two transformers get applied from left to right.
1) `base64`: text is decoded to bytes
2) `file`: bytes are written to a temporary file

In the end a `org.springframework.core.io.Resource` is created.
In this case a `org.springframework.core.io.FileSystemResource` instance is created.

### Example 3

Using EVRL in the `@Value` annotation.
```java
@Value("${evrl://base64/env:KEY_STORE}")
private Resource resource;
```
As in the examples above, here an environment variable called `KEY_STORE` is read, the content is base64 decoded and a InputStreamResource is returned.

### Example 4
Loading properties before EVRL and during EVRL.

In the following EVRL loads the content of the property:
```yaml
    # application.yaml
    sample-lib: 
        ...
        some-b64-text: SEVMTE8=
        printer: evrl://base64/prop:sample-lib.some-b64-text
```
Note the `prop` loader prefix.

It is also possible to use evaluate properties before EVRL:

```yaml
    # application.yaml
    sample-lib: 
        ...
        some-b64-text: SEVMTE8=
        printer: evrl://base64/content:${sample-lib.some-b64-text}
```
Note that the loader prefix is `content`, because the property is resolved before EVRL execution.
## Extend with Custom Loaders, Transformers, and ResourceFactories
The EVRL lib is highly extensible and can be customized to users specific needs.
It is possible to add functionality as well as replace existing functionality.
### Add a Custom Loader
Implement the `io.atesfactory.evrl.Loader` interface and register your custom loader
```java
io.atesfactory.evrl.LoaderRegistry.register("my-custom", myCustomLoader);
```
### Add a Custom Transformer
Implement the `io.atesfactory.evrl.Transformer` interface and register your custom transformer
```java
io.atesfactory.evrl.TransformerRegistry.register("reverseMyText", List.of(myCustomTransformer));
```

### Add a Custom Resource Factory
Implement the `io.atesfactory.evrl.ResourceFactory` interface and register your custom resource factory
```java
io.atesfactory.evrl.ResourceFactoryRegistry.register(String.class, myResourceFactory);
```
## Limitations

* Currently only properties with type `org.springframework.core.io.Resource` can be loaded from your config.
Output instances are either `org.springframework.core.io.InputStreamResource` or `org.springframework.core.io.FileSystemResource`.
* Transformer-Chain has to end either with java.io.File, or a byte[].
To overcome this limitation you can add custom components
## License
MIT

## Development
I am willing to extend EVRL if there is demand for it. 
Also pull request are welcome, needs to maintain code coverage by 100% and matching coding style. 
