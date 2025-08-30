package com.minegolem.wayStone.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@UtilityClass
public class ChatUtils {
    private static final LegacyComponentSerializer COMPONENT_SERIALIZER = LegacyComponentSerializer
            .builder()
            .useUnusualXRepeatedCharacterHexFormat()
            .hexColors()
            .build();

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static Component serialize(String string) {
        if (string == null) {
            Logger.log(Logger.LogLevel.ERROR, "The string cannot be null!", new NullPointerException());
            return null;
        }

        return mm.deserialize(string);
    }

    public static String serialize(Component component) {
        if (component == null) return null;

        return COMPONENT_SERIALIZER.serialize(component);
    }
}

