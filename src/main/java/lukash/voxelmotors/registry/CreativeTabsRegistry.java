package lukash.voxelmotors.registry;

import lukash.voxelmotors.VoxelMotors;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public final class CreativeTabsRegistry {
    private static final Identifier TAB_ID = Identifier.fromNamespaceAndPath(VoxelMotors.MOD_ID, "main");
    private static final ResourceKey<CreativeModeTab> TAB_KEY =
            ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Objects.requireNonNull(TAB_ID));

    private CreativeTabsRegistry() {
    }

    public static void registerAll() {
        Item wrench = Objects.requireNonNull(ItemsRegistry.WRENCH.get());
        CreativeModeTab tab = FabricCreativeModeTab.builder()
                .title(Component.translatable("creativeTab." + VoxelMotors.MOD_ID))
                .icon(() -> new ItemStack(wrench))
                .displayItems((context, entries) -> entries.accept(wrench))
                .build();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Objects.requireNonNull(TAB_KEY), tab);
    }
}
