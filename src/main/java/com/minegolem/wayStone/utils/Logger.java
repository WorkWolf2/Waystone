package com.minegolem.wayStone.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class Logger {

    public static void log(LogLevel level, String message) {
        if (message == null) return;

        Bukkit.getConsoleSender().sendMessage(ChatUtils.serialize(level.getPrefix() + message));
    }

    public static void log(LogLevel level, String message, Throwable throwable) {
        if (message == null) return;

        Bukkit.getConsoleSender().sendMessage(ChatUtils.serialize(level.getPrefix() + message) + throwable.getMessage());
    }

    @Getter
    public enum LogLevel {
        ERROR("<dark_gray>[<bold><red>ERROR<reset><dark_gray>] <white>"),
        WARNING("&8[<bold><gold>WARNING<reset><dark_gray>] <white>"),
        INFO("<dark_gray>[<bold><yellow>INFO<reset><dark_gray>] <white>"),
        SUCCESS("<dark_gray>[<bold><green>SUCCESS<reset><dark_gray>] <white>"),
        OUTLINE("<gray>");

        private final String prefix;

        LogLevel(String prefix) {
            this.prefix = prefix;
        }
    }
}
