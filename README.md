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
## Usage

### Failsafe

Basic fault tolerance mechanism leveraging `java.util.Optional<T>`. The underlying method either did return a value or did not.

- Methods annotated with @Failsafe must return Optional<T>. This is checked at startup-time. If this condition is not met, exception is thrown during startup phase with details about which methods failed the test.
- Beans annotated with @Failsafe must enforce this Optional<T> return type on all declared methods.

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
#### On-fail event observation
```java
@Named
public class ExecutionErrorObserver {

public void observe(@Observes ExecutionErrorEvent executionError){
  // Do whatever is needed e.g. log the Throwable cause
}

}
```

### Semisafe - allowed exceptions & errors

The `@Semisafe` annotation allows listing Throwables allowed to be thrown.

- Methods annotated with @Semisafe({}) must return Optional<T>. This is checked at startup-time. If this condition is not met, exception is thrown during startup phase with details about which methods failed the test.
- Methods annotated with @Semisafe must enforce this Optional<T> return type on all declared methods.

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

```java
@Named
public class ServiceImplementation implements SomeService {

// Will return Optional.empty()
@Semisafe({RuntimeException.class})
public Optional<String> maybeFail(){
  throw new AnyUnlistedException("This exception will be converted");
}

}
```


## Known issues

### GlassFish

Failsafe interceptor may not be automatically enabled and requires manual registration in beans.xml. This **does not apply to Payara**, as Payara team has [fixed this issue](https://github.com/payara/Payara/issues/1532).

```xml
    <interceptors>
        <class>com.github.pscheidl.fortee.failsafe.FailsafeInterceptor</class>
    </interceptors>
``` 

