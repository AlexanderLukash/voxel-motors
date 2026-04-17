package lukash.voxelmotors.client;

import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.GeoItemRenderer;
import com.google.common.base.Suppliers;

import lukash.voxelmotors.items.car_parts.StandardEngineItem;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

import lukash.voxelmotors.VoxelMotors;
import lukash.voxelmotors.block.entity.car_parts.StandardEngineBlockEntity;
import lukash.voxelmotors.registry.BlockEntityRegistry;
import lukash.voxelmotors.registry.ItemsRegistry;


public class VoxelMotorsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(VoxelMotors.MOD_ID);

    @Override
    public void onInitializeClient() {
        BlockEntityType<? extends StandardEngineBlockEntity> standardEngineType =
                (BlockEntityType<? extends StandardEngineBlockEntity>) Objects.requireNonNull(BlockEntityRegistry.STANDARD_ENGINE_BLOCK_ENTITY);

        BlockEntityRenderers.register(
                standardEngineType,
                context -> new GeoBlockRenderer<StandardEngineBlockEntity, BlockEntityRenderState>(
                        context,
                        Objects.requireNonNull(BlockEntityRegistry.STANDARD_ENGINE_BLOCK_ENTITY)
                )
        );

        StandardEngineItem standardEngineItem = (StandardEngineItem) ItemsRegistry.STANDARD_ENGINE.get();
        standardEngineItem.geoRenderProvider.setValue(new GeoRenderProvider() {
            private final Supplier<GeoItemRenderer<StandardEngineItem>> renderer =
                    Suppliers.memoize(() -> new GeoItemRenderer<>(standardEngineItem));

            @Override
            public @Nullable GeoItemRenderer<StandardEngineItem> getGeoItemRenderer() {
                return this.renderer.get();
            }
        });

        LOGGER.info("Client initialized!");
    }
}
