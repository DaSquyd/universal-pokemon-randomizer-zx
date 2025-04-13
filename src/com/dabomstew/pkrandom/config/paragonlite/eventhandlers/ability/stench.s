    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #0x25
    bl      Battle::EventVar_GetValue
    lsl     r0, #24
    lsr     r0, #24
    bne     Return
    
    mov     r0, #0x26
    mov     r1, #ABILITY_STENCH_FLINCH_PERCENT
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4, pc}