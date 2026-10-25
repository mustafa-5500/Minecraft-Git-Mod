package org.almond.minecraftgit.client.selection;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.almond.minecraftgit.config.ModConfig;

/**
 * The wand is a vanilla item carrying a custom-data tag. A client-only mod
 * cannot register a server item, so the item is created on the integrated
 * server when one exists. In multiplayer the same behaviour is available by
 * sneaking with the configured item, or by using the pos1/pos2 commands.
 */
public final class WandItem {

    public static final String TAG = "minecraftgit_wand";

    private WandItem() {
    }

    public static Item configuredItem(ModConfig config) {
        ResourceLocation id = ResourceLocation.tryParse(config.wandItem);
        Item item = id == null ? null : BuiltInRegistries.ITEM.get(id);
        return item == null || item == Items.AIR ? Items.WOODEN_SHOVEL : item;
    }

    public static ItemStack create(ModConfig config) {
        ItemStack stack = new ItemStack(configuredItem(config));
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Git Wand").withStyle(ChatFormatting.AQUA));
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(TAG, true);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    public static boolean isWand(ItemStack stack, ModConfig config, Player player) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        CustomData data = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (data != null && data.copyTag().getBoolean(TAG)) {
            return true;
        }
        // Multiplayer fallback: holding the configured item while sneaking.
        return stack.getItem() == configuredItem(config) && player.isShiftKeyDown();
    }

    /**
     * Give the wand through the integrated server's player inventory. Returns
     * false in multiplayer, where the server would reject a client-only item and
     * desynchronize the inventory.
     */
    public static boolean give(Player player, ModConfig config, Minecraft client) {
        MinecraftServer server = client.getSingleplayerServer();
        if (server == null) {
            return false;
        }
        ServerPlayer serverPlayer = server.getPlayerList().getPlayer(player.getUUID());
        if (serverPlayer == null) {
            return false;
        }
        ItemStack stack = create(config);
        server.execute(() -> {
            if (!serverPlayer.getInventory().add(stack)) {
                serverPlayer.drop(stack, false);
            }
        });
        return true;
    }
}
