#DEFINE TYPE_STEEL 8
#DEFINE EFFECTIVENESS_DOUBLE 4

#DEFINE VAR_ATTACKING_MON 0x03
#DEFINE VAR_POKE_TYPE 0x15
#DEFINE VAR_NO_TYPE_EFFECTIVENESS 0x4B
#DEFINE VAR_SET_TYPE_EFFECTIVENESS 0x4C

    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_ATTACKING_MON
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_POKE_TYPE
    bl      Battle::EventVar_GetValue
    cmp     r0, #TYPE_STEEL
    bne     Return
    
    mov     r0, #VAR_SET_TYPE_EFFECTIVENESS
    mov     r1, #1
    bl      Battle::EventVar_RewriteValue
    
    mov     r0, #VAR_NO_TYPE_EFFECTIVENESS
    mov     r1, #EFFECTIVENESS_DOUBLE
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4, pc}