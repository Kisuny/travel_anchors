package ca.lukegrahamlandry.travelstaff.block;

import ca.lukegrahamlandry.travelstaff.platform.Services;
import ca.lukegrahamlandry.travelstaff.util.TeleportHandler;
import ca.lukegrahamlandry.travelstaff.util.TravelAnchorList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class RenderTravelAnchor implements BlockEntityRenderer<TileTravelAnchor> {
    @Override
    public void render(@Nonnull TileTravelAnchor blockEntity, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || (!TeleportHandler.canBlockTeleport(player) && !TeleportHandler.canItemTeleport(player, InteractionHand.MAIN_HAND) && !TeleportHandler.canItemTeleport(player, InteractionHand.OFF_HAND)) || (blockEntity.getLevel() != null && TravelAnchorList.get(blockEntity.getLevel()).getAnchor(blockEntity.getBlockPos()) == null)) {
            Services.PLATFORM.renderAnchor(matrixStack, buffer, blockEntity.getMimic(), combinedLight, false, false, 0);
        }
    }
}
