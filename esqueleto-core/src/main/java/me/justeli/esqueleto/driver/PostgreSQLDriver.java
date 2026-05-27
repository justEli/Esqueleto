package me.justeli.esqueleto.driver;

/**
 * @author Eli
 * @since January 02, 2023 (creation)
 */
public final class PostgreSQLDriver implements SqlDriver {
    @Override
    public String getClassName() {
        return "org.postgresql.ds.PGSimpleDataSource";
    }

    @Override
    public String getDependency() {
        return """
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.6.0</version>""";
    }

    @Override
    public boolean hasPropertiesSupport() {
        return false;
    }
}
