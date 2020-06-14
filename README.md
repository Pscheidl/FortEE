# FortEE

[![Build Status](https://travis-ci.org/Pscheidl/FortEE.svg?branch=master)](https://travis-ci.org/Pscheidl/FortEE)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/cab8a9609a9a4362a18c1ff3f759cf02)](https://www.codacy.com/app/pavel.junior/FortEE?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Pscheidl/FortEE&amp;utm_campaign=Badge_Grade)

FortEE is a Jakarta EE / Java EE fault-tolerance guard leveraging the Optional pattern. Its power lies in its simplicity. On methods returning Optional<T>, a @Failsafe annotation can be placed. Any uncaught exceptional states are then converted into an Optional.empty(). Synchronous or asynchronous invocation is not enforced.

- Simple and fast
- Startup-time check
- Tiny in size
- Compatible with Jakarta EE 8, requires Java EE 7


## Maven
```xml
<dependency>
    <groupId>com.github.pscheidl</groupId>
    <artifactId>fortee</artifactId>
    <version>1.2.0</version>
</dependency>
```
## Gradle
```groovy
compile 'com.github.pscheidl:fortee:1.2.0'
```
**Release notes**
- Released on 14th of June 2020
- The `@Semisafe` annotation ignored exceptions not only listed as a value of that annotation,
but also exceptions directly specified in the mehod's definition, e.g. `public Optional<String> doSomething() throws UnsupportedOperationException` will let the
`UnsupportedOperationException` through.
- Test suite ran against WildFly 20.0.0-Final.

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

Guard against all exceptions but the declared ones, or the ones listed in the `@Semisafe` annotation..

```java
@Named
public class ServiceImplementation implements SomeService {

// Will end with RuntimeException
@Semisafe
public Optional<String> maybeFail() throws RuntimeException {
  throw new RuntimeException("Failed on purpose");
}

}
```

Alternatively, the exception to be let through can be specified inside the `@Semisafe` annotation.

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
