package com.dabomstew.pkrandom.pokemon;

public enum MoveEffect {
    DMG(0, MoveQualities.DAMAGE), // Pound, Mega Punch, Scratch, etc.
    NO_DMG_SLP(1, MoveQualities.NO_DAMAGE_STATUS, MoveStatusType.SLEEP), // Sing, SLP Powder, Hypnosis, Etc.
    DMG_PSN(2, MoveQualities.DAMAGE_TARGET_STATUS, MoveStatusType.POISON), // Poison Sting, Smog, Sludge, etc.
    DMG_RECOVER(3, MoveQualities.DRAIN_HEALTH), // Absorb, Mega Drain, Leech Life, etc.
    DMG_BRN(4, MoveQualities.DAMAGE_TARGET_STATUS, MoveStatusType.BURN), // Fire Punch, Ember, Flamethrower, etc.
    DMG_FRZ(5, MoveQualities.DAMAGE_TARGET_STATUS, MoveStatusType.FREEZE), // Ice Punch, Ice Beam, Powder Snow, etc.
    DMG_PAR(6, MoveQualities.DAMAGE_TARGET_STATUS, MoveStatusType.PARALYZE), // Thunder Punch, Body Slam, Thunder Shock, etc.
    EXPLOSION(7, MoveQualities.DAMAGE), // Self-Destruct, Explosion
    DREAM_EATER(8),
    MIRROR_MOVE(9),
    USER_ATK_PLUS_1(10, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.ATTACK), // Meditate, Sharpen Howl
    USER_DEF_PLUS_1(11, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.DEFENSE), // Harden, Withdraw
    USER_SPE_PLUS_1(12, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.SPEED), // UNUSED
    USER_SPA_PLUS_1(13, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.SPECIAL_ATTACK), // UNUSED
    USER_SPD_PLUS_1(14, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.SPECIAL_DEFENSE), // UNUSED
    USER_ACC_PLUS_1(15, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.ACCURACY), // UNUSED
    USER_EVA_PLUS_1(16, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.EVASION), // Double Team
    NEVER_MISSES(17, MoveQualities.DAMAGE), // Swift, Feint Attack, Shadow Punch, etc.
    TRGT_ATK_MINUS_1(18, MoveQualities.NO_DAMAGE_STAT_CHANGE, -1, StatChangeType.ATTACK), // Growl
    TRGT_DEF_MINUS_1(19, MoveQualities.NO_DAMAGE_STAT_CHANGE, -1, StatChangeType.DEFENSE), // Tail Whip, Leer
    TRGT_SPE_MINUS_1(20, MoveQualities.NO_DAMAGE_STAT_CHANGE, -1, StatChangeType.SPEED), // String Shot (Gen V), Low Sweep, Electro Web
    TRGT_SPA_MINUS_1(21, MoveQualities.NO_DAMAGE_STAT_CHANGE, -1, StatChangeType.SPECIAL_ATTACK), // UNUSED
    TRGT_SPD_MINUS_1(22, MoveQualities.NO_DAMAGE_STAT_CHANGE, -1, StatChangeType.SPECIAL_DEFENSE), // UNUSED
    TRGT_ACC_MINUS_1(23, MoveQualities.NO_DAMAGE_STAT_CHANGE, -1, StatChangeType.ACCURACY), // Sand Attack, Smokescreen, etc.
    TRGT_EVA_MINUS_1(24, MoveQualities.NO_DAMAGE_STAT_CHANGE, -1, StatChangeType.EVASION), // Sweet Scent
    HAZE(25),
    BIDE(26),
    THRASH_ABOUT(27, MoveQualities.DAMAGE), // Thrash, Petal Dance, Outrage
    NO_DMG_FORCE_SWITCH(28, MoveQualities.FORCE_SWITCH), // Whirlwind, Roar
    HIT_2_TO_5_TIMES(29, MoveQualities.DAMAGE), // Double Slap, Comet Punch, Fury Attack, ec.
    CONVERSION(30),
    DMG_FLINCH(31, MoveQualities.DAMAGE), // Rolling Kick, Headbutt, Bite, etc.
    RECOVER_HP_50(32, MoveQualities.HEAL), // Recover, Soft-Boiled, Milk Drink, etc.
    TOXIC(33, MoveQualities.NO_DAMAGE_STATUS, MoveStatusType.TOXIC_POISON),
    PAY_DAY(34, MoveQualities.DAMAGE),
    LIGHT_SCREEN(35),
    TRI_ATK(36, MoveQualities.DAMAGE),
    REST(37),
    OHKO(38, MoveQualities.OHKO), // Guillotine, Horn Drill, Fissure, Sheer Cold
    RAZOR_WIND(39, MoveQualities.DAMAGE),
    DIRECT_HALF(40, MoveQualities.DAMAGE), // Super Fang
    DIRECT_40(41, MoveQualities.DAMAGE), // Dragon Rage
    DMG_TRAP(42, MoveQualities.DAMAGE), // Bind, Wrap, Fire Spin, etc.
    DMG_INCR_CRIT(43, MoveQualities.DAMAGE), // Karate Chop, Razor Leaf, Crabhammer, etc.
    HIT_2_TIMES(44, MoveQualities.DAMAGE), // Double Kick,  Bonemerang, Double Hit, etc.
    JUMP_KICK(45, MoveQualities.DAMAGE), // Jump Kick, High Jump Kick
    MIST(46),
    CRIT_RATIO_PLUS_2(47, MoveQualities.DAMAGE), // Focus Energy
    DMG_RECOIL_25(48, MoveQualities.DAMAGE), // Take Down, Submission, Wild Charge
    NO_DMG_CNF(49, MoveQualities.NO_DAMAGE_STATUS, MoveStatusType.CONFUSION), // Supersonic, Confuse Ray, Sweet Kiss
    USER_ATK_PLUS_2(50, MoveQualities.NO_DAMAGE_STAT_CHANGE, 2, StatChangeType.ATTACK), // Swords Dance
    USER_DEF_PLUS_2(51, MoveQualities.NO_DAMAGE_STAT_CHANGE, 2, StatChangeType.DEFENSE), // Barrier, Acid Armor, Iron Defense
    USER_SPE_PLUS_2(52, MoveQualities.NO_DAMAGE_STAT_CHANGE, 2, StatChangeType.SPEED), // Agility, Rock Polish
    USER_SPA_PLUS_2(53, MoveQualities.NO_DAMAGE_STAT_CHANGE, 2, StatChangeType.SPECIAL_ATTACK), // Nasty Plot
    USER_SPD_PLUS_2(54, MoveQualities.NO_DAMAGE_STAT_CHANGE, 2, StatChangeType.SPECIAL_DEFENSE), // Amnesia
    USER_ACC_PLUS_2(55, MoveQualities.NO_DAMAGE_STAT_CHANGE, 2, StatChangeType.ACCURACY), // Unused
    USER_EVA_PLUS_2(56, MoveQualities.NO_DAMAGE_STAT_CHANGE, 2, StatChangeType.EVASION), // Unused
    TRANSFORM(57),
    TRGT_ATK_MINUS_2(58, MoveQualities.NO_DAMAGE_STAT_CHANGE, -2, StatChangeType.ATTACK), // Charm, Feather Dance
    TRGT_DEF_MINUS_2(59, MoveQualities.NO_DAMAGE_STAT_CHANGE, -2, StatChangeType.DEFENSE), // Screech
    TRGT_SPE_MINUS_2(60, MoveQualities.NO_DAMAGE_STAT_CHANGE, -2, StatChangeType.SPEED), // String Shot (Gen VI+), Cotton Spore, Scary Face
    TRGT_SPA_MINUS_2(61, MoveQualities.NO_DAMAGE_STAT_CHANGE, -2, StatChangeType.SPECIAL_ATTACK), // Eerie Impulse
    TRGT_SPD_MINUS_2(62, MoveQualities.NO_DAMAGE_STAT_CHANGE, -2, StatChangeType.SPECIAL_DEFENSE), // Fake Tears, Metal Sound
    TRGT_ACC_MINUS_2(63, MoveQualities.NO_DAMAGE_STAT_CHANGE, -2, StatChangeType.ACCURACY), // UNUSED
    TRGT_EVA_MINUS_2(64, MoveQualities.NO_DAMAGE_STAT_CHANGE, -2, StatChangeType.EVASION), // UNUSED
    REFLECT(65),
    NO_DMG_PSN(66, MoveQualities.NO_DAMAGE_STATUS, MoveStatusType.POISON), // Poison Powder, Poison Gas
    NO_DMG_PAR(67, MoveQualities.NO_DAMAGE_STATUS, MoveStatusType.PARALYZE), // Stun Spore, Thunder Wave, Glare
    DMG_TRGT_ATK_MINUS_1(68, MoveQualities.DAMAGE_TARGET_STAT_CHANGE, -1, StatChangeType.ATTACK), // Aurora Beam, Play Rough
    DMG_TRGT_DEF_MINUS_1(69, MoveQualities.DAMAGE_TARGET_STAT_CHANGE, -1, StatChangeType.DEFENSE), // Iron Tail, Crunch, Rock Smash, etc.
    DMG_TRGT_SPE_MINUS_1(70, MoveQualities.DAMAGE_TARGET_STAT_CHANGE, -1, StatChangeType.SPEED), // Bubble Beam, Constrict, Bubble, etc.
    DMG_TRGT_SPA_MINUS_1(71, MoveQualities.DAMAGE_TARGET_STAT_CHANGE, -1, StatChangeType.SPECIAL_ATTACK), // Mist Ball, Struggle Bug, Snarl, Moon Blast, Mystical Fire
    DMG_TRGT_SPD_MINUS_1(72, MoveQualities.DAMAGE_TARGET_STAT_CHANGE, -1, StatChangeType.SPECIAL_DEFENSE), // Acid, Psychic, Shadow Ball, etc.
    DMG_TRGT_ACC_MINUS_1(73, MoveQualities.DAMAGE_TARGET_STAT_CHANGE, -1, StatChangeType.ACCURACY), // Mud-Slap, Octazooka, Muddy Water, etc.
    DMG_TRGT_EVA_MINUS_1(74, MoveQualities.DAMAGE_TARGET_STAT_CHANGE, -1, StatChangeType.EVASION), // UNUSED
    SKY_ATTACK(75, MoveQualities.DAMAGE),
    DMG_CNF(76, MoveQualities.DAMAGE_TARGET_STATUS, MoveStatusType.CONFUSION), // Psybeam, Confusion, Dizzy Punch, etc.
    HIT_2_TIMES_POISON(77, MoveQualities.DAMAGE_TARGET_STATUS, MoveStatusType.POISON), // Twineedle
    DMG_DCR_PRIORITY(78, MoveQualities.DAMAGE), // Vital Throw
    SUBSTITUTE(79),
    DMG_RECHARGE(80, MoveQualities.DAMAGE), // Hyper Beam, Blast Burn, Hydro Cannon, etc.
    RAGE(81, MoveQualities.DAMAGE),
    MIMIC(82),
    METRONOME(83),
    LEECH_SEED(84),
    SPLASH(85),
    DISABLE(86),
    DIRECT_DMG_LEVEL(87, MoveQualities.DAMAGE), // Seismic Toss, Night Shade
    PSYWAVE(88, MoveQualities.DAMAGE), // Psywave
    COUNTER(89),
    ENCORE(90),
    PAIN_SPLIT(91),
    SNORE(92, MoveQualities.DAMAGE),
    CONVERSION_2(93),
    MIND_READER(94), // Mind Reader, Lock-On
    SKETCH(95),
    SLP_TALK(97, MoveQualities.DAMAGE),
    DESTINY_BOND(98),
    DMG_LOW_HP(99, MoveQualities.DAMAGE), // Flail, Reversal
    SPITE(100),
    FALSE_SWIPE(101, MoveQualities.DAMAGE), // False Swipe, Hold Back
    HEAL_TEAM_STATUS(102), // Heal Bell, Aromatherapy
    DMG_INCR_PRIO(103, MoveQualities.DAMAGE), // Quick Attaack, Mach Punch, Extreme Speed, etc.
    TRIPLE_KICK(104, MoveQualities.DAMAGE),
    DMG_TAKE_ITEM(105, MoveQualities.DAMAGE), // Thief, Covet
    PREVENT_ESCAPE(106), // Spider Web, Mean Look, Block
    NIGHTMARE(107),
    MINIMIZE(108, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.EVASION),
    CURSE(109),
    PROTECT(111), // Protect, Detect
    SPIKES(112),
    FORESIGHT(113), // Foresight, Odor Sleuth
    PERISH_SONG(114),
    SANDSTORM(115),
    ENDURE(116),
    ROLLOUT(117, MoveQualities.DAMAGE), // Rollout, Ice Ball
    SWAGGER(118, MoveQualities.NO_DAMAGE_STAT_CHANGE_STATUS, MoveStatusType.CONFUSION, 2, StatChangeType.ATTACK),
    FURY_CUTTER(119, MoveQualities.DAMAGE),
    NO_DMG_INF(120), // Attract
    RETURN(121, MoveQualities.DAMAGE),
    PRESENT(122),
    FRUSTRATION(123, MoveQualities.DAMAGE),
    SAFEGUARD(124),
    DMG_BRN_THAW(125, MoveQualities.DAMAGE_TARGET_STATUS, MoveStatusType.BURN), // Flame Wheel, Sacred Fire
    MAGNITUDE(126, MoveQualities.DAMAGE),
    BATON_PASS(127),
    PURSUIT(128, MoveQualities.DAMAGE),
    RAPID_SPIN(129, MoveQualities.DAMAGE),
    DIRECT_DMG_20(130, MoveQualities.DAMAGE), // Sonic Boom
    RECOVER_HP_50_WEATHER(132, MoveQualities.HEAL), // Morning Sun, Synthesis, Moonlight
    SYNTHESIS(133, -1, -1, -1),
    MOONLIGHT(134, -1, -1, -1),
    HIDDEN_POWER(135, MoveQualities.DAMAGE),
    RAIN_DANCE(136),
    SUNNY_DAY(137),
    DMG_USER_DEF_PLUS_1(138, MoveQualities.DAMAGE_USER_STAT_CHANGE, 1, StatChangeType.DEFENSE), // Steel Wing
    DMG_USER_ATK_PLUS_1(139, MoveQualities.DAMAGE_USER_STAT_CHANGE, 1, StatChangeType.ATTACK), // Metal Claw, Meteor Mash
    DMG_ALL_USER_STATS_PLUS_1(140, MoveQualities.DAMAGE_USER_STAT_CHANGE, 1, StatChangeType.ALL), // Ancient Power, Silver Wind, Ominous Wind
    BELLY_DRUM(142),
    PSYCH_UP(143),
    MIRROR_COAT(144),
    SKULL_BASH(145, MoveQualities.DAMAGE),
    TWISTER(146, MoveQualities.DAMAGE),
    EARTHQUAKE(147, MoveQualities.DAMAGE),
    FUTURE_SIGHT(148, MoveQualities.DAMAGE), // Future Sight, Doom Desire
    GUST(149, MoveQualities.DAMAGE),
    STOMP(150, MoveQualities.DAMAGE), // Stomp, Steamroller
    SOLAR_BEAM(151, MoveQualities.DAMAGE),
    THUNDER(152, MoveQualities.DAMAGE),
    TELEPORT(153),
    BEAT_UP(154, MoveQualities.DAMAGE),
    FLY(155, MoveQualities.DAMAGE),
    DEFENSE_CURL(156, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.DEFENSE),
    SOFT_BOILED(157, -1, -1, -1), // only in Gen III?
    FAKE_OUT(158, MoveQualities.DAMAGE),
    UPROAR(159, MoveQualities.DAMAGE),
    STOCKPILE(160),
    SPIT_UP(161, MoveQualities.DAMAGE),
    SWALLOW(162),
    HAIL(164),
    TORMENT(165),
    FLATTER(166, MoveQualities.NO_DAMAGE_STAT_CHANGE_STATUS, MoveStatusType.CONFUSION, 1, StatChangeType.SPECIAL_ATTACK),
    NO_DMG_BRN(167, MoveQualities.NO_DAMAGE_STATUS, MoveStatusType.BURN),
    MEMENTO(168),
    FACADE(169, MoveQualities.DAMAGE),
    FOCUS_PUNCH(170, MoveQualities.DAMAGE),
    SMELLING_SALTS(171, MoveQualities.DAMAGE),
    FOLLOW_ME(172), // Follow Me, Rage Powder
    NATURE_POWER(173, MoveQualities.DAMAGE),
    CHARGE(174, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.SPECIAL_DEFENSE),
    TAUNT(175),
    HELPING_HAND(176),
    TRICK(177), // Trick, Switcheroo
    ROLE_PLAY(178),
    WISH(179),
    ASSIST(180),
    INGRAIN(181),
    DMG_USER_ATK_DEF_MINUS_1(182, MoveQualities.DAMAGE_USER_STAT_CHANGE, -1, StatChangeType.ATTACK, StatChangeType.DEFENSE),
    MAGIC_COAT(183),
    RECYCLE(184),
    REVENGE(185, MoveQualities.DAMAGE), // Revenge, Avalanche
    BRICK_BREAK(186, MoveQualities.DAMAGE),
    NO_DMG_DROWSY(187), // Yawn
    KNOCK_OFF(188, MoveQualities.DAMAGE),
    ENDEAVOR(189),
    DMG_HIGH_USER_HP(190, MoveQualities.DAMAGE), // Eruption, Water Spout
    SKILL_SWAP(191),
    IMPRISON(192),
    REFRESH(193),
    GRUDGE(194),
    SNATCH(195),
    LOW_KICK(196, MoveQualities.DAMAGE), // Low Kick, Grass Knot
    SECRET_POWER(197, MoveQualities.DAMAGE),
    DMG_RECOIL_33(198, MoveQualities.DAMAGE), // Double-Edge, Brave Bird, Wood Hammer
    NO_DMG_CNF_ALL_ADJACENT(199, MoveQualities.NO_DAMAGE_STATUS, MoveStatusType.CONFUSION), // Teeter Dance
    DMG_BRN_INCR_CRIT(200, MoveQualities.DAMAGE_TARGET_STATUS, MoveStatusType.BURN), // Blaze Kick
    MUD_SPORT(201),
    DMG_BAD_POISON(202, MoveQualities.DAMAGE_TARGET_STATUS, MoveStatusType.TOXIC_POISON), // Poison Fang
    WEATHER_BALL(203, MoveQualities.DAMAGE),
    DMG_USER_SPA_MINUS_2(204, MoveQualities.DAMAGE_USER_STAT_CHANGE, -2, StatChangeType.SPECIAL_ATTACK), // Overheat, Psycho Boost, Draco Meteor, etc.
    TRGT_ATK_DEF_MINUS_1(205, MoveQualities.NO_DAMAGE_STAT_CHANGE, -1, StatChangeType.ATTACK, StatChangeType.DEFENSE), // Tickle
    USER_DEF_SPD_PLUS_1(206, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.DEFENSE, StatChangeType.SPECIAL_DEFENSE), // Cosmic Power, Defend Order
    SKY_UPPERCUT(207, MoveQualities.DAMAGE),
    USER_ATK_DEF_PLUS_1(208, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.ATTACK, StatChangeType.DEFENSE), // Bulk Up
    DMG_PSN_INCR_CRIT(209, MoveQualities.DAMAGE_TARGET_STATUS, MoveStatusType.POISON), // Poison Tail, Cross Poison
    WATER_SPORT(210),
    USER_SPA_SPD_PLUS_1(211, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.SPECIAL_ATTACK, StatChangeType.SPECIAL_DEFENSE), // Calm Mind
    USER_ATK_SPE_PLUS_1(212, MoveQualities.NO_DAMAGE_STAT_CHANGE, 1, StatChangeType.ATTACK, StatChangeType.SPEED), // Dragon Dance
    CAMOUFLAGE(213),

