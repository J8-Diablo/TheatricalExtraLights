package com.github.dumann089.theatricalextralights.client.model;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ConfettiCannonModel extends EntityModel<Entity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(TheatricalExtraLights.MOD_ID, "confetti_cannon"), "main");

    private final ModelPart hexadecagonRing;
    private final ModelPart hexadecagonBarrel;
    private final ModelPart bbMain;

    public ConfettiCannonModel(ModelPart root) {
        this.hexadecagonRing = root.getChild("hexadecagon_ring");
        this.hexadecagonBarrel = root.getChild("hexadecagon_barrel");
        this.bbMain = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition hexadecagonRing = partdefinition.addOrReplaceChild("hexadecagon_ring",
                CubeListBuilder.create()
                        .texOffs(26, 44).addBox(-0.5967F, -3.0F, -6.0F, 1.1935F, 6.0F, 12.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 26).addBox(-3.0F, -0.5967F, -6.0F, 6.0F, 1.1935F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 17.0F, 0.0F));

        hexadecagonRing.addOrReplaceChild("hexadecagon_r1",
                CubeListBuilder.create()
                        .texOffs(36, 13).addBox(-3.0F, -0.5967F, -6.0F, 6.0F, 1.1935F, 12.0F, new CubeDeformation(0.0F))
                        .texOffs(52, 44).addBox(-0.5967F, -3.0F, -6.0F, 1.1935F, 6.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        hexadecagonRing.addOrReplaceChild("hexadecagon_r2",
                CubeListBuilder.create()
                        .texOffs(0, 13).addBox(-3.0F, -0.5967F, -6.0F, 6.0F, 1.1935F, 12.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 39).addBox(-0.5967F, -3.0F, -6.0F, 1.1935F, 6.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        hexadecagonRing.addOrReplaceChild("hexadecagon_r3",
                CubeListBuilder.create().texOffs(0, 57).addBox(-0.5967F, -3.0F, -6.0F, 1.1935F, 6.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        hexadecagonRing.addOrReplaceChild("hexadecagon_r4",
                CubeListBuilder.create().texOffs(36, 26).addBox(-0.5967F, -3.0F, -6.0F, 1.1935F, 6.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition hexadecagonBarrel = partdefinition.addOrReplaceChild("hexadecagon_barrel",
                CubeListBuilder.create()
                        .texOffs(64, 62).addBox(-0.1989F, -5.0F, -1.0F, 0.3978F, 10.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(34, 69).addBox(-1.0F, -5.0F, -0.1989F, 2.0F, 10.0F, 0.3978F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.0F, 13.0F, 0.0F, 0.0F, 0.0F, -0.6545F));

        hexadecagonBarrel.addOrReplaceChild("hexadecagon_r5",
                CubeListBuilder.create()
                        .texOffs(38, 69).addBox(-1.0F, -5.0F, -0.1989F, 2.0F, 10.0F, 0.3978F, new CubeDeformation(0.0F))
                        .texOffs(68, 62).addBox(-0.1989F, -5.0F, -1.0F, 0.3978F, 10.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.3927F, 0.0F));

        hexadecagonBarrel.addOrReplaceChild("hexadecagon_r6",
                CubeListBuilder.create()
                        .texOffs(30, 69).addBox(-1.0F, -5.0F, -0.1989F, 2.0F, 10.0F, 0.3978F, new CubeDeformation(0.0F))
                        .texOffs(60, 62).addBox(-0.1989F, -5.0F, -1.0F, 0.3978F, 10.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.3927F, 0.0F));

        hexadecagonBarrel.addOrReplaceChild("hexadecagon_r7",
                CubeListBuilder.create().texOffs(26, 69).addBox(-0.1989F, -5.0F, -1.0F, 0.3978F, 10.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        hexadecagonBarrel.addOrReplaceChild("hexadecagon_r8",
                CubeListBuilder.create().texOffs(56, 62).addBox(-0.1989F, -5.0F, -1.0F, 0.3978F, 10.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition bbMain = partdefinition.addOrReplaceChild("bb_main",
                CubeListBuilder.create()
                        .texOffs(48, 0).addBox(-7.0F, -7.0F, -7.0F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(26, 62).addBox(-7.0F, -7.0F, 6.0F, 14.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        bbMain.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(62, 33).addBox(-6.0F, -7.0F, -6.0F, 12.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        bbMain.addOrReplaceChild("cube_r2",
                CubeListBuilder.create().texOffs(62, 26).addBox(-6.0F, -7.0F, -6.0F, 12.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(12.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        hexadecagonRing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        hexadecagonBarrel.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        bbMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
