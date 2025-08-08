package com.dabomstew.pkrandom.romhandlers.hack;

import com.dabomstew.pkrandom.constants.Gen5Constants;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.romhandlers.OverlayId;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteOverlay;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveHackModCollection extends BattleObjectHackModCollection<MoveHackMod> {
    private final static int highMoveOffset = 116;
    private final static int battleAnimationScriptsOffset = 561;
    
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
        int maxMoveId = 0;
        for (Move mv : context.moves) {
            if (mv.number > maxMoveId)
                maxMoveId = mv.number;
        }

        int maxMoveScriptIndex = maxMoveId + highMoveOffset - battleAnimationScriptsOffset;

        // Move Animations
        for (MoveHackMod hackMod : hackMods) {
            if (!hackMod.addAnimation())
                continue;
            
            var spaFiles = hackMod.getAnimationSpaFiles();
            var specification = hackMod.getAnimationSpecification();
            setMoveAnimations(context, hackMod.number, spaFiles, specification);
        }

        // Auxiliary
        for (MoveHackMod hackMod : hackMods) {
            String auxiliaryAnimationLabel = hackMod.getAuxiliaryAnimationLabel();
            if (auxiliaryAnimationLabel == null)
                continue;
            
            int[] auxiliaryAnimationSpaFiles = hackMod.getAuxiliaryAnimationSpaFiles();
            setMoveAuxiliaryAnimation(context, hackMod.number, auxiliaryAnimationLabel, maxMoveScriptIndex++, auxiliaryAnimationSpaFiles);
        }

        applyInternal(context);
    }
    
    private void setMoveAnimations(Context context, int moveNumber, int[] spaFiles, String specification) {
        String formattedMoveName = context.moves.get(moveNumber).name.toLowerCase().replace(' ', '_');
        for (int spaFileNumber : spaFiles) {
            byte[] spaFileData = readBytes(String.format("moveanims/spa/%03d_%s_%03d.spa", moveNumber, formattedMoveName, spaFileNumber));
            while (context.moveAnimationsNarc.files.size() <= spaFileNumber) {
                context.moveAnimationsNarc.files.add(new byte[16]);
            }

            if (spaFileNumber >= context.originalMoveAnimationsNarcCount) {
                byte[] existingSpaFile = context.moveAnimationsNarc.files.get(spaFileNumber);
                for (byte b : existingSpaFile) {
                    if (b != 0) // Should be all blank
                        throw new RuntimeException(String.format("Attempted to overwrite existing SPA file #%03d for move #%03d %s",
                                spaFileNumber, moveNumber, context.moves.get(moveNumber).name));
                }
            }

            context.moveAnimationsNarc.files.set(spaFileNumber, spaFileData);
        }

        String scriptFilename;
        if (specification == null)
            scriptFilename = String.format("moveanims/scripts/%03d_%s.bin", moveNumber, formattedMoveName);
        else
            scriptFilename = String.format("moveanims/scripts/%03d_%s_%s.bin", moveNumber, formattedMoveName, specification);

        byte[] script = readBytes(scriptFilename);
        if (moveNumber > Moves.fusionBolt)
            context.battleAnimationScriptsNarc.files.set(moveNumber + highMoveOffset - battleAnimationScriptsOffset, script);
        else
            context.moveAnimationScriptsNarc.files.set(moveNumber, script);
    }

    private void setMoveAuxiliaryAnimation(Context context, int moveNumber, String mode, int index, int[] spaFiles) {
        String formattedMoveName = context.moves.get(moveNumber).name.toLowerCase().replace(' ', '_');
        for (int spaFileNumber : spaFiles) {
            byte[] spaFileData = readBytes(String.format("moveanims/spa/%03d_%s_%s_%03d.spa", moveNumber, formattedMoveName, mode.toLowerCase(), spaFileNumber));
            while (context.moveAnimationsNarc.files.size() <= spaFileNumber) {
                context.moveAnimationsNarc.files.add(new byte[16]);
            }

            byte[] existingSpaFile = context.moveAnimationsNarc.files.get(spaFileNumber);
            for (byte b : existingSpaFile) {
                if (b != 0) // Should be all blank
                    throw new RuntimeException(String.format("Attempted to overwrite existing SPA file #%03d for move #%03d %s",
                            spaFileNumber, moveNumber, context.moves.get(moveNumber).name));
            }

            context.moveAnimationsNarc.files.set(spaFileNumber, spaFileData);
        }
        byte[] script = readBytes(String.format("moveanims/scripts/%03d_%s_%s.bin", moveNumber, formattedMoveName, mode.toLowerCase()));

        while (context.battleAnimationScriptsNarc.files.size() <= index)
            context.battleAnimationScriptsNarc.files.add(getDefaultAnimationScript(context));
        context.battleAnimationScriptsNarc.files.set(index, script);

        formattedMoveName = context.moves.get(moveNumber).name.replaceAll("[ -]", "");
        context.armParser.addGlobalValue(String.format("BTLANM_%s_%s", formattedMoveName, mode), index);
    }

    private byte[] getDefaultAnimationScript(Context context) {
        byte[] defaultScript = context.moveAnimationScriptsNarc.files.get(0);
        return Arrays.copyOf(defaultScript, defaultScript.length);
    }
}
