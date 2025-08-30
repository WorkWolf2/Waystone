package com.minegolem.wayStone.database;

import com.minegolem.wayStone.WayStone;
import com.minegolem.wayStone.data.WaystoneData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public abstract class Database {

    protected final WayStone plugin;

    public abstract void initialize() throws IllegalStateException;

    public abstract void wipeDatabase();

    public abstract void terminate();

    public abstract Optional<WaystoneData> getWaystoneByPlayerUuid(@NotNull UUID uuid);

    public abstract void setWaystone(@NotNull WaystoneData data);

    public abstract void updateWaystoneNameByLocation(@NotNull WaystoneData data);

    public @NotNull abstract CompletableFuture<List<WaystoneData>> getWaystonesByPlayerUUID(@NotNull UUID playerUUID);

    public abstract void deleteWaystone(@NotNull WaystoneData data);

    protected final String[] getSchemaStatements(@NotNull String schemaFileName) throws IOException {
        InputStream resourceStream = Objects.requireNonNull(plugin.getResource(schemaFileName),
                "Schema file not found: " + schemaFileName);

        String schemaContent = formatStatementTables(new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8));

        resourceStream.close();

        return Arrays.stream(schemaContent.split(";"))
                .map(String::trim)
                .filter(statement -> !statement.isEmpty())
                .toArray(String[]::new);
    }

    protected final String formatStatementTables(@NotNull String sql) {
        return sql.replaceAll("%waystones_table%", TableName.WAYSTONES.getDefaultName());
    }

    @Getter
    public enum TableName {
        WAYSTONES("waystones");

        private final String defaultName;

        TableName(@NotNull String defaultName) {
            this.defaultName = defaultName;
        }

        @NotNull
        private Map.Entry<String, String> toEntry() {
            return Map.entry(name(), defaultName);
        }

        @SuppressWarnings("unchecked")
        @NotNull
        public static Map<String, String> getDefaults() {
            return Map.ofEntries(Arrays.stream(values())
                    .map(TableName::toEntry)
                    .toArray(Map.Entry[]::new));
        }
    }
}
