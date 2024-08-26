## Run

```shell
$ docker compose up -d
[+] Building 0.7s (16/16) FINISHED                                                                                                                  docker:default
...
[+] Running 3/3
 ✔ Network co-co-gong-server_default       Created                                                                                                            0.1s
 ✔ Container co-co-gong-server-postgres-1  Started                                                                                                            0.2s
 ✔ Container co-co-gong-server-server-1    Started                                                                                                            0.3s
$ docker ps
CONTAINER ID   IMAGE                      COMMAND                  CREATED          STATUS          PORTS                                       NAMES
2d70ac92debb   co-co-gong-server-server   "./gradlew bootRun"      32 seconds ago   Up 31 seconds   0.0.0.0:8080->8080/tcp, :::8080->8080/tcp   co-co-gong-server-server-1
f1241a06b4b0   postgres:latest            "docker-entrypoint.s…"   32 seconds ago   Up 31 seconds   0.0.0.0:5432->5432/tcp, :::5432->5432/tcp   co-co-gong-server-postgres-1
```

## Logs

```shell
$ docker logs -f co-co-gong-server-server-1
Downloading https://services.gradle.org/distributions/gradle-8.8-bin.zip
.............10%.............20%.............30%.............40%.............50%.............60%..............70%.............80%.............90%.............100%

Welcome to Gradle 8.8!

Here are the highlights of this release:
 - Running Gradle on Java 22
 - Configurable Gradle daemon JVM
 - Improved IDE performance for large projects

For more details see https://docs.gradle.org/8.8/release-notes.html

Starting a Gradle Daemon (subsequent builds will be faster)
> Task :compileJava
> Task :processResources
> Task :classes
> Task :resolveMainClassName

> Task :bootRun

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.3.2)
...
```

## Stop

```shell
$ docker compose down -v
[+] Running 3/3
 ✔ Container co-co-gong-server-server-1    Removed                                                                                                            0.7s
 ✔ Container co-co-gong-server-postgres-1  Removed                                                                                                            0.2s
 ✔ Network co-co-gong-server_default       Removed                                                                                                            0.2s
```
