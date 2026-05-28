package me.justeli.esqueleto.examples.statements;

import me.justeli.esqueleto.examples.Main;
import me.justeli.esqueleto.examples.data.Example;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Eli
 * @since December 28, 2022 (creation)
 */
public final class Querying {
    public record QueryUuidData(UUID data, String message) {}

    public Querying(Main main) {
        main.sql().statement("SELECT id, data FROM TestTable").query().queue(data -> {
            while (data.next()) {
                Main.LOGGER.info(
                    "{}: {}",
                    data.get("id"),
                    data.get("data", Example.class)
                );
            }
        });

        Optional<QueryUuidData> uuid = main.sql().statement(
            "SELECT uuid, message FROM UuidTable ORDER BY added_at DESC LIMIT 1"
        ).query().complete(data -> {
            return new QueryUuidData(
                data.get("uuid", UUID.class),
                data.get("message")
            );
        });

        Main.LOGGER.info("Latest inserted Uuid: {}", uuid.orElse(null));
    }
}
