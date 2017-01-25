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

##### Example:
```kotlin
val loom = Loom(Executors.newCachedThreadPool())
```

## Routers

The Router take a base URL in the constructor.
##### Example:
```kotlin
val router = Router("/users")
```

#### Methods
Loom support the methods: GET, POST, PUT, DELETE

```kotlin
router.get("/") { request, response -> }

router.post("/") { request, response -> }

router.put("/") { request, response -> }

router.delete("/") { request, response -> }
```
#### URL

## Middleware



#### Next

#### Response

