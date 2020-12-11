# Jersey Injection Dependency example with HK2

Example of a Jersey project using the dependency injection framework HK2 to inject logged user into the application via a custum annotation

* [Jersey](https://eclipse-ee4j.github.io/jersey/) is a Java Framework that is commonly used to help generate REST Api. 
* [HK2](https://javaee.github.io/hk2/) is a lightweight framework which allow Inversion of Control (IoC) and dependency injection (DI) 

This project was created to write [this article](https://medium.com/@mnu/jersey-injection-dependency-example-with-hk2-84ebfb7cb2d2) on Medium

## TLDR

To start the project, run the server in the Main.java class and hit it on 

```bash
curl -X GET \
  http://localhost:8080/myapp/myresource \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MDc3MTU4NDQsImV4cCI6MTAwMDAwMTYwNzcxNTg0NCwiaXNzIjoiSVNTVUVSIiwiYXVkIjoiQVVESUVOQ0UiLCJsb2dpbiI6Im00bnU1NiIsImNvbXB0ZSI6ImRldjEwIn0.0Nov_XwHIMOROkiu2cqWxtSd-r_HIQRAXLrAu5pTEv8,Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MDc3MTU4NDQsImV4cCI6MTAwMDAwMTYwNzcxNTg0NCwiaXNzIjoiSVNTVUVSIiwiYXVkIjoiQVVESUVOQ0UiLCJsb2dpbiI6Im00bnU1NiIsImNvbXB0ZSI6ImRldjEwIn0.0Nov_XwHIMOROkiu2cqWxtSd-r_HIQRAXLrAu5pTEv8' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Host: localhost:8080' \
  -H 'cache-control: no-cache'
```

> Return the user found in the JWT Token payload

```bash
curl -X GET \
  http://localhost:8080/myapp/myresource/users \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MDc3MTU4NDQsImV4cCI6MTAwMDAwMTYwNzcxNTg0NCwiaXNzIjoiSVNTVUVSIiwiYXVkIjoiQVVESUVOQ0UiLCJsb2dpbiI6Im00bnU1NiIsImNvbXB0ZSI6ImRldjEwIn0.0Nov_XwHIMOROkiu2cqWxtSd-r_HIQRAXLrAu5pTEv8,Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MDc3MTU4NDQsImV4cCI6MTAwMDAwMTYwNzcxNTg0NCwiaXNzIjoiSVNTVUVSIiwiYXVkIjoiQVVESUVOQ0UiLCJsb2dpbiI6Im00bnU1NiIsImNvbXB0ZSI6ImRldjEwIn0.0Nov_XwHIMOROkiu2cqWxtSd-r_HIQRAXLrAu5pTEv8' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Host: localhost:8080' \
  -H 'cache-control: no-cache'
```

> Returns the list of users

Or use the Postman collection in `jersey-hk2.postman_collection.json`

## Step 1 

Generate a project from [Maven Archetype](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/getting-started.html#new-from-archetype):

```bash
mvn archetype:generate -DarchetypeArtifactId=jersey-quickstart-grizzly2 \
-DarchetypeGroupId=org.glassfish.jersey.archetypes -DinteractiveMode=false \
-DgroupId=com.example -DartifactId=simple-service -Dpackage=com.example \
-DarchetypeVersion=2.29.1
```

## Step 2

Add some business logic to make this test more interesting in `com.example.business`: 
* User
* UserSvc
* UserDao

## Step 3

We want to be able to do Dependency Injection of our services. 
We need to register a `AbstractBinder` to our jersey app that will automatically match the given injected class with the implementation. 

Create a class `ApplicationBinder`:

```java
@Provider
public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(JustInTimeServiceResolver.class).to(JustInTimeInjectionResolver.class);
    }
}
```

And register it with our application: `resourceConfig.register(new ApplicationBinder());`

And an another class `JustInTimeServiceResolver` that will handle the automatic binding of services like Google Guice would do. 

```java
@Service
public class JustInTimeServiceResolver implements JustInTimeInjectionResolver {

    @Inject
    private ServiceLocator serviceLocator;

    @Override
    public boolean justInTimeResolution(Injectee injectee) {
        final Type requiredType = injectee.getRequiredType();

        if (injectee.getRequiredQualifiers().isEmpty() && requiredType instanceof Class) {
            final Class<?> requiredClass = (Class<?>) requiredType;

            // IMPORTANT: check the package name, so we don't accidentally preempt other framework JIT resolvers
            if (requiredClass.getName().startsWith("com.example")) {
                final List<ActiveDescriptor<?>> descriptors = ServiceLocatorUtilities.addClasses(serviceLocator, requiredClass);

                return !descriptors.isEmpty();
            }
        }
        return false;
    }
}
```

From there we can already make use of the dependency injection framework. 
Create a new endpoint to get the list of users: 

```java
@GET
@Path("users")
@Produces(MediaType.APPLICATION_JSON)
public List<User> getUsers() {
    return userSvc.getList();
}
```

And in `MyResource` inject the UserSvc service this way: 

```java
@Inject
private UserSvc userSvc;
```

We can do the same in the `UserSvc` class with the field `UserDao`: 

```java
@Inject
private UserDao userDao;
```

Note that Injected resources need to have a no args constructor. 

Wen can test ou API is responding well with the list of our users this way: 

```java
@Test
public void testGetUsers() {
    List<User> users = target.path("myresource/users").request().get(new GenericType<List<User>>() {});
    assertEquals(2, users.size());
}
```

## Step 4

Now we would like to be able to get information about the user that send requests to our API and use it where needed in our application. 
Our API will have secured access managed by a JWT Token. 

We will create a `@PreMatching` Jersey filter that will be run before any request and that:
* will validate the token that should be in `Authorization` header
* will create a user from the claims in the extracted token
* will put the user in the SecurityContext so that it can be accessed by the HK2 framework

```java
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class PreMatchingCurrentUserFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        try {
            Jws<Claims> jws = new AuthorizationValidator(false).validate(requestContext);
            AppSecurityContext appSecurityContext = new AppSecurityContext(
                    new HashSet<String>(),
                    new User(
                            (String) jws.getBody().get("login"),
                            (String) jws.getBody().get("compte")
                    ),
                    true
            );
            requestContext.setSecurityContext(appSecurityContext);
        }
        catch (Exception ignored) {
        }
    }
}
``` 

And register the filter with our application: `resourceConfig.register(new PreMatchingCurrentUserFilter());`

Next we will create an new custom annotation that will be used to inject the user anywhere we want. 

First create the new annotation, available in Field and in Constructor: 

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface CurrentUser {
}
```

then a new `InjectionResolver`, where we will bind the `User` class with the new annotation. 
We can access the `SecurityContext` using dependency injection and find the User we injected in the `@PreMatching` earlier. 

```java
public class CurrentUserInjectionResolver implements InjectionResolver<CurrentUser> {

	private javax.inject.Provider<SecurityContext> securityContextProvider;

	@Inject
	public CurrentUserInjectionResolver(
		javax.inject.Provider<SecurityContext> securityContextProvider) {
		this.securityContextProvider = securityContextProvider;
	}

	@Override
	public Object resolve(Injectee injectee, ServiceHandle<?> sh) {
		if (User.class == injectee.getRequiredType()) {
			return securityContextProvider.get().getUserPrincipal();
		}
		return null;
	}

    ...
}
```

And thats'it, wen can access the user using an injection like that: 

```java
@CurrentUser
private User user;
```

or in a constructor: 

```java
@CurrentUser
public UserSvc(User user){
    this.user = user;
}
```



If user was found in the JWT Token if will be accessible here. 
