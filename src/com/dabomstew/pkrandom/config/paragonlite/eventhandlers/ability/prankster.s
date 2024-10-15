    push    {r4-r6, lr}
    mov     r5, r1
    mov     r4, r2
    mov     r6, r0
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    bl      ARM9::GetMoveCategory
    cmp     r0, #CAT_Status
    bne     Return
    
    mov     r0, #VAR_MovePriority
    bl      Battle::EventVar_GetValue
    add     r1, r0, #1
    mov     r0, #VAR_MovePriority
    bl      Battle::EventVar_RewriteValue
    
;    ; NEW - Prankster flag
;    mov     r0, r5
;    mov     r1, #HE_SetTurnFlag
;    mov     r2, r4
;    bl      Battle::Handler_PushWork
;    mov     r7, r0
;    
;    mov     r0, r5
;    mov     r1, r7
;    bl      Battle::Handler_PopWork
    
Return:
    pop     {r4-r6, pc}