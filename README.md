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

##### Make Loom use the router:
```kotlin
loom.route(router)
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
```kotlin
val router = Router("/users")

router.get("/name")
```
Will catch the GET requests send to: "/users/name"

#### URL-parameters
```kotlin
val router = Router("/users")

router.get("/name/{name}")
```
Will catch the GET requests send to: "/users/name/**name**"
Where **name** match everything.

The URL-parameters chan be accessed in the handler using the request:
```kotlin
router.get("/name/{name}") { request, response ->
    val name = request.urlParameters["name"]
}
```

#### Request
The request contains the properties:

- method: The request method, ex: GET, POST
- urlParameters: Values from the URL, ex: /users/name/{name}, {name} defines the name url parameter.
- queryParameters: Query parameters, ex: /users?name=Peter
- headers: The key value headers
- body: The optional request body

```kotlin
val method = request.method
val urlName = request.urlParameters["name"]
val queryName = request.queryParameters["name"]
val headerName = request.headers["name"]
val body = request.body
```
#### Response
The response have the methods:

- status: Set the response http-status.
- header: Add the header to the response.
- headers: Add multiple headers to the response.
- body: Set the response body.
- end: Send the response.

```kotlin
response.status(Status.OK).header("name", "Peter").body("Body").end()
```

## Middleware

Middleware will catch a request before a Router.  
More than one Middleware can catch the same request, they will be called in the order they are added to the Loom server.

In this example the first Middleware i called and because the next function i called the second Middleware will be called afterwards.
The Router will not be called because the Second Middleware respond before the router is called.

```kotlin
// First middleware
loom.use(HttpMethod.GET, HttpMethod.POST, path = "/users/name") { request, response, next ->
    next()
}

// Second middleware
loom.use(HttpMethod.GET, HttpMethod.POST, path = "/users/name") { request, response, next ->
    response.status(Status.OK).body("Result").end()
}
```
#### Next
When the next callback is called the next Middleware will be called if it is the last Middleware the Router will be called.

```kotlin
loom.use(HttpMethod.GET, HttpMethod.POST, path = "/users/name") { request, response, next ->
    next()
}
```

