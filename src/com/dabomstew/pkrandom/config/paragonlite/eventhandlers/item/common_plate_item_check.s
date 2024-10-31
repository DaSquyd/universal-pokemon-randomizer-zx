    push    {r4-r5, lr}
    mov     r6, r0  
    mov     r5, r1
    mov     r4, r2
    
    bl      BattleServer::IsPlateItem
    cmp     r0, #0
    beq     Return
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
PushRun:
    mov     r0, r6
    mov     r1, r5
    mov     r2, r4
    bl      Battle::ItemEvent_PushRun
    
Return:
    pop     {r4-r5, pc}