package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteHandler;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_037_HugePower extends AbilityHackMod {
    private final double multiplier;
    private final String message;
    
    public AbilityHackMod_037_HugePower(double multiplier) {
        super(Abilities.hugePower);

        this.multiplier = multiplier;
        this.message = "";

        if (multiplier == 2)
            throw new RuntimeException();
    }
    
    public AbilityHackMod_037_HugePower(String message) {
        super(Abilities.hugePower);

        this.multiplier = 2;
        this.message = message;

        if (message == null || message.isBlank())
            throw new RuntimeException();
    }

    public AbilityHackMod_037_HugePower(double multiplier, String message) {
        super(Abilities.hugePower);

        this.multiplier = multiplier;
        this.message = message;

        if (multiplier == 2 && (message == null || message.isBlank()))
            throw new RuntimeException("One of these should be set!");
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Huge Power, huh...",
                Dialogue.clearLine,
                "This Ability " + (multiplier == 2 ? "doubles" : "increases") + " a Pokémon's",
                "Attack stat."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_HUGE_POWER_MULTIPLIER", multiplier);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // Multiplier
        if (multiplier == 2)
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue));
        else
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "huge_power.s"));

        // Message
        if (message != null && !message.isBlank()) {
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "huge_power_message.s"));
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "huge_power_message.s"));
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "huge_power_message.s"));
        }
    }
}