    // Gen IV+
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
    DMG_PAR_RECOIL_33(262), // Volt Tackle
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
    DMG_PAR_FLINCH(275), // Thunder Fang
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
    HURRICANE(-1, 337, 333, 333), // TODO: Confirm this for Gen IV
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
    DISARMING_VOICE(380),
    SHORE_UP(381),
    FIRST_IMPRESSION(382),
    BANEFUL_BUNKER(383),
    DMG_NO_ESCAPE(384), // Spirit Shackle, Anchor Shot
    SPARKLING_ARIA(385),
    FLORAL_HEALING(386),
    STRENGTH_SAP(387),
    SPOTLIGHT(388),
    TOXIC_THREAD(389),
    LASER_FOCUS(390),
    GEAR_UP(391),
    THROAT_CHOP(392),
    POLLEN_PUFF(393),
    PSYCHIC_TERRAIN(394),
    DMG_TRGT_ATK_MINUS_1_ALWAYS(395), // Lunge, Trop Kick
    DMG_TRGT_DEF_MINUS_1_ALWAYS(396), // Fire Lash
    BURN_UP(397),
    SPEED_SWAP(398),
    PURIFY(399),
    REVELATION_DANCE(400),
    CORE_ENFORCER(401),
    INSTRUCT(402),
    BEAK_BLAST(403),
    CLANGING_SCALES(404),
    BRUTAL_SWING(405),
    AURORA_VEIL(406),
    SHELL_TRAP(407),
    STOMPING_TANTRUM(408),
    SPECTRAL_THIEF(409),
    DMG_IGNORE_ABILITY(410),
    TEARFUL_LOOK(411),
    GUARDIAN_OF_ALOLA(412),
    EXTREME_EVOBOOST(413),
    GENESIS_SUPERNOVA(414),
    
