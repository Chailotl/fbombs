package com.chailotl.fbombs.entity.renderer;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.entity.DynamiteStickEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

public class DynamiteStickEntityRenderer extends EntityRenderer<DynamiteStickEntity> {
    private static final Identifier TEXTURE = FBombs.getId("textures/item/dynamite_stick.png");
    private final ItemRenderer itemRenderer;

    public DynamiteStickEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public Identifier getTexture(DynamiteStickEntity entity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLight(DynamiteStickEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(DynamiteStickEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float scale = 0.8f;
        if (entity.age < 2 && this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < 12.25) return;
        matrices.push();
        matrices.scale(scale, scale, scale);
        matrices.multiply(this.dispatcher.getRotation());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        this.itemRenderer.renderItem(entity.getStack(), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV,
                matrices, vertexConsumers, entity.getWorld(), entity.getId());
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
