# Chat Service

## 개요
- Spring Boot 의 웹소켓 기능을 사용해 보기 위한 목적
- WebSocket EventListener 및 Annotation 을 이용하여 mvc 형태로 사용하는 백엔드 서버
- 로그인 후 로비로 진입하고, 방 생성 및 참가 할 수 있는 기능

## 사용 기술
- JDK: 11
- Spring Boot 2.6 with Kotlin

## 클라이언트 프로젝트
- <https://github.com/rxcats/chat-service-client>

## 빌드

### Windows
- `Build`
```
gradlew build
```

- `Run`
```
java -jar build\libs\chat-service-portfolio-0.0.1-SNAPSHOT.jar
```

### Linux or Mac
- `Build`
```
./gradlew build
```

- `Run`
```
java -jar build/libs/chat-service-portfolio-0.0.1-SNAPSHOT.jar
```


## 데모
- [Link](https://apps-rxcats.duckdns.org)