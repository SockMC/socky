package net.sockmc.socky;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class StuffyBlock extends Block {
    // a default direction property "facing" with cardinal direction values (no up/down)
    static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    private final VoxelShape northShape;
    private final VoxelShape southShape;
    private final VoxelShape eastShape;
    private final VoxelShape westShape;

    private static VoxelShape rotateShapeCW(VoxelShape shape) {
        var box = shape.getBoundingBox();
        return Block.createCuboidShape(
                16 - box.maxZ * 16, box.minY * 16, box.minX * 16,
                16 - box.minZ * 16, box.maxY * 16, box.maxX * 16
        );
    }

    public StuffyBlock(Settings settings, VoxelShape northShape) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));

        this.northShape = northShape;
        this.eastShape = rotateShapeCW(northShape);
        this.southShape = rotateShapeCW(this.eastShape);
        this.westShape = rotateShapeCW(this.southShape);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case SOUTH -> southShape;
            case EAST -> eastShape;
            case WEST -> westShape;
            default -> northShape;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.appendProperties(builder);
    }

    // copied from WallTorchBlock
    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = this.getDefaultState();
        WorldView worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction[] directions = ctx.getPlacementDirections();

        for(Direction direction : directions) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction2 = direction.getOpposite();
                blockState = blockState.with(FACING, direction2);
                if (blockState.canPlaceAt(worldView, blockPos)) {
                    return blockState;
                }
            }
        }

        return null;
    }
}
