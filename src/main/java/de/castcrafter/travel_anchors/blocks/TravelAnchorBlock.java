package de.castcrafter.travel_anchors.blocks;

import de.castcrafter.travel_anchors.TravelAnchorList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class TravelAnchorBlock extends Block implements ITileEntityProvider {

    public TravelAnchorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, @Nonnull ITooltipFlag flags) {
        tooltip.add(new TranslationTextComponent("tooltip.travel_anchors.travel_anchor_block"));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TravelAnchorTile();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull IBlockReader world) {
        return new TravelAnchorTile();
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult trace) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile == null) {
                throw new IllegalStateException("Expected a tile entity of type TravelAnchorTile but got none.");
            } else if (tile instanceof TravelAnchorTile) {
                INamedContainerProvider containerProvider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("screen.travel_anchors.travel_anchor");
                    }

                    @Override
                    public Container createMenu(int window, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity player) {
                        return new TravelAnchorContainer(window, world, pos, inventory, player);
                    }
                };
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tile.getPos());
            } else {
                throw new IllegalStateException("Expected a tile entity of type TravelAnchorTile but got " + tile.getClass() + ".");
            }
        }


        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onReplaced(state, world, pos, newState, isMoving);
        TravelAnchorList.get(world).setAnchor(pos, null);
    }
}

