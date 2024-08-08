    push    {r4, lr}
    mov     r0, #4
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    lsl     r0, #24
    lsr     r1, #24
    cmp     r1, r4
    beq     End ; cannot be user!
    
    mov     r0, r4
    bl      Battle::IsAllyPokeId
    cmp     r0, #0
    bne     End
    
    mov     r0, #18 ; move id
    bl      Battle::EventVar_GetValue
    mov     r0, #26 ; move category
    bl      Battle::EventVar_GetValue
    cmp     r0, #1 ; physical
    bne     End
    
    mov     r0, #53 ; stat
    ldr     r1, =5461 ; 1.33
    bl      Battle::EventVar_MulValue

End:
    pop     {r4, pc}