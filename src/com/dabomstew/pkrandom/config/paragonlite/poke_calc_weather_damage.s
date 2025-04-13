    push    {r4-r5, lr}
    mov     r4, r0
    mov     r5, r1
    
    cmp     r5, #WEATHER_Sand
    beq     Sandstorm
    
#else WEATHER_HAIL
    cmp     r5, #WEATHER_Hail
    beq     Both
#endif
    b       ReturnZero

Sandstorm:
#if WEATHER_SAND_NORMAL_IMMUNE && !WEATHER_HAIL_NORMAL_IMMUNE
    mov     r1, #TYPE_Normal
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Normal_Next
    b       ReturnZero
Sandstorm_Normal_Next:
#endif

#if WEATHER_SAND_FIGHTING_IMMUNE && !WEATHER_HAIL_FIGHTING_IMMUNE
    mov     r1, #TYPE_Fighting
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Fighting_Next
    b       ReturnZero
Sandstorm_Fighting_Next:
#endif

#if WEATHER_SAND_FLYING_IMMUNE && !WEATHER_HAIL_FLYING_IMMUNE
    mov     r1, #TYPE_Flying
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Flying_Next
    b       ReturnZero
Sandstorm_Flying_Next:
#endif

#if WEATHER_SAND_POISON_IMMUNE && !WEATHER_HAIL_POISON_IMMUNE
    mov     r1, #TYPE_Poison
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Poison_Next
    b       ReturnZero
Sandstorm_Poison_Next:
#endif

#if WEATHER_SAND_GROUND_IMMUNE && !WEATHER_HAIL_GROUND_IMMUNE
    mov     r1, #TYPE_Ground
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Ground_Next
    b       ReturnZero
Sandstorm_Ground_Next:
#endif

#if WEATHER_SAND_ROCK_IMMUNE && !WEATHER_HAIL_ROCK_IMMUNE
    mov     r1, #TYPE_Rock
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Rock_Next
    b       ReturnZero
Sandstorm_Rock_Next:
#endif

#if WEATHER_SAND_BUG_IMMUNE && !WEATHER_HAIL_BUG_IMMUNE
    mov     r1, #TYPE_Bug
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Bug_Next
    b       ReturnZero
Sandstorm_Bug_Next:
#endif

#if WEATHER_SAND_GHOST_IMMUNE && !WEATHER_HAIL_GHOST_IMMUNE
    mov     r1, #TYPE_Ghost
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Ghost_Next
    b       ReturnZero
Sandstorm_Ghost_Next:
#endif

#if WEATHER_SAND_STEEL_IMMUNE && !WEATHER_HAIL_STEEL_IMMUNE
    mov     r1, #TYPE_Steel
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Steel_Next
    b       ReturnZero
Sandstorm_Steel_Next:
#endif

#if WEATHER_SAND_FIRE_IMMUNE && !WEATHER_HAIL_FIRE_IMMUNE
    mov     r1, #TYPE_Fire
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Fire_Next
    b       ReturnZero
Sandstorm_Fire_Next:
#endif

#if WEATHER_SAND_WATER_IMMUNE && !WEATHER_HAIL_WATER_IMMUNE
    mov     r1, #TYPE_Water
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Water_Next
    b       ReturnZero
Sandstorm_Water_Next:
#endif

#if WEATHER_SAND_GRASS_IMMUNE && !WEATHER_HAIL_GRASS_IMMUNE
    mov     r1, #TYPE_Grass
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Grass_Next
    b       ReturnZero
Sandstorm_Grass_Next:
#endif

#if WEATHER_SAND_ELECTRIC_IMMUNE && !WEATHER_HAIL_ELECTRIC_IMMUNE
    mov     r1, #TYPE_Electric
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Electric_Next
    b       ReturnZero
Sandstorm_Electric_Next:
#endif

#if WEATHER_SAND_PSYCHIC_IMMUNE && !WEATHER_HAIL_PSYCHIC_IMMUNE
    mov     r1, #TYPE_Psychic
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Psychic_Next
    b       ReturnZero
Sandstorm_Psychic_Next:
#endif

#if WEATHER_SAND_ICE_IMMUNE && !WEATHER_HAIL_ICE_IMMUNE
    mov     r1, #TYPE_Ice
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Ice_Next
    b       ReturnZero
Sandstorm_Ice_Next:
#endif

#if WEATHER_SAND_DRAGON_IMMUNE && !WEATHER_HAIL_DRAGON_IMMUNE
    mov     r1, #TYPE_Dragon
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Dragon_Next
    b       ReturnZero
Sandstorm_Dragon_Next:
#endif

#if WEATHER_SAND_DARK_IMMUNE && !WEATHER_HAIL_DARK_IMMUNE
    mov     r1, #TYPE_Dark
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Dark_Next
    b       ReturnZero
Sandstorm_Dark_Next:
#endif