    // Needs research
    EXPANDING_FORCE(425),
    GRASSY_GLIDE(426),
    RISING_VOLTAGE(427),
    REMOVES_ITEM(428),
    DMG_USER_DEF_PLUS_2(429),
    DOUBLE_ON_STATUS_BURN(430),
    
    BODY_PRESS(437),
    STEEL_BEAM(438),
    SCALE_SHOT(439),
    TRIPLE_AXEL(440),
    HITS_THREE_TIMES_CRIT(441),
    DIRE_CLAW(442),
    DMG_USER_DEF_SPD_PLUS_1(443),
    DMG_STEALTH_ROCK(444),
    DMG_SPIKES(445),
    USER_ATK_DEF_SPE_PLUS_1(446),
    DOUBLE_ON_POISON(447),
    TRIPLE_ARROWS(447),
    DMG_USER_SPE_MINUS_2(448),
    ICE_SPINNER(449),
    GLAIVE_RUSH(450),
    SALT_CURE(451),
    ELECTRO_SHOT(452),
    ALLURING_VOICE(453),
    
    TRGT_SPE_MINUS_3(456),
    HOWL(457),
    OCTAZOOKA(458),
    RAGE_FIST(459),
    MORTAL_SPIN(460),
    
    BURNING_JEALOUSY(462),
    POLTERGEIST(463),
    LIFE_DEW(464);
    
