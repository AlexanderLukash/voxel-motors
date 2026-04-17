package lukash.voxelmotors.registry;

import lukash.voxelmotors.VoxelMotors;
import lukash.voxelmotors.block.entity.car_parts.StandardEngineBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class BlockRegistry {
    public static final Block STANDARD_ENGINE = register(
            "standard_engine",
            new StandardEngineBlockEntity.StandardEngineBlock(StandardEngineBlockEntity.blockProperties())
    );

    private BlockRegistry() {
    }

    public static void init() {
    }

    private static Block register(String name, Block block) {
        Identifier id = Identifier.fromNamespaceAndPath(VoxelMotors.MOD_ID, Objects.requireNonNull(name));
        return Registry.register(BuiltInRegistries.BLOCK, id, Objects.requireNonNull(block));
    }
}
