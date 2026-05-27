package me.justeli.esqueleto.driver;

/**
 * @author Eli
 * @since December 29, 2022 (creation)
 */
public interface SqlDriver {
    String getClassName();

    String getDependency();

    boolean hasPropertiesSupport();

    default String getPortKey() {
        return "port";
    }
}
