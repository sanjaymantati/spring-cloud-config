# spring-cloud-config
* Introduction : Here we have configured Spring cloud config server and config client in such a way that the config server always send ciphered/encrypted properties to config client module. Then the config client module decrypt these properties and inject them in Spring context.
## Modules
1. configserver
    * Configuration
        * Git repository configuration : This configuration is used by config server to get properties from a git repository.
        * Encryption key : This encryption key is the key from which all properties are encrypted.
2. configclient
    * Configuration
        * Config server configuration : From this configuration the config client will know how to connect with config server. 
        * Encryption key : This encryption key is the key from which all properties are encrypted. Here it'll be used to decrypt properties file.

## Problem statement 
* Here the configuration properties are stored in plain text in git repository(A source in our case.) This plain configuration properties will be consumed by config server and the config client module will fetch these properties in plaint text from config server. Properties in plain text will cause a security issue. If the git repository or config server is compromized, the properties will be exposed which contains sensitive data.
-> Image 

![Problem-image](https://github.com/sanjaymantati/spring-cloud-config/blob/master/doc/problem-graph.jpg?raw=true)


## Solutions
* **Solution 1** : We'll encrypt all configuration properties and store them in git repository. The config server will pass encrypted configuration properties as it is to config client. Now the config client will decrypt these properties through the encryption key.
  ![Solution-1-image](https://github.com/sanjaymantati/spring-cloud-config/blob/master/doc/solution-1.jpg?raw=true)
* **Solution 2** : We'll encrypt all configuration properties and store them in git repository. The config server decrypt them throught encryption key and pass them to config client. Now the config client will use them normally.
* In our case we'll use the **solution 1**.

## Flow
1. First start the config server with encrypt key. Now execute below API and get encrypt property value.
    * For example, for `spring.datasource.username=root`  property we need to encrypt the username. So we'll execute below API.
```cURL
curl --location 'http://localhost:8081/encrypt' \
--header 'Content-Type: text/plain' \
--data 'root'
```
    * Response
```
ee8bf4a2712f3711d0f6d476c98e68873731f6ae33810d26eab8773d96614f4e
```
2. Now replace the value coming from response with actual value in properties of git repository. `spring.datasource.username={cipher}ee8bf4a2712f3711d0f6d476c98e68873731f6ae33810d26eab8773d96614f4e`. Now make sure you add prefix **{cipher}**, otherwise the decryption won't work.
3. Just like above example replace all properties value in git repository.
4. In our example there is only in property file in git repository(https://github.com/sanjaymantati/spring-cloud-config) which is **datasource.properties**.
 Note above steps are one-time. 
5. Now Restart the config server and execute below API. As you can see in the response the all properties are encrypted.
```cURL
curl --location 'http://localhost:8081/datasource/default'
```

* Response 
```json
{
    "name": "datasource",
    "profiles": [
        "default"
    ],
    "label": null,
    "version": "f71c214e0c461e4b5c10a584f3b7b8d3b59e4664",
    "state": null,
    "propertySources": [
        {
            "name": "https://github.com/sanjaymantati/spring-cloud-config/datasource.properties",
            "source": {
                "spring.datasource.url": "{cipher}e04bf669af25dae095a2b95560be81a61c7226d3c6ec314f50052e836364e2586af1ea35a19526af516610ce1baa4f635a9897c596248b1782498e817767bc03116fbb5be1943b746125d73deeecab19",
                "spring.datasource.username": "{cipher}ee8bf4a2712f3711d0f6d476c98e68873731f6ae33810d26eab8773d96614f4e",
                "spring.datasource.password": "{cipher}db5dece71addc8262274eca17e712fa12e4bcf2c7fbe348f534c68b092be3e71",
                "spring.jpa.hibernate.ddl-auto": "{cipher}ac5f028c4050b7f68b8ea233466d03a3833a8ea9a8c1ee031176016c3d9ed153",
                "spring.datasource.hikari.connectionTimeout": "{cipher}decc175cbd3a5f0e939c724ae1edeba73901b77f5c9fb2a730f45120b473d2fa",
                "spring.datasource.hikari.maximumPoolSize": "{cipher}dcbe1239e7573b02244ea916059e25d16b9dfe806994668184bd03868a3c6104",
                "spring.datasource.driver-class-name": "{cipher}7ab1d4078a82971346d7cfd62ca6fa9ca74b9d8e5d3667f32beb271aee32d0650ce421b2488b3c837b15eae37d6915ef"
            }
        }
    ]
}
```
6. Now we need to use these properties file in config client module. Start the config client module with encrypted key. Now in the logs of config client you can see that DataSourceConfigProperties is showing its values in plain format which was injected from config server and config client decrypted them throught encryption key.
```txt

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::      (v2.7.12-SNAPSHOT)

...
2023-05-01 18:32:28.694  INFO 22660 --- [  restartedMain] c.s.client.UserApplication               : No active profile set, falling back to 1 default profile: "default"
2023-05-01 18:32:28.748  INFO 22660 --- [  restartedMain] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8081
2023-05-01 18:32:28.748  INFO 22660 --- [  restartedMain] o.s.c.c.c.ConfigServerConfigDataLoader   : Located environment: name=datasource, profiles=[default], label=null, version=f71c214e0c461e4b5c10a584f3b7b8d3b59e4664, state=null
2023-05-01 18:32:28.752  INFO 22660 --- [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
2023-05-01 18:32:28.752  INFO 22660 --- [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : For additional web related logging consider setting the 'logging.level.web' property to 'DEBUG'
2023-05-01 18:32:29.904  INFO 22660 --- [  restartedMain] o.s.cloud.context.scope.GenericScope     : BeanFactory id=4f0b92dc-753d-3cef-925e-011a366b9132
2023-05-01 18:32:30.525  INFO 22660 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2023-05-01 18:32:30.535  INFO 22660 --- [  restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2023-05-01 18:32:30.536  INFO 22660 --- [  restartedMain] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.74]
2023-05-01 18:32:30.635  INFO 22660 --- [  restartedMain] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2023-05-01 18:32:30.636  INFO 22660 --- [  restartedMain] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1883 ms
2023-05-01 18:32:30.787  INFO 22660 --- [  restartedMain] c.s.client.UserApplication               : **DataSourceConfigProperties : DataSourceConfigProperties(url=jdbc:mysql://localhost:3307/fintransaction?useSSL=false, username=root, password=rootPassword, driverClassName=com.mysql.jdbc.Driver, hikari=DataSourceConfigProperties.HikariConfigProperties(connectionTimeout=20000, maximumPoolSize=5))
2023-05-01 18:32:30.789  INFO 22660 --- [  restartedMain] c.s.client.UserApplication               : hbmToDLL : update
2023-05-01 18:32:31.337  INFO 22660 --- [  restartedMain] o.s.b.d.a.OptionalLiveReloadServer       : LiveReload server is running on port 35729
2023-05-01 18:32:31.747  INFO 22660 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2023-05-01 18:32:32.091  INFO 22660 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8082 (http)
2023-05-01 18:32:32.092  INFO 22660 --- [  restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2023-05-01 18:32:32.092  INFO 22660 --- [  restartedMain] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.74]
2023-05-01 18:32:32.120  INFO 22660 --- [  restartedMain] o.a.c.c.C.[Tomcat-1].[localhost].[/]     : Initializing Spring embedded WebApplicationContext
2023-05-01 18:32:32.120  INFO 22660 --- [  restartedMain] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 124 ms
2023-05-01 18:32:32.139  INFO 22660 --- [  restartedMain] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint(s) beneath base path '/actuator'
2023-05-01 18:32:32.180  INFO 22660 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8082 (http) with context path ''
2023-05-01 18:32:32.471  INFO 22660 --- [  restartedMain] c.s.client.UserApplication               : Started UserApplication in 10.569 seconds (JVM running for 12.024)
2023-05-01 18:32:33.170  INFO 22660 --- [1)-192.168.0.31] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2023-05-01 18:32:33.172  INFO 22660 --- [1)-192.168.0.31] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2023-05-01 18:32:33.173  INFO 22660 --- [1)-192.168.0.31] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms

```