    final int gen3;
    final int gen4;
    final int gen5;
    final int gen6;

    public final MoveQualities qualities;
    final StatChangeType[] statChangeTypes = new StatChangeType[3];
    final int statChangeStages;
    final MoveStatusType status;

    MoveEffect(int id) {
        this.gen3 = id;
        this.gen4 = id;
        this.gen5 = id;
        this.gen6 = id;
        this.qualities = MoveQualities.OTHER;
        this.statChangeStages = 0;
        this.status = null;
    }

    MoveEffect(int id, MoveQualities qualities) {
        this.gen3 = id;
        this.gen4 = id;
        this.gen5 = id;
        this.gen6 = id;
        this.qualities = qualities;
        this.statChangeStages = 0;
        this.status = null;
    }
    
    MoveEffect(int id, MoveQualities qualities, int statChangeStages, StatChangeType... statChangeTypes) {
        if (qualities != MoveQualities.NO_DAMAGE_STAT_CHANGE && qualities != MoveQualities.DAMAGE_USER_STAT_CHANGE && qualities != MoveQualities.DAMAGE_TARGET_STAT_CHANGE)
            throw new RuntimeException();
        
        this.gen3 = id;
        this.gen4 = id;
        this.gen5 = id;
        this.gen6 = id;
        this.qualities = qualities;
        System.arraycopy(statChangeTypes, 0, this.statChangeTypes, 0, statChangeTypes.length);
        this.statChangeStages = statChangeStages;
        this.status = null;
    }
    
