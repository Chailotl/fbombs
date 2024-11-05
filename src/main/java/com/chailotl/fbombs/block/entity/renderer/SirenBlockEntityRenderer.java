package com.chailotl.fbombs.block.entity.renderer;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.entity.SirenBlockEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import java.util.ArrayList;
import java.util.List;

public class SirenBlockEntityRenderer implements BlockEntityRenderer<SirenBlockEntity> {
    private final List<ModelPart> modelParts = new ArrayList<>();
    private final ModelPart base, rotator, speaker;

    public SirenBlockEntityRenderer(ModelPart root) {
        this.base = root.getChild("base");
        this.rotator = base.getChild("rotator");
        this.speaker = rotator.getChild("speaker");
        this.modelParts.addAll(List.of(base, rotator, speaker));
    }

    @Override
    public void render(SirenBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity == null || blockEntity.getWorld() == null) return;
        this.rotator.yaw = (blockEntity.getTick() * blockEntity.getNormalizedRedstoneStrength() % 360) * (float) (Math.PI / 180.0);
        matrices.push();
        matrices.translate(0.5, 1.5, 0.5);
        float scale = 1.2f;
        matrices.scale(scale, 1.0f, scale);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        this.base.render(matrices, vertexConsumers.getBuffer(getRenderLayer(blockEntity)), light, overlay);
        matrices.pop();
    }

    private RenderLayer getRenderLayer(SirenBlockEntity blockEntity) {
        String texturePath;
        if (isLit(blockEntity)) texturePath = "siren_active";
        else texturePath = "siren";
        return RenderLayer.getEntityCutout(FBombs.getId("textures/block/" + texturePath + ".png"));
    }

    private boolean isLit(SirenBlockEntity blockEntity) {
        if (blockEntity == null || blockEntity.getWorld() == null) return false;
        return SirenBlockEntity.getStrengthFromStructure(blockEntity.getWorld(), blockEntity.getPos()) > 0;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create(), ModelTransform.pivot(8.0F, 24.0F, -8.0F));
        ModelPartData rotator = base.addChild("rotator", ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-4.0F, -6.5F, -4.0F, 8.0F, 14.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-8.0F, -8.5F, 8.0F)
        );
        ModelPartData speaker = rotator.addChild("speaker", ModelPartBuilder.create()
                        .uv(32, 44).mirrored().cuboid(6.0F, -1.5F, -4.0F, 3.0F, 10.0F, 10.0F, new Dilation(0.0F)).mirrored(false)
                        .uv(0, 48).mirrored().cuboid(4.0F, -0.5F, -3.0F, 2.0F, 8.0F, 8.0F, new Dilation(0.0F)).mirrored(false)
                        .uv(0, 48).cuboid(-6.0F, -6.5F, -3.0F, 2.0F, 8.0F, 8.0F, new Dilation(0.0F))
                        .uv(32, 44).cuboid(-9.0F, -7.5F, -4.0F, 3.0F, 10.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, -1.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
}
