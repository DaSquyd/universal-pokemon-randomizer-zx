    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #3
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #18 ; move id
    bl      Battle::EventVar_GetValue
    lsl     r0, #16
    lsr     r0, #16
    
    cmp     r0, #165 ; struggle
    beq     End
    cmp     r0, #237 ; Hidden Power
    beq     End
    ldr     r1, =311 ; Weather Ball
    cmp     r0, r1
    beq     End
    ldr     r1, =363 ; Natural Gift
    cmp     r0, r1
    beq     End
    ldr     r1, =449 ; Judgment
    cmp     r0, r1
    beq     End
    ldr     r1, =546 ; Techno Blast
    cmp     r0, r1
    beq     End
    
    bl      ARM9::GetMoveType ; base (unmodified) move type
    cmp     r0, #0 ; Normal-type
    bne     End
    
    mov     r0, #49 ; power
    ldr     r1, =4915 ; 1.2x
    bl      Battle::EventVar_MulValue
    
End:
    pop     {r4, pc}