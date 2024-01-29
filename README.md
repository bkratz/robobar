## Build without Mutation testing applied:
```shell
 mvn clean verify
```

## Build with (default) Mutation testing engine applied:
```shell
 mvn clean verify pitest:mutationCoverage 
```

## Build with Descartes Mutation testing engine applied:
```shell
 mvn clean verify pitest:mutationCoverage -P descartes
```

## Start the application
```shell
mvn spring-boot:run
```
Open <http:localhost:8081> in your browser. Here you can find the links to:

- code coverage report: <http://localhost:8081/jacoco/index.html>
- default report: <http://localhost:8081/pitest-reports/index.html>
- Descartes report: <http://localhost:8081/descartes-reports/index.html> 