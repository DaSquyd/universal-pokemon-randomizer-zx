package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_524_HealSpore extends AbilityHackMod {
    public AbilityHackMod_524_HealSpore() {
        super(ParagonLiteAbilities.healSpore);
    }

    @Override
    public String getName(Context context) {
        return "Heal Spore";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "The Pokémon heals its",
                "allies a little when hit."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageReaction1, "heal_spore.s"));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
