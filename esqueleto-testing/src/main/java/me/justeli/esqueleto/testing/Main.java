package me.justeli.esqueleto.testing;

import me.justeli.esqueleto.Esqueleto;
import me.justeli.esqueleto.driver.MSSQLDriver;
import me.justeli.esqueleto.driver.MariaDBDriver;
import me.justeli.esqueleto.driver.MySQLDriver;
import me.justeli.esqueleto.driver.PostgreSQLDriver;

import java.util.Optional;
import java.util.SplittableRandom;

/**
 * @author Eli
 * @since October 10, 2023 (creation)
 */
public class Main
{
    public static void main (String... args)
    throws InterruptedException
    {
        System.out.println("Waiting 5 seconds..");
        Thread.sleep(5000);

        System.out.println("\nOpening MS SQL..");
        Esqueleto mssql = Esqueleto.create(config -> {
            config.setDriver(MSSQLDriver.class);
            config.setDebug(true);
            config.setHost("mssql");
        }).start("esqueleto", "root", "F14WeaG1BLKAnvIT7");

        System.out.println("\nOpening MariaDB..");
        Esqueleto mariadb = Esqueleto.create(config -> {
            config.setDriver(MariaDBDriver.class);
            config.setDebug(true);
            config.setHost("mariadb");
        }).start("esqueleto", "root", "F14WeaG1BLKAnvIT7");

        System.out.println("\nOpening MySQL..");
        Esqueleto mysql = Esqueleto.create(config -> {
            config.setDriver(MySQLDriver.class);
            config.setDebug(true);
            config.setHost("mysql");
        }).start("esqueleto", "root", "F14WeaG1BLKAnvIT7");

        System.out.println("\nOpening PostgreSQL..");
        Esqueleto postgres = Esqueleto.create(config -> {
            config.setDriver(PostgreSQLDriver.class);
            config.setDebug(true);
            config.setHost("postgres");
        }).start("esqueleto", "root", "F14WeaG1BLKAnvIT7");

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

    private static void executeTest (String type, Esqueleto sql)
    {
        if (sql == null)
        {
            fail(type, "connection was not opened successfully");
            return;
        }

        var random = RANDOM.nextLong();
        sql.statement("DROP TABLE IF EXISTS test_table").update().complete();
        sql.statement("CREATE TABLE IF NOT EXISTS test_table ( id BIGINT )").update().complete();
        sql.statement("INSERT INTO test_table (id) VALUE (?)").bind(random).update().complete();
        Optional<Long> id = sql.statement("SELECT id FROM test_table LIMIT 1").query()
            .complete(data -> data.next()? data.getNullableLong("id") : null);

        if (id.isPresent() && id.get() == random) {
            System.out.println("✅   Test successful for " + type + ": selected inserted " + random + ".");
        } else {
            fail(type, "querying was not successful");
        }
    }

    private static void fail (String type, String reason)
    {
        System.out.println("❌   Test failed for " + type + ": " + reason + ".");
    }
}
