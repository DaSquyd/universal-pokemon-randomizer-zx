#DEFINE STEEL_TYPE 8
#DEFINE EFFECTIVENESS_DOUBLE 4

    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #0x15 ; target type
    bl      Battle::EventVar_GetValue
    cmp     r0, #STEEL_TYPE
    bne     Return
    
    mov     r0, #0x4C
    mov     r1, #1
    bl      Battle::EventVar_RewriteValue
    
    mov     r0, #0x4B
    mov     r1, #EFFECTIVENESS_DOUBLE
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4, pc}