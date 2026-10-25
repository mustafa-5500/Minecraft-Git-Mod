package com.example.examplemod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import com.example.examplemod.item.ModItems;

/**
 * A small Brigadier command example: {@code /examplemod give}.
 */
public final class ExampleCommand {
    private ExampleCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("examplemod")
                .then(Commands.literal("give")
                        .executes(ExampleCommand::giveExampleItem)));
    }

    private static int giveExampleItem(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = new ItemStack(ModItems.EXAMPLE_ITEM);

        // Add to the inventory, or drop the item at the player's feet if it is full.
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }

        context.getSource().sendSuccess(
                () -> Component.translatable("command.examplemod.give.success"),
                false);

        return 1;
    }
}
