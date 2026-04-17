package lukash.voxelmotors.registry;

import lukash.voxelmotors.VoxelMotors;
import lukash.voxelmotors.block.entity.car_parts.StandardEngineBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Objects;

public final class BlockEntityRegistry {
    public static final BlockEntityType<StandardEngineBlockEntity> STANDARD_ENGINE_BLOCK_ENTITY =
            register("standard_engine", StandardEngineBlockEntity::new, BlockRegistry.STANDARD_ENGINE);

    private BlockEntityRegistry() {
    }

    public static void init() {
    }

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(VoxelMotors.MOD_ID, Objects.requireNonNull(name));
        @SuppressWarnings("unchecked")
        Registry<BlockEntityType<?>> registry =
                (Registry<BlockEntityType<?>>) (Registry<?>) BuiltInRegistries.BLOCK_ENTITY_TYPE;

        return Registry.register(
                registry,
                id,
                FabricBlockEntityTypeBuilder.<T>create(Objects.requireNonNull(entityFactory), Objects.requireNonNull(blocks)).build()
        );
    }
}
