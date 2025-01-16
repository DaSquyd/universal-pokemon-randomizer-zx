package com.dabomstew.pkrandom.pokemon;

public enum EggGroup {
    MONSTER("Monster", 1),
    WATER_1("Water 1", 2),
    BUG("Bug", 3),
    FLYING("Flying", 4),
    FIELD("Field", 5),
    FAIRY("Fairy", 6),
    GRASS("Grass", 7),
    HUMAN_LIKE("Human-Like", 8),
    WATER_3("Water 3", 9),
    MINERAL("Mineral", 10),
    AMORPHOUS("Amorphous", 11),
    WATER_2("Water 2", 12),
    DITTO("Ditto", 13),
    DRAGON("Dragon", 14),
    UNDISCOVERED("Undiscovered", 15);

    final String displayName;
    final byte value;
    
    EggGroup(String displayName, int value) {
        this.displayName = displayName;
        this.value = (byte) value;
    }
    
    public byte toByte() {
        return value;
    }
    
    public static EggGroup fromByte(byte value) {
        for (EggGroup eggGroup : EggGroup.values()) {
            if (eggGroup.value == value)
                return eggGroup;
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
