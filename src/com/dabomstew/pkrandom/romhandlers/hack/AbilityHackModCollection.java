package com.dabomstew.pkrandom.romhandlers.hack;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5Constants;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteOverlay;

import java.util.*;

public class AbilityHackModCollection extends BattleObjectHackModCollection<AbilityHackMod> {
    public AbilityHackModCollection(AbilityHackMod... hackMods) {
        super(hackMods);
    }

    @Override
    protected String getBattleObjectTypeName() {
        return "Ability";
    }

    @Override
    protected String getFunctionDirectory() {
        return "ability";
    }

    @Override
    protected String getNamesTextOffsetKey() {
        return "AbilityNamesTextOffset";
    }

    @Override
    protected String getDescriptionsTextOffsetKey() {
        return "AbilityDescriptionsTextOffset";
    }

    @Override
    protected String getExplanationsTextOffsetKey() {
        return "AbilityExplanationsTextOffset";
    }

    @Override
    protected List<Table> getTables() {
        List<Table> tables = List.of(
                new Table("IsRolePlayFailAbility", 0x14, 0x1C, (i) -> ((AbilityHackMod) i).isRolePlayFail()),
                new Table("IsSkillSwapFailAbility", 0x14, 0x1C, (i) -> ((AbilityHackMod) i).isSkillSwapFail()),
                new Table("IsBreakableAbility", 0x04, 0x08, (i) -> ((AbilityHackMod) i).isBreakable())
        );

        // For whatever reason, multitype is checked explicitly, so we can remove them from the tables to save 1 slot of space
        for (Table table : tables)
            table.update(Abilities.multitype, false);

        return tables;
    }

    @Override
    protected int getMaxVanillaObjectNumber() {
        return Gen5Constants.highestAbilityIndex;
    }

    @Override
    protected ParagonLiteOverlay getOverlay() {
        return battleOvl;
    }

    @Override
    protected int getAddFunctionRomAddress() {
        return battleOvl.getRomAddress("AbilityEvent_AddObject");
    }

    @Override
    protected int getEffectListCompareOffset() {
        return 0x5A;
    }

    @Override
    protected int getEffectListCountOffset() {
        return -1;
    }

    @Override
    protected int getEffectListNumReferenceOffset() {
        return 0x64;
    }

    @Override
    protected int getEffectListFuncReferenceOffset() {
        return 0x68;
    }

    @Override
    protected String getEffectListLabel() {
        return "Data_AbilityEffectList";
    }
}
