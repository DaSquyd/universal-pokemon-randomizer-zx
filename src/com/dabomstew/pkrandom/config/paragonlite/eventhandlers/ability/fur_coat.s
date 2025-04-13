    push    {r4, lr}
    mov     r0, #4
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    lsl     r0, #24
    lsr     r0, #24
    cmp     r4, r0
    bne     End
    
    mov     r0, #18 ; move id
    bl      Battle::EventVar_GetValue
    lsl     r0, #16
    lsr     r0, #16
    bl      ARM9::GetMoveCategory
    cmp     r0, #1 ; physical
    bne     End
    
    mov     r0, #53 ; stat
    ldr     r1, =(0x1000 * ABILITY_FUR_COAT_MULTIPLIER)
    bl      Battle::EventVar_MulValue
    
End:
    pop     {r4, pc}