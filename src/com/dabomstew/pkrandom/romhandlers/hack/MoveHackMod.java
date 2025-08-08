package com.dabomstew.pkrandom.romhandlers.hack;

public abstract class MoveHackMod extends BattleObjectHackMod {
    public MoveHackMod(int number) {
        super(number);
    }
    
    public boolean addAnimation() {
        return false;
    }
    
    public int[] getAnimationSpaFiles() {
        return new int[0];
    }
    
    public String getAnimationSpecification() {
        return null;
    }
    
    public String getAuxiliaryAnimationLabel() {
        return null;
    }
    
    public int[] getAuxiliaryAnimationSpaFiles() {
        return new int[0];
    }

    public Boolean isProtectionMove() {
        return null;
    }

    public Boolean isEncoreFailMove() {
        return null;
    }

    public Boolean isMeFirstFailMove() {
        return null;
    }

    // Future Sight and Doom Desire
    public Boolean isDelayedHitMove() {
        return null;
    }

    // Pressure removes extra PP from these moves when used
    public Boolean isPressureBonusMove() {
        return null;
    }

    // Pledge moves
    public Boolean isComboMove() {
        return null;
    }

    // Basis for both Sleep Talk, Assist, and Copy Cat Uncallable
    public Boolean isUncallableMove() {
        return null;
    }

    public Boolean isSleepTalkUncallableMove() {
        return null;
    }

    public Boolean isAssistUncallableMove() {
        return null;
    }

    public Boolean isCopycatUncallableMove() {
        return null;
    }

    public Boolean isMimicFailMove() {
        return null;
    }

    public Boolean isMetronomeUncallableMove() {
        return null;
    }
}
