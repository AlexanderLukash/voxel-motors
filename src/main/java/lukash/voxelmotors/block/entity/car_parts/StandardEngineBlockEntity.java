package lukash.voxelmotors.block.entity.car_parts;

import com.mojang.serialization.MapCodec;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

import lukash.voxelmotors.VoxelMotors;
import lukash.voxelmotors.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class StandardEngineBlockEntity extends BlockEntity implements GeoBlockEntity{
    private static final RawAnimation ENGINE_IDLE = RawAnimation.begin().thenLoop("engine_idle");
    private static final RawAnimation ENGINE_RUN = RawAnimation.begin().thenLoop("engine_run");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public static BlockBehaviour.Properties blockProperties() {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(VoxelMotors.MOD_ID, "standard_engine")))
                .mapColor(MapColor.METAL)
                .strength(2.8F)
                .noOcclusion()
                .isViewBlocking((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .requiresCorrectToolForDrops();
    }

    public StandardEngineBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.STANDARD_ENGINE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<StandardEngineBlockEntity>("engine_controller", state -> {
            boolean isRunning = this.getBlockState().getOptionalValue(StandardEngineBlock.RUNNING).orElse(false);
            return isRunning
                    ? state.setAndContinue(ENGINE_RUN)
                    : state.setAndContinue(ENGINE_IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public void toggleRunning() {
        Level blockLevel = this.level;
        if (blockLevel == null) {
            return;
        }

        BlockState state = blockLevel.getBlockState(this.worldPosition);
        boolean isRunning = state.getOptionalValue(StandardEngineBlock.RUNNING).orElse(false);
        blockLevel.setBlock(
                this.worldPosition,
                state.setValue(StandardEngineBlock.RUNNING, !isRunning),
                Block.UPDATE_ALL
        );
    }

    public static class StandardEngineBlock extends BaseEntityBlock {
        public static final MapCodec<StandardEngineBlock> CODEC = simpleCodec(StandardEngineBlock::new);
        public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
        public static final BooleanProperty RUNNING = BlockStateProperties.POWERED;
        private static final VoxelShape ENGINE_SHAPE_NS = Block.box(3.75, 0, 2.5, 11.25, 9, 13.5);
        private static final VoxelShape ENGINE_SHAPE_EW = Block.box(2.5, 0, 3.75, 13.5, 9, 11.25);

        public StandardEngineBlock(BlockBehaviour.Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(RUNNING, false));
        }

        @Override
        protected MapCodec<? extends BaseEntityBlock> codec() {
            return CODEC;
        }

        @Override
        public RenderShape getRenderShape(BlockState state) {
            return RenderShape.INVISIBLE;
        }

        @Override
        public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return shapeForState(state);
        }

        @Override
        public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return shapeForState(state);
        }

        @Override
        public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return shapeForState(state);
        }

        @Override
        public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
            return Shapes.empty();
        }

        @Override
        protected boolean useShapeForLightOcclusion(BlockState state) {
            return false;
        }

        @Override
        public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
            return 1.0F;
        }

        @Override
        public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new StandardEngineBlockEntity(pos, state);
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
            return toggleFromUse(level, pos, InteractionHand.MAIN_HAND);
        }

        @Override
        protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
            return toggleFromUse(level, pos, hand);
        }

        private static InteractionResult toggleFromUse(Level level, BlockPos pos, InteractionHand hand) {
            if (hand != InteractionHand.MAIN_HAND) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide()) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof StandardEngineBlockEntity standardEngineBlockEntity) {
                    standardEngineBlockEntity.toggleRunning();
                }
            }

            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        private static VoxelShape shapeForState(BlockState state) {
            Direction facing = state.getOptionalValue(FACING).orElse(Direction.NORTH);
            if (facing == Direction.EAST || facing == Direction.WEST) {
                return ENGINE_SHAPE_EW;
            }
            return ENGINE_SHAPE_NS;
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING, RUNNING);
        }

        @Override
        public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
            Direction facing = context.getHorizontalDirection().getOpposite();
            return this.defaultBlockState()
                    .setValue(FACING, facing)
                    .setValue(RUNNING, false);
        }

        @Override
        protected BlockState rotate(BlockState state, Rotation rotation) {
            Direction facing = state.getOptionalValue(FACING).orElse(Direction.NORTH);
            Direction rotated = rotation.rotate(facing);
            return state.setValue(FACING, rotated);
        }

        @Override
        protected BlockState mirror(BlockState state, Mirror mirror) {
            Direction facing = state.getOptionalValue(FACING).orElse(Direction.NORTH);
            return state.rotate(mirror.getRotation(facing));
        }
    }
}
