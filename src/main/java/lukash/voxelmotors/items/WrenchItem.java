package lukash.voxelmotors.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class WrenchItem extends Item {
	private static final int MAX_DURABILITY = 165;

	public WrenchItem(Properties properties) {
		super(properties.stacksTo(1).durability(MAX_DURABILITY));
	}

	@Override
	@SuppressWarnings({"null", "deprecation"})
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
		tooltipAdder.accept(Component.translatable("item.voxel-motors.wrench.desc").withStyle(ChatFormatting.GRAY));
		super.appendHoverText(stack, context, display, tooltipAdder, tooltipFlag);
	}

	@Override
	@SuppressWarnings("null")
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
		Player player = context.getPlayer();
		if (player != null) {
			context.getItemInHand().hurtAndBreak(1, player, context.getHand());
		}

		return InteractionResult.SUCCESS;
	}
}
