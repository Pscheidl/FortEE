# FortEE

[![Build Status](https://travis-ci.org/Pscheidl/FortEE.svg?branch=master)](https://travis-ci.org/Pscheidl/FortEE)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/cab8a9609a9a4362a18c1ff3f759cf02)](https://www.codacy.com/app/pavel.junior/FortEE?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Pscheidl/FortEE&amp;utm_campaign=Badge_Grade)

FortEE is a Java EE fault-tolerance guard leveraging the Optional pattern. Its power lies in its simplicity. On methods returning Optional<T>, a @Failsafe annotation can be placed. Any uncaught exceptional states are then converted into an Optional.empty(). Synchronous or asynchronous invocation is not enforced.

- Simple and fast
- Startup-time check
- Tiny in size


## Maven
```xml
<dependency>
    <groupId>com.github.pscheidl</groupId>
    <artifactId>fortee</artifactId>
    <version>1.1.0</version>
</dependency>
```
## Gradle
```groovy
compile 'com.github.pscheidl:fortee:1.1.0'
```
**Release notes**
- Released on 17th of December 2017
- Introduced allowed exceptions with @Semisafe annotation

## How it works ?

For more information, please visit [FortEE wikipedia](https://github.com/Pscheidl/FortEE/wiki). 

Guard against all checked and unchecked exceptions.

```java
@Named
public class ServiceImplementation implements SomeService {

// Will return Optional.empty()
@Failsafe
public Optional<String> maybeFail(){
  throw new RuntimeException("Failed on purpose");
}

}
```

Guard against all exceptions but the listed ones.

```java
@Named
public class ServiceImplementation implements SomeService {

// Will end with RuntimeException
@Semisafe({RuntimeException.class})
public Optional<String> maybeFail(){
  throw new RuntimeException("Failed on purpose");
}

}
```
