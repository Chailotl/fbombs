package com.chailotl.fbombs.entity.renderer;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.entity.DynamiteEntity;
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

public class DynamiteEntityRenderer extends EntityRenderer<DynamiteEntity> {
    private static final Identifier TEXTURE = FBombs.getId("textures/item/dynamite.png");
    private final ItemRenderer itemRenderer;

    public DynamiteEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public Identifier getTexture(DynamiteEntity entity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLight(DynamiteEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(DynamiteEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.age < 2 && this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < 12.25) return;
        matrices.push();
        matrices.multiply(this.dispatcher.getRotation());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        this.itemRenderer.renderItem(entity.getStack(), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV,
                matrices, vertexConsumers, entity.getWorld(), entity.getId());
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}