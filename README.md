# spring-security-instrumentation-demo

Instrument Spring Security using Spring Observability's Recording API

## How to Run
1. Checkout: https://github.com/spring-projects/spring-security/pull/10037
2. Run `./gradlew publishToMavenLocal`
3. Checkout: https://github.com/spring-projects/spring-observability
4. Run `./gradlew publishToMavenLocal`
5. Start a Zipkin server
   1. Checkout: https://github.com/jonatan-ivanov/local-services
   2. Run: `cd zipkin` and `docker compose up`
6. Run `./gradlew bootRun` in this project
7. Open http://localhost:8080/
