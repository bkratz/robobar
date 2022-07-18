## Build without Mutation testing applied:
```shell
 mvn clean install
```

## Build with (default) Mutation testing engine applied:
```shell
 mvn clean install pitest:mutationCoverage 
```

## Build with Descardes Mutation testing engine applied:
```shell
 mvn clean install pitest:mutaionCoverage -P descardes
```

## Start the application
```shell
mvn spring-boot:run
```
Open <http:localhost:8081> in your browser. Here you can find the links to:

- code coverage report: <http://localhost:8081/jacoco/index.html>
- default report: <http://localhost:8081/pitest-reports/index.html>
- Descardes report: <http://localhost:8081/descardes-reports/index.html> 