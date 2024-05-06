package com.dabomstew.pkrandom.pokemon;

public enum MoveEffect {
    DMG(0), // Pound, Mega Punch, Scratch, etc.
    NO_DMG_SLP(1), // Sing, SLP Powder, Hypnosis, Etc.
    DMG_POISON(2), // Poison Sting, Smog, Sludge, etc.
    DMG_RECOVER(3), // Absorb, Mega Drain, Leech Life, etc.
    DMG_BRN(4), // Fire Punch, Ember, Flamethrower, etc.
    DMG_FRZ(5), // Ice Punch, Ice Beam, Powder Snow, etc.
    DMG_PARA(6), // Thunder Punch, Body Slam, Thunder Shock, etc.
    USER_FAINTS(7), // Self-Destruct, Explosion
    DREAM_EATER(8),
    MIRROR_MOVE(9),
    USER_ATK_PLUS_1(10), // Meditate, Sharpen Howl
    USER_DEF_PLUS_1(11), // Harden, Withdraw
    USER_SPE_PLUS_1(12), // UNUSED
    USER_SPA_PLUS_1(13), // UNUSED
    USER_SPD_PLUS_1(14), // UNUSED
    USER_ACC_PLUS_1(15), // UNUSED
    USER_EVA_PLUS_1(16), // Double Team
    NEVER_MISSES(17), // Swift, Feint ATK, Shadow Punch, etc.
    TRGT_ATK_MINUS_1(18), // Growl
    TRGT_DEF_MINUS_1(19), // Tail Whip, Leer
    TRGT_SPE_MINUS_1(20), // String Shot (Gen V), Low Sweep, Electro Web
    TRGT_SPA_MINUS_1(21), // UNUSED
    TRGT_SPD_MINUS_1(22), // UNUSED
    TRGT_ACC_MINUS_1(23), // Sand ATK, Smokescreen, etc.
    TRGT_EVA_MINUS_1(24), // Sweet Scent
    HAZE(25),
    BIDE(26),
    THRASH_ABOUT(27), // Thrash, Petal Dance, Outrage
    NO_DMG_FORCE_SWITCH(28), // Whirlwind, Roar
    HIT_2_TO_5_TIMES(29), // Double Slap, Comet Punch, Fury ATK, ec.
    CONVERSION(30),
    DMG_FLINCH(31), // Rolling Kick, Headbutt, Bite, etc.
    RECOVER_HP_50(32), // Recover, Soft-Boiled, Milk Drink, etc.
    TOXIC(33),
    PAY_DAY(34),
    LIGHT_SCREEN(35),
    TRI_ATK(36),
    REST(37),
    OHKO(38), // Guillotine, Horn Drill, Fissure, Sheer Cold
    RAZOR_WIND(39),
    DIRECT_HALF(40), // Super Fang
    DIRECT_40(41), // Dragon Rage
    DMG_TRAP(42), // Bind, Wrap, Fire Spin, etc.
    DMG_INCR_CRIT(43), // Karate Chop, Razor Leaf, Crabhammer, etc.
    HIT_2_TIMES(44), // Double Kick,  Bonemerang, Double Hit, etc.
    JUMP_KICK(45), // Jump Kick, High Jump Kick
    MIST(46),
    CRIT_RATIO_PLUS_2(47), // Focus Energy
    DMG_RECOIL_25(48), // Take Down, Submission, Wild Charge
    NO_DMG_CNF(49), // Supersonic, CNF Ray, Sweet Kiss
    USER_ATK_PLUS_2(50), // Swords Dance
    USER_DEF_PLUS_2(51), // Barrier, Acid Armor, Iron DEF
    USER_SPE_PLUS_2(52), // Agility, Rock Polish
    USER_SPA_PLUS_2(53), // Nasty Plot
    USER_SPD_PLUS_2(54), // Amnesia
    USER_ACC_PLUS_2(55), // Unused
    USER_EVA_PLUS_2(56), // Unused
    TRANSFORM(57),
    TRGT_ATK_MINUS_2(58), // Charm, Feather Dance
    TRGT_DEF_MINUS_2(59), // Screech
    TRGT_SPE_MINUS_2(60), // String Shot (Gen VI+), Cotton Spore, Scary Face
    TRGT_SPA_MINUS_2(61), // Eerie Impulse
    TRGT_SPD_MINUS_2(62), // Fake Tears, Metal Sound
    TRGT_ACC_MINUS_2(63), // UNUSED
    TRGT_EVA_MINUS_2(64), // UNUSED
    REFLECT(65),
    NO_DMG_POISON(66), // Poison Powder, Poison Gas
    NO_DMG_PARA(67), // Stun Spore, Thunder Wave, Glare
    DMG_TRGT_ATK_MINUS_1(68), // Aurora Beam, Play Rough
    DMG_TRGT_DEF_MINUS_1(69), // Iron Tail, Crunch, Rock Smash, etc.
    DMG_TRGT_SPE_MINUS_1(70), // Bubble Beam, Constrict, Bubble, etc.
    DMG_TRGT_SPA_MINUS_1(71), // Mist Ball, Struggle Bug, Snarl, Moon Blast, Mystical Fire
    DMG_TRGT_SPD_MINUS_1(72), // Acid, Psychic, Shadow Ball, etc.
    DMG_TRGT_ACC_MINUS_1(73), // Mud-Slap, Octazooka, Muddy Water, etc.
    DMG_TRGT_EVA_MINUS_1(74), // UNUSED
    SKY_ATK(75),
    DMG_CNF(76), // Psybeam, Confusion, Dizzy Punch, etc.
    HIT_2_TIMES_POISON(77), // Twineedle
    DMG_DCR_PRIORITY(78), // Vital Throw
    SUBSTITUTE(79),
    DMG_RECHARGE(80), // Hyper Beam, Blast BRN, Hydro Cannon, etc.
    RAGE(81),
    MIMIC(82),
    METRONOME(83),
    LEECH_SEED(84),
    SPLASH(85),
    DISABLE(86),
    DIRECT_DMG_LEVEL(87), // Seismic Toss, Night Shade
    PSYWAVE(88), // Psywave
    COUNTER(89),
    ENCORE(90),
    PAIN_SPLIT(91),
    SNORE(92),
    CONVERSION_2(93),
    MIND_READER(94), // Mind Reader, Lock-On
    SKETCH(95),
    SLP_TALK(97),
    DESTINY_BOND(98),
    DMG_LOW_HP(99), // Flail, Reversal
    SPITE(100),
    FALSE_SWIPE(101), // False Swipe, Hold Back
    HEAL_TEAM_STATUS(102), // Heal Bell, Aromatherapy
    DMG_INCR_PRIO(103), // Quick ATK, Mach Punch, Extreme SPE, etc.
    TRIPLE_KICK(104),
    DMG_TAKE_ITEM(105), // Thief, Covet
    PREVENT_ESCAPE(106), // Spider Web, Mean Look, Block
    NIGHTMARE(107),
    MINIMIZE(108),
    CURSE(109),
    PROTECT(111), // Protect, Detect
    SPIKES(112),
    FORESIGHT(113), // Foresight, Odor Sleuth
    PERISH_SONG(114),
    SANDSTORM(115),
    ENDURE(116),
    ROLLOUT(117), // Rollout, Ice Ball
    SWAGGER(118),
    FURY_CUTTER(119),
    NO_DMG_INF(120), // Attract
    RETURN(121),
    PRESENT(122),
    FRUSTRATION(123),
    SAFEGUARD(124),
    DMG_BRN_THAW(125), // Flame Wheel, Sacred Fire
    MAGNITUDE(126),
    BATON_PASS(127),
    PURSUIT(128),
    RAPID_SPIN(129),
    DIRECT_DMG_20(130), // Sonic Boom
    RECOVER_HP_50_WEATHER(132), // Morning Sun, Synthesis, Moonlight
    HIDDEN_POWER(135),
    RAIN_DANCE(136),
    SUNNY_DAY(137),
    DMG_USER_DEF_PLUS_1(138), // Steel Wing
    DMG_USER_ATK_PLUS_1(139), // Metal Claw, Meteor Mash
    DMG_ALL_USER_STATS_PLUS_1(140), // Ancient Power, Silver Wind, Ominous Wind
    BELLY_DRUM(142),
    PSYCH_UP(143),
    MIRROR_COAT(144),
    SKULL_BASH(145),
    TWISTER(146),
    EARTHQUAKE(147),
    FUTURE_SIGHT(148), // Future Sight, Doom Desire
    GUST(149),
    STOMP(150), // Stomp, Steamroller
    SOLAR_BEAM(151),
    THUNDER(152),
    TELEPORT(153),
    BEAT_UP(154),
    FLY(155),
    DEF_CURL(156),
    FAKE_OUT(158),
    UPROAR(159),
    STOCKPILE(160),
    SPIT_UP(161),
    SWALLOW(162),
    HAIL(164),
    TORMENT(165),
    FLATTER(166),
    NO_DMG_BRN(167),
    MEMENTO(168),
    FACADE(169),
    FOCUS_PUNCH(170),
    SMELLING_SALTS(171),
    FOLLOW_ME(172), // Follow Me, Rage Powder
    NATURE_POWER(173),
    CHARGE(174),
    TAUNT(175),
    HELPING_HAND(176),
    TRICK(177), // Trick, Switcheroo
    ROLE_PLAY(178),
    WISH(179),
    ASSIST(180),
    INGRAIN(181),
    DMG_USER_ATK_DEF_MINUS_1(182),
    MAGIC_COAT(183),
    RECYCLE(184),
    REVENGE(185), // Revenge, Avalanche
    BRICK_BREAK(186),
    NO_DMG_DROWSY(187), // Yawn
    KNOCK_OFF(188),
    ENDEAVOR(189),
    DMG_HIGH_USER_HP(190), // Eruption, Water Spout
    SKILL_SWAP(191),
    IMPRISON(192),
    REFRESH(193),
    GRUDGE(194),
    SNATCH(195),
    LOW_KICK(196), // Low Kick, Grass Knot
    SECRET_POWER(197),
    DMG_RECOIL_33(198), // Double-Edge, Brave Bird, Wood Hammer
    NO_DMG_CNF_ALL_ADJACENT(199), // Teeter Dance
    DMG_BRN_INCR_CRIT(200), // Blaze Kick
    MUD_SPORT(201),
    DMG_BAD_POISON(202), // Poison Fang
    WEATHER_BALL(203),
    DMG_USER_SPA_MINUS_2(204), // Overheat, Psycho Boost, Draco Meteor, etc.
    TRGT_ATK_DEF_MINUS_1(205), // Tickle
    USER_DEF_SPD_PLUS_1(206), // Cosmic Power, Defend Order
    SKY_UPPERCUT(207),
    USER_ATK_DEF_PLUS_1(208), // Bulk Up
    DMG_POISON_INCR_CRIT(209), // Poison Tail, Cross Poison
    WATER_SPORT(210),
    USER_SPA_SPD_PLUS_1(211), // Calm Mind
    USER_ATK_SPE_PLUS_1(212), // Dragon Dance
    CAMOUFLAGE(213),
    ROOST(214),
    GRAVITY(215),
    MIRACLE_EYE(216),
    WAKE_UP_SLAP(217),
    DMG_USER_SPE_MINUS_1(218), // Hammer Arm
    SLOWER(219),
    HEALING_WISH(220),
    BRINE(221),
    NAUTRAL_GIFT(222),
    FEINT(223),
    PLUCK(224), // Pluck, Bug Bite
    TAILWIND(225),
    ACUPRESSURE(226),
    METAL_BURST(227),
    U_TURN(228), // U-turn, Volt Switch
    DMG_USER_DEF_SPD_MINUS_1(229), // Close Combat, Dragon Ascent
    PAYBACK(230),
    ASSURANCE(231),
    EMBARGO(232),
    FLING(233),
    PSYCHO_SHIFT(234),
    TRUMP_CARD(235),
    HEAL_BLOCK(236),
    DMG_HIGH_TRGT_HP(237), // Wring Out, Crush Grip
    POWER_TRICK(238),
    GASTRO_ACID(239),
    LUCKY_CHANT(240),
    ME_FIRST(241),
    COPYCAT(242),
    POWER_SWAP(243),
    GUARD_SWAP(244),
    PUNISHMENT(245),
    LAST_RESORT(246),
    WORRY_SEED(247),
    SUCKER_PUNCH(248),
    TOXIC_SPIKES(249),
    HEART_SWAP(250),
    AQUA_RING(251),
    MAGNET_RISE(252),
    DMG_BRN_RECOIL_33(253), // Flare Blitz
    STRUGGLE(254),
    DIVE(255),
    DIG(256),
    SURF(257),
    DEFOG(258),
    TRICK_ROOM(259),
    BLIZZARD(260),
    WHIRLPOOL(261),
    DMG_PARA_RECOIL_33(262), // Volt Tackle
    BOUNCE(263),
    CAPTIVATE(265),
    STEALTH_ROCK(266),
    CHATTER(267),
    JUDGMENT(268), // Judgment, Techno Blast
    DMG_RECOIL_50(269), // Head Smash, Light of Ruin
    LUNAR_DANCE(270),
    DMG_TRGT_SPD_MINUS_2(271), // Seed Flare
    GHOST_FORCE(272), // Shadow Force, Phantom Force
    DMG_BRN_FLINCH(273), // Fire Fang
    DMG_FRZ_FLINCH(274), // Ice Fang
    DMG_PARA_FLINCH(275), // Thunder Fang
    DMG_USER_SPA_PLUS_1(276), // Charge Beam, Fiery Dance
    USER_ATK_ACC_PLUS_1(277), // Hone Claws
    WIDE_GUARD(278),
    GUARD_SPLIT(279),
    POWER_SPLIT(280),
    WONDER_ROOM(281),
    PHYSICAL_DMG(282), // Psyshock, Psystrike, Secret Sword
    VENOSHOCK(283),
    AUTOTOMIZE(284),
    TELEKINESIS(285),
    MAGIC_ROOM(286),
    SMACK_DOWN(287),
    DMG_ALWAYS_CRIT(288), // Storm Throw, Frost Breath
    FLAME_BURST(289),
    USER_SPA_SPD_SPE_PLUS_2(290), // Quiver Dance
    HEAVY_SLAM(291), // Heavy Slam, Heat Crash
    SYNCHRONOISE(292),
    ELECTRO_BALL(293),
    SOAK(294),
    DMG_USER_SPE_PLUS_1(295), // Flame Charge
    DMG_TRGT_SPD_MINUS_2_ALWAYS(296), // Acid Spray
    FOUL_PLAY(297),
    SIMPLE_BEAM(298),
    ENTRAINMENT(299),
    AFTER_YOU(300),
    ROUND(301),
    ECHOED_VOICE(302),
    CHIP_AWAY(303), // Chip Away, Sacred Sword
    CLEAR_SMOG(304),
    STORED_POWER(305),
    QUICK_GUARD(306),
    ALLY_SWITCH(307),
    SHELL_SMASH(308),
    TRGT_HEAL(309), // Heal Pulse
    HEX(310),
    SKY_DROP(311),
    USER_SPE_PLUS_2_ATK_PLUS_1(312), // Shift Gear
    DMG_FORCE_SWITCH(313), // Circle Throw, Dragon Tail
    INCINERATE(314),
    QUASH(315),
    GROWTH(316),
    ACROBATICS(317),
    REFLECT_TYPE(318),
    RETALIATE(319),
    FINAL_GAMBIT(320),
    USER_SPA_PLUS_3(321),
    USER_ATK_DEF_ACC_PLUS_1(322), // Coil
    BESTOW(323),
    WATER_PLEDGE(324),
    FIRE_PLEDGE(325),
    GRASS_PLEDGE(326),
    USER_ATK_SPA_PLUS_1(327), // Work Up
    USER_DEF_PLUS_3(328), // Cotton Guard
    DMG_SLP(329), // Relic Song
    GLACIATE(330),
    FREEZE_SHOCK(331),
    ICE_BURN(332),
    HURRICANE(337, 333),
    DMG_USER_SPE_DEF_SPD_MINUS_1(334), // V-create
    FUSION_FLARE(335),
    FUSION_BOLT(336),
    FLYING_PRESS(337),
    BELCH(338),
    ROTOTILLER(339),
    STICKY_WEB(340),
    FELL_STINGER(341),
    TRICK_OR_TREAT(342),
    NOBLE_ROAR(343),
    ION_DELUGE(344),
    PARABOLIC_CHARGE(345),
    PARTING_SHOT(346),
    TOPSY_TURVY(347),
    DMG_RECOIL_75(348), // Draining Kiss, Oblivion Wing
    CRAFTY_SHIELD(349),
    FLOWER_SHIELD(350),
    GRASSY_TERRAIN(351),
    MISTY_TERRAIN(352),
    ELECTRIFY(353),
    FAIRY_LOCK(354),
    KINGS_SHIELD(355),
    PLAY_NICE(356),
    CONFIDE(357),
    DIAMOND_STORM(358),
    HYPERSPACE(359),
    WATER_SHURIKEN(360),
    SPIKY_SHIELD(361),
    AROMATIC_MIST(362),
    VENOM_DRENCH(363),
    BABY_DOLL_EYES(364),
    GEOMANCY(365),
    MAGNETIC_FLUX(366),
    HAPPY_HOUR(367),
    ELECTRIC_TERRAIN(368),
    CELEBRATE(369),
    HOLD_HANDS(370),
    NUZZLE(371),
    THOUSAND_ARROWS(372),
    THOUSAND_WAVES(373),
    POWER_UP_PUNCH(374),
    FORESTS_CURSE(375),
    MAT_BLOCK(376),
    POWDER(377),
    BURST(378),
    FREEZE_DRY(379),
    DISARMING_VOICE(380);

    final int gen5;
    final int gen6;

    MoveEffect(int id) {
        this.gen5 = id;
        this.gen6 = id;
    }

    MoveEffect(int gen5, int gen6) {
        this.gen5 = gen5;
        this.gen6 = gen6;
    }

    public int getIndex(int generation) {
        switch (generation) {
            case 5:
                return gen5;
            case 6:
                return gen6;
            default:
                return -1;
        }
    }

    public static MoveEffect fromIndex(int generation, int index) {
        for (MoveEffect moveEffect: MoveEffect.values()) {
            if (index == moveEffect.getIndex(generation))
                return moveEffect;
        }

        return null;
    }
}
