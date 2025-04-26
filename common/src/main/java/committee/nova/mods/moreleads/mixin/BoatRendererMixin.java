package committee.nova.mods.moreleads.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import committee.nova.mods.moreleads.api.ILeash;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/27 04:55
 * @Description:
 */
@Mixin(BoatRenderer.class)
public abstract class BoatRendererMixin extends EntityRenderer<Boat> {
    protected BoatRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "TAIL"), cancellable = true)
    public void moreleads$render(Boat boat, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (boat instanceof ILeash leash) {
            Entity entity = leash.moreLeads$getLeashHolder();
            if (entity != null) {
                this.moreLeads$renderLeash(boat, g, poseStack, multiBufferSource, entity);
            }
        }

    }

    @Unique
    private <E extends Entity> void moreLeads$renderLeash(Boat boat, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, E entity) {
        poseStack.pushPose();
            Vec3 vec3 = entity.getRopeHoldPosition(f);
            double d = (double)(Mth.lerp(f, boat.yRotO, boat.getYRot()) * (float) (Math.PI / 180.0)) + (Math.PI / 2);
            Vec3 vec32 = boat.getLeashOffset(f);
            double e = Math.cos(d) * vec32.z + Math.sin(d) * vec32.x;
            double g = Math.sin(d) * vec32.z - Math.cos(d) * vec32.x;
            double h = Mth.lerp(f, boat.xo, boat.getX()) + e;
            double i = Mth.lerp(f, boat.yo, boat.getY()) + vec32.y;
            double j = Mth.lerp(f, boat.zo, boat.getZ()) + g;
            poseStack.translate(e, vec32.y, g);
            float k = (float)(vec3.x - h);
            float l = (float)(vec3.y - i);
            float m = (float)(vec3.z - j);
            float n = 0.025F;
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.leash());
            Matrix4f matrix4f = poseStack.last().pose();
            float o = Mth.invSqrt(k * k + m * m) * 0.025F / 2.0F;
            float p = m * o;
            float q = k * o;
            BlockPos blockPos = BlockPos.containing(boat.getEyePosition(f));
            BlockPos blockPos2 = BlockPos.containing(entity.getEyePosition(f));
            int r = this.getBlockLightLevel(boat, blockPos);
            int s = this.entityRenderDispatcher.getRenderer(entity).getBlockLightLevel(entity, blockPos2);
            int t = boat.level().getBrightness(LightLayer.SKY, blockPos);
            int u = boat.level().getBrightness(LightLayer.SKY, blockPos2);

            for (int v = 0; v <= 24; v++) {
                moreLeads$addVertexPair(vertexConsumer, matrix4f, k, l, m, r, s, t, u, 0.025F, 0.025F, p, q, v, false);
            }

            for (int v = 24; v >= 0; v--) {
                moreLeads$addVertexPair(vertexConsumer, matrix4f, k, l, m, r, s, t, u, 0.025F, 0.0F, p, q, v, true);
            }
        poseStack.popPose();
    }

    @Unique
    private static void moreLeads$addVertexPair(
            VertexConsumer vertexConsumer,
            Matrix4f matrix4f,
            float f,
            float g,
            float h,
            int i,
            int j,
            int k,
            int l,
            float m,
            float n,
            float o,
            float p,
            int q,
            boolean bl
    ) {
        float r = (float)q / 24.0F;
        int s = (int)Mth.lerp(r, (float)i, (float)j);
        int t = (int)Mth.lerp(r, (float)k, (float)l);
        int u = LightTexture.pack(s, t);
        float v = q % 2 == (bl ? 1 : 0) ? 0.7F : 1.0F;
        float w = 0.5F * v;
        float x = 0.4F * v;
        float y = 0.3F * v;
        float z = f * r;
        float aa = g > 0.0F ? g * r * r : g - g * (1.0F - r) * (1.0F - r);
        float ab = h * r;
        vertexConsumer.vertex(matrix4f, z - o, aa + n, ab + p).color(w, x, y, 1.0F).uv2(u).endVertex();
        vertexConsumer.vertex(matrix4f, z + o, aa + m - n, ab - p).color(w, x, y, 1.0F).uv2(u).endVertex();
    }
}
