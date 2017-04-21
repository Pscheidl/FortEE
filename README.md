# FortEE
FortEE is a Java EE fault-tolerance guard leveraging the Optional pattern. Its power lies in its simplicity. On methods returning Optional<T>, a @Failsafe annotation can be placed. Any uncaught exceptional states are then logged and converted into an Optional.empty(). Synchronous or asynchronous invocation is not enforced.

- Non-intrusive
- Simple and fast
- Startup-time check

**The library is in pre-release state. Until version 1.0, contracts may change.**

[![Build Status](https://travis-ci.org/Pscheidl/FortEE.svg?branch=master)](https://travis-ci.org/Pscheidl/FortEE)

## Maven
```xml
<dependency>
    <groupId>com.github.pscheidl</groupId>
    <artifactId>fortee</artifactId>
    <version>0.3.0</version>
</dependency>
```
## Gradle
```groovy
compile 'com.github.pscheidl:fortee:0.3.0'
```

## Usage

## Failsafe

Basic fault tolerance mechanism leveraging `java.util.Optional<T>`. The underlying method either did deliver or did not. Nothing in between.

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
#### On-fail event observation
```java
@Named
public class ExecutionErrorObserver {

public void observe(@Observes ExecutionError executionError){
  // Do whatever is needed
}

}
```

## Timeout

Leverages `java.util.concurrent.ExecutorService` to provide Timeout mechanism. Number of threads created is `n+1`, where `n` is number of tasks executed. The one additional thread watches for `Future` task timeout.

```java
public class Example {

    @Inject
    private SomeService someService;

    //100 Threads in a pool. Each new task has a timeout of 10 milliseconds.
    @Inject
    @Timeout(threads = 100, millis = 10)
    private ExecutorService executor;


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response test() throws ExecutionException, InterruptedException {
        Future<Optional<String>> submit = executor.submit(someService::doSomething);
        try {
            Optional<String> s = submit.get();
            return Response.ok(s).build();
        } catch (CancellationException e) {
            return Response.noContent().build();
        }
    }
}
```


