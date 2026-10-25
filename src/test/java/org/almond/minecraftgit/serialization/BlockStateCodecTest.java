package org.almond.minecraftgit.serialization;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockStateCodecTest {

    @BeforeAll
    static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void roundTripsStateWithSortedProperties() {
        BlockState state = Blocks.OAK_STAIRS.defaultBlockState()
                .setValue(StairBlock.FACING, Direction.NORTH)
                .setValue(StairBlock.HALF, Half.TOP);
        String encoded = BlockStateCodec.encode(state);
        assertTrue(encoded.startsWith("minecraft:oak_stairs["));
        assertTrue(encoded.contains("facing=north"));
        assertTrue(encoded.contains("half=top"));
        assertEquals(state, BlockStateCodec.decode(encoded));
    }

    @Test
    void unknownBlockDecodesToAir() {
        assertTrue(BlockStateCodec.decode("minecraft:definitely_not_a_block").isAir());
        assertFalse(BlockStateCodec.isKnown("minecraft:definitely_not_a_block"));
    }

    @Test
    void airAndKnownBlocksAreRecognized() {
        assertTrue(BlockStateCodec.isAir("minecraft:air"));
        assertTrue(BlockStateCodec.isKnown("minecraft:stone"));
        assertFalse(BlockStateCodec.isAir("minecraft:stone"));
    }
}
