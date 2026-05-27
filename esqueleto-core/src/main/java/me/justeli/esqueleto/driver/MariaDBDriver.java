package me.justeli.esqueleto.driver;

/**
 * <pre>
 * CREATE DATABASE esqueleto_test;
 * CREATE USER 'esqueleto'@'localhost' IDENTIFIED BY 'dAQ5g61NT';
 * GRANT ALL PRIVILEGES ON esqueleto_test.* TO 'esqueleto'@'localhost';
 * </pre>
 *
 * @author Eli
 * @since December 29, 2022 (creation)
 */
public class MariaDBDriver implements SqlDriver {
    @Override
    public String getClassName() {
        return "org.mariadb.jdbc.MariaDbDataSource";
    }

    @Override
    public String getDependency() {
        return """
            <groupId>org.mariadb.jdbc</groupId>
            <artifactId>mariadb-java-client</artifactId>
            <version>2.7.7</version>""";
    }

    @Override
    public boolean hasPropertiesSupport() {
        return true;
    }
}
