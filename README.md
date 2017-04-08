# BenGuard
Ben is a Java EE fault-tolerance guard leveraging the Optional pattern. Its power lies in its simplicity. On methods returning Optional<T>, a @Failsafe annotation can be placed. Any uncaught exceptional states are then logged and converted into an Optional.empty(). Synchronous or asynchronous invocation is not enforced.

- Non-intrusive
- Simple and fast
- Startup-time check

[![Build Status](https://travis-ci.org/Pscheidl/BenGuard.svg?branch=master)](https://travis-ci.org/Pscheidl/BenGuard)

## Maven
```xml
<dependency>
  <groupId>com.github.pscheidl</groupId>
  <artifactId>benguard</artifactId>
  <version>0.2</version>
</dependency>
```
## Gradle

```groovy
compile group: 'com.github.pscheidl', name: 'benguard', version: '0.2'
```
## Usage

- Methods annotated with @Failsafe must return Optional<T>. This is checked at startup-time. If this condition is not met, exception is thrown during startup phase with details about which methods failed the test.
- Beans annotated with @Failsafe must enforce this Optional<T> return type on all declared methods.

```java
@Named
public class ServiceImplementation implements SomeService {

// Will return Optional.empty(), exception will be logged
@Failsafe
public Optional<String> maybeFail(){
  throw new RuntimeException("Failed on purpose");
}

}
```
