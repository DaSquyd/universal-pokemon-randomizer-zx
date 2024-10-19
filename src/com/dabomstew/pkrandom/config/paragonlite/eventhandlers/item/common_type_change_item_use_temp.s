    push    {r4-r7, lr}    
    mov     r5, r1
    mov     r4, r2
    
    bl      Battle::EventObject_GetSubId
    mov     r6, r0 ; itemId
    
    bl      BattleServer::IsPlateItem
    cmp     r0, #0
    beq     Return
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    ; Set type
    mov     r0, r5
    mov     r1, #HE_ChangeType
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    mov     r0, r6
    bl      ARM9::GetTypeForPlate
    bl      Battle::TypePair_MakeMono
    strh    r0, [r7, #HandlerParam_ChangeType.type]
    
    mov     r0, r4
    strb    r0, [r7, #HandlerParam_ChangeType.pokeId]
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r4-r7, pc}