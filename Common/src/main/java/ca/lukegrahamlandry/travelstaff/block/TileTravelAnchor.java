package ca.lukegrahamlandry.travelstaff.block;

import ca.lukegrahamlandry.travelstaff.Constants;
import ca.lukegrahamlandry.travelstaff.platform.Services;
import ca.lukegrahamlandry.travelstaff.util.TravelAnchorList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class TileTravelAnchor extends BlockEntity {
    
    private String name = "";
    private BlockState mimic = null;

    public TileTravelAnchor(BlockPos pos, BlockState state) {
        super(Registry.BLOCK_ENTITY_TYPE.get(Constants.TRAVEL_ANCHOR_KEY), pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.putString("travel_anchor_name", this.name);
        this.writeMimic(compound);
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.name = nbt.getString("travel_anchor_name");
        this.readMimic(nbt);
        if (this.level != null) {
            TravelAnchorList.get(this.level).setAnchor(this.level, this.worldPosition, this.name, this.mimic);
        }
    }

    private void writeMimic(CompoundTag tag) {
        tag.put("mimic", NbtUtils.writeBlockState(this.mimic == null ? Constants.getTravelAnchor().defaultBlockState() : this.mimic));
    }

    public void readMimic(CompoundTag tag) {
        if (tag.contains("mimic")) {
            BlockState state = NbtUtils.readBlockState(tag.getCompound("mimic"));
            if (state == Constants.getTravelAnchor().defaultBlockState()) {
                this.mimic = null;
            } else {
                this.mimic = state;
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        if (this.level != null) {
            TravelAnchorList.get(this.level).setAnchor(this.level, this.worldPosition, name, this.mimic);
        }
    }

    public BlockState getMimic() {
        if (this.mimic == null) {
            // when you relog, the tile doesnt sync to the client but the list does so if the state is unset it might be set on the list
            TravelAnchorList.Entry entry = TravelAnchorList.get(this.level).getEntry(this.worldPosition);
            if (entry != null) this.mimic = entry.state;
        }
        return this.mimic;
    }

    public void setMimic(BlockState mimic) {
        this.mimic = mimic;
        if (this.level != null) {
            TravelAnchorList.get(this.level).setAnchor(this.level, this.worldPosition, this.name, mimic);
        }
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (!this.level.isClientSide()) Services.NETWORK.syncAnchorToClients(this);
    }

//
//    @Override
//    public void onLoad() {
//        super.onLoad();
//        if (this.level != null) {
//            TravelAnchorList.get(this.level).setAnchor(this.level, this.worldPosition, this.name, this.mimic);
//        }
//    }
}
