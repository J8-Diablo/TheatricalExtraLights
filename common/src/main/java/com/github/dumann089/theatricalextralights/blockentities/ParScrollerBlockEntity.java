package com.github.dumann089.theatricalextralights.blockentities;
import com.github.dumann089.theatricalextralights.blocks.ParScrollerBlock;
import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Arrays;
public class ParScrollerBlockEntity extends ExtraLightsLightBlockEntity {

    private static final float[][] GELS = {
            {1f, 0f, 0f},      // RED
            {0f, 1f, 0f},      // GREEN
            {0f, 0f, 1f},      // BLUE
            {1f, 1f, 0f},      // YELLOW
            {1f, 0f, 1f},      // MAGENTA
            {0f, 1f, 1f},      // CYAN
            {1f, 0.5f, 0f},    // ORANGE
            {1f, 1f, 1f}       // WHITE
    };

    public ParScrollerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.PARSCROLLER.get(), pos, state);
        setChannelCount(2);
    }
    @Override
    public Fixture getFixture() {
        return Fixtures.PARSCROLLER.get();
    }
    @Override
    public int getFocus() {
        return 255;
    }
    @Override
    public void consume(byte[] dmxValues) {
        int start = this.getChannelStart() > 0 ? this.getChannelStart() - 1 : 0;
        byte[] ourValues = Arrays.copyOfRange(dmxValues, start,
                Math.min(start + this.getChannelCount(), dmxValues.length));

        if(ourValues.length < 2){
            return;
        }
                boolean prevAdvanced = beginDmxUpdate();
        int _pi = intensity, _pr = red, _pg = green, _pb = blue, _pf = focus, _pp = pan, _pt = tilt;
        intensity = convertByteToInt(ourValues[0]);

        int scrollerValue = convertByteToInt(ourValues[1]);

        float gelPosition = (scrollerValue / 255f) * (GELS.length - 1);

        int gel1Index = (int) Math.floor(gelPosition);
        int gel2Index = (int) Math.ceil(gelPosition);
        float blendFactor = gelPosition - gel1Index;

        gel1Index = Mth.clamp(gel1Index, 0, GELS.length - 1);
        gel2Index = Mth.clamp(gel2Index, 0, GELS.length - 1);

        float[] gel1 = GELS[gel1Index];
        float[] gel2 = GELS[gel2Index];

        float r = Mth.lerp(blendFactor, gel1[0], gel2[0]);
        float g = Mth.lerp(blendFactor, gel1[1], gel2[1]);
        float b = Mth.lerp(blendFactor, gel1[2], gel2[2]);

        red = (int) (r * 255);
        green = (int) (g * 255);
        blue = (int) (b * 255);

        finishDmxUpdate(intensity != _pi || red != _pr || green != _pg || blue != _pb || focus != _pf || pan != _pp || tilt != _pt, prevAdvanced);
    }
    @Override
    public int getDeviceTypeId() {
        return 0x02;
    }
    @Override
    public String getModelName() {
        return "Par Scroller";
    }
    @Override
    public boolean isUpsideDown() {
        return getBlockState().getValue(ParScrollerBlock.HANGING) && getBlockState().getValue(ParScrollerBlock.HANG_DIRECTION) == Direction.UP;
    }
    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.PARSCROLLER.getId();
    }
    @Override
    public int getActivePersonality() {
        return 0;
    }
    public int convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }
    @Override
    public String getTranslationKey() {
        return "block.theatricalextralights.parscroller";
    }
}