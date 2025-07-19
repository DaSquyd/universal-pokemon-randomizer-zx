package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_293_SupremeOverlord extends AbilityHackMod {
    private final boolean onlyOnEnter;
    private final boolean anyDamage;
    
    public AbilityHackMod_293_SupremeOverlord() {
        super(Abilities.supremeOverlord);
        
        this.onlyOnEnter = true;
        this.anyDamage = false;
    }
    
    public AbilityHackMod_293_SupremeOverlord(boolean onlyOnEnter, boolean anyDamage) {
        super(Abilities.supremeOverlord);
        
        this.onlyOnEnter = onlyOnEnter;
        this.anyDamage = anyDamage;
    }

    @Override
    public String getName(Context context) {
        return "Supreme Overlord";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "The Pokémon draws",
                "strength from the fallen."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of(
                "ABILITY_SUPREME_OVERLORD_ONLY_ON_ENTER", onlyOnEnter,
                "ABILITY_SUPREME_OVERLORD_ANY_DAMAGE", anyDamage
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "supreme_overlord_power.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "supreme_overlord_on_enter.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "supreme_overlord_on_enter.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "supreme_overlord_on_enter.s"));
        
        if (!onlyOnEnter)
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchOut, "supreme_overlord_on_exit.s"));
    }
}
