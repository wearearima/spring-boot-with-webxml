# Introduction

If you try to migrate a Java legacy application to Spring Boot you will find out that [Spring Boot ignores
the web.xml file](https://github.com/spring-projects/spring-boot/issues/2175) when it is run as embedded container.

To solve this problem, the web.xml configuration can be rewritten to the Spring Boot's style (starters, javaconfig, 
etc). However, depending on your web.xml file it might be a bit tricky. 

As an alternative to rewriting, this repository shows how the web.xml can be parsed to register all the components 
(i.e. servlets) automatically. 

Start the application with this command:

```
$ ./mvnw clean spring-boot:run
```

And make a request to the Servlet with this URL `http://localhost:8080/demo`

# Credits
Created by https://www.arima.eu

![ARIMA](https://arima.eu/arima-claim.png)