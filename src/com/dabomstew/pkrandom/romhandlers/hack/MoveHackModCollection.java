package com.dabomstew.pkrandom.romhandlers.hack;

import com.dabomstew.pkrandom.constants.Gen5Constants;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteOverlay;

import java.util.List;

public class MoveHackModCollection extends BattleObjectHackModCollection<MoveHackMod> {
    public MoveHackModCollection(MoveHackMod... hackMods) {
        super(hackMods);
    }

    @Override
    public void apply(Context context) {
        super.apply(context);
    }

    @Override
    protected String getBattleObjectTypeName() {
        return "Move";
    }

    @Override
    protected String getFunctionDirectory() {
        return "Move";
    }

    @Override
    protected String getNamesTextOffsetKey() {
        return "MoveNamesTextOffset";
    }

    @Override
    protected String getDescriptionsTextOffsetKey() {
        return "MoveDescriptionsTextOffset";
    }

    @Override
    protected String getExplanationsTextOffsetKey() {
        return null;
    }

    @Override
    protected List<Table> getTables() {
        return List.of(
                new Table("MoveHandler_Protect_Start", 0x2A, 0x4C, (i) -> ((MoveHackMod) i).isProtectionMove()),
                new Table("IsEncoreFailMove", 0x04, 0x08, (i) -> ((MoveHackMod) i).isEncoreFailMove()),
                new Table("IsMeFirstFailMove", 0x04, 0x08, (i) -> ((MoveHackMod) i).isMeFirstFailMove()),
                new Table("IsDelayedHitMove", 0x04, 0x08, (i) -> ((MoveHackMod) i).isDelayedHitMove()),
                new Table("IsPressureBonusMove", 0x04, 0x08, (i) -> ((MoveHackMod) i).isPressureBonusMove()),
                new Table("IsComboMove", 0x04, 0x08, (i) -> ((MoveHackMod) i).isComboMove()),
                new Table("IsUncallableMove", 0x04, 0x08, (i) -> ((MoveHackMod) i).isUncallableMove()),
                new Table("IsSleepTalkUncallableMove", 0x14, 0x1C, (i) -> ((MoveHackMod) i).isSleepTalkUncallableMove()),
                new Table("IsAssistUncallableMove", 0x14, 0x1C, (i) -> ((MoveHackMod) i).isAssistUncallableMove()),
                new Table("IsCopycatUncallableMove", 0x14, 0x1C, (i) -> ((MoveHackMod) i).isCopycatUncallableMove()),
                new Table("IsMimicFailMove", 0x04, 0x08, (i) -> ((MoveHackMod) i).isMimicFailMove()),
                new Table("IsMetronomeUncallableMove", 0x04, 0x08, (i) -> ((MoveHackMod) i).isMetronomeUncallableMove())
        );
    }

    @Override
    protected int getMaxVanillaObjectNumber() {
        return Gen5Constants.highestMoveIndex;
    }

    @Override
    protected ParagonLiteOverlay getOverlay() {
        return battleOvl;
    }

    @Override
    protected int getAddFunctionRomAddress() {
        return battleOvl.getRomAddress("MoveEvent_AddObject");
    }

    @Override
    protected int getEffectListCompareOffset() {
        return -1;
    }

    @Override
    protected int getEffectListCountOffset() {
        return 0x70;
    }

    @Override
    protected int getEffectListNumReferenceOffset() {
        return 0x6C;
    }

    @Override
    protected int getEffectListFuncReferenceOffset() {
        return 0x74;
    }

    @Override
    protected String getEffectListLabel() {
        return "Data_MoveEffectList";
    }
}
