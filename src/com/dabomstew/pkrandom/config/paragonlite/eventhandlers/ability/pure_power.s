    push    {r4, lr}
    mov     r0, #3
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #18 ; move id
    bl      Battle::EventVar_GetValue
    lsl     r0, #16
    lsr     r0, #16
    
    bl      ARM9::GetMoveCategory
    cmp     r0, #2 ; special
    bne     End
    
    mov     r0, #53 ; stat
    mov     r1, #6
    lsl     r1, #10 ; 6144 (1.5x)
    bl      Battle::EventVar_MulValue
    
End:
    pop     {r4, pc}