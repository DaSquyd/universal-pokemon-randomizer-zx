    push    {r4-r6, lr}
    mov     r4, r1
    mov     r5, r2
    mov     r0, #2
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     End
    
    mov     r0, r4
    mov     r1, r5
    bl      Battle::CanUseItemFromPokeData
    cmp     r0, #0
    beq     End
    
    mov     r0, r4
    mov     r1, r5
    bl      Battle::GetPoke
    bl      Battle::GetPokeHeldItem
    lsl     r0, #16
    lsr     r0, #16
    bl      ARM9::GetTypeForPlate
    mov     r1, r0
    mov     r0, #0x16
    bl      Battle::EventVar_RewriteValue
    
End:
    pop     {r4-r6, pc}