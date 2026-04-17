package lukash.voxelmotors.items.car_parts;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.SingletonGeoAnimatable;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import lukash.voxelmotors.registry.BlockRegistry;
import org.apache.commons.lang3.mutable.MutableObject;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

public class StandardEngineItem extends BlockItem implements GeoItem {
    public final MutableObject<Object> geoRenderProvider = new MutableObject<>();
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public StandardEngineItem(Item.Properties properties) {
        super(BlockRegistry.STANDARD_ENGINE, properties.stacksTo(1));
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void createGeoRenderer(Consumer consumer) {
        Object provider = this.geoRenderProvider.get();
        if (provider != null) {
            consumer.accept(provider);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
