#DEFINE S_HealRatio 0x00
#DEFINE S_Index 0x04
#DEFINE S_AllyCount 0x08
#DEFINE S_HasHealed 0x0C

    push    {r3-r7, lr}
    sub     sp, #0x10
    mov     r5, r0
    mov     r4, r1
    str     r2, [sp, #S_HealRatio]
    
    mov     r0, r5
    bl      Battle::Handler_GetTempWork
    mov     r6, r0
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::Handler_PokeIDToPokePos
    mov     r1, #EXND_AdjacentAllies_Ally
    lsl     r1, #8
    orr     r1, r0
    mov     r0, r5
    mov     r2, r6 ; store at temp work
    bl      Battle::Handler_ExpandPokeID
    str     r0, [sp, #S_AllyCount]
    beq     Return
    
    
Loop_Init:
    mov     r0, #0
    str     r0, [sp, #S_Index]
    
Loop_Start:
    mov     r0, r5
    ldr     r1, [sp, #S_Index]
    ldrb    r1, [r6, r1]
    bl      Battle::GetPoke
    mov     r7, r0
    bl      Battle::IsPokeFainted
    cmp     r0, #FALSE
    bne     Loop_CheckContinue
    
    mov     r0, r7
    bl      Battle::IsPokeFullHP
    cmp     r0, #FALSE
    bne     Loop_CheckContinue
    
    ldr     r0, [sp, #S_HasHealed]
    cmp     r0, #FALSE
    bne     Loop_Recover
    
    mov     r0, #TRUE
    str     r0, [sp, #S_HasHealed]
    
    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Remove
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
Loop_Recover:
    mov     r0, r5
    mov     r1, #HE_RecoverHP
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    mov     r0, r5
    ldr     r1, [sp, #S_Index]
    ldrb    r1, [r6, r1]
    strb    r1, [r7, #HandlerParam_RecoverHP.pokeId]
    bl      Battle::GetPoke
    ldr     r1, [sp, #S_HealRatio]
    bl      Battle::DivideMaxHPZeroCheck
    strh    r0, [r7, #HandlerParam_RecoverHP.amount]
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
Loop_CheckContinue:
    ldr     r0, [sp, #S_Index]
    add     r0, #1
    str     r1, [sp, #S_Index]
    ldr     r1, [sp, #S_AllyCount]
    cmp     r0, r1
    bcc     Loop_Start
    
RemoveAbilityPopup:
    ; check if we healed an ally
    ldr     r0, [sp, #S_HasHealed]
    cmp     r0, #FALSE
    beq     Return

    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Remove
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
Return:
    add     sp, #0x10
    pop     {r3-r7, pc}