    MoveEffect(int id, MoveQualities qualities, MoveStatusType status) {
        if (qualities != MoveQualities.NO_DAMAGE_STATUS && qualities != MoveQualities.DAMAGE_TARGET_STATUS)
            throw new RuntimeException();
        
        this.gen3 = id;
        this.gen4 = id;
        this.gen5 = id;
        this.gen6 = id;
        this.qualities = qualities;
        this.statChangeStages = 0;
        this.status = status;
    }
    
    MoveEffect(int id, MoveQualities qualities, MoveStatusType status, int statChangeStages, StatChangeType... statChangeTypes) {
        if (qualities != MoveQualities.NO_DAMAGE_STAT_CHANGE_STATUS)
            throw new RuntimeException();
        
        this.gen3 = id;
        this.gen4 = id;
        this.gen5 = id;
        this.gen6 = id;
        this.qualities = qualities;
        this.status = status;
        System.arraycopy(statChangeTypes, 0, this.statChangeTypes, 0, statChangeTypes.length);
        this.statChangeStages = statChangeStages;
    }

    MoveEffect(int gen3, int gen4, int gen5, int gen6) {
        this.gen3 = gen3;
        this.gen4 = gen4;
        this.gen5 = gen5;
        this.gen6 = gen6;
        this.qualities = null;
        this.statChangeStages = 0;
        this.status = null;
    }

