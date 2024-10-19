; 0x11
    push    {r4, r5, lr}
    mov     r0, #3
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #18 ; move id
    bl      Battle::EventVar_GetValue
    lsl     r0, #16
    lsr     r0, #16
    mov     r5, r0
    
    bl      ARM9::IsMoveDamaging
    cmp     r0, #1
    bne     Return
    
    mov     r0, r5
    bl      ARM9::GetMoveBasePower
    
    ; ignore stronger moves
    cmp     r0, #60
    bhi     Return
    
    ; ignore variable power moves
    cmp     r0, #1
    beq     Return
    
    ; ignore guaranteed crit moves
    mov     r0, r5
    bl      ARM9::IsMoveAlwaysCrit
    cmp     r0, #0 ; not crit
    bne     Return
    
    ; ignore multi-strike moves
    mov     r0, r5
    mov     r1, #MVD_MaxHits
    bl      ARM9::GetMoveMetadata
    cmp     r0, #1
    bhi     Return
    
    mov     r0, #24
    bl      Battle::EventVar_GetValue
    lsl     r0, #24
    lsr     r1, r0, #24
    mov     r0, #24
    add     r1, r1, #1
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4, r5, pc}
