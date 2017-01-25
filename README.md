# Loom

A minimalistic Kotlin web framework.

## Get started

#### Maven

```xml
<dependency>
    <groupId>dk.magical</groupId>
    <artifactId>Loom</artifactId>
    <version>LATEST</version>
</dependency>
```
#### Loom

The Loom framework use threads from the ExecutorService.  
Use the Executors static creator methods to easily provide the ExecutorService.

##### Example
```kotlin
val loom = Loom(Executors.newCachedThreadPool())
```

## Routers

#### Methods

#### URL

## Mittleware

#### Next

#### Response

