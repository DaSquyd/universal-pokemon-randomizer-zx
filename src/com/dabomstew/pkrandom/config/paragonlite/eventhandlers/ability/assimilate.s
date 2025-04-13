    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2
    mov     r0, r5
    mov     r1, r4
    mov     r2, #TYPE_Psychic
    bl      Battle::CommonTypeImmuneCheck
    cmp     r0, #FALSE
    beq     Return
    
    mov     r0, r5
    mov     r1, r4
    mov     r2, #STSG_SpAtk
    mov     r3, #1 ; boost amount
    bl      Battle::CommonTypeNoEffectBoost
    
Return:
    pop     {r3-r5, pc}