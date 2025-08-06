package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_131_Healer_HealAllies extends AbilityHackMod {
    private final int hpFraction;
    private final int cureChance;
    
    public AbilityHackMod_131_Healer_HealAllies(int hpFraction, int cureChance) {
        super(Abilities.healer);
        
        this.hpFraction = hpFraction;
        this.cureChance = cureChance;
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Restores allies' HP and",
                "their status condition."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Healer, huh...",
                Dialogue.clearLine,
                "Pokémon with this Ability",
                "restore the HP of allies that are",
                "battling with it a little",
                Dialogue.clearLine,
                "It also occasionally heals allies of",
                "their status conditions.",
                Dialogue.clearLine,
                "They can't heal themselves,",
                "so it doesn't mean much in Single Battles,",
                "but it's a very useful Ability",
                "to have in Double and Triple Battles."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of(
                "ABILITY_HEALER_HP_FRACTION", hpFraction,
                "ABILITY_HEALER_CURE_CHANCE", cureChance
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onTurnCheckBegin, "healer.s"));

        return true;
    }
}
