package com.dabomstew.pkrandom.romhandlers.hack;

import com.dabomstew.pkrandom.constants.Gen5Constants;
import com.dabomstew.pkrandom.romhandlers.OverlayId;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteOverlay;

import java.util.List;

public class MoveHackModCollection extends BattleObjectHackModCollection<MoveHackMod> {
    public MoveHackModCollection(MoveHackMod... hackMods) {
        super(hackMods);
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

    @Override
    public void apply(Context context) {
        super.apply(context);
        
        for (MoveHackMod hackMod : hackMods) {
            int number = hackMod.number;

            String auxiliaryAnimationLabel = hackMod.getAuxiliaryAnimationLabel();
            int[] auxiliaryAnimationSpaFiles = hackMod.getAuxiliaryAnimationSpaFiles();
            
            if (auxiliaryAnimationLabel == null)
                continue;
            
            String labelLower = auxiliaryAnimationLabel.toLowerCase();

            String formattedMoveName = hackMod.getName(context).toLowerCase().replace(' ', '_');
            for (int spaFileNumber : auxiliaryAnimationSpaFiles) {
                byte[] spaFileData = readBytes(String.format("moveanims/spa/%03d_%s_%s_%03d.spa", number, formattedMoveName, labelLower, spaFileNumber));
                while (moveAnimationsNarc.files.size() <= spaFileNumber) {
                    moveAnimationsNarc.files.add(new byte[16]);
                }

                byte[] existingSpaFile = moveAnimationsNarc.files.get(spaFileNumber);
                for (byte b : existingSpaFile) {
                    if (b != 0) // Should be all blank
                        throw new RuntimeException(String.format("Attempted to overwrite existing SPA file #%03d for move #%03d %s",
                                spaFileNumber, number, names.get(number)));
                }

                moveAnimationsNarc.files.set(spaFileNumber, spaFileData);
            }
            byte[] script = readBytes(String.format("moveanims/scripts/%03d_%s_%s.bin", number, formattedMoveName, labelLower));

            String auxiliaryAnimationName = String.format("%s_%s", formattedMoveName, labelLower);
            if (!auxiliaryAnimationScriptIndices.containsKey(auxiliaryAnimationName))
                throw new RuntimeException(String.format("Could not find auxiliary animation with id \"%s\"", auxiliaryAnimationName));
            int auxiliaryAnimationScriptIndex = auxiliaryAnimationScriptIndices.get(auxiliaryAnimationName);

            while (battleAnimationScriptsNarc.files.size() <= auxiliaryAnimationScriptIndex)
                battleAnimationScriptsNarc.files.add(getDefaultAnimationScript());
            battleAnimationScriptsNarc.files.set(auxiliaryAnimationScriptIndex, script);

            formattedMoveName = names.get(number).replaceAll("[ -]", "");
            context.armParser().addGlobalValue(String.format("BTLANM_%s_%s", formattedMoveName, auxiliaryAnimationLabel), auxiliaryAnimationScriptIndex);
        }
    }
}
