package com.dabomstew.pkrandom.romhandlers.hack;

import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public abstract class AbilityHackMod extends BattleObjectHackMod {
    public AbilityHackMod(int number) {
        super(number);
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                getName(context) + ", huh...",
                Dialogue.clearLine,
                "This is an Ability that I've",
                "never seen before...",
                Dialogue.clearLine,
                "I'm unable to tell you what it does."
        );
    }

    public Boolean isRolePlayFail() {
        return null;
    }

    public Boolean isSkillSwapFail() {
        return null;
    }

    public Boolean isBreakable() {
        return null;
    }
}
