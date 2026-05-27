package me.justeli.esqueleto.driver;

/**
 * <pre>
 * CREATE DATABASE esqueleto_test;
 * USE esqueleto_test;
 * CREATE LOGIN esqueleto WITH PASSWORD = 'dAQ5g61NT';
 * CREATE USER esqueleto FOR LOGIN esqueleto;
 * EXEC sp_addrolemember 'db_owner', 'esqueleto';
 * </pre>
 *
 * @author Eli
 * @since January 02, 2023 (creation)
 */
public final class MSSQLDriver implements SqlDriver {
    @Override
    public String getClassName() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDataSource";
    }

    @Override
    public String getDependency() {
        return """
            <groupId>com.microsoft.sqlserver</groupId>
            <artifactId>mssql-jdbc</artifactId>
            <version>12.4.1.jre11</version>""";
    }

    @Override
    public boolean hasPropertiesSupport() {
        return false;
    }

    @Override
    public String getPortKey() {
        return "portNumber";
    }
}
