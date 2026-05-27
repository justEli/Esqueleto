package me.justeli.esqueleto.examples;

import me.justeli.esqueleto.Esqueleto;
import me.justeli.esqueleto.driver.MariaDBDriver;
import me.justeli.esqueleto.examples.binary.ExampleBinary;
import me.justeli.esqueleto.examples.statements.Insertions;
import me.justeli.esqueleto.examples.statements.Querying;
import me.justeli.esqueleto.examples.statements.TableCreations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

/**
 * @author Eli
 * @since December 28, 2022 (creation)
 */
public final class Main
{
    public static void main (String... args)
    {
        var main = new Main();
        Runtime.getRuntime().addShutdownHook(new Thread(main::exit));
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private final Esqueleto sql;
    public Esqueleto sql () { return sql; }

    private final ExampleBinary exampleBinary;
    public ExampleBinary example () { return exampleBinary; }

    public Main ()
    {
        this.exampleBinary = new ExampleBinary();
        this.sql = Esqueleto.create(config ->
        {
            config.setDebug(true);
            config.setDriver(MariaDBDriver.class);
            // CREATE DATABASE esqueleto_test;
            config.setDatabase("esqueleto_test");
            // CREATE USER 'esqueleto'@'localhost' IDENTIFIED BY 'dAQ5g61NT';
            // GRANT ALL PRIVILEGES ON esqueleto_test.* TO 'esqueleto'@'localhost';
            config.setUsername("esqueleto");
            config.setPassword("dAQ5g61NT");

            config.setAsyncService(Executors.newVirtualThreadPerTaskExecutor());
            config.registerBinaryTransformer(new ExampleBinary());
        }).start();

        new TableCreations(this);
        new Insertions(this);
        new Querying(this);
    }

    // Ran when program is terminated.
    public void exit ()
    {
        this.sql.close();
    }
}
