package com.dabomstew.pkrandom.romhandlers.hack.mode;

import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackModCollection;
import com.dabomstew.pkrandom.romhandlers.hack.ability.custom.AbilityHackMod_074_PurePower;
import com.dabomstew.pkrandom.romhandlers.hack.ability.custom.AbilityHackMod_117_SnowWarning_HailImmunity;
import com.dabomstew.pkrandom.romhandlers.hack.ability.modernplus.*;
import com.dabomstew.pkrandom.romhandlers.hack.ability.custom.AbilityHackMod_037_HugePower;

import java.util.List;

// A version of Modern that includes some announcement messages for certain abilities (similar to Mold Breaker or Slow Start) as well as abilities + items from the modern games
public class ModernPlusHackMode extends ModernHackMode {
    public ModernPlusHackMode(String name) {
        super(name);

        // Ability
        addHackMod(new AbilityHackModCollection(
                new AbilityHackMod_023_ShadowTag_Message("{0} steps on\nfoe shadows!"),
                new AbilityHackMod_025_WonderGuard_Message("{0} is cloaked in\na mysterious power!"),
                new AbilityHackMod_037_HugePower("{0} is flexing\nits muscles!"),
                new AbilityHackMod_042_MagnetPull_Message("{0} is generating\na magnetic field!"),
                new AbilityHackMod_045_SandStream_SandstormImmunity(),
                new AbilityHackMod_057_Plus_Message("{0} is overflowing\nwith a positive charge!"),
                new AbilityHackMod_058_Minus_Message("{0} is overflowing\nwith a negative charge!"),
                new AbilityHackMod_071_ArenaTrap_Message("{0} digs\na pit trap!"),
                new AbilityHackMod_074_PurePower("{0} is striking\na pose!"),
                new AbilityHackMod_105_SuperLuck_Message("{0} is feeling lucky!"),
                new AbilityHackMod_117_SnowWarning_HailImmunity()
        ));

        // Ability
        abilityWaterVeilMessage = "{0} is cloaked\nin water!"; // #041
        abilityMagnetPullMessage = "{0} is generating\na magnetic field!"; // #042
        abilitySandStreamAllowSelfDamage = false; // #045
        abilityPlusMessage = "{0} is overflowing\nwith a positive charge!"; // #057
        abilityMinusMessage = "{0} is overflowing\nwith a negative charge!"; // #058
        abilityArenaTrapMessage = "{0} digs\na pit trap!"; // #071
        abilityPurePowerMessage = "{0} is striking\na pose!"; // #074
        abilitySuperLuckMessage = "{0} is feeling lucky!"; // #105

        // AI
        aiSimulateDamageFix = true;
    }
}
