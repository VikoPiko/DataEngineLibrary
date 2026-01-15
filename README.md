## DataEngineLibrary
A Library to help define custom rules and validators for parsing files, based on defined annotations and conditions. Built to be easily extendable and integrated.

### How to build library
* To build the library simply clone it with git then run:
```
mvn clean install
```
This builds the library and makes it addable to different projects.

### Running tests

* To run all tests to check integrity of the library run:
```
mvn clean verify
```

### How to add as a depedency
* To add the library as a dependency, after installing it can be added to the *pom.xml* file with the following code:
```
<dependency>
   <groupId>com.viko</groupId>
   <artifactId>DataEngine</artifactId>
   <version>1.0-SNAPSHOT</version>
</dependency>
```

### The library supports:
Validation Annotations:
- NotNull
- RegEx
- Range

Mapping Annotations:
- FileSource
- Column

Library is build to be easily extendable.

To use it simply add as a dependency, create a class (DTO) for the data needed to be ran through the validators, and give it the appropriate mappings.

### Example implementation:

```
@FileSource(delimiter = ",")
public class MockClass {
    @NotNull(message = "name is required")
    @Column(index = 0, name = "name1")
    public String name;

    @Regex(pattern = ".+@.+\\..+", message = "invalid email")
    @Column(index = 1, name = "email")
    public String email;

    @Range(min = 18, max = 50, message = "Out of range....")
    @Column(index = 2, name = "age")
    public int age;
}
```

This ensures that the class will follow the specified rules.

The GenericParser is responsible for safely parsing through the given resource (I.E a text file with delimited values/fields).

The main function is parse();
```parse(String filePath, Class<T> clazz)``` takes in a filepath and a Runtime Class instance.

