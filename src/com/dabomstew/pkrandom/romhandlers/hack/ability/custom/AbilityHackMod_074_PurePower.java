package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Gen5Constants;
import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_074_PurePower extends AbilityHackMod {
    private final double multiplier;
    private final MoveCategory moveCategory;
    private final String message;

    public AbilityHackMod_074_PurePower(double multiplier, MoveCategory moveCategory) {
        super(Abilities.purePower);

        this.multiplier = multiplier;
        this.moveCategory = moveCategory;
        this.message = "";

        if (multiplier == 2 && moveCategory == MoveCategory.PHYSICAL)
            throw new RuntimeException();

        if (moveCategory == null || moveCategory == MoveCategory.STATUS)
            throw new RuntimeException("Move category must be either Physical or Special!");
    }

    // Modern Plus
    public AbilityHackMod_074_PurePower(String message) {
        super(Abilities.purePower);

        this.multiplier = 2;
        this.moveCategory = MoveCategory.PHYSICAL;
        this.message = message;

        if (message == null || message.isBlank())
            throw new RuntimeException();
    }

    public AbilityHackMod_074_PurePower(double multiplier, MoveCategory moveCategory, String message) {
        super(Abilities.purePower);

        this.multiplier = multiplier;
        this.moveCategory = moveCategory;
        this.message = message;

        if (multiplier == 2 && (message == null || message.isBlank()))
            throw new RuntimeException("One of these should be set!");

        if (moveCategory == null || moveCategory == MoveCategory.STATUS)
            throw new RuntimeException("Move category must be either Physical or Special!");
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        String explanation;
        if (multiplier == 1.5)
            explanation = "Pure Power, huh...\uF000븁\\x0000\\xFFFEThis Ability increases a Pokémon's\\xFFFEAttack stat by half.\uF000븁\\x0000";
        else if (multiplier != 2)
            explanation = "Pure Power, huh...\uF000븁\\x0000\\xFFFEThis Ability increases a Pokémon's\\xFFFEAttack stat.\uF000븁\\x0000";
        else
            explanation = allExplanations.get(number);

        if (moveCategory == MoveCategory.SPECIAL)
            explanation = explanation.replace("Attack", "Sp. Attack");

        return explanation;
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of(
                "ABILITY_PURE_POWER_MOVE_CATEGORY", Gen5Constants.moveCategoryToByte(moveCategory),
                "ABILITY_PURE_POWER_MULTIPLIER", multiplier
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // Multiplier
        if (multiplier == 2 || moveCategory != MoveCategory.PHYSICAL)
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue));
        else
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "pure_power.s"));

        // Message
        if (message != null && !message.isBlank()) {
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "pure_power_message.s"));
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "pure_power_message.s"));
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "pure_power_message.s"));
        }
    }
}
