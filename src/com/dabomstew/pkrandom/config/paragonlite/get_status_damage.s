#DEFINE CONDITION_SLEEP 0x02
#DEFINE CONDITION_CURSE 0x0A

#DEFINE BATTLE_POKE_CONDITIONS_ARR 0x1C
#DEFINE BATTLE_POKE_CONDITION_COUNTER 0xAC

    push    {r3-r5, lr}
    mov     r4, r0
    mov     r5, r1
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     ReturnZero
    
    cmp     r5, #CONDITION_CURSE
    bhi     ReturnZero
    
    #SWITCH r0 r5
    #CASE ReturnZero
    #CASE ReturnZero ; Paralysis
    #CASE ReturnZero ; Sleep
    
#if STATUS_FREEZE_REPLACE_WITH_FROSTBITE && STATUS_BURN_DAMAGE_FRACTION == STATUS_FROSTBITE_DAMAGE_FRACTION
    #CASE Burn ; Frostbite (same damage as Burn)
#elif STATUS_FREEZE_REPLACE_WITH_FROSTBITE && STATUS_BURN_DAMAGE_FRACTION != STATUS_FROSTBITE_DAMAGE_FRACTION
    #CASE Frostbite ; Frostbite (different damage from Burn)
#else
    #CASE ReturnZero ; Freeze
#endif

    #CASE Burn ; Burn
    #CASE Poison
    #CASE ReturnZero ; Confusion
    #CASE ReturnZero ; Attract
    #CASE ReturnZero ; Bind
    #CASE Nightmare
    #CASE Curse
    
Burn:
    mov     r0, r4
    mov     r1, #STATUS_BURN_DAMAGE_FRACTION ; 1/16
    b       ReturnDivide

Frostbite:
#if STATUS_FREEZE_REPLACE_WITH_FROSTBITE && STATUS_BURN_DAMAGE_FRACTION != STATUS_FROSTBITE_DAMAGE_FRACTION
    mov     r0, r4
    mov     r1, #STATUS_BURN_DAMAGE_FRACTION ; 1/16
    b       ReturnDivide
#endif
    
Poison:
    lsl     r0, r5, #2
    add     r0, r4
    add     r0, #BATTLE_POKE_CONDITIONS_ARR
    ldr     r0, [r0]
    bl      Battle::ConditionPtr_GetIsBadPoison
    cmp     r0, #0
    bne     BadPoison
    
    mov     r0, r4
    mov     r1, #8 ; 1/8
    b       ReturnDivide
    
BadPoison:
    mov     r0, r4
    mov     r1, #16 ; 1/16
    bl      Battle::DivideMaxHPZeroCheck
    add     r1, r4, r5
    add     r1, #BATTLE_POKE_CONDITION_COUNTER
    ldrb    r1, [r1]
    mul     r0, r1
    pop     {r3-r5, pc}
    
    
Nightmare:
    mov     r0, r4
    mov     r1, #CONDITION_SLEEP
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     ReturnZero
    ; fall through to Curse damage
    
Curse:
    mov     r0, r4
    mov     r1, #4 ; 1/4
    
ReturnDivide:
    bl      Battle::DivideMaxHPZeroCheck
    pop     {r3-r5, pc}
    
ReturnZero:
    mov     r0, #0
    pop     {r3-r5, pc}