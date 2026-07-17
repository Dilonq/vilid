package djh.vilid.block.custom;

import djh.vilid.block.entity.custom.BallotBoxBlockEntity;
import djh.vilid.block.entity.custom.TributeChestBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BallotBoxBlock extends BlockWithEntity {
    private final Supplier<BlockEntityType<? extends BallotBoxBlockEntity>> blockEntityTypeSupplier;

    public BallotBoxBlock(Settings settings, Supplier<BlockEntityType<? extends BallotBoxBlockEntity>> supplier) {
        super(settings);
        setDefaultState(getDefaultState().with(HorizontalFacingBlock.FACING, Direction.NORTH));
        this.blockEntityTypeSupplier = supplier;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(HorizontalFacingBlock.FACING, ctx.getHorizontalPlayerFacing()); // flipped from vanilla's .getOpposite()
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HorizontalFacingBlock.FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // Crucial! Tells Minecraft to render this block as a normal 3D model block, not an invisible entity.
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityTypeSupplier.get().instantiate(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//        if (!world.isClient) {
//            BlockEntity blockEntity = world.getBlockEntity(pos);
//            if (blockEntity instanceof NamedScreenHandlerFactory screenHandlerFactory) {
//                // Opens the standard container screen on the player's client side automatically
//                player.openHandledScreen(screenHandlerFactory);
//            }
//        }
        return ActionResult.SUCCESS;
    }

}