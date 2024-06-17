    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #2
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
    
    mov     r0, #22 ; move type
    bl      Battle::EventVar_GetValue
    cmp     r0, #0 ; Normal-type
    bne     End
    
    mov     r0, #22 ; move type
    mov     r1, #14 ; Ice-type
    bl      Battle::EventVar_RewriteValue
    
End:
    pop     {r4, pc}