    public int getIndex(int generation) {
        return switch (generation) {
            case 3 -> gen3;
            case 4 -> gen4;
            case 5 -> gen5;
            case 6 -> gen6;
            default -> -1;
        };
    }

    public static MoveEffect fromIndex(int generation, int index) {
        for (MoveEffect moveEffect : MoveEffect.values()) {
            if (index == moveEffect.getIndex(generation))
                return moveEffect;
        }

        return null;
    }

    public MoveQualities getQualities() {
        return qualities;
    }
    
    public MoveStatusType getStatusType() {
        return status;
    }
    
    public static MoveEffect fromStatus(MoveStatusType status) {
        for (MoveEffect moveEffect : MoveEffect.values()) {
            if (moveEffect.status == status)
                return moveEffect;
        }

        return null;
    }

    public Move.StatChange[] getStatChanges() {
        if (qualities != MoveQualities.NO_DAMAGE_STAT_CHANGE && qualities != MoveQualities.DAMAGE_USER_STAT_CHANGE && qualities != MoveQualities.DAMAGE_TARGET_STAT_CHANGE)
            return null;
        
        Move.StatChange[] statChanges = new Move.StatChange[3];
        for (int i = 0; i < statChanges.length; i++)
            statChanges[i] = statChangeTypes[i] != null ? new Move.StatChange(statChangeTypes[i], statChangeStages) : new Move.StatChange();
        
        return statChanges;
    }
    
    public static MoveEffect fromStatChange(MoveQualities qualities, int stages, StatChangeType... statChangeTypes) {
        for (MoveEffect moveEffect : MoveEffect.values()) {
            if (qualities != moveEffect.qualities)
                continue;
            if (stages != moveEffect.statChangeStages)
                continue;
            if (statChangeTypes.length != moveEffect.statChangeTypes.length)
                continue;
            
            boolean matches = true;
            for (int i = 0; i < statChangeTypes.length; i++) {
                if (statChangeTypes[i] != moveEffect.statChangeTypes[i]) {
                    matches = false;
                    break;
                }
            }
            
            if (matches)
                return moveEffect;
        }

        return null;
    }
}
