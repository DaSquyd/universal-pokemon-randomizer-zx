package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_083_AngerPoint_Miss extends AbilityHackMod {
    private final boolean keepOldEffect;

    public AbilityHackMod_083_AngerPoint_Miss(boolean keepOldEffect) {
        super(Abilities.angerPoint);

        this.keepOldEffect = keepOldEffect;
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return keepOldEffect ?
                new Dialogue(
                        "Anger Point, huh...",
                        Dialogue.clearLine,
                        "This Ability raises the Pokémon's Attack",
                        "every time it misses or flinches.",
                        Dialogue.clearLine,
                        "What's more...",
                        Dialogue.clearLine,
                        "It raises the Pokémon's Attack to the",
                        "maximum when hit by a critical hit."
                ) :
                new Dialogue(
                        "Anger Point, huh...",
                        Dialogue.clearLine,
                        "This Ability raises the Pokémon's Attack",
                        "every time it it misses, flinches,",
                        "or is hit by a critical hit."
                );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_ANGER_POINT_KEEP_OLD_EFFECT", keepOldEffect);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        if (keepOldEffect)
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageReaction1));
        else
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageReaction1, "anger_point_crit.s"));

        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteFail, "anger_point_flinch.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCheckNoEffect3, "steadfast_taunt.s"));

        return true;
    }
}
