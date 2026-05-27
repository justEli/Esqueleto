package me.justeli.esqueleto;

import com.zaxxer.hikari.HikariConfig;
import me.justeli.esqueleto.binary.IP4Binary;
import me.justeli.esqueleto.binary.Binary;
import me.justeli.esqueleto.binary.UuidBinary;
import me.justeli.esqueleto.driver.SqlDriver;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Eli
 * @since April 13, 2021 (creation); December 26, 2022 (rewrite)
 */
public final class SqlConfig extends HikariConfig {
    private ExecutorService queueService = Executors.newSingleThreadExecutor();
    private ExecutorService asyncService = ForkJoinPool.commonPool();

    SqlConfig() {
        super.setMaximumPoolSize(10);
        super.setMinimumIdle(10);
        super.setMaxLifetime(1800000);
        super.setKeepaliveTime(0);
        super.setConnectionTimeout(5000);
        super.setInitializationFailTimeout(-1);

        registerBinaryTransformer(new IP4Binary());
        registerBinaryTransformer(new UuidBinary());
    }

    public void setQueueService(ExecutorService service) {
        queueService.shutdown();
        this.queueService = service;
    }

    public ExecutorService getQueueService() {
        return queueService;
    }

    public void setAsyncService(ExecutorService service) {
        asyncService.shutdown();
        this.asyncService = service;
    }

    public ExecutorService getAsyncService() {
        return asyncService;
    }

    public void setHost(String host) {
        super.addDataSourceProperty("serverName", host);
    }

    public void setPort(int port) {
        super.addDataSourceProperty("port", String.valueOf(port));
    }

    public void setDatabase(String database) {
        super.addDataSourceProperty("databaseName", database);
    }

    public void setDriver(SqlDriver driver) {
        super.setDataSourceClassName(driver.getClassName());
    }

    public <T extends SqlDriver> void setDriver(Class<T> type) {
        try {
            setDriver(type.getConstructor().newInstance());
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            throw new RuntimeException(exception);
        }
    }

    private final Map<Class<?>, Binary<?>> transformers = new HashMap<>();

    public <T> void registerBinaryTransformer(Binary<T> binary) {
        transformers.put(binary.type(), binary);
    }

    public <T> Binary<T> getBinaryTransformer(Class<T> type) {
        return (Binary<T>) transformers.get(type);
    }

    private boolean debug;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }
}
