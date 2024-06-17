    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2
    mov     r0, r5
    mov     r1, r4
    mov     r2, #16 ; Dark-type
    bl      Battle::CommonDamageRecoverCheck
    cmp     r0, #0
    beq     End
    
    mov     r0, r5
    mov     r1, r4
    mov     r2, #1 ; attack
    mov     r3, #1 ; boost amount
    bl      Battle::CommonTypeNoEffectBoost
    
End:
    pop     {r3-r5, pc}