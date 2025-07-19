package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_506_StoneHome extends AbilityHackMod {
    public AbilityHackMod_506_StoneHome() {
        super(ParagonLiteAbilities.stoneHome);
    }

    @Override
    public String getName(Context context) {
        return "Stone Home";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Boosts the Defense stat",
                "of the Pokémon's allies."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetDefendingStatValue, "stone_home_defense.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "stone_home_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "stone_home_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "stone_home_message.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
