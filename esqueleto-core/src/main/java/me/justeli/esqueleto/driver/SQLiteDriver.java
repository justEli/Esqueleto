package me.justeli.esqueleto.driver;

/**
 * @author Eli
 * @since January 02, 2023 (creation)
 */
public final class SQLiteDriver implements SqlDriver {
    @Override
    public String getClassName() {
        return "org.sqlite.SQLiteDataSource";
    }

    @Override
    public String getDependency() {
        return """
            <groupId>org.xerial</groupId>
            <artifactId>sqlite-jdbc</artifactId>
            <version>3.43.0.0</version>""";
    }

    @Override
    public boolean hasPropertiesSupport() {
        return false;
    }
}
