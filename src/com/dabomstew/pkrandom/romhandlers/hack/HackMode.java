package com.dabomstew.pkrandom.romhandlers.hack;

public enum HackMode {
    Modern(
            AbilityStenchMode.VANILLA,
            AbilityMinusMode.VANILLA,
            AbilityVitalSpiritMode.VANILLA,
            AbilityIceBodyMode.VANILLA,
            AbilityJustifiedMode.VANILLA,
            AbilityRattledMode.MODERN,
            AbilityThermalExchangeMode.VANILLA
    ),

    ParagonLite(
            AbilityStenchMode.VANILLA,
            AbilityMinusMode.ALLY_SPECIAL_DEFENSE,
            AbilityVitalSpiritMode.INCREASE_SPECIAL_DEFENSE,
            AbilityIceBodyMode.ICE_IMMUNE_AND_RECOVER,
            AbilityJustifiedMode.DARK_IMMUNITY,
            AbilityRattledMode.MODERN,
            AbilityThermalExchangeMode.RESIST
    ),

    Redux(
            AbilityStenchMode.FLINCH_20,
            AbilityMinusMode.ALLY_ATTACK,
            AbilityVitalSpiritMode.VANILLA,
            AbilityIceBodyMode.VANILLA,
            AbilityJustifiedMode.DARK_IMMUNITY,
            AbilityRattledMode.BUG_GHOST_RESIST,
            AbilityThermalExchangeMode.RESIST
    );

    // Abilities
    public final AbilityStenchMode abilityStenchMode;
    public final AbilityMinusMode abilityMinusMode;
    public final AbilityVitalSpiritMode abilityVitalSpiritMode;
    public final AbilityIceBodyMode abilityIceBodyMode;
    public final AbilityJustifiedMode abilityJustifiedMode;
    public final AbilityRattledMode abilityRattledMode;
    public final AbilityThermalExchangeMode abilityThermalExchangeMode;

    HackMode(AbilityStenchMode abilityStenchMode, AbilityMinusMode abilityMinusMode, AbilityVitalSpiritMode abilityVitalSpiritMode, AbilityIceBodyMode abilityIceBodyMode,
             AbilityJustifiedMode abilityJustifiedMode, AbilityRattledMode abilityRattledMode, AbilityThermalExchangeMode abilityThermalExchangeMode) {
        this.abilityStenchMode = abilityStenchMode;
        this.abilityMinusMode = abilityMinusMode;
        this.abilityVitalSpiritMode = abilityVitalSpiritMode;
        this.abilityIceBodyMode = abilityIceBodyMode;
        this.abilityJustifiedMode = abilityJustifiedMode;
        this.abilityRattledMode = abilityRattledMode;
        this.abilityThermalExchangeMode = abilityThermalExchangeMode;
    }
}
