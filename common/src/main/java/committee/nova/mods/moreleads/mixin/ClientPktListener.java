package committee.nova.mods.moreleads.mixin;

import committee.nova.mods.moreleads.api.ILeash;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/27 05:20
 * @Description:
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPktListener {
    @Inject(method = "handleEntityLinkPacket", at = @At(value = "TAIL"))
    public void moreleads$handleEntityLinkPacket(ClientboundSetEntityLinkPacket clientboundSetEntityLinkPacket, CallbackInfo ci) {
        Entity entity = ((ClientPacketListener) (Object) this).getLevel().getEntity(clientboundSetEntityLinkPacket.getSourceId());
        if (entity instanceof ILeash) {
            ILeash leash = (ILeash) entity;
            leash.moreLeads$setDelayedLeashHolderId(clientboundSetEntityLinkPacket.getDestId());
        }
    }
}
