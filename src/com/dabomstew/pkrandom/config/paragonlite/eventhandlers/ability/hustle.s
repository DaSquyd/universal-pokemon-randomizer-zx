; 0x11
    push    {r4, r5, lr}
    mov     r0, #3
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #18 ; move id
    bl      Battle::EventVar_GetValue
    lsl     r0, #16
    lsr     r0, #16
    mov     r5, r0
    
    bl      ARM9::IsMoveDamaging
    cmp     r0, #1
    bne     End
    
    mov     r0, r5
    bl      ARM9::GetMoveBasePower
    cmp     r0, #60 ; max move power for effect
    bhi     End
    cmp     r0, #1 ; variable power moves don't get priority
    beq     End
    
    ; ignore guaranteed crit moves
    mov     r0, r5
    bl      ARM9::IsMoveAlwaysCrit
    cmp     r0, #0 ; not crit
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
