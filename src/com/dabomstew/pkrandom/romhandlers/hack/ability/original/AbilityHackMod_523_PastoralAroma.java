package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_523_PastoralAroma extends AbilityHackMod {
    public AbilityHackMod_523_PastoralAroma() {
        super(ParagonLiteAbilities.pastoralAroma);
    }

    @Override
    public String getName(Context context) {
        return "Pastoral Aroma";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "A smoothing aroma lowers",
                "the foe's Sp. Atk stat."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchOutEnd, "pastoral_aroma.s"));

        return true;
    }
}
