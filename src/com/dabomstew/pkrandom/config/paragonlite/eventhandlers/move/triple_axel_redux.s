    push    {r3-r5, lr}
    mov     r5, r2
    mov     r4, r3
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     End
    
; get iteration count
    ldr     r2, [r4]
    
    mov     r1, #MOVE_TRIPLE_AXEL_INCREMENT
    mul     r1, r2
    
#if (MOVE_TRIPLE_AXEL_BASE_POWER - MOVE_TRIPLE_AXEL_INCREMENT) > 0
    add     r1, #(MOVE_TRIPLE_AXEL_BASE_POWER - MOVE_TRIPLE_AXEL_INCREMENT)
#elif (MOVE_TRIPLE_AXEL_BASE_POWER - MOVE_TRIPLE_AXEL_INCREMENT) < 0
    sub     r1, #(MOVE_TRIPLE_AXEL_INCREMENT - MOVE_TRIPLE_AXEL_BASE_POWER)
#endif
    
; increment iteration count
    add     r2, #1
    str     r2, [r4]
    
    mov     r0, #VAR_MoveBasePower
    bl      Battle::EventVar_RewriteValue
    
End:
    pop     {r3-r5, pc}