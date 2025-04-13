package com.dabomstew.pkrandom.romhandlers.hack;

// A version of Modern that includes some announcement messages for certain abilities (similar to Mold Breaker or Slow Start)
public class ModernPlusHackMode extends ModernHackMode {
    public ModernPlusHackMode() {
        super("Modern+");
    }

    public ModernPlusHackMode(String name) {
        super(name);
    }

    @Override
    protected void setValues() {
        // Ability
        abilityShadowTagMessage = "{0} steps on\nfoe shadows!"; // #023
        abilityWonderGuardMessage = "{0} is cloaked in\na mysterious power!"; // #025
        abilityIlluminateMessage = "{0} illuminated\nthe area!"; // #035
        abilityHugePowerMessage = "{0} is flexing\nits muscles!"; // #037
        abilityWaterVeilMessage = "{0} is cloaked\nin water!"; // #041
        abilityMagnetPullMessage = "{0} is generating\na magnetic field!"; // #042
        abilitySandStreamAllowSelfDamage = false; // #045
        abilityPlusMessage = "{0} is overflowing\nwith a positive charge!"; // #057
        abilityMinusMessage = "{0} is overflowing\nwith a negative charge!"; // #058
        abilityArenaTrapMessage = "{0} digs\na pit trap!"; // #071
        abilityPurePowerMessage = "{0} is focusing\nits strength!"; // #074
        abilitySuperLuckMessage = "{0} is feeling lucky!"; // #105
        
        // AI
        aiSimulateDamageFix = true;
    }
}
