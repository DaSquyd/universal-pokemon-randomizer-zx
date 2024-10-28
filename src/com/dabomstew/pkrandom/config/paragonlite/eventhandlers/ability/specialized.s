    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_TypeMatchFlag
    bl      Battle::EventVar_GetValue
    cmp     r0, #FALSE
    beq     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    bl      Battle::GetPokeType
    bl      Battle::TypePair_IsMonoType
    mov     r1, #8
    cmp     r0, #FALSE
    bne     ApplyMod
    
    mov     r1, #7

ApplyMod:
    lsl     r1, #10
    mov     r0, #VAR_Ratio
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r3-r5, pc}