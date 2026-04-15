package lukash.voxelmotors.items.tools;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class WrenchItem extends Item {
	private static final int MAX_DURABILITY = 60;
	private static final float ATTACK_BONUS_DAMAGE = 3.0F;

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
		if (player == null || !player.isShiftKeyDown()) {
			return InteractionResult.PASS;
		}

		Level level = context.getLevel();
		var pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		BlockState rotatedState = getRotatedState(state, player, context.getClickedFace());

		if (rotatedState.equals(state)) {
			return InteractionResult.PASS;
		}

		if (!level.setBlockAndUpdate(pos, rotatedState)) {
			return InteractionResult.FAIL;
		}

		if (!level.isClientSide() && player != null && !player.getAbilities().instabuild) {
			context.getItemInHand().hurtAndBreak(1, player, context.getHand());
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	@SuppressWarnings({"null", "deprecation"})
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (!attacker.level().isClientSide()) {
			if (attacker instanceof Player player) {
				target.hurt(attacker.damageSources().playerAttack(player), ATTACK_BONUS_DAMAGE);
			} else {
				target.hurt(attacker.damageSources().mobAttack(attacker), ATTACK_BONUS_DAMAGE);
			}
		}

		if (attacker instanceof Player player && !player.getAbilities().instabuild) {
			stack.hurtAndBreak(1, player, player.getUsedItemHand());
		}
	}

	private static BlockState getRotatedState(BlockState state, @Nullable Player player, Direction clickedFace) {
		Direction.Axis primaryAxis = getRotationAxis(player, clickedFace);
		Direction.Axis fallbackAxis = primaryAxis == Direction.Axis.Y ? null : Direction.Axis.Y;

		for (Property<?> property : state.getProperties()) {
			if (property.getValueClass() != Direction.class) {
				continue;
			}

			@SuppressWarnings("unchecked")
			Property<Direction> directionProperty = (Property<Direction>) property;
			Direction currentDirection = state.getValue(directionProperty);

			Direction rotatedDirection = rotate90(currentDirection, primaryAxis);
			if (rotatedDirection != currentDirection && directionProperty.getPossibleValues().contains(rotatedDirection)) {
				return state.setValue(directionProperty, rotatedDirection);
			}

			if (fallbackAxis != null) {
				Direction fallbackDirection = rotate90(currentDirection, fallbackAxis);
				if (fallbackDirection != currentDirection && directionProperty.getPossibleValues().contains(fallbackDirection)) {
					return state.setValue(directionProperty, fallbackDirection);
				}
			}
		}

		return state;
	}

	private static Direction.Axis getRotationAxis(@Nullable Player player, Direction clickedFace) {
		if (clickedFace.getAxis().isHorizontal()) {
			return Direction.Axis.Y;
		}

		if (player == null) {
			return Direction.Axis.X;
		}

		Direction horizontalDirection = player.getDirection();
		return horizontalDirection.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
	}

	private static Direction rotate90(Direction direction, Direction.Axis axis) {
		return switch (axis) {
			case Y -> switch (direction) {
				case NORTH -> Direction.EAST;
				case EAST -> Direction.SOUTH;
				case SOUTH -> Direction.WEST;
				case WEST -> Direction.NORTH;
				default -> direction;
			};
			case X -> switch (direction) {
				case UP -> Direction.SOUTH;
				case SOUTH -> Direction.DOWN;
				case DOWN -> Direction.NORTH;
				case NORTH -> Direction.UP;
				default -> direction;
			};
			case Z -> switch (direction) {
				case UP -> Direction.EAST;
				case EAST -> Direction.DOWN;
				case DOWN -> Direction.WEST;
				case WEST -> Direction.UP;
				default -> direction;
			};
		};
	}
}
