# Clounect
## Getting Started
1. Clone Repository
2. Create application.properties file inside resources if not present
3. Create a database in postgresql
4. Copy all contents of application.properties.template into application.properties
5. Update clientId, clientSecret, cognitoPoolRegion, cognitoPoolId, cognitoPoolUi and save
6. Update the following postgres properties
   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/<db_name>
   spring.datasource.username=<db_username>
   spring.datasource.password=<db_password>
   ```
7. Building from Source

   ```$ ./gradlew build```

8. Running Project

   ```$ ./gradlew bootrun```



