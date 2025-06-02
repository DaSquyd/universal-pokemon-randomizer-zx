package com.dabomstew.pkrandom.romhandlers.hack;

import java.util.List;

public abstract class AbilityHackMod extends BattleObjectHackMod {
    public AbilityHackMod(int number) {
        super(number);
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
