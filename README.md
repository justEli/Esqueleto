# Esqueleto
![Java](https://img.shields.io/badge/Java-25-blue)

### Description
SQL util for Java using HikariCP, to make querying SQL in Java easier.

## Maven
[![](https://jitpack.io/v/JustEli/Esqueleto.svg)](https://jitpack.io/#JustEli/Esqueleto)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.JustEli.Esqueleto</groupId>
        <artifactId>esqueleto-core</artifactId>
        <version>VERSION</version>
    </dependency>
    <dependency>
        <groupId>ADAPTER_PACKAGE</groupId>
        <artifactId>ADAPTER_ID</artifactId>
        <version>ADAPTER_VERSION</version>
    </dependency>
</dependencies>
```

## Adapters

| **Adapter**   | **Works?** | **Esqueleto Version**  | **Adapter Version**                                                                   |
|---------------|------------|------------------------|---------------------------------------------------------------------------------------|
| MariaDB       | ✅ Fully    | latest (always tested) | [3.5.8](https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client)      |
| MySQL         | ✅ Fully    | 0.3.0                  | [9.7.0](https://mvnrepository.com/artifact/com.mysql/mysql-connector-j/9.7.0)         |
| PostgreSQL    | ✅ Fully    | 0.3.0                  | [42.7.11](https://mvnrepository.com/artifact/org.postgresql/postgresql)               |
| MS SQL Server | ✅ Fully    | 0.3.0                  | [13.4.0.jre11](https://mvnrepository.com/artifact/com.microsoft.sqlserver/mssql-jdbc) |
| H2            | ❌ Untested |                        |                                                                                       |
| SQLite        | ❌ Untested |                        |                                                                                       |

## Examples

### Open connection
```java
Esqueleto sql = Esqueleto.start(config -> {
    config.setJdbcUrl("jdbc:mariadb://mariadb:3306/esqueleto");
    config.setUsername("root");
    config.setPassword("F14WeaG1BLKAnvIT7");
});
```
Using the following database:
```sql
CREATE DATABASE esqueleto_test;
CREATE USER 'esqueleto'@'localhost' IDENTIFIED BY 'F14WeaG1BLKAnvIT7';
GRANT ALL PRIVILEGES ON esqueleto_test.* TO 'esqueleto'@'localhost';
```

### Update
Returns the inserted id of the signature
```java
UUID playerUuid = ...;
byte[] signature = ...;

Optional<Integer> signatureId = sql.statement(
    "INSERT INTO Signature (playerId, signature) VALUES ((SELECT id FROM Player WHERE uniqueId = ?), ?)"
).bind(
    playerUuid,
    signature
).update().complete(data -> data.next()? data.getInt("id") : null);
```

```java
Company company = ...;
int transactionAmount = ...;

sql.statement("""
    UPDATE Company SET profit = profit + ?
    WHERE id = ?
    """
).bind(
    transactionAmount,
    company.getId()
).update().push();
```
### Select
```java
Player player = ...;

Optional<Long> discordId = sql.statement(
    "SELECT discordId FROM Player WHERE username = ? LIMIT 1"
).bind(
    player.getUsername()
).query().complete(results -> {
    return results.next()? results.get("discordId") : null;
});
```
### Close
```java
sql.close();
```