#if WEATHER_SAND_FAIRY_IMMUNE && !WEATHER_HAIL_FAIRY_IMMUNE
    mov     r1, #TYPE_Fairy
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Sandstorm_Fairy_Next
    b       ReturnZero
Sandstorm_Fairy_Next:
#endif


;
; BOTH
;

Both:
#if WEATHER_SAND_NORMAL_IMMUNE && WEATHER_HAIL_NORMAL_IMMUNE
    mov     r1, #TYPE_Normal
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Normal_Next
    b       ReturnZero
Both_Normal_Next:
#endif

#if WEATHER_SAND_FIGHTING_IMMUNE && WEATHER_HAIL_FIGHTING_IMMUNE
    mov     r1, #TYPE_Fighting
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Fighting_Next
    b       ReturnZero
Both_Fighting_Next:
#endif

#if WEATHER_SAND_FLYING_IMMUNE && WEATHER_HAIL_FLYING_IMMUNE
    mov     r1, #TYPE_Flying
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Flying_Next
    b       ReturnZero
Both_Flying_Next:
#endif

#if WEATHER_SAND_POISON_IMMUNE && WEATHER_HAIL_POISON_IMMUNE
    mov     r1, #TYPE_Poison
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Poison_Next
    b       ReturnZero
Both_Poison_Next:
#endif

#if WEATHER_SAND_GROUND_IMMUNE && WEATHER_HAIL_GROUND_IMMUNE
    mov     r1, #TYPE_Ground
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Ground_Next
    b       ReturnZero
Both_Ground_Next:
#endif

#if WEATHER_SAND_ROCK_IMMUNE && WEATHER_HAIL_ROCK_IMMUNE
    mov     r1, #TYPE_Rock
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Rock_Next
    b       ReturnZero
Both_Rock_Next:
#endif

#if WEATHER_SAND_BUG_IMMUNE && WEATHER_HAIL_BUG_IMMUNE
    mov     r1, #TYPE_Bug
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Bug_Next
    b       ReturnZero
Both_Bug_Next:
#endif

#if WEATHER_SAND_GHOST_IMMUNE && WEATHER_HAIL_GHOST_IMMUNE
    mov     r1, #TYPE_Ghost
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Ghost_Next
    b       ReturnZero
Both_Ghost_Next:
#endif

#if WEATHER_SAND_STEEL_IMMUNE && WEATHER_HAIL_STEEL_IMMUNE
    mov     r1, #TYPE_Steel
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Steel_Next
    b       ReturnZero
Both_Steel_Next:
#endif

#if WEATHER_SAND_FIRE_IMMUNE && WEATHER_HAIL_FIRE_IMMUNE
    mov     r1, #TYPE_Fire
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Fire_Next
    b       ReturnZero
Both_Fire_Next:
#endif

#if WEATHER_SAND_WATER_IMMUNE && WEATHER_HAIL_WATER_IMMUNE
    mov     r1, #TYPE_Water
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Water_Next
    b       ReturnZero
Both_Water_Next:
#endif

#if WEATHER_SAND_GRASS_IMMUNE && WEATHER_HAIL_GRASS_IMMUNE
    mov     r1, #TYPE_Grass
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Grass_Next
    b       ReturnZero
Both_Grass_Next:
#endif

#if WEATHER_SAND_ELECTRIC_IMMUNE && WEATHER_HAIL_ELECTRIC_IMMUNE
    mov     r1, #TYPE_Electric
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Electric_Next
    b       ReturnZero
Both_Electric_Next:
#endif

#if WEATHER_SAND_PSYCHIC_IMMUNE && WEATHER_HAIL_PSYCHIC_IMMUNE
    mov     r1, #TYPE_Psychic
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Psychic_Next
    b       ReturnZero
Both_Psychic_Next:
#endif

#if WEATHER_SAND_ICE_IMMUNE && WEATHER_HAIL_ICE_IMMUNE
    mov     r1, #TYPE_Ice
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Ice_Next
    b       ReturnZero
Both_Ice_Next:
#endif

#if WEATHER_SAND_DRAGON_IMMUNE && WEATHER_HAIL_DRAGON_IMMUNE
    mov     r1, #TYPE_Dragon
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Dragon_Next
    b       ReturnZero
Both_Dragon_Next:
#endif

#if WEATHER_SAND_DARK_IMMUNE && WEATHER_HAIL_DARK_IMMUNE
    mov     r1, #TYPE_Dark
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Dark_Next
    b       ReturnZero
Both_Dark_Next:
#endif

#if WEATHER_SAND_FAIRY_IMMUNE && WEATHER_HAIL_FAIRY_IMMUNE
    mov     r1, #TYPE_Fairy
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Both_Fairy_Next
    b       ReturnZero
Both_Fairy_Next:
#endif
    
    
;
; HAIL
;
    
CheckHail:
    cmp     r5, #WEATHER_Hail
    beq     Hail
    b       ReturnTrue
    
