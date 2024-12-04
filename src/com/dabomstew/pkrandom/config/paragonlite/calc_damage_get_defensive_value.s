; This function is used as the "D" in the base damage formula:
;   Base Damage = ((2 * L / 5 + 2) * Power * A / D / 50) + 2
;
; It includes two event calls, ModifyDefensiveStat (0x3A) and ModifyDefensiveStatValue (0x3C)
;   The first (0x3A) is used by moves like Psyshock/Psystrike/Secret Sword to flag damage as Physical or for
;   the use of the raw stats seen in Unaware or Chip Away/Sacred Sword.
;   Unaware:                            0x51 := 1 (use raw stat)
;   Psyshock/Psystrike/Secret Sword:    0x3D := 1 (flip stat)
;   Chip Away/Sacred Sword:             0x51 := 1 (use raw stat)
;
; It also includes the 1.5x Sp. Def boost for Rock-types in Sandstorm
;   and the 1.5x Defense boost for Ice-types in Hail

#define S_ServerFlow 0x00
#define S_AttackingPoke 0x04
#define S_MoveCategory 0x08
#define StackSize 0x0C
#define PushSize (4 * 5) ; r4-r7, lr

#define Arg_IsCrit (PushSize + 0x00)

    push    {r4-r7, lr}
    sub     sp, #StackSize
    mov     r6, r3
    str     r0, [sp, #S_ServerFlow]
    ldrh    r0, [r6]
    str     r1, [sp, #S_AttackingPoke]
    mov     r5, r2
    bl      ARM9::GetMoveCategory
    mov     r4, #BPV_SpDefStat
    cmp     r0, #CAT_Special
    beq     ModifyStatEvent
    mov     r4, #BPV_DefenseStat

ModifyStatEvent:
     ldr    r0, [r6, #0x08]
     str    r0, [sp, #S_MoveCategory]
     bl     Battle::EventVar_Push
     
     ldr    r0, [sp, #S_AttackingPoke]
     bl     Battle::GetPokeId
     mov    r1, r0
     mov    r0, #VAR_AttackingPoke
     bl     Battle::EventVar_SetConstValue
     
     mov    r0, r5
     bl     Battle::GetPokeId
     mov    r1, r0
     mov    r0, #VAR_DefendingPoke
     bl     Battle::EventVar_SetConstValue
     
     mov    r0, #VAR_Stat
     mov    r1, r4
     bl     Battle::EventVar_SetConstValue
     
     mov    r0, #VAR_StatSwapFlag
     mov    r1, #FALSE
     bl     Battle::EventVar_SetValue
     
     mov    r0, #VAR_GeneralUseFlag
     mov    r1, #FALSE
     bl     Battle::EventVar_SetRewriteOnceValue
    
    mov     r0, #VAR_CritStatFlag
    mov     r1, #FALSE
    bl      Battle::EventVar_SetValue
     
     ldr    r0, [sp, #S_ServerFlow]
     mov    r1, #EVENT_OnGetDefendingStat
     bl     Battle::Event_CallHandlers
     
     mov    r0, #VAR_StatSwapFlag
     bl     Battle::EventVar_GetValue
     mov    r1, #TRUE
     tst    r0, r1
     beq    CheckRawStat
     
; Should flip stats...
     cmp    r4, #BPV_DefenseStat
     bne    SetStatToDefense
     mov    r4, #BPV_SpDefStat
     b      UpdateCategory
     
SetStatToDefense:
    mov     r4, #BPV_DefenseStat

UpdateCategory:
    cmp     r4, #BPV_DefenseStat
    bne     SetCategoryToSpecial
    mov     r0, #CAT_Physical
    b       StoreCategory
    
SetCategoryToSpecial:
    mov     r0, #CAT_Special
    
StoreCategory:
    str     r0, [sp, #S_MoveCategory]
    
CheckRawStat:
    mov     r0, #VAR_GeneralUseFlag
    bl      Battle::EventVar_GetValue
    mov     r7, r0
    
    bl      Battle::EventVar_Pop
    
    cmp     r7, #FALSE
    beq     CheckCrit
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetRawPokeStat
    b       CheckWeather
    
CheckCrit:
    ldr     r0, [sp, #Arg_IsCrit]
    cmp     r0, #FALSE
    bne     UseCritStat
    
    mov     r0, #VAR_CritStatFlag
    bl      Battle::EventVar_GetValue
    cmp     r0, #FALSE
    beq     GetStat
    
UseCritStat:
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetCritPokeStat
    b       CheckWeather
    
GetStat:
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPokeStat
    
CheckWeather:
    lsl     r0, #16
    lsr     r7, r0, #16
    
    ldr     r0, [sp, #S_ServerFlow]
    bl      Battle::ServerEvent_GetWeather
    cmp     r0, #3 ; hail
    beq     CheckHail
    cmp     r0, #4 ; sandstorm
    bne     ModifyStatValueEvent
    
CheckSandstorm:
    mov     r0, r5
    mov     r1, #5 ; Rock-type
    bl      Battle::PokeHasType
    cmp     r0, #0
    beq     ModifyStatValueEvent
    
    cmp     r4, #0x0B ; Sp. Def stat
    beq     WeatherBoost
    b       ModifyStatValueEvent
    
CheckHail:
    mov     r0, r5
    mov     r1, #14 ; Ice-type
    bl      Battle::PokeHasType
    cmp     r0, #0
    beq     ModifyStatValueEvent
    
    cmp     r4, #0x09 ; Defense stat
    bne     ModifyStatValueEvent
    
WeatherBoost:
    mov     r0, r7
    mov     r1, #6
    lsl     r1, #10 ; 1.5x
    bl      Battle::FixedRound
    lsl     r0, #10
    lsr     r7, r0, #10

ModifyStatValueEvent:
    ldr     r4, =0x3178
    mov     r0, r4
    bl      Battle::EventVar_Push
    ldr     r0, [sp, #S_AttackingPoke]
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #3
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, r5
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #4
    mov     r5, #4
    bl      Battle::EventVar_SetConstValue
    
    ldrh    r1, [r6, #0x00]
    mov     r0, #0x12
    bl      Battle::EventVar_SetConstValue
    
    ldrb    r1, [r6, #0x06]
    mov     r0, #0x16
    bl      Battle::EventVar_SetConstValue
    
    ldr     r1, [sp, #S_MoveCategory]
    mov     r0, #0x1A
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #0x34
    mov     r1, r7
    bl      Battle::EventVar_SetValue
    
    ldr     r2, =0x019A
    mov     r0, #0x35
    lsl     r1, r5, #10
    lsl     r3, r5, #15
    bl      Battle::EventVar_SetMulValue
    
    ldr     r0, [sp, #S_ServerFlow]
    mov     r1, #0x3C
    bl      Battle::Event_CallHandlers
    
    mov     r0, #0x34
    bl      Battle::EventVar_GetValue
    lsl     r0, #16
    lsr     r5, r0, #16
    mov     r0, #0x35
    bl      Battle::EventVar_GetValue
    mov     r6, r0
    
    bl      Battle::EventVar_Pop
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::FixedRound
    
    lsl     r0, #16
    lsr     r0, #16
    
    add     sp, #StackSize
    pop     {r4-r7, pc}