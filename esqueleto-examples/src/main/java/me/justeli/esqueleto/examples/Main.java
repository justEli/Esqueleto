package me.justeli.esqueleto.examples;

import me.justeli.esqueleto.Esqueleto;
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
public final class Main {
    static void main(String... args) {
        var main = new Main();
        Runtime.getRuntime().addShutdownHook(new Thread(main::shutdown));
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public Main() {
        this.exampleBinary = new ExampleBinary();
        this.sql = Esqueleto.start(config -> {
            config.setDebug(true);
            // CREATE DATABASE esqueleto_test;
            config.setJdbcUrl("jdbc:mariadb://localhost:3306/esqueleto_test");
            // CREATE USER 'esqueleto'@'localhost' IDENTIFIED BY 'dAQ5g61NT';
            // GRANT ALL PRIVILEGES ON esqueleto_test.* TO 'esqueleto'@'localhost';
            config.setUsername("esqueleto");
            config.setPassword("dAQ5g61NT");

            config.setAsyncService(Executors.newVirtualThreadPerTaskExecutor());
            config.registerBinaryTransformer(new ExampleBinary());
        });

        new TableCreations(this);
        new Insertions(this);
        new Querying(this);
    }

    // ran when program is terminated
    public void shutdown() {
        this.sql.close();
    }

    private final Esqueleto sql;

    public Esqueleto sql() {
        return sql;
    }

    private final ExampleBinary exampleBinary;

    public ExampleBinary example() {
        return exampleBinary;
    }
}
