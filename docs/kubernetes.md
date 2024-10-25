## Run

```bash
$ kubectl create ns co-co-gong
namespace/co-co-gong created
$ kubectl apply -n co-co-gong -f k8s
deployment.apps/backend created
service/backend created
ingressroute.traefik.io/co-co-gong created
deployment.apps/postgres created
service/postgres created
configmap/postgres-config created
secret/postgres-secret created
secret/oauth-secret created
secret/jwt-secret created
$ kubectl get all -n co-co-gong
NAME                            READY   STATUS    RESTARTS   AGE
pod/backend-7ff75486b8-dtmh2    1/1     Running   0          3m39s
pod/postgres-58585c55b4-7vsrs   1/1     Running   0          3m39s

NAME               TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE
service/backend    ClusterIP   10.99.127.11     <none>        8080/TCP   3m39s
service/postgres   ClusterIP   10.109.182.198   <none>        5432/TCP   3m39s

NAME                       READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/backend    1/1     1            1           3m39s
deployment.apps/postgres   1/1     1            1           3m39s

NAME                                  DESIRED   CURRENT   READY   AGE
replicaset.apps/backend-7ff75486b8    1         1         1       3m39s
replicaset.apps/postgres-58585c55b4   1         1         1       3m39s
```

## Logs

```bash
$ kubectl logs -n co-co-gong deploy/backend
Downloading https://services.gradle.org/distributions/gradle-8.8-bin.zip
.............10%.............20%.............30%.............40%.............50%.............60%..............70%.............80%.............90%...........
..100%

Welcome to Gradle 8.8!

Here are the highlights of this release:
 - Running Gradle on Java 22
 - Configurable Gradle daemon JVM
 - Improved IDE performance for large projects

For more details see https://docs.gradle.org/8.8/release-notes.html

Starting a Gradle Daemon (subsequent builds will be faster)
> Task :compileJava
> Task :processResources UP-TO-DATE
> Task :classes
> Task :resolveMainClassName
> Task :bootJar
> Task :jar
> Task :assemble
> Task :compileTestJava UP-TO-DATE
> Task :processTestResources NO-SOURCE
> Task :testClasses UP-TO-DATE
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
2024-10-25T09:16:56.853+09:00  INFO 286 --- [co-co-gong-server] [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactor
y for persistence unit 'default'
2024-10-25T09:16:56.855+09:00  INFO 286 --- [co-co-gong-server] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiat
ed...
2024-10-25T09:16:56.857+09:00  INFO 286 --- [co-co-gong-server] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown complet
ed.
> Task :test
> Task :check
> Task :build

BUILD SUCCESSFUL in 28s
7 actionable tasks: 5 executed, 2 up-to-date

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.3.2)
```

## Stop

```bash
$ kubectl delete ns co-co-gong
namespace "co-co-gong" deleted
```
