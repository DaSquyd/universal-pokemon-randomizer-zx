; 25% -> 20%
    push    {r4, lr}
    mov     r0, #4
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    lsl     r0, #24
    lsr     r1, #24
    cmp     r1, r4
    beq     End
    
    mov     r0, r4
    bl      Battle::IsAllyPokeId
    cmp     r0, #0
    beq     End
    
    ldr     r1, =3277 ; 80%
    mov     r0, #53 ; damage
    bl      Battle::EventVar_MulValue
    
End:
    pop     {r4, pc}