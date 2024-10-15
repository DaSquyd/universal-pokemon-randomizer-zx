    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    
    bl      ARM9::GetMoveCategory
    cmp     r0, #CAT_Physical
    bne     Return
    
    mov     r0, #VAR_Ratio
#if PARAGONLITE
    mov     r1, #6
    lsl     r1, #10 ; 6144 (1.5x)
#else
    mov     r1, #8
    lsl     r1, #10 ; 8192 (2.0x)
#endif
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}