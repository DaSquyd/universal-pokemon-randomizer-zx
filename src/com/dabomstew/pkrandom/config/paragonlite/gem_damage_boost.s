    push    {r4-r6, lr}
    mov     r0, #3
    mov     r6, r1
    mov     r5, r2
    mov     r4, r3
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     End
    
    mov     r0, #0x16
    bl      Battle::EventVar_GetValue
    add     r1, sp, #16
    ldrb    r1, [r1]
    cmp     r1, r0
    bne     End
    
    ldr     r0, [r4]
    cmp     r0, #1
    beq     Boost
    
    mov     r0, r6
    bl      Battle::Handler_IsSimulationMode
    cmp     r0, #0
    beq     End
    
Boost:
    mov     r0, #0x31
    ldr     r1, =(GEM_ITEM_DAMAGE_MULTIPLIER * 0x1000)
    bl      Battle::EventVar_MulValue
    
End:
    pop     {r4-r6, pc}