# Chat Service

### backend
- JDK: 11
- Language: Kotlin
- Framework: Spring Boot

#### Windows
`Build`
```
gradlew build
```

`Run`
```
java -jar build\libs\chat-service-portfolio-0.0.1-SNAPSHOT.jar
```

#### Linux or Mac
`Build`
```
./gradlew build
```

`Run`
```
java -jar build/libs/chat-service-portfolio-0.0.1-SNAPSHOT.jar
```


### frontend
- Language: Javascript
- Framework: Svelte

`Build`
```
cd chat-service-client
npm install
npm run-script build
```

`Run`
```
public/index.html
```

`View`
![chat-client-screenshot.png](docs/img/chat-client-screenshot.png)