package org.almond.minecraftgit.client.selection;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.selection.SelectionException;

import java.util.Optional;

/**
 * Turns wand clicks into selection corners. Returning {@link InteractionResult#FAIL}
 * on the client cancels further processing without sending a break/use packet,
 * so a wand click never edits the world on a remote server.
 */
public final class WandEvents {

    private WandEvents() {
    }

    public static void register(GitServices services) {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!(world instanceof ClientLevel) || hand != InteractionHand.MAIN_HAND) {
                return InteractionResult.PASS;
            }
            Minecraft client = Minecraft.getInstance();
            if (player != client.player || !WandItem.isWand(player.getMainHandItem(), services.config(), player)) {
                return InteractionResult.PASS;
            }
            handleCorner(services, player, new BlockPos(pos.getX(), pos.getY(), pos.getZ()), true);
            return InteractionResult.FAIL;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!(world instanceof ClientLevel) || hand != InteractionHand.MAIN_HAND || hitResult == null) {
                return InteractionResult.PASS;
            }
            Minecraft client = Minecraft.getInstance();
            if (player != client.player || !WandItem.isWand(player.getMainHandItem(), services.config(), player)) {
                return InteractionResult.PASS;
            }
            var pos = hitResult.getBlockPos();
            handleCorner(services, player, new BlockPos(pos.getX(), pos.getY(), pos.getZ()), false);
            return InteractionResult.FAIL;
        });
    }

    public static void handleCorner(GitServices services, Player player, BlockPos pos, boolean first) {
        WorldContext world;
        try {
            world = WorldContext.current(Minecraft.getInstance());
        } catch (RuntimeException e) {
            return;
        }
        SelectionManager manager = services.selectionManager();
        Optional<Cuboid> completed = manager.setCorner(player, first, pos);
        if (completed.isEmpty()) {
            actionBar(player, (first ? "Corner A" : "Corner B") + " set at " + pos.x() + ", " + pos.y() + ", " + pos.z());
            return;
        }
        completeCuboid(services, world, player, completed.get());
    }

    private static void completeCuboid(GitServices services, WorldContext world, Player player, Cuboid cuboid) {
        SelectionManager manager = services.selectionManager();
        String active = manager.active(world, player);
        if (active == null) {
            manager.setPending(player, cuboid);
            player.sendSystemMessage(Component.literal("Cuboid ready (" + cuboid.dimensions()
                    + "). Create or select a selection, then run /git selection add.")
                    .withStyle(ChatFormatting.YELLOW));
            return;
        }
        try {
            Selection selection = services.selections().addCuboid(world.id(), player.getUUID(), active, cuboid);
            manager.setActive(world, player, selection.name());
            player.sendSystemMessage(Component.literal("Added " + cuboid.dimensions() + " to '"
                    + selection.name() + "' (" + selection.cuboidCount() + " cuboid(s), origin "
                    + selection.origin() + ").").withStyle(ChatFormatting.GREEN));
        } catch (SelectionException e) {
            manager.setPending(player, cuboid);
            player.sendSystemMessage(Component.literal(e.getMessage() + " Cuboid kept pending.")
                    .withStyle(ChatFormatting.RED));
        }
    }

    static void actionBar(Player player, String message) {
        player.displayClientMessage(Component.literal(message).withStyle(ChatFormatting.AQUA), true);
    }

    /** Level type is kept in the signatures above to match Fabric's callback shapes. */
    @SuppressWarnings("unused")
    private static boolean isClientLevel(Level level) {
        return level instanceof ClientLevel;
    }
}
