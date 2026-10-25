package com.example.examplemod.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.example.examplemod.ExampleMod;

/**
 * Minimal mixin example.
 *
 * <p>Mixins let you change Minecraft's bytecode without editing the game.
 * This one logs whenever the integrated or dedicated server loads a level.</p>
 */
@Mixin(MinecraftServer.class)
public class ExampleMixin {
    @Inject(method = "loadLevel", at = @At("HEAD"))
    private void examplemod$onLoadLevel(CallbackInfo info) {
        ExampleMod.LOGGER.info("MinecraftServer is loading a level (mixin example)");
    }
}
