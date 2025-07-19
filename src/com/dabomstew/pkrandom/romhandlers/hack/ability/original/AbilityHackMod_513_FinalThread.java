package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_513_FinalThread extends AbilityHackMod {
    public AbilityHackMod_513_FinalThread() {
        super(ParagonLiteAbilities.finalThread);
    }

    @Override
    public String getName(Context context) {
        return "Final Thread";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Upon fainting, it releases",
                "webs that slow down foes."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageReaction1, "final_thread.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
