package me.justeli.esqueleto;

import java.sql.ResultSet;

/**
 * @author Eli
 * @since December 28, 2022 (creation)
 */
public record ExecutionData(ResultSet resultSet, int rows) {}
