package lukash.voxelmotors.registry;

import lukash.voxelmotors.VoxelMotors;
import lukash.voxelmotors.items.WrenchItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public enum ItemsRegistry {

    WRENCH("wrench", WrenchItem::new);
    private final String pathName;
    private final Function<Item.Properties, Item> itemFactory;
    private Item item;

    ItemsRegistry(String pathName, Function<Item.Properties, Item> itemFactory) {
        this.pathName = pathName;
        this.itemFactory = itemFactory;
    }

    public static void registerAll() {
        for (ItemsRegistry value : values()) {
            value.register();
        }
    }

    private void register() {
        Identifier id = Identifier.fromNamespaceAndPath(VoxelMotors.MOD_ID, Objects.requireNonNull(pathName));
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        Item created = Objects.requireNonNull(itemFactory.apply(new Item.Properties().setId(itemKey)));
        item = Registry.register(BuiltInRegistries.ITEM, itemKey, created);
    }

    public @NotNull Item get() {
        Item cached = item;
        if (cached == null) {
            throw new IllegalStateException("Item '%s' is not registered yet".formatted(pathName));
        }
        return cached;
    }

    public String getId() {
        Item registeredItem = Objects.requireNonNull(item, "Item '%s' is not registered yet".formatted(pathName));
        return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(registeredItem)).toString();
    }
}
