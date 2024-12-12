#define BoostAmount 1

    push    {r4-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r1, #STSG_Attack
    mov     r2, #BoostAmount
    bl      Battle::IsStatChangeValid
    cmp     r0, #FALSE
    beq     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    mov     r1, #MF_Bite
    bl      ARM9::MoveHasFlag
    cmp     r0, #FALSE
    beq     Return
    
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0
    
    ldr     r0, [r1, #HandlerParam_ChangeStatStage.header]
    mov     r2, #(BHP_AbilityPopup >> 16)
    lsl     r2, #16
    orr     r0, r2
    str     r0, [r1, #HandlerParam_ChangeStatStage.header]
    
    mov     r0, #STSG_Speed
    str     r0, [r1, #HandlerParam_ChangeStatStage.stat]
    mov     r0, #1
    strb    r0, [r1, #HandlerParam_ChangeStatStage.amount] ; 1
    strb    r0, [r1, #HandlerParam_ChangeStatStage.pokeCount] ; 1
    strb    r4, [r1, #HandlerParam_ChangeStatStage.pokeId]
        
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r4-r5, pc}
