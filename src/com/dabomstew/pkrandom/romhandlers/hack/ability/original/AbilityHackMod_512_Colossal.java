package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteHandler;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_512_Colossal extends AbilityHackMod {
    public AbilityHackMod_512_Colossal() {
        super(ParagonLiteAbilities.colossal);
    }

    @Override
    public String getName(Context context) {
        return "Colossal";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Shear size dissipates the",
                "damage. It's easy to hit."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Colossal, huh...",
                Dialogue.clearLine,
                "This Ability reduces damage taken.",
                Dialogue.clearLine,
                "You should remember that",
                "moves used against the Pokémon with",
                "this Ability always strike their targets."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_COLOSSAL_ALWAYS_HIT", true);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // full effect in is_guaranteed_hit.s
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageProcessing2, "colossal_damage.s"));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
