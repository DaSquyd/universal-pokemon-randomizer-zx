    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #0x12 ; move id
    bl      Battle::EventVar_GetValue
    bl      ARM9::GetMoveCategory
    cmp     r0, #1 ; Physical
    bne     Return
    
    mov     r0, #0x35
    ldr     r1, =3686 ; 0.9x
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}