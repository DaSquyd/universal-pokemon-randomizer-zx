    push    {r4, r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #3
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    bl      Battle::Poke_IsFullHP
    cmp     r0, #0
    beq     End
    
    mov     r0, #18 ; move id
    bl      Battle::EventVar_GetValue
    lsl     r0, #16
    lsr     r0, #16
    bl      ARM9::GetMoveType ; base (unmodified) move type
    cmp     r0, #2 ; Flying-type
    bne     End
    
    mov     r0, #24
    bl      Battle::EventVar_GetValue
    lsl     r0, #24
    lsr     r1, r0, #24
    mov     r0, #24
    add     r1, r1, #1
    bl      Battle::EventVar_RewriteValue
    
End:
    pop     {r4, r5, pc}
