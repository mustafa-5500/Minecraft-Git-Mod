package org.almond.minecraftgit.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.almond.minecraftgit.config.ModConfig;

import java.util.Locale;

/** Chat formatting shared by all client commands. */
public final class Messages {

    private final ModConfig config;

    public Messages(ModConfig config) {
        this.config = config;
    }

    private Component prefix() {
        return Component.literal(config.prefix == null ? "" : config.prefix)
                .withStyle(ChatFormatting.DARK_AQUA);
    }

    public void send(FabricClientCommandSource source, String message) {
        source.sendFeedback(prefix().copy().append(Component.literal(message).withStyle(ChatFormatting.GRAY)));
    }

    public void success(FabricClientCommandSource source, String message) {
        source.sendFeedback(prefix().copy().append(Component.literal(message).withStyle(ChatFormatting.GREEN)));
    }

    public void warn(FabricClientCommandSource source, String message) {
        source.sendFeedback(prefix().copy().append(Component.literal(message).withStyle(ChatFormatting.YELLOW)));
    }

    public void error(FabricClientCommandSource source, String message) {
        source.sendError(prefix().copy().append(Component.literal(message).withStyle(ChatFormatting.RED)));
    }

    public void raw(FabricClientCommandSource source, String message) {
        source.sendFeedback(Component.literal(message).withStyle(ChatFormatting.GRAY));
    }

    public void format(FabricClientCommandSource source, String format, Object... args) {
        send(source, String.format(Locale.ROOT, format, args));
    }
}
