package com.dabomstew.pkrandom.pokemon;

public class Item {    
    public enum TempUseEffect {
        NONE,
        CURE_PARALYSIS,
        CURE_SLEEP,
        CURE_POISON,
        CURE_BURN,
        CURE_FREEZE,
        RESTORE_PP,
        RESTORE_HP,
        CURE_CONFUSION,
        CURE_ANY,
        RESTORE_HP_RATIO,
        RESTORE_HP_SPICY,
        RESTORE_HP_DRY,
        RESTORE_HP_SWEET,
        RESTORE_HP_BITTER,
        RESTORE_HP_SOUR,
        RAISE_ATTACK,
        RAISE_DEFENSE,
        RAISE_SPEED,
        RAISE_SP_ATK,
        RAISE_SP_DEF,
        RAISE_CRIT,
        RAISE_RANDOM,
        BOOST_NEXT_ACCURACY,
        REMOVE_NEGATIVE_STATS,
        MENTAL,
        FLINCH,
        GIVE_PARALYSIS,
        GIVE_POISON,
        GIVE_BAD_POISON,
        GIVE_BURN,
    }
    
    public enum FieldFuncType {
        NONE(0),
        MEDICINE(1),
        TOWN_MAP(2),
        EXPLORATION_KIT(3),
        BICYCLE(4),
        GUIDEBOOK(5),
        TM_HM(6),
        MAIL(7),
        BERRY(8),
        PAL_PAD(10),
        POKE_RADAR(11),
        PAIL(12),
        MULCH(13),
        HONEY(14),
        VS_SEEKER(15),
        FISHING_ROD(18),
        REPEL(19),
        EVOLUTION_STONE(20),
        VS_RECORDER(23),
        GRACIDEA(24),
        DOWSING_MCHN(25),
        XTRANCEIVER(26),
        MEDAL_BOX(27),
        DNA_SPLICERS_1(28),
        DNA_SPLICERS_2(29),
        REVEAL_GLASS(30);

        final byte byteValue;
        FieldFuncType(int byteValue) {
            this.byteValue = (byte) byteValue;
        }
        
        public byte getByteValue() {
            return byteValue;
        }
    }
    
    public enum FieldPocket {
        ITEMS,
        MEDICINE,
        TMS_AND_HMS,
        BERRIES,
        KEY_ITEMS
    }
    
    public enum BattlePocket {
        POKE_BALLS,
        BATTLE_ITEMS,
        HP_PP_RESTORE,
        STATUS_RESTORE
    }
    
    public enum BattleFuncType {
        NONE,
        POKE_BALL,
        MEDICINE,
        ESCAPE
    }

    public enum BattleCategory {
        NONE,
        BALL,
        MEDICINE,
        ESCAPE
    }
    
    public enum ItemType {
        NONE,
        HELD,
        MISC,
        BATTLE,
        POKE_BALL,
        MAIL
    }
    
    public String name;
    public String description;
    public int number;
    public int internalId;
    public int price; // 0x00
    public int battleEffect; // 0x02
    public int useValue; // 0x03
    public int flingEffect; // 0x05
    public int flingPower; // 0x06
    public int naturalGiftPower; // 0x07
    public Type naturalGiftType; // 0x08 [0-4]
    public boolean canHold; // 0x08 ~[5]
    public boolean canRegister; // 0x08 [6]
    public boolean unknownFlag; // 0x08 [7]
    public int usageFlags; // 0x09
    public FieldFuncType fieldFuncType; // 0x0A
    public BattleCategory battleCategory; // 0x0B
    public ItemType itemType; // 0x0D
    public int sortValue; // 0x0F
    public int hpEVs; // 0x17
    public int attackEVs; // 0x18
    public int defenseEVs; // 0x19
    public int speedEVs; // 0x1A
    public int spatkEVs; // 0x1B
    public int spdefEVs; // 0x1C
    public int hpRestore; // 0x1D
    public int ppRestore; // 0x1E
    public int friendship0; // 0x1F
    public int friendship100; // 0x20
    public int friendship200; // 0x21
}
