package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.HackMod;
import com.dabomstew.pkrandom.romhandlers.hack.ability.expansion.AbilityHackMod_168_Protean;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbilityHackMod_016_ColorChange_Protean extends AbilityHackMod {
    public final boolean firstOnly;

    public AbilityHackMod_016_ColorChange_Protean(boolean firstOnly) {
        super(Abilities.colorChange);

        this.firstOnly = firstOnly;
    }

    @Override
    public Set<Class<? extends HackMod>> getDependencies() {
        return Set.of(AbilityHackMod_168_Protean.class);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("Changes type to the match", "the current move.");
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_COLOR_CHANGE_MODERN_PROTEAN", firstOnly);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        AbilityHackMod_168_Protean proteanHackMod = (AbilityHackMod_168_Protean) context.applied().get(AbilityHackMod_168_Protean.class);

        String filename;
        filename = firstOnly == (proteanHackMod == null || proteanHackMod.firstOnly) ? "protean.s" : "redux_color_change.s";

        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveTarget, filename));
    }
}
