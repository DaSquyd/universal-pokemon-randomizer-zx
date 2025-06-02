package com.dabomstew.pkrandom.romhandlers;

public enum OverlayId {
    LOCAL("Local", -1, 12),
    FIELD("Field", 21, 36),
    SCRIPT_BATTLE_SUBWAY("Script_BattleSubway", -1, 50),
    SCRIPT_PLASMA_FRIGATE("Script_PlasmaFrigate", -1, 51),
    SCRIPT_ELITE_FOUR("Script_EliteFour", -1, 52),
    SCRIPT_PWT("Script_PWT", -1, 55),
    SCRIPT_PWT_LOBBY("Script_PWTLobby", -1, 56),
    SCRIPT_PWT_STAGE("Script_PWTStage", -1, 57),
    SCRIPT_POKE_CENTER("Script_PokeCenter", -1, 65),
    PWT_BATTLE("PWTBattle", -1, 134),
    UI_COMMON("UICommon", 65, 139),
    BAG("Bag", 68, 142),
    TITLE("Title", 88, 162),
    BATTLE("Battle", 93, 167),
    BATTLE_LEVEL("BattleLevel", 94, 168),
    BATTLE_SERVER("BattleServer", 95, 169),
    TRAINER_AI("TrainerAI", 96, 170),
    MULTIBOOT("Multiboot", 107, 181),
    MYSTERY_GIFT("MysteryGift", 121, 197),
    POKEMON_SUMMARY("PokemonSummary", 131, 207),
    STORAGE_SYSTEM("StorageSystem", 170, 255),
    LOCAL_XTRANSCEIVER("LocalXtransceiver", 174, 259),
    HALL_OF_FAME("HallOfFame",180, 265),
    CREDITS("Credits", 181, 266),
    THE_END("TheEnd", 182, 267),
    NAME_ENTRY("NameEntry", -1, 280),
    EVOLUTION("Evolution", 195, 284),
    WIFI_TOURNAMENT("WifiTournament", -1, 290),
    INTRO_GRAPHIC("InfoGraphic", 204, 294),
    POKEDEX("Pokedex", 206, 296),
    SEARCH("Search", 212, 302),
    META_SAVE("MetaSave", -1, 331),
    UNOVA_LINK("UnovaLink", -1, 332);

    public final String name;
    public final int lowerVersionNumber;
    public final int upperVersionNumber;

    OverlayId(String name, int lowerVersionNumber, int upperVersionNumber) {
        this.name = name;
        this.lowerVersionNumber = lowerVersionNumber;
        this.upperVersionNumber = upperVersionNumber;
    }
}
