#DEFINE VAR_ATTACKING_MON 0x03
#DEFINE VAR_DEFENDING_MON 0x04
#DEFINE CONDITION_BURN 0x05

    push    {r3-r5, lr}
    mov     r4, r1
    mov     r5, r2
    
    mov     r0, #VAR_ATTACKING_MON
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return
    
    mov     r0, #VAR_DEFENDING_MON
    bl      Battle::EventVar_GetValue
    mov     r1, #CONDITION_POISON
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     Return
    
    mov     r0, #2
    bl      Battle::CommonMultiplyBasePower
    
Return:
    pop     {r3-r5, pc}
    