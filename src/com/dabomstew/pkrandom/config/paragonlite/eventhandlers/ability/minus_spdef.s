    push    {r4, lr}
    mov     r0, #4
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    lsl     r0, #24
    lsr     r1, #24
    cmp     r1, r4
    beq     Return ; cannot be user!
    
    mov     r0, r4
    bl      Battle::IsAllyPokeId
    cmp     r0, #0
    bne     Return
    
    mov     r0, #0x12 ; move id
    bl      Battle::EventVar_GetValue
    mov     r0, #0x1A ; move category
    bl      Battle::EventVar_GetValue
    cmp     r0, #2 ; special
    bne     Return
    
    mov     r0, #0x35 ; stat
    ldr     r1, =5325 ; 1.3x
    bl      Battle::EventVar_MulValue

Return:
    pop     {r4, pc}