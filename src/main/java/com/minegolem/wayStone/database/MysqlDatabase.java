package com.minegolem.wayStone.database;

import com.minegolem.wayStone.WayStone;
import com.minegolem.wayStone.data.WaystoneData;
import com.minegolem.wayStone.settings.Settings;
import com.minegolem.wayStone.utils.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MysqlDatabase extends Database {

    private static final String DATA_POOL_NAME = "WayStoneHikariPool";
    private final String driverClass;
    private final String protocol;
    private HikariDataSource dataSource;

    public MysqlDatabase(WayStone plugin) {
        super(plugin);

        this.protocol = "mariadb";
        this.driverClass = "org.mariadb.jdbc.Driver";
    }

    @NotNull
    private Connection getConnection() throws SQLException {
        if (dataSource == null) throw new IllegalStateException("Database has not been initialized yet.");

        return dataSource.getConnection();
    }

    @Override
    public void initialize() throws IllegalStateException {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to load MariaDB driver class", e);
        }

        dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setJdbcUrl(String.format(
                "jdbc:%s://%s:%s/%s%s",
                protocol,
                Settings.database_host,
                Settings.database_port,
                Settings.database_name,
                String.join("&",
                        "?autoReconnect=true",
                        "useUnicode=true",
                        "characterEncoding=UTF-8",
                        "sslMode=trust"
                )
        ));

        dataSource.setUsername(Settings.database_user);
        dataSource.setPassword(Settings.database_password);

        dataSource.setPoolName(DATA_POOL_NAME);

        final Properties properties = new Properties();
        properties.putAll(
                Map.of("cachePrepStmts", "true",
                        "prepStmtCacheSize", "250",
                        "prepStmtCacheSqlLimit", "2048",
                        "useServerPrepStmts", "true",
                        "useLocalSessionState", "true",
                        "useLocalTransactionState", "true"
                ));
        properties.putAll(
                Map.of(
                        "rewriteBatchedStatements", "true",
                        "cacheResultSetMetadata", "true",
                        "cacheServerConfiguration", "true",
                        "elideSetAutoCommits", "true",
                        "maintainTimeStats", "false")
        );
        dataSource.setDataSourceProperties(properties);

        try (Connection connection = dataSource.getConnection()) {

            final String[] databaseSchema = this.getSchemaStatements(String.format("database/%s_schema.sql", protocol));
            try (Statement statement = connection.createStatement()) {
                for (String tableCreationStatement : databaseSchema) {
                    statement.execute(tableCreationStatement);
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Failed to create database tables. Please ensure you are running MySQL v8.0+ " +
                        "and that your connecting user account has privileges to create tables.", e);
            }
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Failed to establish a connection to the MySQL database. " +
                    "Please check the supplied database credentials in the config file", e);
        }

    }

    @Override
    public void wipeDatabase() {
        // ! NOT USED FN
    }

    @Override
    public void terminate() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public Optional<WaystoneData> getWaystoneByPlayerUuid(@NotNull UUID uuid) {
        return Optional.empty();
    }

    @Override
    public void setWaystone(@NotNull WaystoneData data) {
        String query = formatStatementTables(
                "INSERT INTO `%waystones_table%` (`player_uuid`, `name`, `world`, `x`, `y`, `z`) VALUES (?, ?, ?, ?, ?, ?)"
        );

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, data.player_uuid().toString());
            statement.setString(2, data.name());
            statement.setString(3, data.location().getWorld().getName());
            statement.setInt(4, data.location().getBlockX());
            statement.setInt(5, data.location().getBlockY());
            statement.setInt(6, data.location().getBlockZ());

            statement.executeUpdate();

        } catch (SQLException e) {
            Logger.log(Logger.LogLevel.ERROR, "Failed to insert Waystone for player UUID: " + data.player_uuid(), e);
        }
    }

    @Override
    public void updateWaystoneNameByLocation(@NotNull WaystoneData data) {
        String query = formatStatementTables(
                "UPDATE `%waystones_table%` SET `name` = ? WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?"
        );

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, data.name());
            statement.setString(2, data.location().getWorld().getName());
            statement.setInt(3, data.location().getBlockX());
            statement.setInt(4, data.location().getBlockY());
            statement.setInt(5, data.location().getBlockZ());

            statement.executeUpdate();

        } catch (SQLException e) {
            Logger.log(Logger.LogLevel.ERROR, "Failed to update Waystone name at location: " + data.location(), e);
        }
    }

    public @NotNull CompletableFuture<List<WaystoneData>> getWaystonesByPlayerUUID(@NotNull UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            List<WaystoneData> waystones = new ArrayList<>();

            String query = formatStatementTables(
                    "SELECT `name`, `world`, `x`, `y`, `z` FROM `%waystones_table%` WHERE `player_uuid` = ?"
            );

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, playerUUID.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        String worldName = resultSet.getString("world");
                        int x = resultSet.getInt("x");
                        int y = resultSet.getInt("y");
                        int z = resultSet.getInt("z");

                        World world = Bukkit.getWorld(worldName);
                        if (world == null) {
                            Logger.log(Logger.LogLevel.INFO, "World not found for Waystone: " + worldName);
                            continue;
                        }

                        Location location = new Location(world, x, y, z);
                        WaystoneData data = new WaystoneData(playerUUID, name, location);
                        waystones.add(data);
                    }
                }

            } catch (SQLException e) {
                Logger.log(Logger.LogLevel.ERROR, "Failed to fetch Waystones for player UUID: " + playerUUID, e);
            }

            return waystones;
        });
    }

    @Override
    public void deleteWaystone(@NotNull WaystoneData data) {
        String query = formatStatementTables(
                "DELETE FROM `%waystones_table%` WHERE `player_uuid` = ? AND `name` = ? AND `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?"
        );

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, data.player_uuid().toString());
            statement.setString(2, data.name());
            statement.setString(3, data.location().getWorld().getName());
            statement.setInt(4, data.location().getBlockX());
            statement.setInt(5, data.location().getBlockY());
            statement.setInt(6, data.location().getBlockZ());

            statement.executeUpdate();

        } catch (SQLException e) {
            Logger.log(Logger.LogLevel.ERROR, "Failed to delete Waystone for player UUID: " + data.player_uuid(), e);
        }
    }

}
