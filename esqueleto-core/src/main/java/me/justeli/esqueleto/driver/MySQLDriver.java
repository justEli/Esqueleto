package me.justeli.esqueleto.driver;

/**
 * @author Eli
 * @since January 02, 2023 (creation)
 */
public final class MySQLDriver implements SqlDriver {
    @Override
    public String getClassName() {
        return "com.mysql.cj.jdbc.MysqlDataSource";
    }

    @Override
    public String getDependency() {
        return """
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>""";
    }

    @Override
    public boolean hasPropertiesSupport() {
        return false;
    }
}
