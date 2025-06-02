package com.dabomstew.pkrandom.romhandlers.hack.mode;

import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackModCollection;
import com.dabomstew.pkrandom.romhandlers.hack.ability.custom.AbilityHackMod_023_ShadowTag_Message;
import com.dabomstew.pkrandom.romhandlers.hack.ability.custom.AbilityHackMod_025_WonderGuard_Message;
import com.dabomstew.pkrandom.romhandlers.hack.ability.custom.AbilityHackMod_037_HugePower;

import java.util.List;

// A version of Modern that includes some announcement messages for certain abilities (similar to Mold Breaker or Slow Start) as well as abilities + items from the modern games
public class ModernPlusHackMode extends ModernHackMode {
    public ModernPlusHackMode(String name) {
        super(name);

        // Ability
        getHackMod(AbilityHackModCollection.class).addHackMods(List.of(
                new AbilityHackMod_023_ShadowTag_Message("{0} steps on\nfoe shadows!"),
                new AbilityHackMod_025_WonderGuard_Message("{0} is cloaked in\na mysterious power!"),
                new AbilityHackMod_037_HugePower("{0} is flexing\nits muscles!")
        ));

        // Ability
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
