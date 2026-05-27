package me.justeli.esqueleto.examples.statements;

import me.justeli.esqueleto.examples.Main;
import me.justeli.esqueleto.examples.data.Example;

import java.util.SplittableRandom;
import java.util.UUID;

/**
 * @author Eli
 * @since December 28, 2022 (creation)
 */
public final class Insertions
{
    private static final SplittableRandom RANDOM = new SplittableRandom();

    public Insertions (Main main)
    {
        for (int i = 0; i < 3; i++)
        {
            var data = new Example(
                RANDOM.nextInt(),
                Integer.toHexString(RANDOM.nextInt())
            );

            main.sql().statement("INSERT INTO TestTable (data) VALUES (?)").bind(data).update().queue();
        }

        int inserted = main.sql().statement(
            "INSERT INTO UuidTable (uuid, message) VALUES (?, ?)"
        ).bind(
            UUID.randomUUID(),
            Integer.toHexString(RANDOM.nextInt())
        ).update().complete();

        Main.LOGGER.info("Inserted rows: " + inserted);
    }
}
