package me.justeli.esqueleto.testing;

import me.justeli.esqueleto.Esqueleto;
import me.justeli.esqueleto.UpdateResult;

import java.util.Optional;
import java.util.SplittableRandom;

/**
 * @author Eli
 * @since October 10, 2023 (creation)
 */
public class Main {
    static void main() throws InterruptedException {
        System.out.println("Waiting 2 seconds..");
        Thread.sleep(2000);

        performTests();
    }

    private static void performTests() {
        // using docker containers for address, hence no localhost

        // MSSQL:
        // * CREATE DATABASE esqueleto_test;
        // * USE esqueleto_test;
        // * CREATE LOGIN esqueleto WITH PASSWORD = 'dAQ5g61NT';
        // * CREATE USER esqueleto FOR LOGIN esqueleto;
        // * EXEC sp_addrolemember 'db_owner', 'esqueleto';

        System.out.println("\nOpening MS SQL..");
        Esqueleto mssql = Esqueleto.start(config -> {
            config.setDebug(true);
            config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            config.setJdbcUrl("jdbc:sqlserver://mssql:1433;databaseName=master;encrypt=true;trustServerCertificate=true");
            config.setUsername("sa");
            config.setPassword("F14WeaG1BLKAnvIT7");
        });

        // MariaDB:
        // * CREATE DATABASE esqueleto_test;
        // * CREATE USER 'esqueleto'@'localhost' IDENTIFIED BY 'dAQ5g61NT';
        // * GRANT ALL PRIVILEGES ON esqueleto_test.* TO 'esqueleto'@'localhost';

        System.out.println("\nOpening MariaDB..");
        Esqueleto mariadb = Esqueleto.start(config -> {
            config.setDebug(true);
            config.setJdbcUrl("jdbc:mariadb://mariadb:3306/esqueleto");
            config.setUsername("root");
            config.setPassword("F14WeaG1BLKAnvIT7");
        });

        System.out.println("\nOpening MySQL..");
        Esqueleto mysql = Esqueleto.start(config -> {
            config.setDebug(true);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl("jdbc:mysql://mysql:3306/esqueleto?allowPublicKeyRetrieval=true&useSSL=false");
            config.setUsername("root");
            config.setPassword("F14WeaG1BLKAnvIT7");
        });

        System.out.println("\nOpening PostgreSQL..");
        Esqueleto postgres = Esqueleto.start(config -> {
            config.setDebug(true);
            config.setDriverClassName("org.postgresql.Driver");
            config.setJdbcUrl("jdbc:postgresql://postgres:5432/esqueleto");
            config.setUsername("postgres");
            config.setPassword("F14WeaG1BLKAnvIT7");
        });

        System.out.println("\n\n\n");
        executeTest("MS SQL", mssql);
        executeTest("MariaDB", mariadb);
        executeTest("MySQL", mysql);
        executeTest("PostgreSQL", postgres);

        Optional.ofNullable(mssql).ifPresent(Esqueleto::close);
        Optional.ofNullable(mariadb).ifPresent(Esqueleto::close);
        Optional.ofNullable(mysql).ifPresent(Esqueleto::close);
        Optional.ofNullable(postgres).ifPresent(Esqueleto::close);
    }

    private static final SplittableRandom RANDOM = new SplittableRandom();

    private static void executeTest(String type, Esqueleto sql) {
        if (sql == null) {
            fail(type, "connection was not opened successfully");
            return;
        }

        long random = RANDOM.nextLong();
        String name = "abcdef";

        System.out.println("    Random value to insert for " + type + ": " + random);

        sql.statement("DROP TABLE IF EXISTS test_table").update().complete();
        sql.statement("CREATE TABLE test_table ( id BIGINT, name CHAR(6) )").update().complete();
        sql.statement("INSERT INTO test_table (id, name) VALUES (?, ?)").bind(random, name).update().complete();

        Optional<Long> id = sql.statement(
            "SELECT id, name FROM test_table"
        ).query().complete(data -> data.next() && name.equals(data.getString("name"))? data.getNullableLong("id") : null);

        if (id.isPresent() && id.get() == random) {
            System.out.println("✅  Test successful for " + type + ": selected inserted " + random + ".\n\n");
        }
        else {
            fail(type, "querying was not successful");
        }
    }

    private static void fail(String type, String reason) {
        System.out.println("❌  Test failed for " + type + ": " + reason + ".\n\n");
    }
}
