#DEFINE IDX 0x00
#DEFINE ALLY_COUNT 0x04
#DEFINE HEAL_ALLY_COUNT 0x8

    push    {r3-r7, lr}
    sub     sp, #0x0C
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    bl      Battle::Handler_GetTempWork
    mov     r6, r0
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::Handler_PokeIDToPokePos
    mov     r1, #EXND_AdjacentAllies_Ally
    lsl     r1, #8
    orr     r1, r0
    lsl     r1, #16
    lsr     r1, #16
    mov     r0, r5
    mov     r2, r6
    bl      Battle::Handler_ExpandPokeID
    str     r0, [sp, #ALLY_COUNT]
    beq     Return
    
Loop1_Setup:
    mov     r0, #0
    str     r0, [sp, #IDX]
    str     r0, [sp, #HEAL_ALLY_COUNT]
    
Loop1_Start:
    mov     r0, r5
    ldr     r1, [sp, #IDX]
    ldrb    r1, [r6, r1]
    bl      Battle::GetPoke
    mov     r7, r0
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    bne     Loop1_CheckContinue
    
    mov     r0, r7
    bl      Battle::IsPokeFullHP
    cmp     r0, #0
    bne     Loop1_CheckContinue
    
    ldr     r1, [sp, #HEAL_ALLY_COUNT]
    add     r1, #1
    str     r1, [sp, #HEAL_ALLY_COUNT]
    
Loop1_CheckContinue:
    ldr     r0, [sp, #IDX]
    add     r0, #1
    str     r1, [sp, #IDX]
    ldr     r1, [sp, #ALLY_COUNT]
    cmp     r0, r1
    bcc     Loop1_Start
    
    
CheckHasAnyHealAllies:
    ldr     r0, [sp, #HEAL_ALLY_COUNT]
    cmp     r0, #0
    beq     Return
    
AddAbilityPopup:
    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Add
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
Loop2_Setup:
    mov     r0, #0
    str     r0, [sp, #IDX]
    
Loop2_Start:
    mov     r0, r5
    mov     r1, #HE_RecoverHP
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    mov     r0, r5
    ldr     r1, [sp, #IDX]
    ldrb    r1, [r6, r1]
    strb    r1, [r7, #HandlerParam_RecoverHP.pokeId]
    bl      Battle::GetPoke
    mov     r1, #16
    bl      Battle::DivideMaxHPZeroCheck
    strh    r0, [r7, #HandlerParam_RecoverHP.amount]
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
    ldr     r0, [sp, #IDX]
    add     r0, #1
    str     r1, [sp, #IDX]
    ldr     r1, [sp, #ALLY_COUNT]
    cmp     r0, r1
    bcc     Loop2_Start
    
RemoveAbilityPopup:
    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Remove
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
Return:
    add     sp, #0x0C
    pop     {r3-r7, pc}