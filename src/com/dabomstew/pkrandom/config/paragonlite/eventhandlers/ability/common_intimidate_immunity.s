    push    {r3-r6, lr}
    mov     r5, r1
    mov     r4, r2
    mov     r6, r3
    
    mov     r0, #VAR_IntimidateFlag
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    beq     Return
    
    mov     r0, r5
    mov     r1, r4
    mov     r2, r6
    mov     r3, #STSG_Attack
    bl      Battle::CommonStatDropGuardCheck
    
    mov     r0, r5
    mov     r1, r4
    mov     r2, r6
    mov     r3, #BTLTXT_AttackNotLowered
    bl      Battle::CommonStatDropGuardMessage
    
Return:
    pop     {r3-r6, pc}