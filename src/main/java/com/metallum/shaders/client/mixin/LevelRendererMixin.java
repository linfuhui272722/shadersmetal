package com.metallum.shaders.client.mixin;

import com.metallum.shaders.render.ShaderRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LevelRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks the end of {@code LevelRenderer#renderLevel} so we can run our
 * post-processing chain after the world (and Sodium, if present) has
 * finished populating the g-buffer.
 *
 * <p>Injection point is right before the method returns, after the
 * hand/item pass — we want the player's hand to also receive lighting.
 */
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderLevel", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/LevelRenderer;renderWeather(Lnet/minecraft/client/world/ClientWorld;ILnet/minecraft/util/math/Vec3d;)V",
            shift = At.Shift.AFTER
    ))
    private void metallum_shaders$afterWorldRender(MatrixStack matrices, float tickDelta,
                                                   long limitTime, boolean renderBlockOutline,
                                                   Camera camera,
                                                   org.joml.Matrix4f positionMatrix,
                                                   org.joml.Matrix4f projectionMatrix,
                                                   CallbackInfo ci) {
        // Run our deferred + post chain. No-op if disabled or Metallum missing.
        ShaderRenderer.render(camera, tickDelta);
    }
}