Hail:
#if !WEATHER_SAND_NORMAL_IMMUNE && WEATHER_HAIL_NORMAL_IMMUNE
    mov     r1, #TYPE_Normal
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Normal_Next
    b       ReturnZero
Hail_Normal_Next:
#endif

#if !WEATHER_SAND_FIGHTING_IMMUNE && WEATHER_HAIL_FIGHTING_IMMUNE
    mov     r1, #TYPE_Fighting
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Fighting_Next
    b       ReturnZero
Hail_Fighting_Next:
#endif

#if !WEATHER_SAND_FLYING_IMMUNE && WEATHER_HAIL_FLYING_IMMUNE
    mov     r1, #TYPE_Flying
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Flying_Next
    b       ReturnZero
Hail_Flying_Next:
#endif

#if !WEATHER_SAND_POISON_IMMUNE && WEATHER_HAIL_POISON_IMMUNE
    mov     r1, #TYPE_Poison
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Poison_Next
    b       ReturnZero
Hail_Poison_Next:
#endif

#if !WEATHER_SAND_GROUND_IMMUNE && WEATHER_HAIL_GROUND_IMMUNE
    mov     r1, #TYPE_Ground
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Ground_Next
    b       ReturnZero
Hail_Ground_Next:
#endif

#if !WEATHER_SAND_ROCK_IMMUNE && WEATHER_HAIL_ROCK_IMMUNE
    mov     r1, #TYPE_Rock
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Rock_Next
    b       ReturnZero
Hail_Rock_Next:
#endif

#if !WEATHER_SAND_BUG_IMMUNE && WEATHER_HAIL_BUG_IMMUNE
    mov     r1, #TYPE_Bug
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Bug_Next
    b       ReturnZero
Hail_Bug_Next:
#endif

#if !WEATHER_SAND_GHOST_IMMUNE && WEATHER_HAIL_GHOST_IMMUNE
    mov     r1, #TYPE_Ghost
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Ghost_Next
    b       ReturnZero
Hail_Ghost_Next:
#endif

#if !WEATHER_SAND_STEEL_IMMUNE && WEATHER_HAIL_STEEL_IMMUNE
    mov     r1, #TYPE_Steel
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Steel_Next
    b       ReturnZero
Hail_Steel_Next:
#endif

#if !WEATHER_SAND_FIRE_IMMUNE && WEATHER_HAIL_FIRE_IMMUNE
    mov     r1, #TYPE_Fire
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Fire_Next
    b       ReturnZero
Hail_Fire_Next:
#endif

#if !WEATHER_SAND_WATER_IMMUNE && WEATHER_HAIL_WATER_IMMUNE
    mov     r1, #TYPE_Water
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Water_Next
    b       ReturnZero
Hail_Water_Next:
#endif

#if !WEATHER_SAND_GRASS_IMMUNE && WEATHER_HAIL_GRASS_IMMUNE
    mov     r1, #TYPE_Grass
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Grass_Next
    b       ReturnZero
Hail_Grass_Next:
#endif

#if !WEATHER_SAND_ELECTRIC_IMMUNE && WEATHER_HAIL_ELECTRIC_IMMUNE
    mov     r1, #TYPE_Electric
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Electric_Next
    b       ReturnZero
Hail_Electric_Next:
#endif

#if !WEATHER_SAND_PSYCHIC_IMMUNE && WEATHER_HAIL_PSYCHIC_IMMUNE
    mov     r1, #TYPE_Psychic
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Psychic_Next
    b       ReturnZero
Hail_Psychic_Next:
#endif

#if !WEATHER_SAND_ICE_IMMUNE && WEATHER_HAIL_ICE_IMMUNE
    mov     r1, #TYPE_Ice
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Ice_Next
    b       ReturnZero
Hail_Ice_Next:
#endif

#if !WEATHER_SAND_DRAGON_IMMUNE && WEATHER_HAIL_DRAGON_IMMUNE
    mov     r1, #TYPE_Dragon
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Dragon_Next
    b       ReturnZero
Hail_Dragon_Next:
#endif

#if !WEATHER_SAND_DARK_IMMUNE && WEATHER_HAIL_DARK_IMMUNE
    mov     r1, #TYPE_Dark
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Dark_Next
    b       ReturnZero
Hail_Dark_Next:
#endif

#if !WEATHER_SAND_FAIRY_IMMUNE && WEATHER_HAIL_FAIRY_IMMUNE
    mov     r1, #TYPE_Fairy
    bl      Battle::PokeHasType
    cmp     r0, #FALSE
    beq     Hail_Fairy_Next
    b       ReturnZero
Hail_Fairy_Next:
#endif
    
ReturnTrue:
    mov     r0, r4
    mov     r1, #BPV_MaxHP
    bl      Battle::GetPokeStat
    lsr     r0, #4
    bne     Return ; max between 1/16 HP and 1
    
    mov     r0, #1
    
Return:
    pop     {r4, pc}
    
ReturnZero:
    mov     r0, #0
    pop     {r4, pc}
    
    
