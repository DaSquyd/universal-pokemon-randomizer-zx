; This function is used as the "A" in the base damage formula:
;   Base Damage = ((2 * L / 5 + 2) * Power * A / D / 50) + 2
;
; It includes two event calls, ModifyOffensiveStat (0x39) and ModifyOffensiveStatValue (0x3B)
;   The first (0x39) is used by Foul Play to modify which Pokémon's stat will be used or for
;   the use of the raw stats seen in Unaware.
;   Unaware:    0x51 := 1 (use raw stat)
;   Foul Play:  0x3B := target
;
; We make a modification that allows us to pass in the actual stat of choice to be modified or
;   left alone using 0x35.

#DEFINE STACK_SIZE 0x18

; Stack Vars
#DEFINE POKE (STACK_SIZE - 0x18)

; Stack Args
#DEFINE IS_CRIT (STACK_SIZE + 0x00)

    push    {r3-r7, lr}
    mov     r7, r0
    mov     r6, r1
    mov     r5, r3
    str     r2, [sp, #POKE]
    
    ldrh    r0, [r5]
    bl      ARM9::GetMoveCategory
    mov     r4, #0x0A ; Sp. Atk
    cmp     r0, #2 ; Special
    beq     CallModifyStatHandlers
    mov     r4, #0x08 ; Attack
    
CallModifyStatHandlers:
    bl      Battle::EventVar_Push
    
    mov     r0, r6
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_SetConstValue
    
    ldr     r0, [sp, #POKE]
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_SwapPokeId
    mov     r1, #0x1F ; default is -1 (5 bits)
    bl      Battle::EventVar_SetValue
    
    mov     r0, #VAR_GeneralUseFlag ; use raw stats
    mov     r1, #0
    bl      Battle::EventVar_SetValue
    
    mov     r0, #0x35 ; stat
    mov     r1, r4
    bl      Battle::EventVar_SetValue
    
    mov     r0, r7
    mov     r1, #0x39 ; OnModifyStat
    bl      Battle::Event_CallHandlers
    
PostModifyStat:
    mov     r0, #0x35 ; stat
    bl      Battle::EventVar_GetValue
    mov     r4, r0

    mov     r0, #0x3B ; poke
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    cmp     r0, #0x1F
    beq     CheckUseRawStat
    
    ldr     r0, [r7, #0x08]
    bl      Battle::PokeCon_GetPoke
    mov     r6, r0
    
CheckUseRawStat:
    mov     r0, #0x51 ; use raw stats
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    beq     CheckUseCritStat
    
    mov     r0, r6
    mov     r1, r4
    bl      Battle::GetRawPokeStat
    b       CallModifyStatValueHandlers
    
CheckUseCritStat:
    ldr     r0, [sp, #IS_CRIT]
    cmp     r0, #0
    beq     GetStat
    
    mov     r0, r6
    mov     r1, r4
    bl      Battle::GetCritPokeStat
    b       CallModifyStatValueHandlers
    
GetStat:
    mov     r0, r6
    mov     r1, r4
    bl      Battle::GetPokeStat
    
CallModifyStatValueHandlers:
    mov     r4, r0
    
    mov     r0, #0x12 ; move id
    ldrh    r1, [r5]
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #0x16 ; move type
    ldrb    r1, [r5, #0x06]
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #0x1A ; move category
    ldr     r1, [r5, #0x08]
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #0x33 ; stat value
    mov     r1, r4
    bl      Battle::EventVar_SetValue
    
    mov     r1, #1
    lsl     r1, #12
    mov     r0, #0x35 ; stat value multiplier
    mov     r2, #0xCD
    lsl     r2, #1 ; 0x019A (0.1x)
    lsl     r3, r1, #5 ; 0x50000 (5.0x)
    bl      Battle::EventVar_SetMulValue
    
    mov     r0, r7
    mov     r1, #0x3B ; OnModifyStatValue
    bl      Battle::Event_CallHandlers
    
PostModifyStatValue:
    mov     r0, #0x33 ; stat value
    bl      Battle::EventVar_GetValue
    mov     r4, r0
    
    mov     r0, #0x35 ; stat value multiplier
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    mov     r0, r4
    bl      Battle::FixedRound
    mov     r4, r0
    
    bl      Battle::EventVar_Pop
    
    mov     r0, r4
    pop     {r3-r7, pc}
    