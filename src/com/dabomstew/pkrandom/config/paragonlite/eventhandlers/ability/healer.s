#define SP_PokeCount 0x00
#define SP_PokeStartPos 0x04

    push    {r4-r7, lr}
    sub     sp, #0x08
    mov     r5, r1
    mov     r6, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r6, r0
    bne     Return

    mov     r0, r5
    mov     r1, r6
    mov     r2, #ABILITY_HEALER_HP_FRACTION
    bl      Battle::CommonHealAlliesAbility
    
; vanilla    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::Handler_PokeIDToPokePos
    ldr     r1, =(EXND_AdjacentAllies_Ally << 8)
    orr     r1, r0
    mov     r0, r5
    add     r2, sp, #SP_PokeStartPos
    bl      Battle::Handler_ExpandPokeID
    str     r0, [sp, #SP_PokeCount]
    cmp     r0, #0
    bls     Return
    
    ldr     r7, =BHP_AutoRemove
    mov     r4, #0 ; index
    
Loop_Start:
    add     r0, sp, #SP_PokeStartPos
    ldrb    r1, [r0, r4]
    cmp     r1, r6
    beq     Loop_CheckContinue
    
    mov     r0, r5
    bl      Battle::GetPoke
    bl      Battle::GetPokeStatus
    cmp     r0, #MC_None
    beq     Loop_CheckContinue
    
    mov     r0, r5
    mov     r1, #ABILITY_HEALER_CURE_CHANCE
    bl      Battle::AbilityEvent_RollEffectChance
    cmp     r0, #FALSE
    beq     Loop_CheckContinue
    
    mov     r0, r5
    mov     r1, #HE_CureStatus
    mov     r2, r6
    bl      Battle::Handler_PushWork
    mov     r1, r0
    ldr     r0, [r1, #HandlerParam_CureCondition.header]
    ldr     r2, =BHP_AbilityPopup
    orr     r0, r2
    str     r0, [r1, #HandlerParam_CureCondition.header]
    
    mov     r0, #1
    strb    r0, [r1, #HandlerParam_CureCondition.pokeCount]
    
    add     r0, sp, #SP_PokeStartPos
    ldrb    r0, [r0, r4]
    strb    r0, [r1, #HandlerParam_CureCondition.pokeIds]
    
    mov     r0, #MC_MAX
    str     r0, [r1, #HandlerParam_CureCondition.conditionType]
    
    mov     r1, r5
    bl      Battle::Handler_PopWork
    
Loop_CheckContinue:
    add     r4, #1
    ldr     r0, [sp, #SP_PokeCount]
    cmp     r4, r0
    bcc     Loop_Start
    
Return:
    add     sp, #0x08
    pop     {r4-r7, pc}
    