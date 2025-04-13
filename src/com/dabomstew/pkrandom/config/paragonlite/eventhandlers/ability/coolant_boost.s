    push    {r4-r6, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    mov     r1, #MC_Frostbite
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     Return
    
    mov     r0, #VAR_MoveCategory
    bl      Battle::EventVar_GetValue
    cmp     r0, #CAT_Special
    bne     Return
    
    mov     r0, #VAR_MovePower
    mov     r1, #6
    lsl     r1, #10
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4-r6, pc}