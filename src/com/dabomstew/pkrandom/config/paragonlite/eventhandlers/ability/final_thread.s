    push    {r3-r7, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    beq     Return
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    mov     r6, r0
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::GetPoke
    mov     r7, r0
    
    
    
    bl      Battle::MakeIndefiniteCondition
    mov     r4, r0